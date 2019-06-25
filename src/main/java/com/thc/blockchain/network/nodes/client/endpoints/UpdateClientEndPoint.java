package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.*;

@ClientEndpoint(encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class UpdateClientEndPoint {

    @OnOpen
    public void onOpen(Session session) {
        MainChain mc = new MainChain();
        if (NodeManager.registerNode(session, "update-chain-client")) {
            System.out.println("ClientManager connected to update server!\n");
            try {
                NodeManager.pushBlock(new BlockDecoder().decode(BlockChain.blockChain.get(mc.getIndexOfBlockChain())), session);
            } catch (DecodeException e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onBlockMessage(Block block, Session session) {
        System.out.println("Processing block number: " + block.getIndex());
        NodeManager.remove(session);
    }
}
