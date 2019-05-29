package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.AlertDecoder;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.AlertEncoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.ClientManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Alert;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.HashArray;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

@ServerEndpoint(value = "/" + Constants.syncKey, encoders = { BlockEncoder.class, AlertEncoder.class }, decoders = { BlockDecoder.class, AlertDecoder.class })
public class SyncAlertServer {

    public static int remoteChainSize;

    @OnOpen
    public void onOpen(Session session) {
        try {
            FileInputStream fis = new FileInputStream("/home/dev-environment/Desktop/java_random/TeacHingChain/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashArray.hashArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.println("An IO error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            System.out.println("A class not found error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        }
        System.out.println("ClientManager connected to sync client!\n");
        NodeManager.registerNode(session, "sync-server");
        int localChainSize = HashArray.hashArray.size();
        String sizeAsString = String.valueOf(localChainSize);
        Alert sizeAlert = new Alert("sync size", sizeAsString);
        NodeManager.pushAlert(sizeAlert, session);
        WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " pushed alert: \n" + "alert type: " + sizeAlert.getAlertType() + " alert message: " + sizeAlert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));

        String chainChecksum = SHA256.generateSHA256Hash(HashArray.hashArray.toString());
        Alert checksum = new Alert("sync checksum", chainChecksum);
        NodeManager.pushAlert(checksum, session);
        WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " pushed alert: \n" + "alert type: " + sizeAlert.getAlertType() + " alert message: " + sizeAlert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
    }

    @OnMessage
    public void onAlertMessage(Alert alert, Session session) {
        ClientManager clientManager = new ClientManager();
        try {
            System.out.println("null path hit\n");
            FileInputStream fis = new FileInputStream("/home/dev-environment/Desktop/java_random/TeacHingChain/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashArray.hashArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.println("An IO error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            System.out.println("A class not found error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        }
        if (alert.getAlertType().contentEquals("sync size")) {
            WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: \n" + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
            remoteChainSize = Integer.parseInt(alert.getAlertMessage());
        } else if (alert.getAlertType().contentEquals("sync checksum") && remoteChainSize < HashArray.hashArray.size()) {
            WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: \n" + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
            String remoteCheckSum = alert.getAlertMessage();
            if (Consensus.compareChainChecksum(remoteChainSize, remoteCheckSum)) {
                clientManager.connectAsClient("sync block");
            } else {
                System.out.println("Refusing to connect to sync block client due to a consensus error!\n");
            }
        }
    }
}