package com.thc.blockchain.network.nodes.server.endpoints;

import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/hello")
public class HelloServerEndpoint {

    @OnOpen
    public void onOpen () {
        System.out.println("Hello client connected!\n");
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}

