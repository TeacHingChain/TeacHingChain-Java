package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.CloseReason;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/hello")
public class HelloServerEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Hello client connected!\n");
        NodeManager.close(session, CloseReason.CloseCodes.NORMAL_CLOSURE, "closing session...");
    }

    @OnError
    public void onError(Throwable exception) {
        System.out.println(exception.getMessage());
    }
}

