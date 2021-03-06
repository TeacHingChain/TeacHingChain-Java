package com.thc.blockchain.network.nodes.server.endpoints;

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

@ServerEndpoint(value = "/" + Constants.PUSH_CHAIN_KEY, encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class SyncBlockServer {

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        NodeManager.registerNode(session, "sync-block-server");
        System.out.println("remote-chain-size: " + config.getUserProperties().get("remote-chain-size"));
        System.out.println("Session connected: " + session.getUserProperties().get("id").toString());
    }



    @OnMessage
    public void onMessage(Session session, Block block) {
        try {
            MainChain mc = new MainChain();
            mc.readBlockChain();
            System.out.println("Processing block: " + block.getIndex() + " for session: " + session.getUserProperties().get("id").toString());
            String encodedBlock = new BlockEncoder().encode(block);
            BlockChain.blockChain.add(encodedBlock);
            mc.writeBlockChain();
        } catch (EncodeException ee) {
            WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp() + " Encode exception occurred during sync operation! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
        }
    }
}
