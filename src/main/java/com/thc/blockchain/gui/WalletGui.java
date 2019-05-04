package com.thc.blockchain.gui;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.util.Miner;
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

import static com.thc.blockchain.wallet.MainChain.minerKey;

public class WalletGui extends JFrame implements ActionListener {

    private static String algo;
    private static JTextField numberOfBlocks;
    private static JTextField txAmount;
    private static JTextField txRecvKey;


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
        txRecvKey = new JTextField(32);

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
        newPanel.add(txRecvKey, constraints);

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
        newPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "TeacHingChain Wallet"));
        constraints.anchor = GridBagConstraints.CENTER;

        add(newPanel);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        String action = actionEvent.getActionCommand();
        if (action.equals("mine")) {
            try {
                MiningPane mp = new MiningPane();
                Miner miner = new Miner();
                Random algoSelector = new Random();
                MainChain mc = new MainChain();
                ChainBuilder cb = new ChainBuilder();
                int algoIndex = algoSelector.nextInt(2);
                if (algoIndex == 0) {
                    algo = "sha256";
                } else if (algoIndex == 1) {
                    algo = "sha512";
                } else if (algoIndex == 2) {
                    algo = "scrypt";
                }
                int numBlocksMined = 0;
                int howManyBlocks = Integer.parseInt(String.valueOf(numberOfBlocks.getText()));
                System.out.println(howManyBlocks);
                while (howManyBlocks > numBlocksMined) {
                    cb.readTxPool();
                    File tempFile = new File("tx-pool.dat");
                    if (!tempFile.exists()) {
                        TxPoolArray txPool = new TxPoolArray();
                        long indexValue = (HashArray.hashArray.size() / 12);
                        long timeStamp = mc.getUnixTimestamp();
                        String sendKey = "";
                        String recvKey = "";
                        String previousHash = mc.getPreviousBlockHash();
                        String txHash = SHA256.generateSHA256Hash(indexValue + sendKey + recvKey);
                        numBlocksMined++;
                        miner.mine(indexValue, timeStamp, sendKey, recvKey, minerKey, txHash, 0L, previousHash, algo, MainChain.difficulty, MainChain.nSubsidy);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (action.equals("send tx")) {
            MainChain mc = new MainChain();
            String enteredRecvKey = txRecvKey.getText();
            int enteredAmount = Integer.parseInt(txAmount.getText());
            if (enteredAmount > MainChain.balance) {
                System.out.println("ERROR! You do not have enough coins for this transaction, check your balance and try again!");
            } else {
                mc.sendTx(MainChain.sendKey, enteredRecvKey, enteredAmount);
            }
        }
    }

    public void paint(Graphics g){
        g.drawString(String.valueOf(Miner.hashRate), 10, 10);
    }
}