package com.thc.blockchain.network;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;

@javax.websocket.ClientEndpoint
public class ClientEndpoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());


        try {
            ArrayList al = new ArrayList<>();
            al.add("testing");
            al.add("send object");
            al.add("minimal changes");
            FileOutputStream fos = new FileOutputStream("testList.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(al);
            oos.close();
            fos.close();
            session.getBasicRemote().sendObject(al.get(0));
            session.close();

        } catch (IOException ex) {
            Logger.getLogger(ClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncodeException e) {
            e.printStackTrace();
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

/*
TODO: Integrate client endpoint as class for initiating ws connection to stream blocks via publish method
 */