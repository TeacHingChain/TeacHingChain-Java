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
        System.out.println("Trying to connect...\n");
        while (true) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                String uri = "ws://localhost:8080/server/node-comm";
                System.out.println("Connecting to " + uri);
                container.connectToServer(ChainClientEndpoint.class, URI.create(uri));
                messageLatch.await(100, TimeUnit.SECONDS);

            } catch (DeploymentException de) {
                System.out.println("Connection to server refused!\n");
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, de);
            } catch (InterruptedException ie) {
                System.out.println("An interrupted exception occurred!\n");
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ie);
            } catch (IOException ioe) {
                System.out.println("An IO exception occurred!\n");
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ioe);
            }
        }
    }
}


