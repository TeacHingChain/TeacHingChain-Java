package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.decoders.AlertDecoder;
import com.thc.blockchain.network.encoders.AlertEncoder;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Alert;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint(encoders = { AlertEncoder.class }, decoders = { AlertDecoder.class })
public class SyncAlertClient {

    static int remoteChainSize;
    private StringBuilder sb = new StringBuilder();
    private MainChain mc = new MainChain();


    @OnOpen
    public void onOpen(Session session) {
        mc.readBlockChain();
        System.out.println("ClientManager connected to sync client!\n");
        if (NodeManager.registerNode(session, "sync-client")) {
            int localChainSize = BlockChain.blockChain.size();
            String sizeAsString = String.valueOf(localChainSize);
            Alert sizeAlert = new Alert("sync size", sizeAsString);
            NodeManager.pushAlert(sizeAlert, session);
            for (Object o : BlockChain.blockChain) {
                String blockAsString = o.toString();
                sb.append(blockAsString);
            }
            String chainAsString = sb.toString();
            String checksum = SHA256.SHA256HashString(chainAsString);
            Alert checksumAlert = new Alert("sync checksum", checksum);
            NodeManager.pushAlert(checksumAlert, session);
        }
    }

    @OnMessage
    public void onAlertMessage(Alert alert, Session session) {
        EndpointManager endpointManager = new EndpointManager();
        mc.readBlockChain();
        WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " received alert: " + "alert type: " + alert.getAlertType() + " alert message: " + alert.getAlertMessage() + " from session: " + session.getUserProperties().get("id"));
        if (alert.getAlertType().contentEquals("sync size")) {
            remoteChainSize = Integer.parseInt(alert.getAlertMessage());
        } else if (alert.getAlertType().contentEquals("sync checksum") && remoteChainSize < BlockChain.blockChain.size()) {
            String remoteCheckSum = alert.getAlertMessage();
            if (Consensus.compareChainChecksum(remoteChainSize, remoteCheckSum)) {
                endpointManager.connectAsClient("sync block");
            } else {
                WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + " Refusing to connect to sync block client due to a consensus error!\n");
                System.out.println("Refusing to connect to sync block client due to a consensus error!\n");
            }
        }
    }
}


