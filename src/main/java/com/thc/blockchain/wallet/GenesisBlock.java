package com.thc.blockchain.wallet;

import java.io.*;

import com.thc.blockchain.algos.SHA256;

public class GenesisBlock { // this is where the hardcoded genesis block goes, it is only called in the event of the chain.dat not existing
                            // which is why it includes checks for keys and the actual initialization/serialization of the ArrayList object
    public void initChain() {
        MainChain mc = new MainChain();
        ChainBuilder cb = new ChainBuilder();
        long genesisIndex = 0;
        long currentTimeMillis = System.currentTimeMillis();
        File addressKeyFile = new File("addressKey.dat");
        File recvKeyFile = new File("recvKey.dat");
        File minerKeyFile = new File("minerKey.dat");
        File sendKeyFile = new File("sendKey.dat");
        if (!addressKeyFile.exists()) {
            mc.generateAddressKey();
        } else if (!recvKeyFile.exists()) {
            mc.generateRecvKey();
        } else if (!minerKeyFile.exists()) {
            mc.generateMinerKey();
        } else if (!sendKeyFile.exists()) {
            mc.generateSendKey();
        } else if (!addressKeyFile.exists() && !recvKeyFile.exists() && !minerKeyFile.exists() && !sendKeyFile.exists()) {
            mc.generateAllKeys();
        }

        String recvKey = "";
        String minerKey = "";
        String sendKey = "";
        String txHash = SHA256.generateSHA256Hash(genesisIndex + sendKey + recvKey);
        long Nonce = 0;
        String previousBlockHash = null;
        String genesisHash = null;
        int difficulty = 5;
        float amount = MainChain.nSubsidy;
        String indexToStr = Long.toString(genesisIndex);
        String timeToStr = Long.toString(currentTimeMillis);
        String nonceToStr = Long.toString(Nonce);
        String difficultyToStr = Integer.toString(difficulty);
        String amountToStr = Float.toString(amount);
        boolean isGenesisValid = cb.isGenesisValid(genesisIndex, currentTimeMillis, sendKey, recvKey, minerKey, txHash, Nonce, previousBlockHash, genesisHash, difficulty, amount);
        if (isGenesisValid) {
            HashArray.hashArray.add("Index: " + indexToStr);
            HashArray.hashArray.add("Time stamp: " + timeToStr);
            HashArray.hashArray.add("Send key: " + sendKey);
            HashArray.hashArray.add("Receive key: " + recvKey);
            HashArray.hashArray.add("Miner key: " + minerKey);
            HashArray.hashArray.add("Tx Hash: " + txHash);
            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
            HashArray.hashArray.add("Nonce: " + nonceToStr);
            HashArray.hashArray.add("Previous " + previousBlockHash);
            HashArray.hashArray.add("Block hash: " + genesisHash);
            HashArray.hashArray.add("Difficulty: " + difficultyToStr);
            HashArray.hashArray.add(amountToStr);

            try {
                System.out.println("Trying to serialize chain.dat...\n");
                FileOutputStream fos = new FileOutputStream("chain.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(HashArray.hashArray);
                oos.close();
                fos.close();
                mc.readBlockChain();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
