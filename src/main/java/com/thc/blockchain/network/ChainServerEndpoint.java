package com.thc.blockchain.network;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
@ServerEndpoint(value = "/node-comm")

public class ChainServerEndpoint {


        @OnMessage
        public void onMessage(String message, Session session) {
            System.out.println("Message received: " + message);
        }

        @OnOpen
        public void onOpen() {

            System.out.println("Client connected");
        }

        @OnClose
        public void onClose() {

            System.out.println("Connection closed");
        }
    }



