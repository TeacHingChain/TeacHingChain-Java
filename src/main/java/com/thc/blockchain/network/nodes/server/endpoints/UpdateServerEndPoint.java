package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@SuppressWarnings("unused")
@ServerEndpoint(value = "/update", encoders = BlockEncoder.class, decoders = BlockDecoder.class)
class UpdateServerEndPoint {

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
            boolean verifyIndex = consensus.isBlockOrphan(Long.parseLong(block.getIndex()));
            if (verifyIndex) {
                String encodedBlock = new BlockEncoder().encode(block);
                BlockChain.blockChain.add(encodedBlock);
                mc.writeBlockChain();
            }
        } catch (EncodeException ee) {
            WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp() + " Encode exception occurred during mining operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
        }
        NodeManager.remove(session);
    }
}

