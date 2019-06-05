package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.algos.SHA512;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.TxEncoder;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.util.addresses.Base58;

import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class MainChain {

    public static double difficulty;
    public static String targetHex;
    public static float balance;
    public static final float nSubsidy = 50;
    private static String address;
    private static String privKey;
    private static byte[] checkHash;

    public MainChain() {}

    public static boolean isBlockHashValid(long index, long currentTimeMillis, String fromAddress, String toAddress, String[] txHash, String merkleRoot, long Nonce, String previousBlockHash, String algo, String currentHash, String target, float amount) {
        MainChain.targetHex = target;
        byte[] blockHeaderBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray(MainChain.getHex((index + currentTimeMillis + fromAddress + toAddress + Arrays.toString(txHash) + merkleRoot + Nonce + previousBlockHash + algo + target + amount).getBytes())));
        if (algo.contentEquals("sha256")) {
            checkHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(blockHeaderBytes));
        } else if (algo.contentEquals("sha512")) {
            checkHash = SHA512.SHA512HashByteArray(SHA512.SHA512HashByteArray(blockHeaderBytes));
        }
        return (getHex(checkHash).contentEquals(currentHash));
    }

    void writeTxPool(String fromAddress, String toAddress, float amount, String txHash) {
        String configPath;
        MainChain mc = new MainChain();
        try {
            if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
                configPath = Constants.BASEDIR + "/../../config/config.properties";
            } else {
                configPath = Constants.BASEDIR + "/config/config.properties";
            }
            Properties configProps = new Properties();
            configProps.load(new FileInputStream(configPath));
            File tempFile = new File(configProps.getProperty("datadir") + "/tx-pool.dat");
            if (!tempFile.exists()) {
                TxPoolArray txpool = new TxPoolArray();
                Tx tx = new Tx(fromAddress, toAddress, amount, txHash);
                try {
                    String txPoolTX = new TxEncoder().encode(tx);
                    TxPoolArray.TxPool.add(txPoolTX);

                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "warning", WalletLogger.getLogTimeStamp() + " Failed to encode tx! See details below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            } else {
                Tx tx = new Tx(fromAddress, toAddress, amount, txHash);
                TxEncoder encoder = new TxEncoder();
                try {
                    String txPoolTX = encoder.encode(tx);
                    TxPoolArray.TxPool.add(txPoolTX);
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "warning", WalletLogger.getLogTimeStamp() + " Failed to encode tx! See details below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
            overwriteTxPool();
        } catch (FileNotFoundException fnfe) {
            WalletLogger.logException(fnfe, "severe", "Could not find specified file! See details below:\n" + WalletLogger.exceptionStacktraceToString(fnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", "IOException occurred reading/writing tx-pool.dat! See details below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    String getBestHash() throws DecodeException {
        readBlockChain();
        try {
            return new BlockDecoder().decode(BlockChain.blockChain.get(getIndexOfBlockChain())).getBlockHash();
        } catch (DecodeException de) {
            throw new DecodeException(BlockChain.blockChain.get(getIndexOfBlockChain()), "Unable to decode text to Block", de);
        }
    }

    String getGenesisHash() throws DecodeException {
        readBlockChain();
        try {
            return new GenesisBlockDecoder().decode(BlockChain.blockChain.get(0)).getBlockHash();
        } catch (DecodeException de) {
            throw new DecodeException(BlockChain.blockChain.get(0), "Unable to decode text to Block", de);
        }
    }

    public String getPreviousBlockHash() throws DecodeException {
        readBlockChain();
        try {
            return new BlockDecoder().decode(BlockChain.blockChain.get(getIndexOfBlockChain())).getPreviousBlockHash();
        } catch (DecodeException de) {
            throw new DecodeException(BlockChain.blockChain.get(getIndexOfBlockChain()), "Unable to decode text to Block", de);
        }
    }

    void viewBlockChain() {
        readBlockChain();
        for (String block : BlockChain.blockChain) {
            System.out.println(block);
        }
    }

    public void readBlockChain() {
        try {
            String configPath;
            if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
                configPath = Constants.BASEDIR + "/../../config/config.properties";
            } else {
                configPath = Constants.BASEDIR + "/config/config.properties";
            }
            Properties configProps = new Properties();
            configProps.load(new FileInputStream(configPath));
            FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BlockChain.blockChain = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException e) {
            WalletLogger.logException(e, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(e));
        }
    }

    void calculateBalance() {
        balance = 0;
        try {
            readBlockChain();
            readKeyRing();
            for (int i = 1; i < BlockChain.blockChain.size(); i++) {
                Block blockObject = new BlockDecoder().decode(BlockChain.blockChain.get(i));
                String[] txs = blockObject.getTransactions();
                String toAddress = blockObject.getToAddress();
                String fromAddress = blockObject.getFromAddress();
                for (Object addressObj : AddressBook.addressBook) {
                    address = addressObj.toString();
                    if (fromAddress.contentEquals(address)) {
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (MainChain.getHex(Base58.decode(fromAddress)).contentEquals(MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(privKey.getBytes()))))) {
                                String txHash = txs[txs.length - 1];
                                String blockHash = blockObject.getBlockHash();
                                WalletLogger.logEvent("info", "Found sent transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                float amount = Float.parseFloat(blockObject.getAmount());
                                balance -= amount;
                            }
                        }
                    } else if (toAddress.contentEquals(address) && !fromAddress.contentEquals(Constants.CB_ADDRESS)) {
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (MainChain.getHex(Base58.decode(toAddress)).contentEquals(MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(privKey.getBytes()))))) {
                                String txHash = txs[txs.length - 1];
                                String blockHash = blockObject.getBlockHash();
                                WalletLogger.logEvent("info", "Found received transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                float amount = Float.parseFloat(blockObject.getAmount());
                                balance += amount;
                            }
                        }
                    } else if (fromAddress.contentEquals(Constants.CB_ADDRESS) && toAddress.contentEquals(address)) {
                        for (Object o : KeyRing.keyRing) {
                            privKey = o.toString();
                            if (MainChain.getHex(Base58.decode(toAddress)).contentEquals(MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(privKey.getBytes()))))) {
                                String txHash = txs[txs.length - 1];
                                String blockHash = blockObject.getBlockHash();
                                WalletLogger.logEvent("info", "Found mined transaction: \n" + txHash + "\n corresponding block: \n" + blockHash);
                                float amount = Float.parseFloat(blockObject.getAmount());
                                balance += amount;
                            }
                        }
                    }
                }
            }
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp() + " Failed to decode block fetching chain.dat! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        } catch (Base58.AddressFormatException afe) {
            WalletLogger.logException(afe, "warning", WalletLogger.getLogTimeStamp() + " An error occurred trying to decode an address! See details below:\n" + WalletLogger.exceptionStacktraceToString(afe));
        }
    }

    public void writeBlockChain() {
        try {
            String configPath;
            if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
                configPath = Constants.BASEDIR + "/../../config/config.properties";
            } else {
                configPath = Constants.BASEDIR + "/config/config.properties";
            }
            Properties configProps = new Properties();
            configProps.load(new FileInputStream(configPath));
            FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/chain.dat");
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
        return BlockChain.blockChain.get(index);
    }

    long getUnixTimestamp() {
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
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            Runtime rt = Runtime.getRuntime();
            String[] commands = {configProps.getProperty("datadir") + "/keygen.sh"};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            System.out.println("Command output: \n");
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            privKey = readPrivateKey(configProps.getProperty("datadir") + "/THC_PRIVATE_KEY");
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while generating private key! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
        File keyRingFile = new File( configProps.getProperty("datadir") + "/keyring.dat");
        if (!keyRingFile.exists()) {
            new KeyRing();
            try {
                KeyRing.keyRing.add(privKey);
                FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/keyring.dat");
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
                configProps.load(new FileInputStream(configPath));
                FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/keyring.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(KeyRing.keyRing);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        }
    }

    public String generateAddress(int keyIndex){
        byte[] hashedPrivKeyBytes = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(KeyRing.keyRing.get(keyIndex).getBytes()));
        address = Base58.encode(hashedPrivKeyBytes);
        addToAddressBook(address);
        return address;
    }

    private void addToAddressBook(String address) {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            File abFile = new File(configProps.getProperty("datadir") + "/addressBook.dat");
            if (!abFile.exists()) {
                new AddressBook();
                AddressBook.addressBook.add(address);
                try {
                    configProps.load(new FileInputStream(configPath));
                    FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/addressBook.dat");
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
                    configProps.load(new FileInputStream(configPath));
                    FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/addressBook.dat");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(AddressBook.addressBook);
                    oos.close();
                    fos.close();
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to address book! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            }
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while writing to address book! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public String getAddressFromAddressBook(int index) {
        return AddressBook.addressBook.get(index);
    }
    
    void readAddressBook() {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/addressBook.dat");
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
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            File keyRingFile = new File(configProps.getProperty("datadir") + "/keyring.dat");
            if (!keyRingFile.exists()) {
                new KeyRing();
                FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/keyring.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(KeyRing.keyRing);
            } else {
                FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/keyring.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                KeyRing.keyRing = (ArrayList) ois.readObject();
                ois.close();
                fis.close();
            }
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp() + " Null pointer exception occurred while reading keyring! See below:\n" + WalletLogger.exceptionStacktraceToString(npe));
        }
    }

    void sendTx(String fromAddress, String toAddress, float amount) {
        byte[] txHashBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray(MainChain.getHex((fromAddress + toAddress + amount).getBytes())));
        String txHash = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes)));
        writeTxPool(fromAddress, toAddress, amount, txHash);
    }

    void overwriteTxPool() {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/tx-pool.dat");
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
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/tx-pool.dat");
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

    void getDifficulty(){
        System.out.println("\n");
        System.out.println("Difficulty: \n" + difficulty);
    }

    public static BigDecimal calculateTarget(long deltaT, String previousTarget) {
        double adjustmentFactor;

        if (MainChain.difficulty <= 1 || BlockChain.blockChain.size() == 1) {
            MainChain.difficulty = 1;
            
        }
        BigDecimal targetAsBigDec = new BigDecimal(new BigInteger(previousTarget, 16));
        if (deltaT < Constants.TARGET_TIME_WINDOW) {
            BigDecimal deltaTargetTime = new BigDecimal(String.valueOf(Constants.TARGET_TIME_WINDOW - deltaT));
            adjustmentFactor = deltaTargetTime.multiply(new BigDecimal(String.valueOf((
                    deltaTargetTime.doubleValue() / Constants.TARGET_TIME_WINDOW) / 6))).doubleValue();
            MainChain.difficulty += adjustmentFactor;
            System.out.println(deltaTargetTime);
            System.out.println("adj factor: " + adjustmentFactor);
            targetAsBigDec = targetAsBigDec.multiply(new BigDecimal(String.valueOf(1 / adjustmentFactor)));

            targetHex = getHex(targetAsBigDec.toBigInteger().toByteArray());
        } else if (deltaT > Constants.TARGET_TIME_WINDOW) {
            BigDecimal deltaTargetTime = new BigDecimal(String.valueOf(deltaT - Constants.TARGET_TIME_WINDOW));
            adjustmentFactor = deltaTargetTime.multiply(new BigDecimal(String.valueOf((
                    deltaTargetTime.doubleValue() / Constants.TARGET_TIME_WINDOW) / 6))).doubleValue();
            System.out.println("delta2T: " + deltaTargetTime);
            targetAsBigDec = targetAsBigDec.multiply(new BigDecimal(String.valueOf(adjustmentFactor)));
            targetHex = getHex(targetAsBigDec.toBigInteger().toByteArray());
            MainChain.difficulty -= adjustmentFactor;
        }
        return targetAsBigDec;
    }

    static String getTargetHex() {
        return targetHex;
    }


    public static byte[] swapEndianness(byte[] hash) {
        byte[] result = new byte[hash.length];
        for (int i = 0; i < hash.length; i++) {
            result[i] = hash[hash.length-i-1];
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getHex(byte[] raw) {
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(Constants.HEXES.charAt((b & 0xF0) >> 4)).append(Constants.HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
    public static String calculateMerkleRoot(String[] txs) {
        String merkleRoot = null;
        if (txs.length == 2) {
            byte[] txABytes = (MainChain.swapEndianness(MainChain.hexStringToByteArray(txs[0])));
            byte[] txBBytes = (MainChain.swapEndianness(MainChain.hexStringToByteArray(txs[1])));
            byte[] txABBytes = Arrays.copyOf(txABytes, txABytes.length + txBBytes.length);
            System.arraycopy(txBBytes, 0, txABBytes, txABytes.length, txABBytes.length);
            merkleRoot = MainChain.getHex(MainChain.swapEndianness(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txABBytes))));
        }
        return merkleRoot;
    }

    static class InsufficientBalanceException extends Exception {
        InsufficientBalanceException() {
            System.out.println("Insufficient balance exception occurred! See log for details\n");
        }
    }
}

