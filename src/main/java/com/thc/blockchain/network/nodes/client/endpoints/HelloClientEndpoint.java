package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnOpen;


@ClientEndpoint (encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class HelloClientEndpoint {

    @OnOpen
    public void onOpen() {
        System.out.println("Was able to reach hello-server!\n");
    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}
