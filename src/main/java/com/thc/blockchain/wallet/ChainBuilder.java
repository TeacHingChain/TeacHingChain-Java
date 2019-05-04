package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ChainBuilder extends MainChain {

    private long currentTimeMillis;
    private String pszTimeStamp;
    private String txHash;
    private static long deltaS;
    private static long deltaN;
    private static long startTime;
    private static long endTime;
    private static long hashRate;

    public ChainBuilder() {
    }

    // Block builder only assembles a block, it doesn't add it to the chain (can generate genesis blocks)
    public ChainBuilder(long index, long currentTimeMillis, String sendKey, String recvKey, String minerKey, String txHash, long Nonce, String previousBlockHash, int difficulty, float amount) throws InterruptedException {
        super();
        this.currentTimeMillis = System.currentTimeMillis();
        MainChain.difficulty = difficulty;
        boolean iterator = true;
        String blockHeader = (index + currentTimeMillis + sendKey + recvKey + minerKey + txHash + Nonce + previousBlockHash + difficulty + amount);
        String hash;
        startTime = System.nanoTime();
        TimeUnit.SECONDS.sleep(2);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\n");
                System.out.println("Current hash rate: " + hashRate + " " + "hash/s");
            }
        }, 0, 3000);
        while (iterator) {

            blockHeader = (index + currentTimeMillis + sendKey + recvKey + minerKey + txHash + Nonce + previousBlockHash + difficulty + amount);
            hash = SHA256.generateSHA256Hash(blockHeader);
            if (difficulty == 1) {

                if (!hash.startsWith("0")) {
                    Nonce++;
                    endTime = System.nanoTime();
                    deltaN = endTime - startTime;
                    deltaS = (deltaN / 1000000000);
                    hashRate = (Nonce / deltaS);

                } else {
                    System.out.println("\n");
                    System.out.println("Hash found! \n");
                    System.out.println("\n");
                    System.out.println("--------------------------BLOCK DETAILS--------------------------");
                    System.out.println("\n");
                    System.out.println("Mined block hash: \n" + hash);
                    System.out.println("\n");
                    System.out.println("Index: \n" + index);
                    System.out.println("\n");
                    System.out.println("Unix time stamp: \n" + currentTimeMillis);
                    System.out.println("\n");
                    System.out.println("Data: \n" + sendKey);
                    System.out.println("\n");
                    System.out.println("Previous " + previousBlockHash);
                    System.out.println("\n");
                    System.out.println("Nonce: \n" + Nonce);
                    System.out.println("\n");
                    System.out.println("Difficulty: \n" + difficulty);
                    System.out.println("\n");
                    timer.cancel();
                    iterator = false;
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
                    System.out.println("Hash found! \n");
                    System.out.println("\n");
                    System.out.println("--------------------------BLOCK DETAILS--------------------------");
                    System.out.println("\n");
                    System.out.println("Mined block hash: \n" + hash);
                    System.out.println("\n");
                    System.out.println("Index: \n" + index);
                    System.out.println("\n");
                    System.out.println("Unix time stamp: \n" + currentTimeMillis);
                    System.out.println("\n");
                    System.out.println("Data: \n" + sendKey);
                    System.out.println("\n");
                    System.out.println("Previous " + previousBlockHash);
                    System.out.println("\n");
                    System.out.println("Nonce: \n" + Nonce);
                    System.out.println("\n");
                    System.out.println("Difficulty: \n" + difficulty);
                    System.out.println("\n");
                    timer.cancel();
                    iterator = false;
                }

            } else if (difficulty == 3) {

                if (!hash.startsWith("000")) {
                    Nonce++;
                    endTime = System.nanoTime();
                    deltaN = endTime - startTime;
                    deltaS = (deltaN / 1000000000);
                    hashRate = (Nonce / deltaS);

                } else {
                    System.out.println("\n");
                    System.out.println("Hash found! \n");
                    System.out.println("\n");
                    System.out.println("--------------------------BLOCK DETAILS--------------------------");
                    System.out.println("\n");
                    System.out.println("Mined block hash: \n" + hash);
                    System.out.println("\n");
                    System.out.println("Index: \n" + index);
                    System.out.println("\n");
                    System.out.println("Unix time stamp: \n" + currentTimeMillis);
                    System.out.println("\n");
                    System.out.println("Data: \n" + sendKey);
                    System.out.println("\n");
                    System.out.println("Previous " + previousBlockHash);
                    System.out.println("\n");
                    System.out.println("Nonce: \n" + Nonce);
                    System.out.println("\n");
                    System.out.println("Difficulty: \n" + difficulty);
                    System.out.println("\n");
                    timer.cancel();
                    iterator = false;
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
                    System.out.println("Hash found! \n");
                    System.out.println("\n");
                    System.out.println("--------------------------BLOCK DETAILS--------------------------");
                    System.out.println("\n");
                    System.out.println("Mined block hash: \n" + hash);
                    System.out.println("\n");
                    System.out.println("Index: \n" + index);
                    System.out.println("\n");
                    System.out.println("Unix time stamp: \n" + currentTimeMillis);
                    System.out.println("\n");
                    System.out.println("Data: \n" + sendKey);
                    System.out.println("\n");
                    System.out.println("Previous " + previousBlockHash);
                    System.out.println("\n");
                    System.out.println("Nonce: \n" + Nonce);
                    System.out.println("\n");
                    System.out.println("Difficulty: \n" + difficulty);
                    System.out.println("\n");
                    timer.cancel();
                    iterator = false;
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
                    System.out.println("Hash found! \n");
                    System.out.println("\n");
                    System.out.println("--------------------------BLOCK DETAILS--------------------------");
                    System.out.println("\n");
                    System.out.println("Mined block hash: \n" + hash);
                    System.out.println("\n");
                    System.out.println("Index: \n" + index);
                    System.out.println("\n");
                    System.out.println("Unix time stamp: \n" + currentTimeMillis);
                    System.out.println("\n");
                    System.out.println("Data: \n" + sendKey);
                    System.out.println("\n");
                    System.out.println("Previous " + previousBlockHash);
                    System.out.println("\n");
                    System.out.println("Nonce: \n" + Nonce);
                    System.out.println("\n");
                    System.out.println("Difficulty: \n" + difficulty);
                    System.out.println("\n");
                    timer.cancel();
                    iterator = false;
                }
            }
        }
    }

    boolean isGenesisValid(long index, long genesisTimeStamp, String pszTimeStamp, String recvKey, String minerKey, String txHash, long Nonce, String previousBlockHash, String algo,  String genesisHash, int difficulty, float amount) {
        MainChain mc = new MainChain();
        String checkHash = SHA256.generateSHA256Hash(index + genesisTimeStamp + pszTimeStamp + recvKey + minerKey + txHash + Nonce + previousBlockHash + algo + difficulty + amount);
        System.out.println("Index: \n" + index);
        System.out.println("Timestamp: \n" + genesisTimeStamp);
        System.out.println("pszTimeStamp: \n" + pszTimeStamp);
        System.out.println("Actual genesis hash: \n" + genesisHash);
        System.out.println("Hash: \n" + checkHash);
        if (!checkHash.contentEquals(genesisHash)) {
            System.out.println("\n");
            System.out.println("Hash is invalid!\n");
            System.out.println("\n");
            return false;

        } else {
            System.out.println("\n");
            System.out.println("Hash is valid!\n");
            System.out.println("\n");
            return true;
        }
    }

    public boolean isBlockHashValid(long index, long currentTimeMillis, String sendKey, String recvKey, String minerKey, String txHash, long Nonce, String previousBlockHash, String algo, String currentHash, int difficulty, float amount) {

        this.currentTimeMillis = currentTimeMillis;
        MainChain.difficulty = difficulty;
        if (algo.contentEquals("sha256")) {
            txHash = SHA256.generateSHA256Hash(index + sendKey + recvKey);

            String checkHash = SHA256.generateSHA256Hash(index + currentTimeMillis + sendKey + recvKey + minerKey + txHash + Nonce + previousBlockHash + algo + difficulty + amount);
            if (!checkHash.contentEquals(currentHash)) {
                System.out.println("\n");
                System.out.println("Hash is invalid!\n");
                System.out.println("\n");
                return false;
            } else {
                System.out.println("\n");
                System.out.println("Hash is valid!\n");
                System.out.println("\n");
            }
            return true;

        }
        return true;
    }



    void writeTxPool(long blockIndex, String sendKey, String recvKey, float amount) {
        String txHash = SHA256.generateSHA256Hash(blockIndex + sendKey + recvKey);
        File tempFile = new File("tx-pool.dat");
        if (!tempFile.exists()) {
            TxPoolArray txpool = new TxPoolArray();
            String amountToStr = Float.toString(amount);
            TxPoolArray.TxPool.add(sendKey);
            TxPoolArray.TxPool.add(recvKey);
            TxPoolArray.TxPool.add(amountToStr);
            TxPoolArray.TxPool.add(txHash);
            TxPoolArray.TxPool.add("------------------------------------------------------------------------------------");
        } else {
            String amountToStr = Float.toString(amount);
            TxPoolArray.TxPool.add(sendKey);
            TxPoolArray.TxPool.add(recvKey);
            TxPoolArray.TxPool.add(amountToStr);
            TxPoolArray.TxPool.add(txHash);
            TxPoolArray.TxPool.add("------------------------------------------------------------------------------------");
        }
        try {
            System.out.println("\n");
            System.out.println("Writing to tx pool...\n");
            System.out.println("\n");
            FileOutputStream fos = new FileOutputStream("tx-pool.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TxPoolArray.TxPool);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            System.out.println("\n");
            System.out.println("Failed to write tx-pool.dat!\n");
            System.out.println("\n");
        }
    }

    public void overwriteTxPool() {
        try {
            FileOutputStream fos = new FileOutputStream("tx-pool.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TxPoolArray.TxPool);
            oos.close();
            fos.close();
            readTxPool();

        } catch (IOException ioe) {
            System.out.println("\n");
            System.out.println("Failed to write tx-pool.dat!\n");
            System.out.println("\n");
        }
    }


    public void readTxPool() {
        try {
            FileInputStream fis = new FileInputStream("tx-pool.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TxPoolArray.TxPool = (ArrayList) ois.readObject();
            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            TxPoolArray txpool = new TxPoolArray();
            overwriteTxPool();
        } catch (IOException e) {
            System.out.println("Couldn't read tx-pool!\n");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found!\n");
        }
    }


    void getTxPool() {
        if (!TxPoolArray.TxPool.isEmpty()) {
            System.out.println("tx pool: \n");
            for (int i = 0; i < TxPoolArray.TxPool.size(); i++) {
                System.out.println(TxPoolArray.TxPool.get(i));
            }
        } else {
            System.out.println("tx pool: \n" + "[]");
        }
    }
}

/*
TODO: Remove timers in favor of websockets integration
TODO: Fix miner method to allow for stale work detection
TODO: Devise new difficulty adjustment with much higher resolution
TODO: Make genesis hardcoded, move buildGenesisBlock to util package
*/