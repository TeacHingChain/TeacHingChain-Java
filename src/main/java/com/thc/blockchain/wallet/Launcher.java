package com.thc.blockchain.wallet;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.util.Miner;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import java.util.Random;

import static com.thc.blockchain.wallet.MainChain.minerKey;

public class Launcher extends JPanel { // extending JPanel to build GUI instead of pseudo-cli-parsing
    public static int numBlocksMined;
    public static String algo;

    public static void main(String[] args) throws IOException, InterruptedException {
        MainChain mc = new MainChain();
        ChainBuilder cb = new ChainBuilder();
        Miner miner = new Miner();
        File chainFile = new File("chain.dat");
        if (!chainFile.exists()) {
            GenesisBlock gb = new GenesisBlock();
            gb.initChain();
        } else {
            mc.readAddressKey();
            mc.readSendKey();
            mc.readMinerKey();
            mc.readBlockChain();
            cb.readTxPool();
            String difficultyStr = (String) HashArray.hashArray.get(HashArray.hashArray.size() - 2);
            char[] difficultyCharArray = difficultyStr.toCharArray();
            String difficultyIntAsString = String.valueOf(difficultyCharArray[12]);
            MainChain.difficulty = Integer.parseInt(difficultyIntAsString);


            System.out.println("\n");
            System.out.println("Welcome to the light-weight, PoC, java implementation of the THC Block Chain!\n");
            try {
                MainChain.indexAtStart = (HashArray.hashArray.size() / 12);
                cb.readTxPool();
            } catch (NullPointerException npe) {
                System.out.println("\n");
                System.out.println("ERROR! chain.dat not found!\n");
            }

            boolean loop = true;

            while (loop) {
                mc.checkForChainUpdates();
                Scanner input = new Scanner(System.in);
                System.out.println("\n");
                System.out.println("Enter command:\n");
                String cliInput = input.nextLine();

                if (cliInput.contentEquals("build genesis block")) {
                    Scanner ts = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Enter difficulty:\n");
                    int enteredDifficulty = ts.nextInt();
                    String txHash = SHA256.generateSHA256Hash(0 + "" + "");
                    ChainBuilder buildGenesis = new ChainBuilder(0, System.currentTimeMillis(), "", "", minerKey, txHash, 0L, null, enteredDifficulty, MainChain.nSubsidy);

                } else if (cliInput.contentEquals("initialize chain")) {
                    Scanner parameters = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Enter unix time stamp:\n");
                    System.out.println("\n");
                    long enteredTime = parameters.nextLong();
                    Scanner ts = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Enter nonce:\n");
                    System.out.println("\n");
                    long enteredNonce = parameters.nextLong();
                    Scanner gh = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Enter genesis hash:\n");
                    System.out.println("\n");
                    String enteredHash = gh.nextLine();
                    System.out.println("\n");
                    System.out.println("Enter difficulty:\n");
                    System.out.println("\n");
                    int enteredDifficulty = ts.nextInt();
                    String txHash = SHA256.generateSHA256Hash(0 + "" + "");
                    mc.setGenesisHash(0, enteredTime, "", "", minerKey, txHash, enteredNonce, null, enteredHash, enteredDifficulty, MainChain.nSubsidy);

                } else if (cliInput.contentEquals("send tx")) {
                    Scanner txData = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Please enter receive key\n");
                    String keyInput = txData.nextLine();
                    System.out.println("\n");
                    System.out.println("Please enter amount: \n");
                    float amountInput = txData.nextFloat();
                    if (amountInput > MainChain.balance) {
                        System.out.println("ERROR! You don't have enough coins to send this tx, please check your balance and try again..\n");
                    } else {
                        mc.sendTx(MainChain.sendKey, keyInput, amountInput);
                    }

                } else if (cliInput.contentEquals("view chain")) {
                    mc.getBlockChain();

                } else if (cliInput.contentEquals("get chain index")) {
                    mc.getIndexOfBlockChain();

                } else if (cliInput.contentEquals("get genesis hash")) {

                    if (HashArray.hashArray.size() >= 12) {
                        mc.getGenesisHash();

                    } else {
                        System.out.println("No block found on chain!");
                    }

                } else if (cliInput.contentEquals("get current hash")) {

                    if (HashArray.hashArray.size() >= 12) {
                        mc.getCurrentHash();

                    } else {
                        System.out.println("No block found on chain!");
                    }

                } else if (cliInput.contentEquals("get block at index")) {
                    Scanner indexInt = new Scanner(System.in);
                    System.out.println("\n");
                    System.out.println("Enter index value:\n");
                    long indexValue = indexInt.nextLong();
                    mc.getBlockAtIndex(indexValue);

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
                        File tempFile = new File("tx-pool.dat");
                        if (!tempFile.exists()) {
                            TxPoolArray txPool = new TxPoolArray();
                            long indexValue = (HashArray.hashArray.size() / 12);
                            long timeStamp = mc.getUnixTimestamp();
                            String sendKey = "";
                            String recvKey = "";
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + sendKey + recvKey);
                            miner.mine(indexValue, timeStamp, sendKey, recvKey, minerKey, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            numBlocksMined++;

                        } else if (TxPoolArray.TxPool == null) {
                            TxPoolArray txpool = new TxPoolArray();

                        } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty()) {
                            long indexValue = (HashArray.hashArray.size() / 12);
                            long timeStamp = mc.getUnixTimestamp();
                            String sendKey = "";
                            String recvKey = "";
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + sendKey + recvKey);
                            miner.mine(indexValue, timeStamp, sendKey, recvKey, minerKey, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            numBlocksMined++;

                        } else {
                            long indexValue = (HashArray.hashArray.size() / 12);
                            long timeStamp = mc.getUnixTimestamp();
                            String sendKey = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size());
                            System.out.println("Send key says: \n" + sendKey);
                            String recvKey = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 1);
                            System.out.println("recv key says: \n" + recvKey);
                            String amountToStr = ((String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 2));
                            float amount = Float.parseFloat(amountToStr);
                            System.out.println("Amount key says: \n" + amount);
                            String previousHash = mc.getPreviousBlockHash();
                            String txHash = SHA256.generateSHA256Hash(indexValue + sendKey + recvKey);
                            miner.mine(indexValue, timeStamp, sendKey, recvKey, minerKey, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                            TxPoolArray.TxPool.remove(sendKey);
                            System.out.println("removing: \n" + sendKey);
                            TxPoolArray.TxPool.remove(recvKey);
                            System.out.println("removing: \n" + recvKey);
                            TxPoolArray.TxPool.remove(amountToStr);
                            System.out.println("removing: \n" + amountToStr);
                            TxPoolArray.TxPool.remove(txHash);
                            System.out.println("removing: \n" + txHash);
                            TxPoolArray.TxPool.remove("------------------------------------------------------------------------------------");
                            System.out.println("removing: \n" + "------------------------------------------------------------------------------------");
                            cb.overwriteTxPool();
                            numBlocksMined++;

                        }
                    }
                } else if (cliInput.contentEquals("generate address key")) {
                    mc.generateAddressKey();

                } else if (cliInput.contentEquals("generate receive key")) {
                    mc.generateRecvKey();

                } else if (cliInput.contentEquals("generate send key")) {
                    mc.generateSendKey();

                } else if (cliInput.contentEquals("generate miner key")) {
                    mc.generateMinerKey();

                } else if (cliInput.contentEquals("get address key")) {
                    mc.getAddressKey();

                } else if (cliInput.contentEquals("get receive key")) {
                    mc.getRecvKey();

                } else if (cliInput.contentEquals("get send key")) {
                    mc.getSendKey();

                } else if (cliInput.contentEquals("get miner key")) {
                    mc.getMinerKey();

                } else if (cliInput.contentEquals("get tx pool")) {
                    cb.getTxPool();

                } else if (cliInput.contentEquals("view difficulty")) {
                    mc.getDifficulty();

                } else if (cliInput.contentEquals("quit")) {
                    loop = false;
                    System.exit(1);
                }
            }
        }
    }
}



/*
--------------------------------------------BLOCK DETAILS--------------------------------------------


Index:
0 (Genesis Block)


currentTimeMillis:
1554426628488


Data:
test genesis block


Previous hash:
null


Nonce:
678965


Difficulty:
5


Hash found!
00000434001f1cb4164b9b97d0388a18784ae610d3034b1c9b469f9bd3f8c436

test recv key:
efd3c8ce074d7861f8e54c18be9fd685afd2bef9e154dd867eb90ed9e5a7c884

my send key:
b137fda9d8f66b8251bc9610170e28c9bbd8388968cb028bb3a61b7295834e57

 */