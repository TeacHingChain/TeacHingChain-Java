package com.thc.blockchain.consensus;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.DecodeException;
import java.util.Arrays;

public class Consensus {

    private static final StringBuilder sb = new StringBuilder();

    public boolean isBlockOrphan(long index) {
        MainChain mc = new MainChain();
        int mostRecentIndex = mc.getIndexOfBlockChain();
        return index == mostRecentIndex + 1;
    }

    public static boolean compareChainChecksum(int remoteChainSize, String remoteChecksum) {
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

    public boolean validateBlock(Block block) {
        String blockHeader =
                (Long.parseLong(block.getIndex()) + Long.parseLong(block.getTimeStamp()) + block.getFromAddress()
                + block.getToAddress() + Arrays.toString(block.getTransactions()) + block.getMerkleRoot()
                + Long.parseLong(block.getNonce()) + block.getPreviousBlockHash() + block.getAlgo() + block.getTarget()
                + Double.parseDouble(block.getDifficulty()) + Float.parseFloat(block.getAmount()));
        String hashedHeaderBytes = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(
                MainChain.swapEndianness(blockHeader.getBytes()))));
        return block.getBlockHash().contentEquals(hashedHeaderBytes);
    }
}
