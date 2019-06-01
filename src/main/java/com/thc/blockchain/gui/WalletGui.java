package com.thc.blockchain.gui;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;
import com.thc.blockchain.wallet.TxPoolArray;

import javax.swing.*;
import javax.websocket.DecodeException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.thc.blockchain.wallet.MainChain.difficulty;

public class WalletGui extends JFrame implements ActionListener {

    private static String algo;
    private static JTextField numberOfBlocks;
    private static JTextField txAmount;
    private static JTextField txToAddress;

    public WalletGui() {
        super("TeacHingChain Wallet");
        JLabel balanceLabel = new JLabel("Balance: " + MainChain.balance + " THC");
        JLabel amountLabel = new JLabel("Amount: ");
        JLabel recvKeyLabel = new JLabel("Receive key: ");
        JLabel numBlockToMine = new JLabel("Number of blocks: ");
        JButton sendTxButton = new JButton("Send Tx");
        JButton mineButton = new JButton("Mine");
        numberOfBlocks = new JTextField(3);
        txAmount = new JTextField(5);
        txToAddress = new JTextField(32);
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 10, 10);
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(balanceLabel);
        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(sendTxButton, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        newPanel.add(amountLabel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        newPanel.add(txAmount, constraints);
        constraints.gridx = 3;
        constraints.gridy = 1;
        newPanel.add(recvKeyLabel, constraints);
        constraints.gridx = 4;
        constraints.gridy = 1;
        newPanel.add(txToAddress, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.WEST;
        newPanel.add(mineButton, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.WEST;
        newPanel.add(numBlockToMine, constraints);
        constraints.gridx = 2;
        constraints.gridy = 2;
        newPanel.add(numberOfBlocks, constraints);
        mineButton.addActionListener(this);
        mineButton.setActionCommand("mine");
        sendTxButton.addActionListener(this);
        sendTxButton.setActionCommand("send tx");
        newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "TeacHingChain Wallet"));
        constraints.anchor = GridBagConstraints.CENTER;
        add(newPanel);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Miner miner = new Miner();
        MainChain mc = new MainChain();
        String action = actionEvent.getActionCommand();
        if (action.equals("mine")) {
            int howManyBlocks = Integer.parseInt(String.valueOf(numberOfBlocks.getText()));
            int numBlocksMined = 0;
            Random algoSelector = new Random();
            int algoIndex = algoSelector.nextInt(1);
            if (algoIndex == 0) {
                algo = "sha256";
            } else if (algoIndex == 1) {
                algo = "sha512";
            }
            while (howManyBlocks > numBlocksMined) {
                mc.readTxPool();
                mc.getTxPool();
                File tempFile = new File("/home/dev-environment/Desktop/java_random/TeacHingChain" + "/tx-pool.dat");
                if (!tempFile.exists() && BlockChain.blockChain.size() >= 3) {
                    TxPoolArray txPool = new TxPoolArray();
                    difficulty = mc.calculateDifficulty();
                    int indexValue = (BlockChain.blockChain.size());
                    long timeStamp = mc.getUnixTimestamp();
                    String previousHash = null;
                    try {
                        previousHash = mc.getPreviousBlockHash();
                    } catch (DecodeException e) {
                        e.printStackTrace();
                    }
                    String toAddress = AddressBook.addressBook.get(0).toString();
                    byte[] txHashBytes = (indexValue + Constants.cbAddress + toAddress).getBytes();
                    byte[] txHash = SHA256.SHA256HashByteArray(txHashBytes);
                    String[] txs = {MainChain.getHex(txHash)};
                    try {
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, MainChain.getHex(txHash), 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    }
                    numBlocksMined++;
                } else if (TxPoolArray.TxPool == null) {
                    TxPoolArray txpool = new TxPoolArray();
                } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && BlockChain.blockChain.size() >= 3) {
                    mc.calculateBalance();
                    int indexValue = BlockChain.blockChain.size();
                    long timeStamp = mc.getUnixTimestamp();
                    difficulty = mc.calculateDifficulty();
                    String toAddress = AddressBook.addressBook.get(0).toString();
                    String previousHash = null;
                    try {
                        previousHash = mc.getPreviousBlockHash();
                    } catch (DecodeException e) {
                        e.printStackTrace();
                    }
                    byte[] txHashBytes = (indexValue + Constants.cbAddress + toAddress).getBytes();
                    byte[] txHash = SHA256.SHA256HashByteArray(txHashBytes);
                    String[] txs = {MainChain.getHex(txHash)};
                    try {
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, MainChain.getHex(txHashBytes), 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    }
                    numBlocksMined++;
                } else if (tempFile.exists() && TxPoolArray.TxPool.isEmpty() && BlockChain.blockChain.size() < 3) {
                    mc.calculateBalance();
                    int indexValue = BlockChain.blockChain.size();
                    long timeStamp = mc.getUnixTimestamp();
                    difficulty = mc.calculateDifficulty();
                    String toAddress = AddressBook.addressBook.get(0).toString();
                    String previousHash = null;
                    try {
                        previousHash = mc.getPreviousBlockHash();
                    } catch (DecodeException e) {
                        e.printStackTrace();
                    }
                    byte[] txHashBytes = (indexValue + Constants.cbAddress + toAddress).getBytes();
                    byte[] txHash = SHA256.SHA256HashByteArray(txHashBytes);
                    String[] txs = {MainChain.getHex(txHash)};
                    try {
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, MainChain.getHex(txHashBytes), 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    }
                    numBlocksMined++;
                } else {
                    mc.calculateBalance();
                    int indexValue = BlockChain.blockChain.size();
                    long timeStamp = mc.getUnixTimestamp();
                    difficulty = mc.calculateDifficulty();
                    String fromAddress = TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size()).toString();
                    System.out.println("From address says: \n" + fromAddress);
                    String toAddress = TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 1).toString();
                    System.out.println("To address says: \n" + toAddress);
                    String amountToStr = ((String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 2));
                    float amount = Float.parseFloat(amountToStr);
                    System.out.println("Amount key says: \n" + amount);
                    String previousHash = null;
                    try {
                        previousHash = mc.getPreviousBlockHash();
                    } catch (DecodeException e) {
                        e.printStackTrace();
                    }
                    byte[] txHashBytes = (indexValue + Constants.cbAddress + toAddress).getBytes();
                    byte[] txHash = SHA256.SHA256HashByteArray(txHashBytes);
                    String[] txs = {MainChain.getHex(txHash)};
                    try {
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txs, MainChain.getHex(txHashBytes), 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        TxPoolArray.TxPool.remove(fromAddress);
                        System.out.println("removing: \n" + fromAddress);
                        TxPoolArray.TxPool.remove(toAddress);
                        System.out.println("removing: \n" + toAddress);
                        TxPoolArray.TxPool.remove(amountToStr);
                        System.out.println("removing: \n" + amountToStr);
                        TxPoolArray.TxPool.remove(txHash);
                        System.out.println("removing: \n" + txHash);
                        numBlocksMined++;
                        mc.overwriteTxPool();
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    }
                }
            }
        } else if (action.equals("send tx")) {
            String enteredRecvKey = txToAddress.getText();
            int enteredAmount = Integer.parseInt(txAmount.getText());
            if (enteredAmount > MainChain.balance) {
                System.out.println("ERROR! You do not have enough coins for this transaction, check your balance and try again!");
            } else {
                mc.sendTx(AddressBook.addressBook.get(0).toString(), enteredRecvKey, enteredAmount);
            }
        }
    }
}