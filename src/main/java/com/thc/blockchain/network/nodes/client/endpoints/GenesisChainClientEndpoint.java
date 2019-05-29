package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.GenesisBlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.GenesisBlock;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint(encoders = { GenesisBlockEncoder.class }, decoders = { GenesisBlockDecoder.class })
public class GenesisChainClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("ClientManager connected to genesis server!\n");
        NodeManager.registerNode(session, "genesis-chain-client");
        GenesisBlock genesisBlock = new GenesisBlock(Constants.genesisIndex, Constants.genesisTimestamp, Constants.genesisPszTimestamp, Constants.genesisFromAddress,
                Constants.genesisToAddress, Constants.genesisTxHash, Constants.genesisMerkleHash, Constants.genesisNonce, Constants.genesisPreviousBlockHash, Constants.genesisAlgo,
                Constants.genesisHash, Constants.genesisDifficulty, Constants.genesisAmount);
        NodeManager.pushGenesisBlock(genesisBlock, session);
    }

    @OnMessage
    public void writeGenesisBlock (GenesisBlock genesisBlock) {
        System.out.println("Adding block object " + genesisBlock.getAlgo());
    }
}
