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
import org.apache.commons.lang.StringUtils;
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

    public void mine(long index, long currentTimeMillis, String fromAddress, String toAddress, String[] txHash, String merkleRoot,
                     long Nonce, String previousBlockHash, String algo, String target, double difficulty, float amount) {
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
                    hashedBlockHeaderBytes = StringUtils.leftPad(hashedBlockHeaderBytes, 64, "0");
                }
                System.out.println("Current hash: " + hashedBlockHeaderBytes);
                System.out.println("Big decimal value of hash: " + new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)));
                System.out.println("Current target: " + targetAsBigDec);
                if (MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray()).length() < 64) {
                    String tempTarget = MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray());
                    tempTarget = StringUtils.leftPad(tempTarget, 64, "0");
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
                        + currentTimeMillis + fromAddress + toAddress + Arrays.toString(txHash) + merkleRoot + Nonce
                        + previousBlockHash + algo + target + difficulty + amount).getBytes()))));
                if (new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)).compareTo(targetAsBigDec) <= 0) {
                    System.out.println("\n");
                    System.out.println("[" + hashedBlockHeaderBytes + "]");
                    System.out.println("Current value - target value: " + new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16))
                            .subtract(targetAsBigDec));
                    System.out.println("\n");
                    String indexToStr = Long.toString(index);
                    String timeToStr = Long.toString(currentTimeMillis);
                    String nonceToStr = Long.toString(Nonce);
                    String amountToStr = Float.toString(amount);
                    System.out.println("Adding block to chain...\n");
                    if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot,
                            Nonce, previousBlockHash, algo, hashedBlockHeaderBytes, target, difficulty, amount)) {
                        endpointManager.connectAsClient("hello");
                        if (endpointManager.getIsNode1Connected() || endpointManager.getIsNode2Connected()) {
                            System.out.println("Is node 1 connected: " + endpointManager.getIsNode1Connected());
                            System.out.println("Is node 2 connected: " + endpointManager.getIsNode2Connected());
                            Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleRoot,
                                    nonceToStr, previousBlockHash, algo, hashedBlockHeaderBytes,
                                    target, String.valueOf(difficulty), amountToStr);
                            String encodedBlock = new BlockEncoder().encode(block);
                            Session sessionForMiner = NodeManager.getSession();
                            NodeManager.pushBlock(block, sessionForMiner);
                            if (new Consensus().isBlockOrphan(Long.parseLong(block.getIndex()))) {
                                BlockChain.blockChain.add(encodedBlock);
                                mc.writeBlockChain();
                                System.out.println("Size: " + BlockChain.blockChain.size());
                                if (BlockChain.blockChain.size() == 5) {
                                    long deltaT = (Long.parseLong(new BlockDecoder().decode(BlockChain.blockChain.get(
                                           4)).getTimeStamp()) - Long.parseLong(new GenesisBlockDecoder()
                                            .decode(BlockChain.blockChain.get(0)).getTimeStamp())) / 1000;
                                    String previousTarget = new BlockDecoder().decode(BlockChain.blockChain.get(
                                            4)).getTarget();
                                    MainChain.calculateTarget(deltaT, previousTarget);
                                } else if (BlockChain.blockChain.size() > 5 && BlockChain.blockChain.size() % 5 == 0) {
                                    long deltaT = (Long.parseLong(new BlockDecoder().decode(BlockChain.blockChain.get(
                                            mc.getIndexOfBlockChain())).getTimeStamp()) - Long.parseLong(new BlockDecoder()
                                            .decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain() - 5)).getTimeStamp())) / 1000;
                                    String previousTarget = new BlockDecoder().decode(BlockChain.blockChain.get(
                                            mc.getIndexOfBlockChain() - 5)).getTarget();
                                   MainChain.calculateTarget(deltaT, previousTarget);
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
                    Nonce++;
                    endTime = System.nanoTime();
                    deltaN = endTime - startTime;
                    deltaS = (deltaN / 1000000000);
                    hashRate = Nonce / deltaS;
                    if (updatedIndex > indexAtStart) {
                        previousBlockHash = mc.getPreviousBlockHash();
                        currentTimeMillis = System.currentTimeMillis();
                        Nonce = 0L;
                        byte[] txHashBytes = (fromAddress + toAddress + amount).getBytes();
                        merkleRoot = MainChain.getHex(SHA256.SHA256HashByteArray(txHashBytes));
                        restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot,
                                Nonce, previousBlockHash, algo, target, amount);
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

    private void restartMiner(long index, long currentTimeMillis, String fromAddress, String toAddress, String[] txHash,
        String merkleRoot, long Nonce, String previousBlockHash, String algo, String target, float amount) {
        timer.cancel();
        System.out.println("Trying to restart miner!\n");
        mine(index, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot, Nonce, previousBlockHash, algo, target, difficulty, amount);
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
                long parsedNonce  = Long.parseLong(decodedBlock.getNonce());
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
}




