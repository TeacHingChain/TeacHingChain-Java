package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.*;

public class MainChain {

    private long currentTimeMillis;
    private long Nonce;
    private long index;
    public static int difficulty;
    private String currentHash;
    private String blockHash;
    private String previousBlockHash;
    private String txHash;
    private String sendKeyTx;
    private String recvKeyTx;
    private float amount;
    static float balance;
    static long indexAtStart;
    private static List<String> blockChain;
    public static List<String> txPool;
    static final float nSubsidy = 50;
    static String addressKey = "";
    static String minerKey = "";
    static String sendKey = "";
    private static String recvKey = "";
    private static Socket connect;


    public MainChain() {}

    public String getCurrentHash() {
        currentHash = (String) HashArray.hashArray.get(HashArray.hashArray.size() - 3);
        System.out.println("\n");
        System.out.println(currentHash);
        return currentHash;
    }

    public String getGenesisHash() {
        blockHash = (String) HashArray.hashArray.get((HashArray.hashArray.size() - HashArray.hashArray.size()) + 8);
        System.out.println("\n");
        System.out.println("Genesis " + blockHash);
        return blockHash;
    }

    void setGenesisHash(long index, long currentTimeMillis, String sendKeyTx, String recvKeyTx, String minerKey, String txHash, long Nonce, String previousBlockHash, String blockHash, int difficulty, float amount) throws IOException {
        this.index = index;
        this.currentTimeMillis = currentTimeMillis;
        this.sendKeyTx = sendKeyTx;
        this.recvKeyTx = recvKeyTx;
        MainChain.minerKey = minerKey;
        this.txHash = txHash;
        this.Nonce = Nonce;
        this.previousBlockHash = previousBlockHash;
        this.blockHash = blockHash;
        this.difficulty = difficulty;

        String endBlockLine = "-------------END BLOCK-------------";
        String indexToStr = Long.toString(index);
        String timeToStr = Long.toString(currentTimeMillis);
        String nonceToStr = Long.toString(Nonce);
        String difficultyToStr = Integer.toString(difficulty);
        String amountToStr = Float.toString(amount);

        ChainBuilder bb = new ChainBuilder();

        boolean validateGenesis = bb.isGenesisValid(index, currentTimeMillis, sendKeyTx, recvKeyTx, minerKey, txHash, Nonce, previousBlockHash, blockHash, difficulty, amount);
        if (validateGenesis) {
            System.out.println("\n");
            System.out.println("Initializing chain...\n");
            System.out.println("\n");
            System.out.println("index says: " + index  + " " + indexToStr);
            HashArray hashArray = new HashArray();
            HashArray.hashArray.add("Index: " + indexToStr);
            HashArray.hashArray.add("Time stamp: " + timeToStr);
            HashArray.hashArray.add("Send key: " + sendKeyTx);
            HashArray.hashArray.add("Receive key: " + recvKeyTx);
            HashArray.hashArray.add("Miner key: " + minerKey);
            HashArray.hashArray.add("Tx Hash: " + txHash);
            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash(txHash));
            HashArray.hashArray.add("Nonce: " + nonceToStr);
            HashArray.hashArray.add("Previous " + previousBlockHash);
            HashArray.hashArray.add("Block hash: " + blockHash);
            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
            HashArray.hashArray.add(amountToStr);

            try {
                System.out.println("\n");
                System.out.println("Trying to serialize chain.dat...\n");
                System.out.println("\n");
                FileOutputStream fos = new FileOutputStream("chain.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(HashArray.hashArray);
                oos.close();
                fos.close();

            } catch (IOException ioe) {
                System.out.println("\n");
                System.out.println("Failed to write chain.dat!\n");
                System.out.println("\n");
            }

        } else {
            System.out.println("\n");
            System.out.println("ERROR! Genesis block not passing validation, please check values!");
            System.out.println("\n");
        }
    }

    public String getPreviousBlockHash() {
        previousBlockHash = (String) HashArray.hashArray.get(HashArray.hashArray.size() - 3);
        return previousBlockHash;
    }

    @SuppressWarnings("deprecation")
    List<String> getBlockChain() throws NullPointerException, IOException {
        try {
            System.out.println("\n");
            System.out.println("Trying to read serialized chain.dat...\n");
            FileInputStream fis = new FileInputStream("chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashArray.hashArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            for (int i = 0; i < HashArray.hashArray.size(); i++) {
                System.out.println(HashArray.hashArray.get(i));
                System.out.println("--------------------------------------------------------------------");
            }
            return blockChain;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! chain.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! chain.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }

        return blockChain;
    }

    public List<String> readBlockChain() throws NullPointerException, IOException {
        balance = 0;
        readRecvKey();
        try {
          //  System.out.println("\n");
          //  System.out.println("Trying to read serialized chain.dat...\n");
            FileInputStream fis = new FileInputStream("chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashArray.hashArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            for (int i = 0; i < HashArray.hashArray.size(); i++) {
                if (HashArray.hashArray.get(i).equals("Miner key: " + minerKey)) {
                    balance += nSubsidy;
                }
            }
            for (int j = 0; j < HashArray.hashArray.size(); j++) {
                if (HashArray.hashArray.get(j).equals("Send key: " + sendKey)) {
                    String amountToStr = String.valueOf(HashArray.hashArray.get(j + 9));
                    balance -= Float.parseFloat(amountToStr);
                }
            }
            for (int k = 0; k < HashArray.hashArray.size(); k++) {
                if (HashArray.hashArray.get(k).equals("Receive key: " + recvKey)) {
                    String amountToStr = String.valueOf(HashArray.hashArray.get(k + 8));
                    balance += Float.parseFloat(amountToStr);
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! chain.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! chain.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }

        return blockChain;
    }

    public long getIndexOfBlockChain() {
        long chainIndex = (HashArray.hashArray.size() / 12);
        System.out.println("\n");
        System.out.println("Number of blocks on chain: " + chainIndex);
        return chainIndex;
    }

    public String getBlockAtIndex(long index) {
        this.index = index;
        if (((index + 1) * 8) > HashArray.hashArray.size()) {
            System.out.println("\n");
            System.out.println("ERROR! Index out of range!\n");

        } else {
            String hashAtIndex = (String) HashArray.hashArray.get((int) (((index + 1) * 8) - 3));
            System.out.println("\n");
            System.out.println("Block hash at index " + index + ": \n" + hashAtIndex);
            return null;
        }
    return null;
    }

    public long getUnixTimestamp() {
        currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis;
    }

    public void generateAddressKey(){
        Random randKeyGen = new Random();
        BigInteger b = new BigInteger(256, randKeyGen);
        String keyToStr = b.toString(10);
        System.out.println("\n");
        System.out.println("Address key generated: \n" + b);
        addressKey = keyToStr;
        try {
            System.out.println("Trying to serialize addressKey.dat...\n");
            FileOutputStream fos = new FileOutputStream("addressKey.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(addressKey);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            System.out.println("Something went wrong while writing key to file..");
        }
    }

    public void generateRecvKey(){
        recvKey = SHA256.generateSHA256Hash(Constants.pubKey + addressKey);
        System.out.println("Receive key generated: \n" + recvKey);
        try {
            System.out.println("Trying to serialize recvKey.dat...\n");
            FileOutputStream fos = new FileOutputStream("recvKey.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(recvKey);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            System.out.println("Something went wrong while writing key to file..");

        }
    }


    public void generateMinerKey(){
        minerKey = SHA256.generateSHA256Hash(Constants.pubKey + recvKey);
        System.out.println("Receive key generated: \n" + minerKey);
        try {
            System.out.println("Trying to serialize minerKey.dat...\n");
            FileOutputStream fos = new FileOutputStream("minerKey.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(minerKey);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            System.out.println("Something went wrong while writing key to file..");

        }
    }

    public void generateSendKey() {
        sendKey = SHA256.generateSHA256Hash(addressKey + recvKey);
        System.out.println("Send key generated: \n" + sendKey);
        try {
            System.out.println("Trying to serialize sendKey.dat...\n");
            FileOutputStream fos = new FileOutputStream("sendKey.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sendKey);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            System.out.println("Something went wrong while writing key to file..");

        }
    }

    public void generateAllKeys() {
        generateAddressKey();
        generateRecvKey();
        generateMinerKey();
        generateSendKey();
    }



    public String getAddressKey() {
        try {
            System.out.println("\n");
            System.out.println("Trying to read serialized addressKey.dat...\n");
            FileInputStream fis = new FileInputStream("addressKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            addressKey = (String) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("\n");
            System.out.println("Address key found: \n" + addressKey);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! addressKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! addressKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return addressKey;
    }

    public String readAddressKey(){
        try {
            System.out.println("\n");
            System.out.println("Trying to read serialized addressKey.dat...\n");
            FileInputStream fis = new FileInputStream("addressKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            addressKey = (String) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! addressKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! addressKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return addressKey;
    }

    public String readRecvKey() {
        try {
            FileInputStream fis = new FileInputStream("recvKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            recvKey = (String) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! minerKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! minerKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return recvKey;
    }

    public String getRecvKey() {
        try {
            FileInputStream fis = new FileInputStream("recvKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            recvKey = (String) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("\n");
            System.out.println("Receive key: \n" + recvKey);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! minerKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! minerKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return recvKey;
    }

    public String getSendKey() {
        try {
            System.out.println("\n");
            System.out.println("Trying to read serialized sendKey.dat...\n");
            FileInputStream fis = new FileInputStream("sendKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            sendKey = (String) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("\n");
            System.out.println("Send key: \n" + sendKey);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! sendKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! sendKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return sendKey;
    }

    public String readSendKey() {
        try {
            System.out.println("\n");
            System.out.println("Trying to read serialized sendKey.dat...\n");
            FileInputStream fis = new FileInputStream("sendKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            sendKey = (String) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! sendKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! sendKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return sendKey;
    }

    public String getMinerKey() {
        try {
           // System.out.println("\n");
            //System.out.println("Trying to read serialized minerKey.dat...\n");
            FileInputStream fis = new FileInputStream("minerKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            minerKey = (String) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! minerKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! minerKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return minerKey;
    }

    public String readMinerKey() {
        try {
            // System.out.println("\n");
            // System.out.println("Trying to read serialized minerKey.dat...\n");
            FileInputStream fis = new FileInputStream("minerKey.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            minerKey = (String) ois.readObject();
            ois.close();
            fis.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("ERROR! minerKey.dat not found, please make sure wallet and chain are in sync!\n");
        } catch (NullPointerException npe) {
            System.out.println("ERROR! minerKey.dat not found or of size zero, please make sure wallet and chain are in sync!\n");
        }
        return minerKey;
    }

    public void sendTx(String sendKeyTx, String recvKeyTx, float amount) throws IOException {
        ChainBuilder cb = new ChainBuilder();
        this.sendKeyTx = sendKeyTx;
        this.recvKeyTx = recvKeyTx;
        this.amount = amount;
        long blockIndex = (HashArray.hashArray.size() / 12);
        cb.writeTxPool(blockIndex, sendKeyTx, recvKeyTx, amount);
    }

    public void checkForChainUpdates(){
        ChainBuilder cb = new ChainBuilder();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

            }
        }, 10000, 10000);
    }

    public void checkForTxPoolUpdates(){
        ChainBuilder cb = new ChainBuilder();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {


            }
        }, 8000, 3000);
    }


    public long getDifficulty(){
        System.out.println("\n");
        System.out.println("Difficulty: \n" + difficulty);
        return difficulty;
    }
}

/*
TODO: Add send/receive parameter to chain
TODO: Add algorithm ID parameter to chain
 */

