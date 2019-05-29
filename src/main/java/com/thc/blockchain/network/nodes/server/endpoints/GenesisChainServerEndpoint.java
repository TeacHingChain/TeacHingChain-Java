package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.GenesisBlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.wallet.KeyRing;
import com.thc.blockchain.wallet.MainChain;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/" + Constants.genesisServerKey, encoders =  { GenesisBlockEncoder.class }, decoders = { GenesisBlockDecoder.class })
public class GenesisChainServerEndpoint {

    private MainChain mc = new MainChain();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to genesis server!\n");
        NodeManager.registerNode(session, "genesis-chain-server");
    }

    @OnMessage
    public void writeGenesisBlock (GenesisBlock genesisBlock) {
        System.out.println(genesisBlock);
    }

    public void initChain() {
        mc.generatePrivateKey();
        System.out.println("Generated private key: " + KeyRing.keyRing.get(0) + " Generated address: " +  mc.generateAddress());
    }
}
