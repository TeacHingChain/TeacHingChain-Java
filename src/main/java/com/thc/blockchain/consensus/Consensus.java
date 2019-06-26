package com.thc.blockchain.consensus;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.DecodeException;
import java.math.BigInteger;
import java.util.Arrays;

public class Consensus {

    private static final StringBuilder sb = new StringBuilder();

    public boolean isBlockOrphan(long index) {
        int mostRecentIndex = new MainChain().getIndexOfBlockChain();
        return index == mostRecentIndex + 1;
    }

    public static boolean validateChainChecksum(int remoteChainSize, String remoteChecksum) {
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
                (block.getIndex() + Arrays.toString(block.getTimeStamps()) + Arrays.toString(block.getTxins())
                + Arrays.toString(block.getTxouts()) + Arrays.toString(block.getTransactions()) + block.getMerkleRoot()
                + block.getNonce() + block.getPreviousBlockHash() + block.getAlgo() + block.getTarget()
                + block.getDifficulty() + Arrays.toString(block.getAmounts()));
        String hashedHeaderBytes = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(
                MainChain.swapEndianness(blockHeader.getBytes()))));
        return block.getBlockHash().contentEquals(hashedHeaderBytes);
    }

    public boolean validateChain() {
        try {
            for (int i = 1; i < BlockChain.blockChain.size();) {
                Block decodedBlock = new BlockDecoder().decode(BlockChain.blockChain.get(i));
                if (validateBlock(decodedBlock)) {
                    i++;
                } else {
                    System.out.println("Chain failed validation!\n" + "Block " + decodedBlock.getIndex() + " with hash " + decodedBlock.getBlockHash()
                            + " is invalid!\n");
                    WalletLogger.logEvent("severe", WalletLogger.getLogTimeStamp()
                            + " Chain failed validation! See details below:\n" + "Block " + decodedBlock.getIndex() + " with hash "
                            + decodedBlock.getBlockHash() + " is invalid!\n");
                    return false;
                }
            }
        } catch (DecodeException de) {
            WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp()
                    + " Decode exception occurred during a consensus operation! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
        WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " Chain passed validation!");
        return true;
    }
}
