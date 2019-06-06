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

public class Miner {

    private static long hashRate;
    private static Timer timer;
    private static int updatedIndex;
    private static String hashedBlockHeaderBytes;

    public void mine(long index, long currentTimeMillis, String fromAddress, String toAddress, String[] txHash, String merkleRoot, long Nonce, String previousBlockHash, String algo, String target, float amount) {
        try {
            new NetworkConfigFields();
            EndpointManager endpointManager = new EndpointManager();
            int indexAtStart = BlockChain.blockChain.size();
            MainChain mc = new MainChain();
            MainChain.targetHex = target;
            BigDecimal targetAsBigDec = new BigDecimal(new BigInteger(target, 16));
            System.out.println("difficulty says: \n" + MainChain.difficulty);
            long startTime = System.nanoTime();
            TimeUnit.MILLISECONDS.sleep(1000);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                mc.readBlockChain();
                System.out.println("\n");
                System.out.println("Current hash rate: " + hashRate + " " + "hash/s");
                System.out.println("Current hash: " + hashedBlockHeaderBytes);
                System.out.println("Big decimal value of hash: " + new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)));
                System.out.println("Current target: " + targetAsBigDec);
                System.out.println("Current target hex: " + MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray()));
                updatedIndex = BlockChain.blockChain.size();
                }
            }, 1000, 3000);
            while (true) {
                long deltaS = 0;
                long deltaN;
                long endTime;
                hashedBlockHeaderBytes = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(MainChain.swapEndianness((index + currentTimeMillis + fromAddress + toAddress + Arrays.toString(txHash) + merkleRoot + Nonce + previousBlockHash + algo + target + amount).getBytes()))));
                if (new BigDecimal(new BigInteger(hashedBlockHeaderBytes, 16)).compareTo(targetAsBigDec) <= 0) {
                    System.out.println("\n");
                    System.out.println("[" + hashedBlockHeaderBytes + "]");
                    System.out.println("\n");
                    String indexToStr = Long.toString(index);
                    String timeToStr = Long.toString(currentTimeMillis);
                    String nonceToStr = Long.toString(Nonce);
                    String amountToStr = Float.toString(amount);
                    System.out.println("Adding block to chain...\n");
                    if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot,
                            Nonce, previousBlockHash, algo, hashedBlockHeaderBytes, target, amount)) {
                        endpointManager.connectAsClient("hello");
                        if (endpointManager.getIsNode1Connected() || endpointManager.getIsNode2Connected()) {
                            System.out.println("Is node 1 connected: " + endpointManager.getIsNode1Connected());
                            System.out.println("Is node 2 connected: " + endpointManager.getIsNode2Connected());
                            endpointManager.connectAsClient("update");
                            Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleRoot,
                                    nonceToStr, previousBlockHash, algo, MainChain.getHex(targetAsBigDec.toBigInteger().toByteArray()),
                                    target, amountToStr);
                            String encodedBlock = new BlockEncoder().encode(block);
                            boolean verifyIndex = new Consensus().isBlockOrphan(Long.parseLong(block.getIndex()));
                            if (verifyIndex) {
                                BlockChain.blockChain.add(encodedBlock);
                                mc.writeBlockChain();
                                if (BlockChain.blockChain.size() <= 2) {
                                    GenesisBlock gb = new GenesisBlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain()));
                                    long deltaT = (System.currentTimeMillis() / 1000) - ((Long.parseLong(gb.getTimeStamp()) / 1000));
                                    String previousTarget = gb.getTarget();
                                    MainChain.targetHex = MainChain.getHex(MainChain.calculateTarget(deltaT, previousTarget).toBigInteger().toByteArray());
                                } else {
                                    Block mrb = new BlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain()));
                                    Block bbl = new BlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain() - 1));
                                    long deltaT = (Long.parseLong(mrb.getTimeStamp()) / 1000) - (Long.parseLong(bbl.getTimeStamp()) / 1000);
                                    String previousTarget = mrb.getTarget();
                                    MainChain.targetHex = MainChain.getHex(MainChain.calculateTarget(deltaT, previousTarget).toBigInteger().toByteArray());
                                }
                            } else {
                                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp()
                                        + " Detected orphan block, not adding to chain!\n");
                            }
                            Session sessionForMiner = NodeManager.getSession();
                            System.out.println("deltaS: " + deltaS);
                            System.out.println("New target: " + MainChain.calculateTarget(deltaS / 1000000000, target));
                            NodeManager.pushBlock(block, sessionForMiner);
                            timer.cancel();
                            break;
                        }
                    }
                } else {
                    Nonce++;
                    endTime = System.nanoTime();
                    deltaN = endTime - startTime;
                    deltaS = (deltaN / 1000000000);
                    hashRate = (Nonce / deltaS);
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
        mine(index, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot, Nonce, previousBlockHash, algo, target, amount);
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
            System.out.println("Genesis time nano: " + genesisTime + " current time nano: " + currentTime + " total hashes: " + totalHashes);
            deltaS = (currentTime - genesisTime) / 1000;
            System.out.println("delta s: " + deltaS);
        } catch (DecodeException de) {
            WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp()
                    + " Decode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
        return (totalHashes / deltaS);
    }
}




