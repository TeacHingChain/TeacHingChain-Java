package com.thc.blockchain.network;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    final static CountDownLatch messageLatch = new CountDownLatch(1);
    public void connectClient(){
        while (true) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                String uri = "ws://localhost:8080/socket";
                System.out.println("Connecting to " + uri);
                container.connectToServer(ClientEndpoint.class, URI.create(uri));
                messageLatch.await(100, TimeUnit.SECONDS);

            } catch (DeploymentException | InterruptedException | IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


