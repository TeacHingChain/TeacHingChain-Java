package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.GenesisBlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.*;

@ClientEndpoint(encoders = { GenesisBlockEncoder.class }, decoders = { GenesisBlockDecoder.class })
public class GenesisChainClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("ClientManager connected to genesis server!\n");
        NodeManager.registerNode(session, "genesis-chain-client");
        GenesisBlock genesisBlock = new GenesisBlock(Constants.GENESIS_INDEX, Constants.GENESIS_TIMESTAMP, Constants.GENESIS_PSZ_TIMESTAMP,
                Constants.GENESIS_FROM_ADDRESS, Constants.GENESIS_TO_ADDRESS, Constants.GENESIS_TX_HASH, Constants.GENESIS_MERKLE_ROOT,
                Constants.GENESIS_NONCE, Constants.GENESIS_PREVIOUS_BLOCK_HASH, Constants.GENESIS_ALGO, Constants.GENESIS_HASH,
                Constants.GENESIS_TARGET, Constants.GENESIS_DIFFICULTY, Constants.GENESIS_AMOUNT);
        NodeManager.pushGenesisBlock(genesisBlock, session);
    }

    @OnMessage
    public void writeGenesisBlock(GenesisBlock genesisBlock) {
        System.out.println("Adding block object " + genesisBlock.getBlockHash());
    }
}
