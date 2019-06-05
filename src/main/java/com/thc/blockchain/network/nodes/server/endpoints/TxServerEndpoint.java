package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.decoders.TxDecoder;
import com.thc.blockchain.network.encoders.TxEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Tx;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/tx", encoders = { TxEncoder.class }, decoders = { TxDecoder.class })
class TxServerEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        if (NodeManager.registerNode(session, "tx-server")) {
            System.out.println("Registered as: " + session.getUserProperties().get("id"));
        }
    }

    @OnMessage
    public void onMessage(Tx tx, Session session) {
        System.out.println("Processing tx: " + tx.getTxHash() + " for session: " + session.getUserProperties().get("id").toString());
    }
}
