package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.math.BigInteger;

@ServerEndpoint(value = "/" + Constants.UPDATE_KEY, encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class UpdateServerEndPoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("A client connected to the server!\n");
        NodeManager.registerNode(session, "update-chain-server");
    }

    @OnMessage
    public void onBlockMessage(Block block, Session session) {
        MainChain mc = new MainChain();
        Consensus consensus = new Consensus();
        try {
            mc.readBlockChain();
            System.out.println("Processing block number: " + block.getIndex());
            if (!consensus.isBlockOrphan(block.getIndex())) {
                if (consensus.validateTarget(block)) {
                    String encodedBlock = new BlockEncoder().encode(block);
                    BlockChain.blockChain.add(encodedBlock);
                    mc.writeBlockChain();
                    if (BlockChain.blockChain.size() > 5 && BlockChain.blockChain.size() % 5 == 0) {
                        MainChain.calculateTarget(((new BlockDecoder().decode(BlockChain.blockChain.get(
                                mc.getIndexOfBlockChain())).getTimeStamps()[0]) - (new BlockDecoder()
                                .decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain() - 5)).getTimeStamps()[0])) / 1000, MainChain.targetHex);
                    } else {
                        MainChain.difficulty = block.getDifficulty();
                        MainChain.targetHex = block.getTarget();
                    }
                } else {
                    System.out.println("A consensus error occurred while trying to add a block! See log for details\n");
                    WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp()
                            + " A consensus error occurred while trying to add a block with the following details:\n"
                            + "Block index: " + block.getIndex() + "\n Block hash: " + block.getBlockHash() + "\n Block target: "
                            + block.getTarget());
                }
            } else {
                System.out.println("A consensus error occurred while trying to add a block! See log for details\n");
                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp()
                        + " An orphan block was detected with the following details:\n"
                        + "Block index: " + block.getIndex() + "\n Block hash: " + block.getBlockHash() + "\n Block target: "
                        + block.getTarget());
            }
        } catch (EncodeException ee) {
            WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp() + " Encode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
        } catch (DecodeException e) {
            e.printStackTrace();
        }
        NodeManager.remove(session);
        NodeManager.close(session, CloseReason.CloseCodes.NORMAL_CLOSURE, "Closing session...\n");
    }
}

