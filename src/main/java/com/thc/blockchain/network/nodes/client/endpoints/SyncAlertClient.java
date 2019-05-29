package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.decoders.AlertDecoder;
import com.thc.blockchain.network.encoders.AlertEncoder;
import com.thc.blockchain.network.nodes.ClientManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Alert;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

@ClientEndpoint(encoders = { AlertEncoder.class }, decoders = { AlertDecoder.class })
public class SyncAlertClient {

    static int remoteChainSize;
    private StringBuilder sb = new StringBuilder();

    @OnOpen
    public void onOpen(Session session) {
        try {
            FileInputStream fis = new FileInputStream("/home/dev-environment/Desktop/java_random/TeacHingChain/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BlockChain.blockChain = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.println("An IO error occurred in SyncAlertClient:onAlertMessage, see log for details!");
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            System.out.println("A class not found error occurred in SyncAlertClient:onAlertMessage, see log for details!");
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        }
        System.out.println("ClientManager connected to sync client!\n");
        NodeManager.registerNode(session, "sync-client");
        int localChainSize = BlockChain.blockChain.size();
        String sizeAsString = String.valueOf(localChainSize);
        Alert sizeAlert = new Alert("sync size", sizeAsString);
        NodeManager.pushAlert(sizeAlert, session);
        for (Object o : BlockChain.blockChain) {
            String blockAsString = o.toString();
            sb.append(blockAsString);
        }
        String chainAsString = sb.toString();
        String checksum = SHA256.generateSHA256Hash(chainAsString);
        Alert checksumAlert = new Alert("sync checksum", checksum);
        NodeManager.pushAlert(checksumAlert, session);
    }

    @OnMessage
    public void onAlertMessage(Alert alert, Session session) {
        ClientManager clientManager = new ClientManager();
        try {
            System.out.println("null path hit\n");
            FileInputStream fis = new FileInputStream("/home/dev-environment/Desktop/java_random/TeacHingChain/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BlockChain.blockChain = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.println("An IO error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        } catch (ClassNotFoundException cnfe) {
            System.out.println("A class not found error occurred in SyncAlertServer:onAlertMessage, see log for details!");
            WalletLogger.logException(cnfe, "severe", WalletLogger.getLogTimeStamp() + " Class not found exception occurred while trying to read chain.dat in SyncAlertServer! See below:\n" + WalletLogger.exceptionStacktraceToString(cnfe));
        }
        WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: " + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
        if (alert.getAlertType().contentEquals("sync size")) {
            remoteChainSize = Integer.parseInt(alert.getAlertMessage());
        } else if (alert.getAlertType().contentEquals("sync checksum") && remoteChainSize < BlockChain.blockChain.size()) {
            String remoteCheckSum = alert.getAlertMessage();
            if (Consensus.compareChainChecksum(remoteChainSize, remoteCheckSum)) {
                clientManager.connectAsClient("sync block");
            } else {
                WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " Refusing to connect to sync block client due to a consensus error!\n");
                System.out.println("Refusing to connect to sync block client due to a consensus error!\n");
            }
        }
    }
}


