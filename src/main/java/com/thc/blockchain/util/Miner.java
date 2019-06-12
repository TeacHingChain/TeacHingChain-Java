package com.thc.blockchain.util;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;
import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.thc.blockchain.wallet.MainChain.difficulty;

public class Miner {

    private static double hashRate;
    private static Timer timer;
    private static int updatedIndex;
    private static String hashedBlockHeaderBytes;

    public void mine(long index, long[] timeStamps, String[] txins, String[] txouts, String[] txHash, String merkleRoot,
                     long nonce, String previousBlockHash, String algo, String target, double difficulty, double[] amounts) {
        try {
            new NetworkConfigFields();
            EndpointManager endpointManager = new EndpointManager();
            int indexAtStart = BlockChain.blockChain.size();
            MainChain mc = new MainChain();
            MainChain.targetHex = target;
            BigDecimal targetAsBigDec = new BigDecimal(new BigInteger(target, 16));
            System.out.println("difficulty says: \n" + difficulty);
            long startTime = System.nanoTime();
            TimeUnit.MILLISECONDS.sleep(1000);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                mc.readBlockChain();
                System.out.println("\n");
                System.out.println("Current hash rate: " + hashRate / 1000 + "k" + " " + "hash/s");
                if (hashedBlockHeaderBytes.length() < 64) {
                    hashedBlockHeaderBytes = leftPad(hashedBlockHeaderBytes, 64, '0');
                }
                System.out.println("Current hash: " + hashedBlockHeaderBytes);
                System.out.println("Big decimal value of hash: " + new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)));
                System.out.println("Current target: " + targetAsBigDec);
                if (MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray()).length() < 64) {
                    String tempTarget = MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray());
                    tempTarget = leftPad(tempTarget, 64, '0');
                    System.out.println("Current target hex: " + tempTarget);
                }
                updatedIndex = BlockChain.blockChain.size();
                }
            }, 0, 3000);
            while (true) {
                long deltaS;
                long deltaN;
                long endTime;

                hashedBlockHeaderBytes = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray
                        (MainChain.swapEndianness((index
                        + Arrays.toString(timeStamps) + Arrays.toString(txins) + Arrays.toString(txouts) + Arrays.toString(txHash) + merkleRoot + nonce
                        + previousBlockHash + algo + target + difficulty + Arrays.toString(amounts)).getBytes()))));
                if (new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)).compareTo(targetAsBigDec) <= 0) {
                    System.out.println("\n");
                    System.out.println("[" + hashedBlockHeaderBytes + "]");
                    System.out.println("Current value - target value: " + new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16))
                            .subtract(targetAsBigDec));
                    System.out.println("\n");
                    if (target.length() < 64) {
                        leftPad(target, 64, '0');
                    }
                    System.out.println("Adding block to chain...\n");
                    Block block = new Block(index, timeStamps, txins, txouts, txHash, merkleRoot,
                            nonce, previousBlockHash, algo, hashedBlockHeaderBytes,
                            target, difficulty, amounts);
                    String encodedBlock = new BlockEncoder().encode(block);
                    if (new Consensus().validateBlock(block)) {
                        endpointManager.connectAsClient("hello");
                        if (endpointManager.getIsNode1Connected() || endpointManager.getIsNode2Connected()) {
                            System.out.println("Is node 1 connected: " + endpointManager.getIsNode1Connected());
                            System.out.println("Is node 2 connected: " + endpointManager.getIsNode2Connected());
                            Session sessionForMiner = NodeManager.getSession();
                            NodeManager.pushBlock(block, sessionForMiner);
                            if (new Consensus().isBlockOrphan(block.getIndex())) {
                                BlockChain.blockChain.add(encodedBlock);
                                mc.writeBlockChain();
                                System.out.println("Size: " + BlockChain.blockChain.size());
                                if (BlockChain.blockChain.size() == 5) {
                                    long deltaT = ((new BlockDecoder().decode(BlockChain.blockChain.get(
                                           4)).getTimeStamps()[0]) - Long.parseLong(new GenesisBlockDecoder()
                                            .decode(BlockChain.blockChain.get(0)).getTimeStamp())) / 1000;
                                    MainChain.calculateTarget(deltaT, MainChain.targetHex);
                                } else if (BlockChain.blockChain.size() > 5 && BlockChain.blockChain.size() % 5 == 0) {
                                    long deltaT = ((new BlockDecoder().decode(BlockChain.blockChain.get(
                                            mc.getIndexOfBlockChain())).getTimeStamps()[0]) - (new BlockDecoder()
                                            .decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain() - 5)).getTimeStamps()[0])) / 1000;
                                    MainChain.calculateTarget(deltaT, MainChain.targetHex);
                                }
                            } else {
                                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp()
                                        + " Detected orphan block, not adding to chain!\n");
                            }
                            timer.cancel();
                            break;
                        } else {
                            System.out.println("No peers found! Check network connection!\n");
                            timer.cancel();
                            break;
                        }
                    }
                } else {
                    nonce++;
                    endTime = System.nanoTime();
                    deltaN = endTime - startTime;
                    deltaS = (deltaN / 1000000000);
                    hashRate = nonce / deltaS;
                    if (updatedIndex > indexAtStart) {
                        previousBlockHash = mc.getPreviousBlockHash();
                        timeStamps[0] = System.currentTimeMillis();
                        nonce = 0L;
                        byte[] txHashBytes = (Arrays.toString(txins) + Arrays.toString(txouts) + Arrays.toString(amounts)).getBytes();
                        merkleRoot = MainChain.getHex(SHA256.SHA256HashByteArray(txHashBytes));
                        restartMiner(updatedIndex, timeStamps, txins, txouts, txHash, merkleRoot,
                                nonce, previousBlockHash, algo, target, amounts);
                        break;
                    }
                }
            }
        } catch (InterruptedException ie) {
            WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp()
                    + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
        } catch (EncodeException ee) {
            WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp()
                    + " Encode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
        } catch (DecodeException de) {
            WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp()
                    + " Decode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
    }

    private void restartMiner(long index, long[] timeStamps, String[] txins, String[] txouts, String[] txs,
                              String merkleRoot, long nonce, String previousBlockHash, String algo, String target, double[] amounts) {
        timer.cancel();
        System.out.println("Trying to restart miner!\n");
        mine(index, timeStamps, txins, txouts, txs, merkleRoot, nonce, previousBlockHash, algo, target, difficulty, amounts);
    }

    public double calculateNetworkHashRate() {
        long totalHashes = 0;
        double deltaS = 0;
        try {
            GenesisBlock genesisBlock = new GenesisBlockDecoder().decode(BlockChain.blockChain.get(0));
            long genesisTime = Long.parseLong(genesisBlock.getTimeStamp());
            long currentTime = System.currentTimeMillis();
            for (int i = 1; i < BlockChain.blockChain.size(); i++) {
                Block decodedBlock = new BlockDecoder().decode(BlockChain.blockChain.get(i));
                long parsedNonce  = (decodedBlock.getNonce());
                totalHashes += parsedNonce;
            }
            System.out.println("Genesis time: " + genesisTime + " current time: " + currentTime + " total hashes: " + totalHashes);
            deltaS = (currentTime - genesisTime) / 1000;
            System.out.println("delta s: " + deltaS);
        } catch (DecodeException de) {
            WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp()
                    + " Decode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
        return (totalHashes / deltaS);
    }

    public String leftPad(String originalString, int length, char padChar) {
        StringBuilder sb = new StringBuilder();
        if (originalString.length() > length) {
            try {
                throw new PadLengthException("Error! Original string is longer than desired padded length!\n");
            } catch (PadLengthException e) {
                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp() + " Pad length exception occurred!\n");
            }
        } else {
            int padLength = length - originalString.length();
            for (int i = 0; i < padLength; i++) {
                sb.append(padChar);
            }
        }
        return sb.toString() + originalString;
    }

    public class PadLengthException extends Exception {
        PadLengthException(String msg) {
            System.out.println(msg);
        }
    }
}




