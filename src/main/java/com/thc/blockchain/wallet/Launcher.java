package com.thc.blockchain.wallet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.gui.WalletGui;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.nodes.ClientManager;
import com.thc.blockchain.network.nodes.server.endpoints.GenesisChainServerEndpoint;
import com.thc.blockchain.util.ConfigParser;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.util.addresses.Base58;
import javax.swing.*;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import static com.thc.blockchain.network.Constants.baseDir;

public class Launcher {

    private static String algo;
    public static int difficulty;

    public static void main(String[] args) {
        // File rootDir = new File(baseDir);
        // Process proc = Runtime.getRuntime().exec("mvn cargo:run -e", null, rootDir);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String formattedTime = (formatter.format(calendar.getTime()));
        WalletLogger logger = new WalletLogger();
        System.out.println(baseDir);
        ConfigParser configParser = new ConfigParser();
        configParser.readConfigFile();
        MainChain mc = new MainChain();
        ChainBuilder cb = new ChainBuilder();
        Miner miner = new Miner();
        File chainFile = new File(baseDir + "/chain.dat");
        if (!chainFile.exists()) {
            GenesisChainServerEndpoint gb = new GenesisChainServerEndpoint();
            gb.initChain();
            ClientManager clientManager = new ClientManager();
            clientManager.connectAsClient("init chain");
        } else {
            mc.readKeyRing();
            mc.readAddressBook();
            mc.readBlockChain();
            cb.readTxPool();
        }
        if(!chainFile.exists()) {
            difficulty = 5;
        } else if (chainFile.exists() && HashArray.hashArray.size() <= 1) {
            difficulty = 5;
        } else if (HashArray.hashArray.size() >= 2) {
            mc.calculateDifficulty();
        }
        System.out.println("\n");
        System.out.println("Welcome to the light-weight, PoC, java implementation of TeacHingChain!\n");
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.println("\n");
            System.out.println("Enter command:\n");
            String cliInput = input.nextLine();
            if (cliInput.contentEquals("send tx")) {
                Scanner txData = new Scanner(System.in);
                System.out.println("\n");
                System.out.println("Please enter receive key\n");
                String fromAddress = (String) AddressBook.addressBook.get(0);
                String toAddress = txData.nextLine();
                System.out.println("\n");
                System.out.println("Please enter amount: \n");
                float amountInput = txData.nextFloat();
                if (amountInput > MainChain.balance) {
                    System.out.println("ERROR! You don't have enough coins to send this tx, please check your balance and try again..\n");
                } else {
                    mc.sendTx(fromAddress, toAddress, amountInput);
                }
            } else if (cliInput.contentEquals("view chain")) {
                mc.getBlockChain();
            } else if (cliInput.contentEquals("get chain index")) {
                System.out.println("Index: " + mc.getIndexOfBlockChain());
            } else if (cliInput.contentEquals("get genesis hash")) {
                if (!HashArray.hashArray.isEmpty()) {
                    System.out.println("Genesis hash: " + mc.getGenesisHash());
                } else {
                    System.out.println("No block found on chain!");
                }
            } else if (cliInput.contentEquals("get current hash")) {
                if (HashArray.hashArray.isEmpty()) {
                    System.out.println("No block found on chain!\n");
                } else {
                    System.out.println("Best hash: " + mc.getBestHash());
                }
            } else if (cliInput.contentEquals("get block at index")) {
                Scanner indexInt = new Scanner(System.in);
                System.out.println("\n");
                System.out.println("Enter index value:\n");
                long indexValue = indexInt.nextLong();
                System.out.println("Block at index " + indexValue + ": " + mc.getBlockAtIndex(Math.toIntExact(indexValue)));
            } else if (cliInput.contentEquals("get balance")) {
                System.out.println("\n");
                System.out.println("Current balance:\n" + MainChain.balance);
            } else if (cliInput.contentEquals("mine")) {
                Scanner howMany = new Scanner(System.in);
                System.out.println("\n");
                System.out.println("Enter number of blocks to mine: \n");
                int howManyBlocks = howMany.nextInt();
                int numBlocksMined = 0;
                Random algoSelector = new Random();
                int algoIndex = algoSelector.nextInt(2);
                if (algoIndex == 0) {
                    algo = "sha256";
                } else if (algoIndex == 1) {
                    algo = "sha512";
                } else if (algoIndex == 2) {
                    algo = "scrypt";
                }
                while (howManyBlocks > numBlocksMined) {
                    cb.readTxPool();
                    cb.getTxPool();
                    File tempFile = new File(baseDir + "/tx-pool.dat");
                    if (!tempFile.exists() && HashArray.hashArray.size() >= 3) {
                        TxPoolArray txPool = new TxPoolArray();
                        difficulty = mc.calculateDifficulty();
                        long indexValue = (HashArray.hashArray.size());
                        long timeStamp = mc.getUnixTimestamp();
                        String previousHash = mc.getPreviousBlockHash();
                        String toAddress = (String) AddressBook.addressBook.get(0);
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        try {
                            miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ie) {
                            WalletLogger.logException(ie, "severe", formattedTime + " Interrupted exception occurred during mining operation! See below:\n");
                            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                            WalletLogger.logException(ie, "warning", stacktraceAsString);
                        }
                        numBlocksMined++;
                    } else if (TxPoolArray.TxPool == null) {
                        TxPoolArray txpool = new TxPoolArray();
                    } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && HashArray.hashArray.size() >= 3) {
                        mc.readBlockChain();
                        String mostRecentBlock = (HashArray.hashArray.get(HashArray.hashArray.size() - 1).toString());
                        JsonElement jsonElement = new JsonParser().parse(mostRecentBlock);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement indexElement = jsonObject.get("index");
                        int indexValue = indexElement.getAsInt() + 1;
                        long timeStamp = mc.getUnixTimestamp();
                        difficulty = mc.calculateDifficulty();
                        String toAddress = (String) AddressBook.addressBook.get(0);
                        String previousHash = mc.getPreviousBlockHash();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        try {
                            miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ie) {
                            WalletLogger.logException(ie, "severe", formattedTime + " Interrupted exception occurred during mining operation! See below:\n");
                            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                            WalletLogger.logException(ie, "warning", stacktraceAsString);
                        }
                        numBlocksMined++;
                    } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && HashArray.hashArray.size() < 3) {
                        mc.readBlockChain();
                        String mostRecentBlock = (HashArray.hashArray.get(HashArray.hashArray.size() - 1).toString());
                        JsonElement jsonElement = new JsonParser().parse(mostRecentBlock);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement indexElement = jsonObject.get("index");
                        int indexValue = indexElement.getAsInt() + 1;
                        long timeStamp = mc.getUnixTimestamp();
                        JsonElement parseLastBlock = new JsonParser().parse(mostRecentBlock);
                        JsonObject latBlockObject = parseLastBlock.getAsJsonObject();
                        JsonElement difficultyElement = latBlockObject.get("difficulty");
                        difficulty = difficultyElement.getAsInt();
                        String toAddress = (String) AddressBook.addressBook.get(0);
                        String previousHash = mc.getPreviousBlockHash();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        try {
                            miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ie) {
                            WalletLogger.logException(ie, "severe", formattedTime + " Interrupted exception occurred during mining operation! See below:\n");
                            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                            WalletLogger.logException(ie, "warning", stacktraceAsString);
                        }
                        numBlocksMined++;
                    } else {
                        mc.readBlockChain();
                        String mostRecentBlock = (HashArray.hashArray.get(HashArray.hashArray.size() - 1).toString());
                        JsonElement jsonElement = new JsonParser().parse(mostRecentBlock);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement indexElement = jsonObject.get("index");
                        int indexValue = indexElement.getAsInt() + 1;
                        long timeStamp = mc.getUnixTimestamp();
                        String fromAddress = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size());
                        System.out.println("From address says: \n" + fromAddress);
                        String toAddress = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 1);
                        System.out.println("To address says: \n" + toAddress);
                        String amountToStr = ((String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 2));
                        float amount = Float.parseFloat(amountToStr);
                        System.out.println("Amount key says: \n" + amount);
                        String previousHash = mc.getPreviousBlockHash();
                        String txHash = SHA256.generateSHA256Hash(indexValue + fromAddress + toAddress);
                        try {
                            miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            TxPoolArray.TxPool.remove(fromAddress);
                            System.out.println("removing: \n" + fromAddress);
                            TxPoolArray.TxPool.remove(toAddress);
                            System.out.println("removing: \n" + toAddress);
                            TxPoolArray.TxPool.remove(amountToStr);
                            System.out.println("removing: \n" + amountToStr);
                            TxPoolArray.TxPool.remove(txHash);
                            System.out.println("removing: \n" + txHash);
                            numBlocksMined++;
                            cb.overwriteTxPool();
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ie) {
                            WalletLogger.logException(ie, "severe", formattedTime + " Interrupted exception occurred during mining operation! See below:\n");
                            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                            WalletLogger.logException(ie, "warning", stacktraceAsString);
                        }
                    }
                }
            } else if (cliInput.contentEquals("generate address")) {
                String address = mc.generateAddress();
                System.out.println("Address generated: " + address);
            } else if (cliInput.contentEquals("get tx pool")) {
                cb.getTxPool();
            } else if (cliInput.contentEquals("view difficulty")) {
                mc.getDifficulty();
            } else if (cliInput.contentEquals("create magicstr")) {
                Random rand = new Random();
                BigInteger b = new BigInteger(256, rand);
                String keyToStr = b.toString(10);
                String magicStr = SHA256.generateSHA256Hash(keyToStr);
                System.out.println("MagrecvKeyic string: " + magicStr);
            } else if (cliInput.contentEquals("sync")) {
                ClientManager clientManager = new ClientManager();
                clientManager.connectAsClient("sync");
            } else if (cliInput.contentEquals("test base58")) {
                String privKey = "f8dc5dfe196a41c8c33e0c0cbed01a7c0b3cf35ae6a9cb0648d929088dcad30a";
                String hashedPrivKey = SHA256.generateSHA256Hash(privKey);
                System.out.println("Hashed private key: " + hashedPrivKey);
                byte[] privKeyBytes = hashedPrivKey.getBytes();
                String address = Base58.encode(privKeyBytes);
                System.out.println("Address returned: " + address);
                try {
                    byte[] decodedB58 = Base58.decode(address);
                    String s = new String(decodedB58, StandardCharsets.UTF_8);
                    System.out.println("Decoded address: " + s);
                    if (SHA256.generateSHA256Hash(privKey).contentEquals(s)) {
                        System.out.println("Verified address ownership!\n");
                    } else {
                        System.out.println("ERROR! Address doesn't belong to you!\n");
                    }
                } catch (Base58.AddressFormatException e) {
                    WalletLogger.logException(e, "warning", formattedTime + " An error occurred during address encoding or decoding! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(e);
                    WalletLogger.logException(e, "warning", stacktraceAsString);
                }
            } else if (cliInput.contentEquals("start gui")) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    SwingUtilities.invokeLater(() -> new WalletGui().setVisible(true));
                } catch (Exception e) {
                    WalletLogger.logException(e, "warning", formattedTime + " An exception occurred while starting GUI! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(e);
                    WalletLogger.logException(e, "warning", stacktraceAsString);
                }
            } else if (cliInput.contentEquals("view private key")) {
                System.out.println("Private key: " + KeyRing.keyRing.get(0));
            } else if (cliInput.contentEquals("view address")) {
                System.out.println("Address: " + AddressBook.addressBook.get(0));
            } else if (cliInput.contentEquals("quit")) {
                System.exit(1);
                break;
            }
        }
    }
}






/*
--------------------------BLOCK DETAILS--------------------------


Mined block hash:
000009c2024118e1c49e11b8d8f7aea2591f40d073dfcc53e5fe87fd2de81cd4


Index:
0


Unix time stamp:
1558813055732


Data:
TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!


Tx hash: 5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53


Merkle hash: c433108d65576596d46ae7840a06cb9f2bce102e0d5b2bfa32157d7675123d0f


Previous none


Nonce:
2402952


Difficulty:
5



 */