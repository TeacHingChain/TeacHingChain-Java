package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.Constants;

import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/" + Constants.HELLO_KEY)
public class HelloServerEndpoint {

    @OnOpen
    public void onOpen () {
        System.out.println("Hello from client!\n");
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}

