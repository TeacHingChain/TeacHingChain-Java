package com.thc.blockchain.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.addresses.AddressBook;
import com.thc.blockchain.wallet.ChainBuilder;
import com.thc.blockchain.wallet.HashArray;
import com.thc.blockchain.wallet.MainChain;
import com.thc.blockchain.wallet.TxPoolArray;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static com.thc.blockchain.network.Constants.baseDir;
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
        ChainBuilder cb = new ChainBuilder();
        MainChain mc = new MainChain();
        String action = actionEvent.getActionCommand();
        if (action.equals("mine")) {
            int numBlocksMined = 0;
            int howManyBlocks = Integer.parseInt(String.valueOf(numberOfBlocks.getText()));
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
                try {
                    cb.readTxPool();
                    cb.getTxPool();
                    File tempFile = new File(baseDir + "/tx-pool.dat");
                    if (!tempFile.exists() && HashArray.hashArray.size() >= 3) {
                        TxPoolArray txPool = new TxPoolArray();
                        difficulty = mc.calculateDifficulty();
                        long indexValue = (HashArray.hashArray.size());
                        long timeStamp = mc.getUnixTimestamp();
                        String sendKey = "";
                        String recvKey = "";
                        String previousHash = mc.getPreviousBlockHash();
                        String toAddress = AddressBook.addressBook.get(0).toString();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        numBlocksMined++;
                        TimeUnit.SECONDS.sleep(5);
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
                        String sendKey = "";
                        String recvKey = "";
                        String toAddress = AddressBook.addressBook.get(0).toString();
                        String previousHash = mc.getPreviousBlockHash();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        numBlocksMined++;
                        TimeUnit.SECONDS.sleep(5);
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
                        String sendKey = "";
                        String recvKey = "";
                        String previousHash = mc.getPreviousBlockHash();
                        String toAddress = AddressBook.addressBook.get(0).toString();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
                        numBlocksMined++;
                        TimeUnit.SECONDS.sleep(5);
                    } else {
                        mc.readBlockChain();
                        String mostRecentBlock = (HashArray.hashArray.get(HashArray.hashArray.size() - 1).toString());
                        JsonElement jsonElement = new JsonParser().parse(mostRecentBlock);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement indexElement = jsonObject.get("index");
                        int indexValue = indexElement.getAsInt() + 1;
                        long timeStamp = mc.getUnixTimestamp();
                        String sendKey = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size());
                        System.out.println("Send key says: \n" + sendKey);
                        String recvKey = (String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 1);
                        System.out.println("recv key says: \n" + recvKey);
                        String amountToStr = ((String) TxPoolArray.TxPool.get(TxPoolArray.TxPool.size() - TxPoolArray.TxPool.size() + 2));
                        float amount = Float.parseFloat(amountToStr);
                        System.out.println("Amount key says: \n" + amount);
                        String previousHash = mc.getPreviousBlockHash();
                        String toAddress = AddressBook.addressBook.get(0).toString();
                        String txHash = SHA256.generateSHA256Hash(indexValue + Constants.cbAddress + toAddress);
                        miner.mine(indexValue, timeStamp, Constants.cbAddress, toAddress, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
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
                        TimeUnit.SECONDS.sleep(5);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Inability to read/write a file OR an interruption has caused an exception!\n");
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