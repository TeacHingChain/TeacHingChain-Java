package com.thc.blockchain.consensus;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.Miner;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.DecodeException;
import java.math.BigInteger;
import java.util.Arrays;

public class Consensus {

    private static final StringBuilder sb = new StringBuilder();

    public boolean isBlockOrphan(long index) {
        new MainChain().readBlockChain();
        int mostRecentIndex = new MainChain().getIndexOfBlockChain();
        boolean isOrphan = index == mostRecentIndex + 1;
        return !isOrphan;
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

    public boolean validateTarget(Block block) {
        BigInteger previousTarget = null;
        try {
            previousTarget = new BigInteger(new BlockDecoder().decode(BlockChain.blockChain.get(
                    new MainChain().getIndexOfBlockChain())).getTarget(), 16);
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp() + " An error occurred decoding a block! See details below:\n"
                    + WalletLogger.exceptionStacktraceToString(de));        }
        if (BlockChain.blockChain.size() > 10 && (block.getIndex() % 5 == 0)) {
            return (!leftPad(previousTarget.toString(16), 64, '0').contentEquals(block.getTarget()) && new BigInteger(block.getTarget(), 16).compareTo(
                    new BigInteger(Constants.GENESIS_TARGET, 16)) <= 0);
        } else if (BlockChain.blockChain.size() > 10) {
            return (leftPad(previousTarget.toString(16), 64, '0').contentEquals(block.getTarget()) && new BigInteger(block.getTarget(), 16).compareTo(
                    new BigInteger(Constants.GENESIS_TARGET, 16)) <= 0);
        } else {
            return true;
        }
    }

    private String leftPad(String originalString, int length, char padChar) {
        StringBuilder sb = new StringBuilder();
        if (originalString.length() > length) {
            try {
                throw new PadLengthException("Error! Original string is longer than desired padded length!\n");
            } catch (PadLengthException e) {
                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp() + " Pad length exception occurred!\n");
            }
        } else {
            int padLength = length - originalString.length();
            for (int i = 0; i < padLength; i++) {
                sb.append(padChar);
            }
        }
        return sb.toString() + originalString;
    }

    public class PadLengthException extends Exception {
        PadLengthException(String msg) {
            System.out.println(msg);
        }
    }
}
