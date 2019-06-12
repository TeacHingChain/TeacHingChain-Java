package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.decoders.TxDecoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.server.endpoints.GenesisChainServerEndpoint;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;

import javax.websocket.DecodeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
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
                MainChain.difficulty = 1;
            } else if (chainFile.exists()) {
                mc.readKeyRing();
                mc.readAddressBook();
                mc.readBlockChain();
                if (BlockChain.blockChain.size() > 1 && !new Consensus().validateChain()) {
                    System.out.println("A consensus error occurred while validating chain, See debug.log for details!\n");
                    System.exit(1);
                }
                if (BlockChain.blockChain.size() > 1) {
                    mc.readTxPool();
                    MainChain.setTargetHex(new BlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain())).getTarget());
                    MainChain.difficulty = (new BlockDecoder().decode(BlockChain.blockChain.get
                            (mc.getIndexOfBlockChain())).getDifficulty());
                } else {
                    mc.readTxPool();
                    MainChain.setTargetHex(new GenesisBlockDecoder().decode(BlockChain.blockChain.get(0)).getTarget());
                    MainChain.difficulty = Double.parseDouble(new GenesisBlockDecoder().decode(BlockChain.blockChain.get(0)).getDifficulty());
                }
            }
            System.out.println("\n");
            System.out.println("Welcome to the java implementation of TeacHingChain!\n");
            while (true) {
                Random rand = new Random();
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
                        double amountInput = txData.nextDouble();
                        try {
                            if (amountInput > MainChain.balance) {
                                throw new MainChain.InsufficientBalanceException();
                            } else {
                                mc.sendTx(System.currentTimeMillis(), fromAddress, toAddress, amountInput);
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
                        while (howManyBlocks > numBlocksMined) {
                            mc.readTxPool();
                            int indexValue = BlockChain.blockChain.size();
                            if (BlockChain.blockChain.size() < 5 && TxPoolArray.TxPool.size() == 0) {
                                mc.readBlockChain();
                                MainChain.difficulty = 1;
                                String previousHash = mc.getPreviousBlockHash();
                                double[] amounts = {MainChain.nSubsidy};
                                String[] txins = {Constants.CB_ADDRESS};
                                String[] txouts = {AddressBook.addressBook.get(0)};
                                String[] txs = {MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(
                                        MainChain.swapEndianness((System.currentTimeMillis() + Constants.CB_ADDRESS
                                                + AddressBook.addressBook.get(0) + MainChain.nSubsidy).getBytes()))))};
                                long[] timeStamps = {System.currentTimeMillis()};
                                try {
                                    miner.mine(indexValue, timeStamps, txins, txouts, txs, txs[0], 0L, previousHash, algo,
                                            MainChain.getTargetHex(), MainChain.difficulty, amounts);
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ie) {
                                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp()
                                            + " Interrupted exception occurred during mining operation! See below:\n"
                                            + WalletLogger.exceptionStacktraceToString(ie));
                                }
                                numBlocksMined++;

                            } else if (BlockChain.blockChain.size() >= 5 && TxPoolArray.TxPool.size() == 0) {
                                mc.readBlockChain();
                                String previousHash = mc.getPreviousBlockHash();
                                double[] amounts = {MainChain.nSubsidy};
                                String[] txins = {Constants.CB_ADDRESS};
                                String[] txouts = {AddressBook.addressBook.get(0)};
                                String[] txs = {MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(
                                        MainChain.swapEndianness((System.currentTimeMillis() + Constants.CB_ADDRESS
                                                + AddressBook.addressBook.get(0) + MainChain.nSubsidy).getBytes()))))};
                                long[] timeStamps = {System.currentTimeMillis()};

                                try {
                                    miner.mine(indexValue, timeStamps, txins, txouts, txs, txs[0], 0L, previousHash, algo,
                                            MainChain.getTargetHex(), MainChain.difficulty, amounts);
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
                    case "get previous hash": {
                        System.out.println(mc.getPreviousBlockHash());
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
                    case "view network hash rate": {
                        System.out.println("Network hash rate: " + new Miner().calculateNetworkHashRate());
                        break;
                    }
                    case "get peer count": {
                        System.out.println("Number of connected peers: " + NodeManager.getPeerCount());
                        break;
                    }
                    case "test pad string": {
                        Block decodedBlock = new BlockDecoder().decode(BlockChain.blockChain.get(6));
                        String testTarget = decodedBlock.getTarget();
                        String testPad = new Miner().leftPad(testTarget, 64, '0');
                        System.out.println("test padded string: " + testPad);
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
        }
    }
}
