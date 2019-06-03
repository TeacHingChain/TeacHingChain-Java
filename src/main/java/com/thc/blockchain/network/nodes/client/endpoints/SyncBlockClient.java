package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.server.endpoints.SyncAlertServer;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.*;

@ClientEndpoint(encoders = { BlockEncoder.class }, decoders = { BlockDecoder.class })
public class SyncBlockClient {

    private int size;
    private MainChain mc = new MainChain();

    @OnOpen
    public void initSyncBlock(Session session) {
        NodeManager.registerNode(session, "sync-block-client");
        try {
            mc.readBlockChain();
            if (BlockChain.blockChain.size() > SyncAlertServer.remoteChainSize) {
                size = SyncAlertClient.remoteChainSize;
            } else {
                size = SyncAlertServer.remoteChainSize;
            }
            for (int i = size; i < BlockChain.blockChain.size(); i++) {
                System.out.println("Block " + i + " in flight!\n");
                String blockAsString = BlockChain.blockChain.get(i);
                Block block = new BlockDecoder().decode(blockAsString);
                NodeManager.pushBlock(block, session);
            }
        } catch (DecodeException de) {
            WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Decode exception occurred during sync operation! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
        }
    }

    @OnMessage
    public void onMessage(Session session, Block block) {
        System.out.println("Processing block: " + block.getBlockHash() + " At height: " + block.getIndex() + " for session: " + session.getUserProperties().get("id"));
    }
}
