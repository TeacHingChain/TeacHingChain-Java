package com.thc.blockchain.wallet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.algos.SHA512;
import com.thc.blockchain.algos.Scrypt;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.util.ConfigParser;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.util.addresses.Base58;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static com.thc.blockchain.network.Constants.baseDir;

public class MainChain {

    public static int difficulty;
    public static float balance;
    public static final float nSubsidy = 50;
    private static String address;
    private static String privKey;
    private static String checkHash;


    private ConfigParser config = new ConfigParser();

    public MainChain() {}

    public static boolean isBlockHashValid(long index, long currentTimeMillis, String fromAddress, String toAddress, String txHash, long Nonce, String previousBlockHash, String algo, String currentHash, int difficulty, float amount) {
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

    private void writeTxPool(long blockIndex, String sendKey, String recvKey, float amount) {
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

    String getBestHash() {
        String bestBlock = BlockChain.blockChain.get(BlockChain.blockChain.size() - 1).toString();
        JsonElement jsonElement = new JsonParser().parse(bestBlock);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement best_hash = jsonObject.get("block hash");
        return best_hash.getAsString();
    }

    String getGenesisHash() {
        String genesisBlock = BlockChain.blockChain.get(BlockChain.blockChain.size() - BlockChain.blockChain.size()).toString();
        JsonElement jsonElement = new JsonParser().parse(genesisBlock);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement genesis_hash = jsonObject.get("block hash");
        return genesis_hash.getAsString();
    }

    public String getPreviousBlockHash() {
        readBlockChain();
        String previousBlock = BlockChain.blockChain.get(BlockChain.blockChain.size() - 1).toString();
        JsonElement jsonElement = new JsonParser().parse(previousBlock);
        JsonObject blockJSONObject = jsonElement.getAsJsonObject();
        JsonElement je = blockJSONObject.get("block hash");
        return je.getAsString();
    }

    void getBlockChain() {
        try {
            FileInputStream fis = new FileInputStream(baseDir + "/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BlockChain.blockChain = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            for (Object o : BlockChain.blockChain) {
                System.out.println(o.toString());
            }
        } catch (ClassNotFoundException e) {
            WalletLogger.logException(e, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(e));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp() + " Null pointer exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(npe));
        }
    }

    public void readBlockChain() {
        config.readConfigFile();
        balance = 0;
        try {
            FileInputStream fis = new FileInputStream(baseDir + "/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BlockChain.blockChain = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            for (Object blockObj : BlockChain.blockChain) {
                String block = blockObj.toString();
                JsonElement jsonElement = new JsonParser().parse(block);
                JsonObject blockJSONObject = jsonElement.getAsJsonObject();
                JsonElement from_address = blockJSONObject.get("from address");
                String fromAddress = from_address.getAsString();
                JsonElement to_address = blockJSONObject.get("to address");
                String toAddress = to_address.toString();
                for (Object addressObj : AddressBook.addressBook) {
                    address = addressObj.toString();
                    System.out.println("ADDRESS TEST: " + address);
                    if (fromAddress.contentEquals(address)) {
                        byte[] decodedAddressBytes = Base58.decode(fromAddress);
                        String decodedAddress = new String(decodedAddressBytes, StandardCharsets.UTF_8);
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (SHA256.generateSHA256Hash(privKey).contentEquals(decodedAddress)) {
                                JsonElement tx_hash = blockJSONObject.get("tx hash");
                                String txHash = tx_hash.getAsString();
                                JsonElement block_hash = blockJSONObject.get("block hash");
                                String blockHash = block_hash.getAsString();
                                WalletLogger.logEvent("info", "Found sent transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                JsonElement tx_amount = blockJSONObject.get("amount");
                                float amount = tx_amount.getAsFloat();
                                balance -= amount;
                            }
                        }
                    } else if (toAddress.contentEquals(address)) {
                        byte[] decodedAddressBytes = Base58.decode(toAddress);
                        String decodedAddress = new String(decodedAddressBytes, StandardCharsets.UTF_8);
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (SHA256.generateSHA256Hash(privKey).contentEquals(decodedAddress)) {
                                JsonElement tx_hash = blockJSONObject.get("tx hash");
                                String txHash = tx_hash.getAsString();
                                JsonElement block_hash = blockJSONObject.get("block hash");
                                String blockHash = block_hash.getAsString();
                                WalletLogger.logEvent("info", "Found received transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                JsonElement tx_amount = blockJSONObject.get("amount");
                                float amount = tx_amount.getAsFloat();
                                balance += amount;
                            }
                        }
                    } else if (fromAddress.contentEquals(Constants.cbAddress) && toAddress.contentEquals(address)) {
                        byte[] decodedAddressBytes = Base58.decode(toAddress);
                        String decodedAddress = new String(decodedAddressBytes, StandardCharsets.UTF_8);
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (SHA256.generateSHA256Hash(privKey).contentEquals(decodedAddress)) {
                                JsonElement tx_hash = blockJSONObject.get("tx hash");
                                String txHash = tx_hash.getAsString();
                                JsonElement block_hash = blockJSONObject.get("block hash");
                                String blockHash = block_hash.getAsString();
                                WalletLogger.logEvent("info", "Found mined transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                JsonElement tx_amount = blockJSONObject.get("amount");
                                float amount = tx_amount.getAsFloat();
                                balance -= amount;
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            WalletLogger.logException(e, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(e));
        } catch (Base58.AddressFormatException afe) {
            WalletLogger.logException(afe, "severe", WalletLogger.getLogTimeStamp() + " Address format exception occurred while encoding/decoding an address! See below:\n" + WalletLogger.exceptionStacktraceToString(afe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public void writeBlockChain() {
        try {
            System.out.println("Trying to serialize chain.dat...\n");
            FileOutputStream fos = new FileOutputStream("/home/dev-environment/Desktop/java_random/TeacHingChain" + "/chain.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(BlockChain.blockChain);
            oos.close();
            fos.close();
            readBlockChain();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to write to blockchain! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public int getIndexOfBlockChain() {
        return BlockChain.blockChain.size() - 1;
    }

    String getBlockAtIndex(int index) {
        return BlockChain.blockChain.get(index).toString();
    }

    public long getUnixTimestamp() {
        return System.currentTimeMillis();
    }

    private String readPrivateKey(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            privKey = new String (encoded, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while reading private key! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    return privKey;
    }

    public void generatePrivateKey() {
        try {
            Process bash_script = Runtime.getRuntime().exec(baseDir + "/keygen.sh");
            bash_script.destroy();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while generating private key! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
        System.out.println("Private key: " + readPrivateKey(baseDir + "/THC_PRIVATE_KEY"));
        File keyRingFile = new File(baseDir + "keyring.dat");
        if (!keyRingFile.exists()) {
            KeyRing keyRing = new KeyRing();
            try {
                KeyRing.keyRing.add(privKey);
                FileOutputStream fos = new FileOutputStream(baseDir + "/keyring.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(KeyRing.keyRing);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        } else {
            KeyRing.keyRing.add(privKey);
            try {
                FileOutputStream fos = new FileOutputStream(baseDir + "/keyring.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(KeyRing.keyRing);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        }
    }

    public String generateAddress(){
        if (KeyRing.keyRing.size() == 1) {
            String privKeyStr = KeyRing.keyRing.get(0).toString();
            byte[] privKeyBytes = privKeyStr.getBytes();
            address = Base58.encode(privKeyBytes);
        } else if (KeyRing.keyRing.size() >= 2) {
            String privKeyStr = KeyRing.keyRing.get(0).toString();
            byte[] privKeyBytes = privKeyStr.getBytes();
            address = Base58.encode(privKeyBytes);
        }
        addToAddressBook(address);
        return address;
    }

    private void addToAddressBook(String address) {
        File abFile = new File(baseDir + "addressBook.dat");
        if (!abFile.exists()) {
            AddressBook addressBook = new AddressBook();
            AddressBook.addressBook.add(address);
            try {
                FileOutputStream fos = new FileOutputStream(baseDir + "/addressBook.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(AddressBook.addressBook);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to address book! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        } else {
            AddressBook.addressBook.add(address);
            try {
                FileOutputStream fos = new FileOutputStream(baseDir + "/addressBook.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(AddressBook.addressBook);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to address book! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        }
    }

    public String getAddressFromAddressBook(int index) {
        return AddressBook.addressBook.get(index).toString();
    }
    
    void readAddressBook() {
        try {
            FileInputStream fis = new FileInputStream(baseDir + "/addressBook.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            AddressBook.addressBook = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while reading address book! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while reading address book! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp() + " Null pointer exception occurred while reading address book! See below:\n" + WalletLogger.exceptionStacktraceToString(npe));
        }
    }
    
    void readKeyRing() {
        try {
            FileInputStream fis = new FileInputStream(baseDir + "/keyring.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            KeyRing.keyRing = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp() + " Null pointer exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(npe));
        }
    }

    public void sendTx(String sendKeyTx, String recvKeyTx, float amount) {
        long blockIndex = (BlockChain.blockChain.size());
        writeTxPool(blockIndex, sendKeyTx, recvKeyTx, amount);
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

    long getDifficulty(){
        System.out.println("\n");
        System.out.println("Difficulty: \n" + difficulty);
        return difficulty;
    }

    public int calculateDifficulty() {
        readBlockChain();
        if (BlockChain.blockChain.size() >= 2) {
            String mostRecentBlock = BlockChain.blockChain.get(BlockChain.blockChain.size() - 1).toString();
            JsonElement parseLastBlock = new JsonParser().parse(mostRecentBlock);
            JsonObject latBlockObject = parseLastBlock.getAsJsonObject();
            JsonElement difficultyElement = latBlockObject.get("difficulty");
            difficulty = difficultyElement.getAsInt();
            JsonElement lastBlockTime = latBlockObject.get("time stamp");
            long lbtAsLong = lastBlockTime.getAsLong();
            String blockBeforeLast = BlockChain.blockChain.get(BlockChain.blockChain.size() - 2).toString();
            JsonElement jsonElement1 = new JsonParser().parse(blockBeforeLast);
            JsonObject jsonObject1 = jsonElement1.getAsJsonObject();
            JsonElement timeElement = jsonObject1.get("time stamp");
            long bblTimeAsLong = timeElement.getAsLong();
            long deltaT = lbtAsLong - bblTimeAsLong;
            if (deltaT > 60000) {
                difficulty--;
            } else if (deltaT < 60000) {
                difficulty++;
            }
        }
    return difficulty;
    }

    static class InsufficientBalanceException extends Exception {
        InsufficientBalanceException(String msg) {
            System.out.println(msg);
        }
    }
}

