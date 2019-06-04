package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;

import javax.websocket.*;


@ClientEndpoint (encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class HelloClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Was able to reach hello-server!\n");
        NodeManager.close(session, CloseReason.CloseCodes.NORMAL_CLOSURE, "closing session..");

    }

    @OnError
    public void onError (Throwable exception) {
        System.out.println(exception.getMessage());
    }
}
