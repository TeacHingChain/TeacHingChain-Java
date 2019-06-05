package com.thc.blockchain.consensus;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

public class Consensus {

    private static final StringBuilder sb = new StringBuilder();

    public boolean isBlockOrphan(long index) {
        MainChain mc = new MainChain();
        int mostRecentIndex = mc.getIndexOfBlockChain();
        return index == mostRecentIndex + 1;
    }

    public static Boolean compareChainChecksum(int remoteChainSize, String remoteChecksum) {
        if (remoteChainSize == 1) {
            String blockAsString = BlockChain.blockChain.get(0);
            sb.append(blockAsString);
        } else {
            for (int i = 0; i < remoteChainSize; i++) {
                String blockAsString = BlockChain.blockChain.get(i);
                sb.append(blockAsString);
            }
        }
        String chainAsString = sb.toString();
        String checksum = SHA256.SHA256HashString(SHA256.SHA256HashString(chainAsString));
        System.out.println("Remote checksum: " + remoteChecksum);
        System.out.println("Local checksum to remoteChainSize " + remoteChainSize + ": " + checksum);

        return remoteChecksum.contentEquals(checksum);
    }
}
