package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.TxEncoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.util.addresses.Base58;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class MainChain {

    public static double difficulty;
    public static String targetHex;
    public static float balance;
    public static final double nSubsidy = 50;
    private static String address;
    private static String privKey;

    public MainChain() {}

    private void writeTxPool(long timeStamp, String fromAddress, String toAddress, double amount, String txHash) {
        String configPath;
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
                new TxPoolArray();
                Tx tx = new Tx(timeStamp, fromAddress, toAddress, amount, txHash, signTx(fromAddress, toAddress, txHash));
                try {
                    String txPoolTX = new TxEncoder().encode(tx);
                    TxPoolArray.TxPool.add(txPoolTX);
                    if (TxPoolArray.TxPool.size() > 1) {
                        propagateTx();
                    }

                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "warning", WalletLogger.getLogTimeStamp()
                            + " Failed to encode tx! See details below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            } else {
                Tx tx = new Tx(timeStamp, fromAddress, toAddress, amount, txHash, signTx(fromAddress, toAddress, txHash));
                try {
                    String txPoolTX = new TxEncoder().encode(tx);
                    TxPoolArray.TxPool.add(txPoolTX);
                    if (TxPoolArray.TxPool.size() > 1) {
                        propagateTx();
                    }
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "warning", WalletLogger.getLogTimeStamp()
                            + " Failed to encode tx! See details below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
            overwriteTxPool();
        } catch (FileNotFoundException fnfe) {
            WalletLogger.logException(fnfe, "severe", "Could not find specified file! See details below:\n"
                    + WalletLogger.exceptionStacktraceToString(fnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", "IOException occurred reading/writing tx-pool.dat! See details below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    void propagateTx() {
        new EndpointManager().connectAsClient("tx");
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
            if (BlockChain.blockChain.size() == 1) {
                return new GenesisBlockDecoder().decode(BlockChain.blockChain.get(0)).getBlockHash();
            } else {
                return new BlockDecoder().decode(BlockChain.blockChain.get(BlockChain.blockChain.size() - 1)).getBlockHash();
            }
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
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while fetching chain.dat! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException e) {
            WalletLogger.logException(e, "severe", WalletLogger.getLogTimeStamp()
                    + " Class not found exception occurred while fetching chain.dat! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(e));
        }
    }

    void calculateBalance() {
        Sign.SignatureData sigData;
        String recoveredKey;
        balance = 0;
        try {
            readBlockChain();
            readKeyRing();
            for (int i = 1; i < BlockChain.blockChain.size(); i++) {
                try {
                    Block blockObject = new BlockDecoder().decode(BlockChain.blockChain.get(i));
                    String[] txs = blockObject.getTransactions();
                    String[] txins = blockObject.getTxins();
                    String[] txouts = blockObject.getTxouts();
                    sigData = signTx(txins[0], txouts[0], calculateTxHashHex(blockObject.getTimeStamps()[0],
                            txins[0], txouts[0], blockObject.getAmounts()[0]));
                    for (String txInput : txins) {
                        if (txInput.startsWith("CB")) {
                            for (int j = 0; j < KeyRing.keyRing.size(); j++) {
                                recoveredKey = Sign.signedMessageToKey(txs[0].getBytes(), sigData).toString(16);
                                if (recoveredKey.contentEquals(KeyRing.keyRing.get(j))) {
                                    WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " Mined transaction " + txs[0] + " found!\n"
                                            + "Found in block: " + blockObject.getBlockHash() + " at index: "
                                            + blockObject.getIndex() + " amount: " + blockObject.getAmounts()[0] + "\n" + "Recovered key: "
                                            + Sign.signedMessageToKey(txs[0].getBytes(), sigData).toString(16) + " ("
                                            + KeyRing.keyRing.indexOf(Sign.signedMessageToKey(txs[0].getBytes(), sigData).toString(16)) + ")");
                                    balance += blockObject.getAmounts()[0];
                                }
                            }
                        }
                    }
                } catch (NullPointerException npe) {
                    i++;
                }
            }
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp()
                    + " Failed to decode block fetching chain.dat! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(de));
        } catch (SignatureException e) {
            e.printStackTrace();
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
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while trying to write to blockchain! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public int getIndexOfBlockChain() {
        return BlockChain.blockChain.size() - 1;
    }

    String getBlockAtIndex(int index) {
        return BlockChain.blockChain.get(index);
    }

    private String readPrivateKey(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            privKey = new String (encoded, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while reading private key! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        }
    return privKey;
    }

    public void generateKeyPair() {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        KeyPairGenerator keyGen;
        try {
            configProps.load(new FileInputStream(configPath));
            keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);
            KeyPair kp = keyGen.generateKeyPair();
            PrivateKey privKey = kp.getPrivate();
            ECPrivateKey ecPrivKey = (ECPrivateKey) privKey;
            String ecPrivKeyAsString = new Miner().leftPad(ecPrivKey.getS().toString(16), 64, '0');
            BigInteger ecPubKey = Sign.publicKeyFromPrivate(new BigInteger(ecPrivKeyAsString, 16));
            String ecPubKeyAsString = ecPubKey.toString(16);
            KeyRing.keyRing.add(ecPrivKeyAsString);
            KeyRing.keyRing.add(ecPubKeyAsString);
            FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/keyring.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(KeyRing.keyRing);
            oos.close();
            fos.close();
        } catch (NoSuchAlgorithmException e) {
            WalletLogger.logException(e, "severe", WalletLogger.getLogTimeStamp()
                    + " No such algo exception occurred while writing to key ring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(e));
        } catch (FileNotFoundException fnfe) {
            WalletLogger.logException(fnfe, "severe", WalletLogger.getLogTimeStamp()
                    + " File not found exception occurred while writing to keyring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(fnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while writing to key ring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (InvalidAlgorithmParameterException iape) {
            WalletLogger.logException(iape, "severe", WalletLogger.getLogTimeStamp()
                    + " Invalid algo parameter exception occurred while writing to key ring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(iape));
        }
    }

    public String generateCBPubKey() {
        BigInteger ecPubKey = null;
        try {
            KeyPairGenerator keyGen;
            keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);
            KeyPair kp = keyGen.generateKeyPair();
            PrivateKey privKey = kp.getPrivate();
            ECPrivateKey ecPrivKey = (ECPrivateKey) privKey;
            String ecPrivKeyAsString = new Miner().leftPad(ecPrivKey.getS().toString(16), 64, '0');
            ecPubKey = Sign.publicKeyFromPrivate(new BigInteger(ecPrivKeyAsString, 16));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return ecPubKey.toString(16);
    }

    public String generateAddress(int keyIndex) {
        if (KeyRing.keyRing.get(keyIndex).length() > 64) {
            String pubKey = KeyRing.keyRing.get(keyIndex);
            byte[] hashedPubKey = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(pubKey.getBytes()));
            address = Base58.encode(hashedPubKey);
            addToAddressBook(address);
        } else {
            try {
                throw new Base58.AddressFormatException("Error! Attempted to generate address using private key! Please use a public key!\n");
            } catch (Base58.AddressFormatException e) {
                WalletLogger.logException(e, "warning", WalletLogger.getLogTimeStamp() + " Address format exception occurred whilst" +
                        " generating an address, see below:\n" + WalletLogger.exceptionStacktraceToString(e));
            }
        }
        return address;
    }

    public String generateCBAddress(String cbPubKey) {
        if (cbPubKey.length() > 64) {
            byte[] hashedPubKey = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(cbPubKey.getBytes()));
            address = Base58.encode(hashedPubKey);
        } else {
            try {
                throw new Base58.AddressFormatException("Error! Attempted to generate address using private key! Please use a public key!\n");
            } catch (Base58.AddressFormatException e) {
                WalletLogger.logException(e, "warning", WalletLogger.getLogTimeStamp() + " Address format exception occurred whilst" +
                        " generating an address, see below:\n" + WalletLogger.exceptionStacktraceToString(e));
            }
        }
        return "CB" + address;
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
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                            + " IO exception occurred while writing to address book! See below:\n"
                            + WalletLogger.exceptionStacktraceToString(ioe));
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
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                            + " IO exception occurred while writing to address book! See below:\n"
                            + WalletLogger.exceptionStacktraceToString(ioe));
                }
            }
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while writing to address book! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
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
            AddressBook.addressBook = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp()
                    + " Class not found exception occurred while reading address book! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(cnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while reading address book! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp()
                    + " Null pointer exception occurred while reading address book! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(npe));
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
                FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/keyring.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                new KeyRing();
                oos.writeObject(KeyRing.keyRing);
                fos.close();
                oos.close();
            } else {
                FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/keyring.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                KeyRing.keyRing = (ArrayList<String>) ois.readObject();
                ois.close();
                fis.close();
            }
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp()
                    + " Class not found exception occurred while reading keyring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(cnfe));
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while reading keyring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (NullPointerException npe) {
            WalletLogger.logException(npe, "severe", WalletLogger.getLogTimeStamp()
                    + " Null pointer exception occurred while reading keyring! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(npe));
        }
    }

    void sendTx(long timeStamp, String fromAddress, String toAddress, double amount) {
        String txHash = MainChain.calculateTxHashHex(timeStamp, fromAddress, toAddress, amount);
        writeTxPool(timeStamp, fromAddress, toAddress, amount, txHash);
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
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while overwriting tx-pool! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
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
            TxPoolArray.TxPool = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            new TxPoolArray();
            overwriteTxPool();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while reading tx-pool! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp()
                    + " Class not found exception occurred while reading tx-pool! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(cnfe));
        }
    }

    void getTxPool() {
        if (TxPoolArray.TxPool == null) {
            new TxPoolArray();
            System.out.println("tx pool: \n" + "[]");
        }
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
        readTargetCache();
        System.out.println("\n");
        System.out.println("Difficulty: \n" + difficulty);
    }

    // maybe use bitwise ops in some of the target adjustment vs. big decimal arithmetic?
    public static void calculateTarget(long deltaT, String previousTarget) {
        readTargetCache();
        double adjustmentFactor;
        BigDecimal targetAsBigDec;
        if (BlockChain.blockChain.size() < 5) {
            MainChain.difficulty = 1;
            targetAsBigDec = new BigDecimal(new BigInteger(previousTarget, 16));
            setTargetHex(getHex(targetAsBigDec.toBigInteger().toByteArray()));
            if (MainChain.targetHex.length() < 64) {
                setTargetHex(new Miner().leftPad(MainChain.targetHex, 64, '0'));
                writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
                System.out.println("New target hex: " + getTargetHex());
            }
        } else if (BlockChain.blockChain.size() % 5 == 0) {
            targetAsBigDec = new BigDecimal(new BigInteger(previousTarget, 16));
            if (deltaT < Constants.TARGET_WINDOW_DURATION) {
                BigDecimal deltaTAsBigDec = new BigDecimal(String.valueOf(deltaT));
                BigDecimal targetWindow = new BigDecimal(String.valueOf(Constants.TARGET_WINDOW_DURATION));
                adjustmentFactor = (deltaTAsBigDec.multiply(new BigDecimal(String.valueOf(1 / targetWindow.doubleValue()))).doubleValue());
                System.out.println("Delta time: " + deltaT);
                System.out.println("Adjustment factor: " + adjustmentFactor + " Actual change: " + (1 - adjustmentFactor));
                targetAsBigDec = targetAsBigDec.multiply(new BigDecimal(String.valueOf(adjustmentFactor)));
                setTargetHex(getHex(targetAsBigDec.toBigInteger().toByteArray()));
                if (MainChain.targetHex.length() < 64) {
                    setTargetHex(new Miner().leftPad(MainChain.targetHex, 64, '0'));
                    writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
                    System.out.println("New target hex: " + getTargetHex());
                }
                MainChain.difficulty += 1 - adjustmentFactor;
                writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
            } else if (deltaT > Constants.TARGET_WINDOW_DURATION) {
                BigDecimal deltaTAsBigDec = new BigDecimal(String.valueOf(deltaT));
                BigDecimal targetWindow = new BigDecimal(String.valueOf(Constants.TARGET_WINDOW_DURATION));
                adjustmentFactor = (deltaTAsBigDec.multiply(new BigDecimal(String.valueOf(1 / targetWindow.doubleValue()))).doubleValue());
                if (adjustmentFactor > 3.5) {
                    adjustmentFactor = 3.5;
                }
                System.out.println("Delta time: " + deltaT);
                System.out.println("Adjustment factor: " + adjustmentFactor);
                targetAsBigDec = targetAsBigDec.multiply(new BigDecimal(String.valueOf(adjustmentFactor)));
                setTargetHex(getHex(targetAsBigDec.toBigInteger().toByteArray()));
                System.out.println("New target as big dec: " + targetAsBigDec);
                if (MainChain.difficulty - adjustmentFactor < 1) {
                    MainChain.difficulty = 1;
                    targetAsBigDec = new BigDecimal(new BigInteger(Constants.GENESIS_TARGET, 16));
                    setTargetHex(getHex(targetAsBigDec.toBigInteger().toByteArray()));
                    writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
                } else {
                    MainChain.difficulty -= (adjustmentFactor - 1);
                    setTargetHex(getHex(targetAsBigDec.toBigInteger().toByteArray()));
                    writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
                }
                if (MainChain.targetHex.length() < 64) {
                    setTargetHex(new Miner().leftPad(MainChain.targetHex, 64, '0'));
                    writeTargetCache(new BigInteger(getTargetHex(), 16), MainChain.difficulty);
                    System.out.println("New target hex: " + getTargetHex());
                }
            }
        }
    }

    public static void writeTargetCache(BigInteger target, double difficulty) {
        String paddedTarget;
        if (target.toString(16).length() < 64) {
            paddedTarget = new Miner().leftPad(target.toString(16), 64, '0');
        } else {
            paddedTarget = target.toString(16);
        }
        MainChain.setTargetHex(paddedTarget);
        MainChain.difficulty = difficulty;
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            FileOutputStream fos = new FileOutputStream(configProps.getProperty("datadir") + "/target-cache.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MainChain.targetHex);
            oos.close();
            fos.close();
            fos = new FileOutputStream(configProps.getProperty("datadir") + "/difficulty-cache.dat");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MainChain.difficulty);
            oos.close();
            fos.close();
            readTargetCache();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while writing target cache! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }

    public static void readTargetCache() {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            FileInputStream fis = new FileInputStream(configProps.getProperty("datadir") + "/target-cache.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            MainChain.targetHex = (String) ois.readObject();
            ois.close();
            fis.close();
            fis = new FileInputStream(configProps.getProperty("datadir") + "/difficulty-cache.dat");
            ois = new ObjectInputStream(fis);
            MainChain.difficulty = (double) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " IO exception occurred while writing target cache! See below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String calculateTxHashHex(long timeStamp, String fromAddress, String toAddress, double amount) {
        AtomicReference<String> txHash = new AtomicReference<>();
        String txValueHash =  (MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(MainChain.swapEndianness((
                timeStamp + fromAddress + toAddress + amount).getBytes())))));
        KeyRing.keyRing.forEach(key -> {
            if (key.length() > 64) {
                String addressFromPubKey = Base58.encode(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(key.getBytes())));
                if (fromAddress.contentEquals(addressFromPubKey) || toAddress.contentEquals(addressFromPubKey)) {
                    byte[] txHashBytes = Hash.sha3(txValueHash.getBytes());
                    txHash.set(MainChain.getHex(txHashBytes));
                }
            }
        });
        return String.valueOf(txHash);
    }

    static Sign.SignatureData signTx(String fromAddress, String toAddress, String txHash) {
        final ECKeyPair[] keyPair = new ECKeyPair[1];
        final byte[][] txHashBytes = new byte[txHash.length()][1];
        try {
            KeyRing.keyRing.forEach(key -> {
                if (key.length() > 64) {
                    String addressFromPubKey = Base58.encode(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(key.getBytes())));
                    if (fromAddress.contentEquals(addressFromPubKey) || toAddress.contentEquals(addressFromPubKey)) {
                        int privateKeyIndex = KeyRing.keyRing.indexOf(key) - 1;
                        keyPair[0] = new ECKeyPair(new BigInteger(KeyRing.keyRing.get(privateKeyIndex), 16),
                                new BigInteger(KeyRing.keyRing.get(privateKeyIndex + 1), 16));
                        txHashBytes[0] = Hash.sha3(txHash.getBytes());
                    }
                }
            });
        } catch (NullPointerException npe) {
            System.out.println("Invalid EC Key pair detected!\n");
        }
        return Sign.signMessage(txHashBytes[0], keyPair[0], false);
    }

    static double calculateTxFee(double amount) {
        return (amount * Constants.TX_FEE);
    }

    public static String getTargetHex() {
        return targetHex;
    }

    static void setTargetHex(String targetHex) {
        MainChain.targetHex = targetHex;
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
        if (txs.length == 1) {
            return (txs[0]);
        }
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

