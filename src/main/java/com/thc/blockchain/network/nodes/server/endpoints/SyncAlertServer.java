package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.AlertDecoder;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.AlertEncoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Alert;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/" + Constants.syncKey, encoders = { BlockEncoder.class, AlertEncoder.class }, decoders = { BlockDecoder.class, AlertDecoder.class })
public class SyncAlertServer {

    public static int remoteChainSize;
    private MainChain mc = new MainChain();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        StringBuilder sb = new StringBuilder();
        mc.readBlockChain();
        System.out.println("ClientManager connected to sync client!\n");
        if (NodeManager.registerNode(session, "sync-server")) {
            int localChainSize = BlockChain.blockChain.size();
            String sizeAsString = String.valueOf(localChainSize);
            Alert sizeAlert = new Alert("sync size", sizeAsString);
            config.getUserProperties().put("remote-chain-size", remoteChainSize);
            NodeManager.pushAlert(sizeAlert, session);
            for (String block : BlockChain.blockChain) {
                sb.append(block);
            }
            String chainAsString = sb.toString();
            String checksum = SHA256.SHA256HashString(SHA256.SHA256HashString(chainAsString));
            Alert checksumAlert = new Alert("sync checksum", checksum);
            NodeManager.pushAlert(checksumAlert, session);
        }
    }

    @OnMessage
    public void onAlertMessage(Alert alert, Session session) {
        EndpointManager endpointManager = new EndpointManager();
        mc.readBlockChain();
        if (alert.getAlertType().contentEquals("sync size")) {
            WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: \n" + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
            remoteChainSize = Integer.parseInt(alert.getAlertMessage());
        } else if (alert.getAlertType().contentEquals("sync checksum") && remoteChainSize < BlockChain.blockChain.size()) {
            WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: \n" + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
            String remoteCheckSum = alert.getAlertMessage();
            if (Consensus.compareChainChecksum(remoteChainSize, remoteCheckSum)) {
                endpointManager.connectAsClient("sync-block");
            } else {
                System.out.println("Refusing to connect to sync block client due to a consensus error!\n");
            }
        }
    }
}