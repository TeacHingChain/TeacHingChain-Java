package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.gui.WalletGui;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.server.endpoints.GenesisChainServerEndpoint;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;

import javax.swing.*;
import javax.websocket.DecodeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class Launcher {

    public static void main(String[] args) {
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
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
                MainChain.targetHex = Constants.GENESIS_TARGET;
                mc.readBlockChain();
                MainChain.difficulty = 1;
            } else if (chainFile.exists()) {
                mc.readKeyRing();
                mc.readAddressBook();
                mc.readBlockChain();
                mc.readTxPool();
                if (BlockChain.blockChain.size() == 1) {
                    GenesisBlock gb = new GenesisBlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain()));
                    long deltaT = (System.currentTimeMillis() / 1000) - ((Long.parseLong(gb.getTimeStamp()) / 1000));
                    String previousTarget = gb.getTarget();
                    MainChain.targetHex = MainChain.getHex(MainChain.calculateTarget(deltaT, previousTarget).toBigInteger().toByteArray());
                } else {
                    Block mrb = new BlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain()));
                    long deltaT = (System.currentTimeMillis() / 1000) - ((Long.parseLong(mrb.getTimeStamp()) / 1000));
                    String previousTarget = mrb.getTarget();
                    MainChain.targetHex = MainChain.getHex(MainChain.calculateTarget(deltaT, previousTarget).toBigInteger().toByteArray());
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
                        String fromAddress = AddressBook.addressBook.get(0);
                        String toAddress = txData.nextLine();
                        System.out.println("\n");
                        System.out.println("Please enter amount: \n");
                        float amountInput = txData.nextFloat();
                        try {
                            if (amountInput > MainChain.balance) {
                                throw new MainChain.InsufficientBalanceException();
                            } else {
                                mc.sendTx(fromAddress, toAddress, amountInput);
                            }
                        } catch (MainChain.InsufficientBalanceException ibe) {
                            WalletLogger.logException(ibe, "warning", WalletLogger.getLogTimeStamp() + "Insufficient balance for tx "
                                    + SHA256.SHA256HashString(fromAddress + toAddress + amountInput) + "\nAmount: "
                                    + amountInput + " Balance: " + MainChain.balance + "\n"
                                    + WalletLogger.exceptionStacktraceToString(ibe));
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
                            System.out.println("Block at index " + indexValue + ": "
                                    + mc.getBlockAtIndex(Math.toIntExact(indexValue)));
                        } catch (IndexOutOfBoundsException iobe) {
                            System.out.println("Requested block doesn't exist! See log for details\n");
                            WalletLogger.logException(iobe, "warning", WalletLogger.getLogTimeStamp() +
                                    " Index out of bound exception occurred trying to fetch a block! See below:\n"
                                    + WalletLogger.exceptionStacktraceToString(iobe));
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
                        //Random algoSelector = new Random();
                        String algo = "sha256";
                        Random rand = new Random();
                        while (howManyBlocks > numBlocksMined) {
                            byte[] cbTxHashBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray
                                    (MainChain.getHex((Constants.CB_ADDRESS + AddressBook.addressBook.get(0)
                                            + MainChain.nSubsidy).getBytes())));
                            String cbTxHash = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(cbTxHashBytes)));
                            mc.writeTxPool(Constants.CB_ADDRESS, AddressBook.addressBook.get(rand.nextInt(
                                    AddressBook.addressBook.size())), MainChain.nSubsidy, cbTxHash);
                            mc.readTxPool();
                            int indexValue = BlockChain.blockChain.size();
                            long timeStamp = mc.getUnixTimestamp();
                            if (BlockChain.blockChain.size() < 2 && TxPoolArray.TxPool.size() == 1) {
                                mc.readBlockChain();
                                MainChain.difficulty = 1;
                                String toAddress = AddressBook.addressBook.get(0);
                                String previousHash;
                                if (BlockChain.blockChain.size() == 1) {
                                    previousHash = mc.getGenesisHash();
                                } else {
                                    previousHash = mc.getPreviousBlockHash();
                                }
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.CB_ADDRESS + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.CB_ADDRESS, toAddress, txs, txs[0], 0L, previousHash, algo,
                                            MainChain.getTargetHex(), amount);
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp()
                                            + " Interrupted exception occurred during mining operation! See below:\n"
                                            + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;
                                TxPoolArray.TxPool.remove(0);
                                mc.overwriteTxPool();

                            } else if (BlockChain.blockChain.size() >= 2 && TxPoolArray.TxPool.size() <= 1) {
                                mc.readBlockChain();
                                String toAddress = AddressBook.addressBook.get(0);
                                String previousHash = mc.getPreviousBlockHash();
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.CB_ADDRESS + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.CB_ADDRESS, toAddress, txs, txs[0], 0L, previousHash, algo,
                                            MainChain.getTargetHex(), amount);
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp()
                                            + " Interrupted exception occurred during mining operation! See below:\n"
                                            + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;
                                TxPoolArray.TxPool.remove(0);
                                mc.overwriteTxPool();
                            } else if (BlockChain.blockChain.size() < 2 && TxPoolArray.TxPool.size() > 1) {
                                mc.readBlockChain();
                                MainChain.difficulty = 5;
                                String toAddress = AddressBook.addressBook.get(0);
                                String previousHash = mc.getPreviousBlockHash();
                                float amount = MainChain.nSubsidy;
                                byte[] txHashBytes = (Constants.CB_ADDRESS + toAddress + amount).getBytes();
                                byte[] txHash = SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes));
                                String[] txs = {MainChain.getHex(txHash)};
                                try {
                                    miner.mine(indexValue, timeStamp, Constants.CB_ADDRESS, toAddress, txs, txs[0], 0L, previousHash, algo,
                                            MainChain.getTargetHex(), amount);
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp()
                                            + " Interrupted exception occurred during mining operation! See below:\n"
                                            + WalletLogger.exceptionStacktraceToString(ie));
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
                                byte[] cbTxBytes = (MainChain.hexStringToByteArray(Constants.CB_ADDRESS
                                + AddressBook.addressBook.get(0).toString() + MainChain.nSubsidy));
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
                            WalletLogger.logException(e, "warning", WalletLogger.getLogTimeStamp()
                                    + " An exception occurred while starting GUI! See below:\n"
                                    + WalletLogger.exceptionStacktraceToString(e));
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
                    case "test hash rate": {
                        System.out.println("Network hash rate: " + new Miner().calculateNetworkHashRate());
                        break;
                    }
                    case "get peer count": {
                        System.out.println("Number of connected peers: " + NodeManager.getPeerCount());
                        break;
                    }
                    case "test targetHex": {
                        double testDifDouble = 1;
                        long start = System.nanoTime() / 1000000000;
                        BigDecimal bigIntTarget = new BigDecimal(new BigInteger(
                                "00000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", 16));
                        long nonce = 0L;
                        BigInteger randomValue = new BigInteger(256, new Random());
                        long deltaS;
                        TimeUnit.MILLISECONDS.sleep(10);
                        System.out.println("Hashing...\n");
                        while (true) {
                            double adjustmentFactor = 0;
                            long updatedTime = System.nanoTime() / 1000000000;
                            deltaS = (updatedTime - start);
                            long targetTime = 60;
                            String hasedHeaderHex = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray((
                                    randomValue + "block" + "header" + nonce).getBytes())));
                            if (new BigDecimal(new BigInteger(hasedHeaderHex, 16)).subtract(bigIntTarget).compareTo(
                                    new BigDecimal(0)) <= 0) {
                                System.out.println("Start targetHex: " + bigIntTarget);
                                System.out.println("Start targetHex hex: " + MainChain.getHex(bigIntTarget.toBigInteger().toByteArray()));
                                if (deltaS < targetTime) {
                                    BigDecimal deltaTargetTime = new BigDecimal(String.valueOf(targetTime - deltaS));
                                    adjustmentFactor = deltaTargetTime.multiply(new BigDecimal(String.valueOf((
                                            deltaTargetTime.doubleValue() / targetTime) / 6))).doubleValue();
                                    System.out.println("delta2T: " + deltaTargetTime.doubleValue());
                                    testDifDouble += adjustmentFactor;
                                    bigIntTarget = bigIntTarget.multiply(new BigDecimal(String.valueOf(1 / adjustmentFactor)));
                                } else if (deltaS > targetTime) {
                                    BigDecimal deltaTargetTime = new BigDecimal(String.valueOf(deltaS - targetTime));
                                    adjustmentFactor = deltaTargetTime.multiply(new BigDecimal(String.valueOf((
                                            deltaTargetTime.doubleValue() / targetTime) / 6))).doubleValue();
                                    System.out.println("delta2T: " + deltaTargetTime);
                                    bigIntTarget = bigIntTarget.multiply(new BigDecimal(String.valueOf(adjustmentFactor)));
                                    testDifDouble -= adjustmentFactor;
                                    if (testDifDouble <= 1) {
                                        testDifDouble = 1;
                                        bigIntTarget = new BigDecimal(new BigInteger(
                                                "00000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", 16));
                                    }
                                }
                                System.out.println("Found hash: " + hasedHeaderHex);
                                System.out.println("Actual value: " + new BigInteger(hasedHeaderHex, 16));
                                System.out.println("Adjustment factor: " + adjustmentFactor);
                                System.out.println("deltaS: " + deltaS);
                                System.out.println("test difficulty: " + testDifDouble);
                                System.out.println("New targetHex: " + bigIntTarget);
                                System.out.println("New targetHex hex: " + MainChain.getHex(bigIntTarget.toBigInteger().toByteArray()));
                                System.out.println("Continue? \n");
                                String cont = new Scanner(System.in).nextLine();
                                if (cont.contentEquals("y")) {
                                    randomValue = new BigInteger(256, new Random());
                                    start = System.nanoTime() / 1000000000;
                                } else {
                                    break;
                                }
                            } else {
                                nonce++;
                            }
                        }
                        break;
                    }
                    case "get target": {
                        System.out.println("Target: " + MainChain.getTargetHex());
                        break;
                    }
                    case "quit": {
                        System.exit(1);
                        break;
                    }
                }
            }
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp()
                    + " Unable to read/write config file at startup! See details below:\n"
                    + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp()
                    + " Failed to decode block! See details below:\n" + WalletLogger.exceptionStacktraceToString(de));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
