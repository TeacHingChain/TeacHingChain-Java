package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Block;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/update", encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class UpdateServerEndPoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("A client connected to the server!\n");
        NodeManager.registerNode(session, "update-chain-server");

    }


    @OnMessage
    public void onBlockMessage(Block block, Session session) {
        System.out.println("Processing block number: " + block.getIndex());
        NodeManager.pushBlock(block, session);
        NodeManager.remove(session);
    }
}

