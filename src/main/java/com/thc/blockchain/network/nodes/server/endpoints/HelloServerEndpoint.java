package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/hello", encoders = { Encoder.Text.class }, decoders = { Decoder.Text.class })
public class HelloServerEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        NodeManager.registerNode(session, "hello-server");
        System.out.println("Hello client connected!\n");
        session.setMaxIdleTimeout(5000);
        NodeManager.sendHello("hello", session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received " + message + " from " + session.getUserProperties().get("id").toString());
        NodeManager.remove(session);
    }

    @OnError
    public void onError(Throwable exception) {
        System.out.println(exception.getMessage());
    }
}

