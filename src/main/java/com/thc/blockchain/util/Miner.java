package com.thc.blockchain.util;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.gui.MiningPane;
import com.thc.blockchain.wallet.ChainBuilder;
import com.thc.blockchain.wallet.HashArray;
import com.thc.blockchain.wallet.Launcher;
import com.thc.blockchain.wallet.MainChain;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Miner {

    private long currentTimeMillis;
    private String txHash;
    private static long deltaS;
    private static long deltaN;
    private static long startTime;
    private static long endTime;
    public static long hashRate;
    public static boolean iterator;


    public boolean mine(long index, long currentTimeMillis, String sendKey, String recvKey, String minerKey, String txHash, long Nonce, String previousBlockHash, String algo, int difficulty, float amount) throws InterruptedException {
        MainChain mc = new MainChain();
        ChainBuilder cb = new ChainBuilder();
        long start = System.currentTimeMillis();
        this.txHash = txHash;
        this.currentTimeMillis = currentTimeMillis;
        MainChain.difficulty = difficulty;
        if (algo.contentEquals("sha256")) {
            System.out.println("difficulty says: \n" + MainChain.difficulty);
            startTime = System.nanoTime();
            TimeUnit.SECONDS.sleep(1);
            mc.checkForChainUpdates();
            mc.checkForTxPoolUpdates();
            MiningPane mp = new MiningPane();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("\n");
                    System.out.println("Current hash rate: " + hashRate + " " + "hash/s");

                }
            }, 0, 3000);
            iterator = true;
            while (iterator) {
                String blockHeader = (index + currentTimeMillis + sendKey + recvKey + minerKey + txHash + Nonce + previousBlockHash + algo + difficulty + amount);
                String hash = SHA256.generateSHA256Hash(blockHeader);
                long stop;
                long deltaM;
                if (difficulty <= 1) {
                    difficulty = 1;

                    if (!hash.startsWith("0")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            timer.cancel();
                            iterator = false;

                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }
                } else if (difficulty == 2) {
                    if (!hash.startsWith("00")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);

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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            timer.cancel();
                            iterator = false;

                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 3) {
                    if (!hash.startsWith("000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);
           ;


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            timer.cancel();
                            iterator = false;
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 4) {
                    if (!hash.startsWith("0000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            Launcher.numBlocksMined++;

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 5) {


                    if (!hash.startsWith("00000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            Launcher.numBlocksMined++;


                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }


                    }
                } else if (difficulty == 6) {
                    if (!hash.startsWith("000000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);

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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            Launcher.numBlocksMined++;

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 7) {

                    if (!hash.startsWith("0000000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            Launcher.numBlocksMined++;

                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 8) {
                    if (!hash.startsWith("00000000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 9) {

                    if (!hash.startsWith("000000000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }

                } else if (difficulty == 10) {
                    if (!hash.startsWith("000000000000")) {
                        Nonce++;
                        endTime = System.nanoTime();
                        deltaN = endTime - startTime;
                        deltaS = (deltaN / 1000000000);
                        hashRate = (Nonce / deltaS);


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
                        boolean validateHash = cb.isBlockHashValid(index, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, hash, difficulty, amount);
                        if (validateHash) {
                            HashArray.hashArray.add("Index: " + indexToStr);
                            HashArray.hashArray.add("Time stamp: " + timeToStr);
                            HashArray.hashArray.add("Send key: " + sendKey);
                            HashArray.hashArray.add("Receive key: " + recvKey);
                            HashArray.hashArray.add("Miner key: " + minerKey);
                            HashArray.hashArray.add("Tx Hash: " + txHash);
                            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
                            HashArray.hashArray.add("Nonce: " + nonceToStr);
                            HashArray.hashArray.add("Previous " + previousBlockHash);
                            HashArray.hashArray.add("Algorithm: " + algo);
                            HashArray.hashArray.add("Block hash: " + hash);
                            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
                            HashArray.hashArray.add(amountToStr);
                            try {
                                System.out.println("Trying to serialize chain.dat...\n");
                                FileOutputStream fos = new FileOutputStream("chain.dat");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(HashArray.hashArray);
                                oos.close();
                                fos.close();
                                mc.readBlockChain();

                                System.out.println("\n");
                                stop = System.currentTimeMillis();
                                deltaM = stop - start;
                                System.out.println("DeltaM: \n" + deltaM);
                                if (deltaM < 15000) {
                                    MainChain.difficulty++;
                                } else if (deltaM > 15000) {
                                    MainChain.difficulty--;
                                }

                            } catch (IOException ioe) {
                                System.out.println("Something went wrong while writing cache..");
                            }
                            iterator = false;
                            timer.cancel();
                        } else {
                            System.out.println("Error adding block to chain! Hash is not valid!\n");
                        }
                    }
                }
            }
        } else {
            iterator = false;
            System.out.println("Coming soon!\n");
        }
    return true;
    }

    public void paint(Graphics g){
        g.drawString(String.valueOf(hashRate), 10, 10);
    }
}


