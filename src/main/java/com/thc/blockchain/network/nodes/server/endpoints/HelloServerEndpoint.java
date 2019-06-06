package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/" + Constants.HELLO_KEY)
public class HelloServerEndpoint {

    @OnOpen
    public void onOpen (Session session) {
        NodeManager.registerNode(session, "hello-server");
        System.out.println("Hello from " + session.getUserProperties().get("id").toString());
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}

