package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint(encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class UpdateClientEndPoint {

    @OnOpen
    public void onOpen(Session session) {
        if (NodeManager.registerNode(session, "update-chain-client")) {
            System.out.println("ClientManager connected to update server!\n");
            NodeManager.setSession(session);
        }
    }

    @OnMessage
    public void onBlockMessage(Block block, Session session) {
        System.out.println("Processing block number: " + block.getIndex());
        NodeManager.remove(session);
    }
}
