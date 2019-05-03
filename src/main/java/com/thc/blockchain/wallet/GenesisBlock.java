package com.thc.blockchain.wallet;

import java.io.*;

import com.thc.blockchain.algos.SHA256;

class GenesisBlock { // this is where the hardcoded genesis block goes, it is only called in the event of the chain.dat not existing
                     // which is why it includes checks for keys and the actual initialization/serialization of the chain ArrayList object
    void initChain() {
        MainChain mc = new MainChain();
        ChainBuilder cb = new ChainBuilder();
        long genesisIndex = 0;
        File addressKeyFile = new File("addressKey.dat");
        File recvKeyFile = new File("recvKey.dat");
        File minerKeyFile = new File("minerKey.dat");
        File sendKeyFile = new File("sendKey.dat");
        if (!addressKeyFile.exists() && !recvKeyFile.exists() && !minerKeyFile.exists() && !sendKeyFile.exists()) {
            mc.generateAllKeys();
        } else if (!addressKeyFile.exists()) {
            mc.generateAddressKey();
        } else if (!recvKeyFile.exists()) {
            mc.generateRecvKey();
        } else if (!minerKeyFile.exists()) {
            mc.generateMinerKey();
        } else if (!sendKeyFile.exists()) {
            mc.generateSendKey();
        }

        String pszTimeStamp = "test"; // replace recvKey with pszTimeStamp for genesis block
        String recvKey = "";
        String minerKey = "";
        String algo = "sha256";
        long genesisTimeStamp = 1556849753564L;
        String txHash = SHA256.generateSHA256Hash(genesisIndex + pszTimeStamp + recvKey);
        long Nonce = 279442;
        String previousBlockHash = null;
        String genesisHash = "00000329b26279f8550b297e4b232832473e76c3c6260366a88da90404310c7f";
        int difficulty = 5;
        float amount = MainChain.nSubsidy;
        String indexToStr = Long.toString(genesisIndex);
        String timeToStr = Long.toString(genesisTimeStamp);
        String nonceToStr = Long.toString(Nonce);
        String difficultyToStr = Integer.toString(difficulty);
        String amountToStr = Float.toString(amount);
        boolean isGenesisValid = cb.isGenesisValid(genesisIndex, genesisTimeStamp, pszTimeStamp, recvKey, minerKey, txHash, Nonce, previousBlockHash, algo, genesisHash, difficulty, amount);
        if (isGenesisValid) {
            HashArray hashArray = new HashArray();
            HashArray.hashArray.add("Index: " + indexToStr);
            HashArray.hashArray.add("Time stamp: " + timeToStr);
            HashArray.hashArray.add("pszTimeStamp: " + pszTimeStamp);
            HashArray.hashArray.add("Receive key: " + recvKey);
            HashArray.hashArray.add("Miner key: " + minerKey);
            HashArray.hashArray.add("Tx Hash: " + txHash);
            HashArray.hashArray.add("Merkle hash: " + SHA256.generateSHA256Hash((String) HashArray.hashArray.get(HashArray.hashArray.size() - 6)));
            HashArray.hashArray.add("Nonce: " + nonceToStr);
            HashArray.hashArray.add("Previous " + previousBlockHash);
            HashArray.hashArray.add("Algorithm: " + algo);
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
                System.out.println("File not found exception!\n");
            } catch (IOException e) {
                System.out.println("An IO exception occurred\n");
            }
        }
    }
}
