package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.objects.Block;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/" + Constants.pushChainKey, encoders = BlockEncoder.class, decoders = BlockDecoder.class)
public class SyncBlockServer {

    @OnMessage
    public void onMessage(Session session, Block block) {
        System.out.println("Processing block: " + block.getIndex() + " for session: " + session.getUserProperties().get("id"));
    }
}
