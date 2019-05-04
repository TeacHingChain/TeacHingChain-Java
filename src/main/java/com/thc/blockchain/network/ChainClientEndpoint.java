package com.thc.blockchain.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;

@ClientEndpoint
public class ChainClientEndpoint {
    @OnOpen
    public void onOpen(Session session) {

            System.out.println("Connected to endpoint: " + session.getBasicRemote());
            try {
                String name = "Duke";
                System.out.println("Sending message to endpoint: " + name);
                session.getBasicRemote().sendText(name);
            } catch (IOException ex) {
                Logger.getLogger(ChainClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
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

