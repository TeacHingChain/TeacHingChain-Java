package com.thc.blockchain.util;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.algos.SHA512;
import com.thc.blockchain.algos.Scrypt;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.Session;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Miner {

    public static long index;
    public static float amount;
    private static long hashRate;
    private static Timer timer;
    private static int updatedIndex;
    private String hash;
    private String merkleHash;

    public void mine(long index, long currentTimeMillis, String fromAddress, String toAddress, String txHash, long Nonce, String previousBlockHash, String algo, int difficulty, float amount) {
        try {
            System.out.println("Seeing if any configured nodes are up...\n");
            EndpointManager endpointManager = new EndpointManager();
            if (endpointManager.isNodeConnected(1) || endpointManager.isNodeConnected(2)) {
                int indexAtStart = BlockChain.blockChain.size();
                MainChain mc = new MainChain();
                MainChain.difficulty = difficulty;
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
                    String blockHeader = (index + currentTimeMillis + fromAddress + toAddress + txHash + Nonce + previousBlockHash + algo + difficulty + amount);
                    if (algo.contentEquals("sha256")) {
                        hash = SHA256.generateSHA256Hash(blockHeader);
                        merkleHash = SHA256.generateSHA256Hash(txHash);
                    } else if (algo.contentEquals("sha512")) {
                        hash = SHA512.generateSHA512Hash(blockHeader);
                        merkleHash = SHA512.generateSHA512Hash(txHash);
                    } else if (algo.contentEquals("scrypt")) {
                        hash = Scrypt.generateScryptHash(blockHeader);
                        merkleHash = Scrypt.generateScryptHash(txHash);
                    }
                    long deltaS;
                    long deltaN;
                    long endTime;
                    if (difficulty <= 1) {
                        difficulty = 1;
                        if (!hash.startsWith("0")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                WalletLogger.logEvent("info", new Date() + "A block was mined! See details below:");
                                WalletLogger.logEvent("info", "Hash: " + hash + " New best height: " + index);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 2) {
                        if (!hash.startsWith("00")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 3) {
                        if (!hash.startsWith("000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 4) {
                        if (!hash.startsWith("0000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 5) {
                        if (!hash.startsWith("00000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 6) {
                        if (!hash.startsWith("000000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 7) {
                        if (!hash.startsWith("0000000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 8) {
                        if (!hash.startsWith("00000000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 9) {
                        if (!hash.startsWith("000000000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    } else if (difficulty == 10) {
                        if (!hash.startsWith("000000000000")) {
                            Nonce++;
                            endTime = System.nanoTime();
                            deltaN = endTime - startTime;
                            deltaS = (deltaN / 1000000000);
                            hashRate = (Nonce / deltaS);
                            if (updatedIndex > indexAtStart) {
                                previousBlockHash = mc.getPreviousBlockHash();
                                currentTimeMillis = System.currentTimeMillis();
                                Nonce = 0L;
                                txHash = SHA256.generateSHA256Hash(updatedIndex + fromAddress + toAddress);
                                difficulty = mc.calculateDifficulty();
                                restartMiner(updatedIndex, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
                                break;
                            }
                        } else {
                            System.out.println("\n");
                            System.out.println("[" + hash + "]");
                            System.out.println("\n");
                            String indexToStr = Long.toString(index);
                            String timeToStr = Long.toString(currentTimeMillis);
                            String nonceToStr = Long.toString(Nonce);
                            String difficultyToStr = Integer.toString(difficulty);
                            String amountToStr = Float.toString(amount);
                            System.out.println("Adding block to chain...\n");
                            if (MainChain.isBlockHashValid(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount)) {
                                endpointManager.connectAsClient("update");
                                Block block = new Block(indexToStr, timeToStr, fromAddress, toAddress, txHash, merkleHash, nonceToStr, previousBlockHash, algo, hash, difficultyToStr, amountToStr);
                                Session sessionForMiner = NodeManager.getSession();
                                NodeManager.pushBlock(block, sessionForMiner);
                                timer.cancel();
                                break;
                            } else {
                                System.out.println("Error adding block to chain! Hash is not valid!\n");
                                timer.cancel();
                                break;
                            }
                        }
                    }
                }
            } else {
                System.out.println("ERROR! You aren't connected to any node, therefore the network doesn't know if you're chain is in sync! Check your network connection, or try another node");
            }
        } catch (InterruptedException ie) {
            WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
        }
    }

    private void restartMiner(long index, long currentTimeMillis, String fromAddress, String toAddress, String txHash, long Nonce, String previousBlockHash, String algo, int difficulty, float amount) {
        timer.cancel();
        System.out.println("Trying to restart miner!\n");
        mine(index, currentTimeMillis, fromAddress, toAddress, txHash, Nonce, previousBlockHash, algo, difficulty, amount);
    }
}




