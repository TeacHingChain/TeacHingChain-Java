package com.thc.blockchain.network;

import javax.websocket.*;

@ClientEndpoint
public class ChainClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {

            System.out.println("Connected to endpoint: " + session.getBasicRemote());
    }

        @OnMessage
        public void processMessage(String message) {
            System.out.println("Received message in client: " + message);
            Client.messageLatch.countDown();
        }

        @OnError
        public void processError(Throwable t) {
            t.printStackTrace();
        }
    }

