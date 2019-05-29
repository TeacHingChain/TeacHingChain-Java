package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.gui.WalletGui;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.nodes.ClientManager;
import com.thc.blockchain.network.nodes.server.endpoints.GenesisChainServerEndpoint;
import com.thc.blockchain.util.ConfigParser;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import javax.swing.*;
import java.io.File;
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
        if (!chainFile.exists()) {
            difficulty = 5;
        } else if (chainFile.exists() && HashArray.hashArray.size() <= 1) {
            difficulty = 5;
        } else if (HashArray.hashArray.size() >= 2) {
            mc.calculateDifficulty();
        }
        System.out.println("\n");
        System.out.println("Welcome to the light-weight, PoC, java implementation of TeacHingChain!\n");
        while (true) {
            ClientManager clientManager = new ClientManager();
            Scanner input = new Scanner(System.in);
            System.out.println("\n");
            System.out.println("Enter command:\n");
            String cliInput = input.nextLine();
            switch (cliInput) {
                case "send tx": {
                    Scanner txData = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Please enter to address: \n");
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
                    break;
                }
                case "view chain": {
                    mc.getBlockChain();
                    break;
                }
                case "get chain index": {
                    System.out.println("Index: " + mc.getIndexOfBlockChain());
                    break;
                }
                case "get genesis hash": {
                    if (!HashArray.hashArray.isEmpty()) {
                        System.out.println("Genesis hash: " + mc.getGenesisHash());
                    } else {
                        System.out.println("No block found on chain!");
                    }
                    break;
                }
                case "get best block hash": {
                    if (!HashArray.hashArray.isEmpty()) {
                        System.out.println("Best block hash: " + mc.getBestHash());
                    } else {
                        System.out.println("No block found on chain!");
                    }
                    break;
                }
                case "get block at index": {
                    try {
                        Scanner indexInt = new Scanner(System.in);
                        System.out.println("\n");
                        System.out.println("Enter index value:\n");
                        int indexValue = indexInt.nextInt();
                        System.out.println("Block at index " + indexValue + ": " + mc.getBlockAtIndex(Math.toIntExact(indexValue)));
                    } catch (IndexOutOfBoundsException iobe) {
                        WalletLogger.logException(iobe, "warning", WalletLogger.getLogTimeStamp() + " Index out of bound exception occurred trying to fetch a block! See below:\n" + WalletLogger.exceptionStacktraceToString(iobe));
                    }
                    break;
                }
                case "get balance": {
                    mc.readBlockChain();
                    System.out.println("\n");
                    System.out.println("Current balance:\n" + MainChain.balance);
                    break;
                }
                case "mine": {
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
                            int indexValue = (HashArray.hashArray.size());
                            long timeStamp = mc.getUnixTimestamp();
                            String previousHash = mc.getPreviousBlockHash();
                            String toAddress = AddressBook.addressBook.get(0).toString();
                            String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                            try {
                                miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException ie) {
                                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                            }
                            numBlocksMined++;
                        } else if (TxPoolArray.TxPool == null) {
                            TxPoolArray txpool = new TxPoolArray();
                        } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && HashArray.hashArray.size() >= 3) {
                            mc.readBlockChain();
                            int indexValue = HashArray.hashArray.size();
                            long timeStamp = mc.getUnixTimestamp();
                            difficulty = mc.calculateDifficulty();
                            String toAddress = AddressBook.addressBook.get(0).toString();
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                            try {
                                miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException ie) {
                                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                            }
                            numBlocksMined++;
                        } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && HashArray.hashArray.size() < 3) {
                            mc.readBlockChain();
                            int indexValue = HashArray.hashArray.size();
                            long timeStamp = mc.getUnixTimestamp();
                            difficulty = mc.calculateDifficulty();
                            String toAddress = AddressBook.addressBook.get(0).toString();
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                            try {
                                miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException ie) {
                                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                            }
                            numBlocksMined++;
                        } else {
                            mc.readBlockChain();
                            int indexValue = HashArray.hashArray.size();
                            long timeStamp = mc.getUnixTimestamp();
                            difficulty = mc.calculateDifficulty();
                            String fromAddress = TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size()).toString();
                            System.out.println("From address says: \n" + fromAddress);
                            String toAddress = TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 1).toString();
                            System.out.println("To address says: \n" + toAddress);
                            String amountToStr = ((String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 2));
                            float amount = Float.parseFloat(amountToStr);
                            System.out.println("Amount key says: \n" + amount);
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + fromAddress + toAddress);
                            try {
                                miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, difficulty, MainChain.nSubsidy);
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
                                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                            }
                        }
                    }
                    break;
                }
                case "generate address": {
                    String address = mc.generateAddress();
                    System.out.println("Address generated: " + address);
                    break;
                }
                case "view tx pool": {
                    cb.getTxPool();
                    break;
                }
                case "view difficulty": {
                    mc.getDifficulty();
                    break;
                }
                case "sync": {
                    clientManager.connectAsClient("sync");
                    break;
                }
                case "start gui": {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        SwingUtilities.invokeLater(() -> new WalletGui().setVisible(true));
                    } catch (Exception e) {
                        WalletLogger.logException(e, "warning", WalletLogger.getLogTimeStamp() + " An exception occurred while starting GUI! See below:\n" + WalletLogger.exceptionStacktraceToString(e));
                    }
                    break;
                }
                case "view private key": {
                    System.out.println("Private key: " + KeyRing.keyRing.get(0));
                    break;
                }
                case "view address": {
                    System.out.println("Address: " + AddressBook.addressBook.get(0));
                    break;
                }
                case "quit": {
                    System.exit(1);
                    break;
                }
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