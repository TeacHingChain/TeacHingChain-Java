package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.gui.WalletGui;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.server.endpoints.GenesisChainServerEndpoint;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;

import javax.swing.*;
import javax.websocket.DecodeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static com.thc.blockchain.wallet.MainChain.swapEndianness;

public class Launcher {

    private static String algo;

    public static void main(String[] args) {
        String configPath;
        if (Constants.baseDir.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.baseDir + "/../../config/config.properties";
        } else {
            configPath = Constants.baseDir + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            MainChain mc = new MainChain();
            Miner miner = new Miner();
            File chainFile = new File(configProps.getProperty("datadir") + "/chain.dat");
            if (!chainFile.exists()) {
                GenesisChainServerEndpoint gb = new GenesisChainServerEndpoint();
                gb.initChain();
                EndpointManager endpointManager = new EndpointManager();
                endpointManager.connectAsClient("init chain");
                MainChain.difficulty = 5;
            } else if (chainFile.exists()) {
                mc.readKeyRing();
                mc.readAddressBook();
                mc.readBlockChain();
                mc.readTxPool();
                if (BlockChain.blockChain.size() <= 1) {
                    MainChain.difficulty = 5;
                } else {
                    mc.calculateDifficulty();
                }
            }
            System.out.println("\n");
            System.out.println("Welcome to the light-weight, PoC, java implementation of TeacHingChain!\n");
            while (true) {
                EndpointManager endpointManager = new EndpointManager();
                Scanner input = new Scanner(System.in);
                System.out.println("\n");
                System.out.println("Enter command:\n");
                String cliInput = input.nextLine();
                switch (cliInput) {
                    case "send tx": {
                        Scanner txData = new Scanner(System.in);
                        System.out.println("\n");
                        System.out.println("Please enter to address: \n");
                        String fromAddress = AddressBook.addressBook.get(0).toString();
                        String toAddress = txData.nextLine();
                        System.out.println("\n");
                        System.out.println("Please enter amount: \n");
                        float amountInput = txData.nextFloat();
                        try {
                            if (amountInput > MainChain.balance) {
                                throw new MainChain.InsufficientBalanceException("Insufficient balance exception occurred! See log for details\n");
                            } else {
                                mc.sendTx(fromAddress, toAddress, amountInput);
                            }
                        } catch (MainChain.InsufficientBalanceException ibe) {
                            WalletLogger.logException(ibe, "warning", WalletLogger.getLogTimeStamp() + "Insufficient balance for tx " + SHA256.SHA256HashString(fromAddress + toAddress + amountInput) + "\nAmount: " + amountInput + " Balance: " + MainChain.balance + "\n" + WalletLogger.exceptionStacktraceToString(ibe));
                        }
                        break;
                    }
                    case "view chain": {
                        mc.viewBlockChain();
                        break;
                    }
                    case "get chain index": {
                        System.out.println("Index: " + mc.getIndexOfBlockChain());
                        break;
                    }
                    case "get genesis hash": {
                        if (!BlockChain.blockChain.isEmpty()) {
                            System.out.println("Genesis hash: " + mc.getGenesisHash());
                        } else {
                            System.out.println("No block found on chain!");
                        }
                        break;
                    }
                    case "get best block hash": {
                        if (!BlockChain.blockChain.isEmpty()) {
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
                            System.out.println("Requested block doesn't exist! See log for details\n");
                            WalletLogger.logException(iobe, "warning", WalletLogger.getLogTimeStamp() + " Index out of bound exception occurred trying to fetch a block! See below:\n" + WalletLogger.exceptionStacktraceToString(iobe));
                        }
                        break;
                    }
                    case "get balance": {
                        mc.calculateBalance();
                        System.out.println("\n");
                        System.out.println("Current balance:\n" + MainChain.balance);
                        break;
                    }
                    case "mine": {
                        mc.readBlockChain();
                        Scanner howMany = new Scanner(System.in);
                        System.out.println("\n");
                        System.out.println("Enter number of blocks to mine: \n");
                        int howManyBlocks = howMany.nextInt();
                        int numBlocksMined = 0;
                        Random algoSelector = new Random();
                        int algoIndex = algoSelector.nextInt(1);
                        if (algoIndex == 0) {
                            algo = "sha256";
                        } else if (algoIndex == 1) {
                            algo = "sha512";
                        }
                        Random rand = new Random();
                        while (howManyBlocks > numBlocksMined) {
                            byte[] cbTxHashBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray(MainChain.getHex((Constants.cbAddress + AddressBook.addressBook.get(0).toString() + MainChain.nSubsidy).getBytes())));
                            String cbTxHash = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(cbTxHashBytes)));
                            mc.writeTxPool(Constants.cbAddress, AddressBook.addressBook.get(rand.nextInt(AddressBook.addressBook.size())).toString(), MainChain.nSubsidy, cbTxHash);                            mc.readTxPool();
                            int indexValue = BlockChain.blockChain.size();
                            long timeStamp = mc.getUnixTimestamp();
                            if (BlockChain.blockChain.size() < 2 && TxPoolArray.TxPool.size() == 1) {
                                mc.readBlockChain();
                                MainChain.difficulty = 5;
                                String toAddress = AddressBook.addressBook.get(0).toString();
                                String previousHash;
                                if (BlockChain.blockChain.size() == 1) {
                                    previousHash = mc.getGenesisHash();
                                } else {
                                    previousHash = mc.getPreviousBlockHash();
                                }
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.cbAddress + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, txs[0], 0L, previousHash, algo, MainChain.difficulty, amount);
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;
                                TxPoolArray.TxPool.remove(0);
                                mc.overwriteTxPool();
                            } else if (BlockChain.blockChain.size() >= 2 && TxPoolArray.TxPool.size() <= 1) {
                                mc.readBlockChain();
                                MainChain.difficulty = mc.calculateDifficulty();
                                String toAddress = AddressBook.addressBook.get(0).toString();
                                String previousHash = mc.getPreviousBlockHash();
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.cbAddress + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, txs[0], 0L, previousHash, algo, MainChain.difficulty, amount);
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;
                                TxPoolArray.TxPool.remove(0);
                                mc.overwriteTxPool();
                            } else if (BlockChain.blockChain.size() < 2 && TxPoolArray.TxPool.size() > 1) {
                                mc.readBlockChain();
                                MainChain.difficulty = 5;
                                String toAddress = AddressBook.addressBook.get(0).toString();
                                String previousHash = mc.getPreviousBlockHash();
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.cbAddress + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, txs[0], 0L, previousHash, algo, MainChain.difficulty, amount);
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;
                            }

                            // not ready to send tx's yet
                             /*} else {
                                mc.calculateBalance();
                                String parsedToAddress = null;
                                String parsedFromAddress = null;
                                String parsedTxHash = null;
                                float parsedAmount = 0;
                                String previousHash = mc.getPreviousBlockHash();
                                indexValue = BlockChain.blockChain.size();
                                timeStamp = mc.getUnixTimestamp();
                                MainChain.difficulty = mc.calculateDifficulty();
                                mc.readTxPool();
                                byte[] cbTxBytes = (MainChain.hexStringToByteArray(Constants.cbAddress + AddressBook.addressBook.get(0).toString() + MainChain.nSubsidy));
                                String[] txs = new String[TxPoolArray.TxPool.size()];
                                for (int i = 0; i < TxPoolArray.TxPool.size(); i++) {
                                    String parsedTx = TxPoolArray.TxPool.get(i).toString();
                                    JsonElement txParser = new JsonParser().parse(parsedTx);
                                    JsonObject jsonObject = txParser.getAsJsonObject();
                                    JsonElement toAddressElement = jsonObject.get("to address");
                                    parsedToAddress = toAddressElement.getAsString();
                                    JsonElement fromAddressElement = jsonObject.get("from address");
                                    parsedFromAddress = fromAddressElement.getAsString();
                                    JsonElement txHashElement = jsonObject.get("tx hash");
                                    parsedTxHash = txHashElement.getAsString();
                                    txs[i] = parsedTxHash;
                                    JsonElement amountElement = jsonObject.get("parsedAmount");
                                    parsedAmount = amountElement.getAsFloat();
                                }
                            }
                        }
                        */
                        }
                        break;
                    }
                    case "generate private key": {
                        mc.generatePrivateKey();
                        break;
                    }

                    case "generate address": {
                        Scanner which = new Scanner(System.in);
                        int whichKey = which.nextInt();
                        String address = mc.generateAddress(whichKey);
                        System.out.println("Address generated: " + address);
                        break;
                    }
                    case "view tx pool": {
                        mc.getTxPool();
                        break;
                    }
                    case "view difficulty": {
                        mc.getDifficulty();
                        break;
                    }
                    case "sync": {
                        endpointManager.connectAsClient("sync");
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
                        for (Object o : KeyRing.keyRing) {
                            System.out.println("Private key: " + o.toString());
                        }
                        break;
                    }
                    case "view address": {
                        System.out.println("Address: " + AddressBook.addressBook.get(0));
                        break;
                    }
                    case "test merkle hash": {
                        // txid A
                        byte[] A = MainChain.hexStringToByteArray("b1fea52486ce0c62bb442b530a3f0132b826c74e473d1f2c220bfa78111c5082");
                        System.out.println(MainChain.getHex(A));
                        // txid A byte-swapped
                        byte[] A_little = swapEndianness(A);
                        System.out.println(MainChain.getHex(A_little));

                        // txid B
                        byte[] B = MainChain.hexStringToByteArray("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16");
                        System.out.println(MainChain.getHex(B));

                        // txid B byte-swapped
                        byte[] B_little = swapEndianness(B);
                        System.out.println(MainChain.getHex(B_little));

                        // txid A + B concatenated
                        byte[] AB_little = Arrays.copyOf(A_little, A_little.length + B_little.length);
                        System.arraycopy(B_little, 0, AB_little, A_little.length, B_little.length);
                        System.out.println(MainChain.getHex(AB_little));

                        // double hash of byte-swapped concatenated A+B
                        byte[] ABdoubleHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(AB_little));
                        System.out.println(MainChain.getHex(ABdoubleHash));

                        // print result byte-swapped back to big-endian
                        byte[] result = swapEndianness(ABdoubleHash);
                        System.out.println(Arrays.toString(result));
                        System.out.println(MainChain.getHex(result));
                    }
                    case "test cargo": {

                        break;
                    }


                    case "quit": {
                        System.exit(1);
                        break;
                    }
                }
            }
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " Unable to read/write config file at startup! See details below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp() + " Failed to decode block! See details below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
    }
}






/*
--------------------------BLOCK DETAILS--------------------------


Mined block hash:
00000c754ed334ced901c1be18403baffc34ff9512dcdce9b2b947e90cfa2e41


Index:
0


Unix time stamp:
1559271704446


Data:
TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!


Tx hash: 5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53


Merkle hash: 5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53


Previous: none


Nonce:
540875


Difficulty:
5




 */