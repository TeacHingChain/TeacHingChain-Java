package com.thc.blockchain.wallet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.algos.SHA512;
import com.thc.blockchain.algos.Scrypt;
import com.thc.blockchain.util.WalletLogger;

import java.io.*;
import java.util.ArrayList;

import static com.thc.blockchain.network.Constants.baseDir;

public class ChainBuilder extends MainChain {

    private static String checkHash;

    public ChainBuilder() {}

    public boolean isBlockHashValid(long index, long currentTimeMillis, String fromAddress, String toAddress, String txHash, long Nonce, String previousBlockHash, String algo, String currentHash, int difficulty, float amount) {
        MainChain.difficulty = difficulty;
        String blockHeader = (index + currentTimeMillis + fromAddress + toAddress + txHash + Nonce + previousBlockHash + algo + difficulty + amount);
        if (algo.contentEquals("sha256")) {
            checkHash = SHA256.generateSHA256Hash(blockHeader);
        } else if (algo.contentEquals("sha512")) {
            checkHash = SHA512.generateSHA512Hash(blockHeader);
        } else if (algo.contentEquals("scrypt")) {
            checkHash = Scrypt.generateScryptHash(blockHeader);
        }
        return (checkHash.contentEquals(currentHash));
    }

    public long getMostRecentIndex() {
        String block = BlockChain.blockChain.get(BlockChain.blockChain.size() - 1).toString();
        JsonElement checkIndex = new JsonParser().parse(block);
        JsonObject checkIndexObj = checkIndex.getAsJsonObject();
        JsonElement parseIndex = checkIndexObj.get("index");
        return parseIndex.getAsLong();
    }

    void writeTxPool(long blockIndex, String sendKey, String recvKey, float amount) {
        String txHash = SHA256.generateSHA256Hash(blockIndex + sendKey + recvKey);
        File tempFile = new File(baseDir + "/tx-pool.dat");
        if (!tempFile.exists()) {
            TxPoolArray txpool = new TxPoolArray();
            String amountToStr = Float.toString(amount);
            TxPoolArray.TxPool.add(sendKey);
            TxPoolArray.TxPool.add(recvKey);
            TxPoolArray.TxPool.add(amountToStr);
            TxPoolArray.TxPool.add(txHash);
        } else {
            String amountToStr = Float.toString(amount);
            TxPoolArray.TxPool.add(sendKey);
            TxPoolArray.TxPool.add(recvKey);
            TxPoolArray.TxPool.add(amountToStr);
            TxPoolArray.TxPool.add(txHash);
        }
        try {
            System.out.println("\n");
            System.out.println("Writing to tx-pool...\n");
            System.out.println("\n");
            FileOutputStream fos = new FileOutputStream(baseDir + "/tx-pool.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TxPoolArray.TxPool);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to tx-pool! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public void overwriteTxPool() {
        try {
            FileOutputStream fos = new FileOutputStream(baseDir + "/tx-pool.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TxPoolArray.TxPool);
            oos.close();
            fos.close();
            readTxPool();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while overwriting tx-pool! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public void readTxPool() {
        try {
            FileInputStream fis = new FileInputStream(baseDir + "/tx-pool.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TxPoolArray.TxPool = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            TxPoolArray txpool = new TxPoolArray();
            overwriteTxPool();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while reading tx-pool! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while reading tx-pool! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        }
    }

    public void getTxPool() {
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
