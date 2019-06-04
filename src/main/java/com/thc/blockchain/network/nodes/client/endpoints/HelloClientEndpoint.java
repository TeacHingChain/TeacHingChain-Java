package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.*;


@ClientEndpoint (encoders = Encoder.Text.class, decoders = Decoder.Text.class)
public class HelloClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        NodeManager.registerNode(session, "hello-client");
        System.out.println("Was able to reach hello-server!\n");
        session.setMaxIdleTimeout(5000);
        NodeManager.sendHello("hello", session);

    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received " + message + " from " + session.getUserProperties().get("id").toString());
        NodeManager.remove(session);
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}
