package com.thc.blockchain.consensus;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

public class Consensus {

    private static StringBuilder sb = new StringBuilder();

    public boolean isBlockOrphan(long index) {
        MainChain mc = new MainChain();
        int mostRecentIndex = mc.getIndexOfBlockChain();
        return index == mostRecentIndex + 1;
    }

    public static Boolean compareChainChecksum(int remoteChainSize, String remoteChecksum) {
        for (int i = 0; i < remoteChainSize; i++) {
            String blockAsString = BlockChain.blockChain.get(i).toString();
            sb.append(blockAsString);
        }
        String chainAsString = sb.toString();
        String checksum = SHA256.generateSHA256Hash(chainAsString);
        System.out.println("Remote checksum: " + remoteChecksum);
        System.out.println("Local checksum to remoteChainSize " + remoteChainSize + ": " + checksum);

        return remoteChecksum.contentEquals(checksum);
    }
}
