package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;


@ClientEndpoint (encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class HelloClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        NodeManager.registerNode(session, "hello-client");
        System.out.println("Hello from " + session.getUserProperties().get("id").toString());
        NodeManager.remove(session);
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}





