package com.thc.blockchain.util;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.algos.SHA512;
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
    private byte[] hash;

    public void mine(long index, long currentTimeMillis, String fromAddress, String toAddress, String[] txHash, String merkleRoot, long Nonce, String previousBlockHash, String algo, String target, float amount) {
        try {
            System.out.println("Seeing if any configured nodes are up...\n");
            EndpointManager endpointManager = new EndpointManager();
            if (endpointManager.isNodeConnected(1) || endpointManager.isNodeConnected(2)) {
                int indexAtStart = BlockChain.blockChain.size();
                MainChain mc = new MainChain();
                MainChain.targetAsBigDec = target;
                BigDecimal targetAsBigDec = new BigDecimal(new BigInteger(target, 16));
                System.out.println("difficulty says: \n" + MainChain.difficulty);
                long startTime = System.nanoTime();
                TimeUnit.SECONDS.sleep(1);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mc.readBlockChain();
                        System.out.println("\n");
                        System.out.println("Current hash rate: " + hashRate + " " + "hash/s");
                        updatedIndex = BlockChain.blockChain.size();
                    }
                }, 0, 3000);
                while (true) {
                    long deltaS;
                    long deltaN;
                    long endTime;
                    byte[] blockHeaderBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray(MainChain.getHex((
                            index + currentTimeMillis + fromAddress + toAddress + Arrays.toString(txHash) + merkleRoot
                                    + Nonce + previousBlockHash + algo + target + amount).getBytes())));
                    if (algo.contentEquals("sha256")) {
                        hash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(blockHeaderBytes));
                    } else if (algo.contentEquals("sha512")) {
                        hash = SHA512.SHA512HashByteArray(SHA512.SHA512HashByteArray(blockHeaderBytes));
                    }
                    if (MainChain.difficulty <= 1) {
                        MainChain.difficulty = 1;
                        targetAsBigDec = new BigDecimal(new BigInteger(
                                "00000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", 16));
                    }
                    if (new BigDecimal(new BigInteger(MainChain.getHex(blockHeaderBytes), 16)).subtract(targetAsBigDec).compareTo(
                            new BigDecimal(0)) <= 0) {
                        System.out.println("\n");
                        System.out.println("[" + MainChain.getHex(hash) + "]");
                        System.out.println("\n");
                        String indexToStr = Long.toString(index);
                        String timeToStr = Long.toString(currentTimeMillis);
                        String nonceToStr = Long.toString(Nonce);
                        String amountToStr = Float.toString(amount);
                        System.out.println("Adding block to chain...\n");
                        if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot,
                                Nonce, previousBlockHash, algo, MainChain.getHex(hash), target, amount)) {
                            endpointManager.connectAsClient("update");
                            Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleRoot,
                                    nonceToStr, previousBlockHash, algo, MainChain.getHex(hash), target, amountToStr);
                            String encodedBlock = new BlockEncoder().encode(block);
                            boolean verifyIndex = new Consensus().isBlockOrphan(Long.parseLong(block.getIndex()));
                            if (verifyIndex) {
                                BlockChain.blockChain.add(encodedBlock);
                                mc.writeBlockChain();
                            } else {
                                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp()
                                        + " Detected orphan block, not adding to chain!\n");
                            }
                            Session sessionForMiner = NodeManager.getSession();
                            NodeManager.pushBlock(block, sessionForMiner);
                            timer.cancel();
                            break;
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
                            target = MainChain.getHex(String.valueOf((MainChain.calculateTarget(deltaS, target))).getBytes());
                            restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, merkleRoot,
                                    Nonce, previousBlockHash, algo, target, amount);
                            break;
                        }
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




