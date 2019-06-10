package com.thc.blockchain.network.nodes.server.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.decoders.GenesisBlockDecoder;
import com.thc.blockchain.network.encoders.GenesisBlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.client.endpoints.GenesisChainClientEndpoint;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.KeyRing;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/" + Constants.GENESIS_SERVER_KEY, encoders =  { GenesisBlockEncoder.class }, decoders = { GenesisBlockDecoder.class })
public class GenesisChainServerEndpoint {

    private final MainChain mc = new MainChain();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to genesis server!\n");
        NodeManager.registerNode(session, "genesis-chain-server");
    }

    @OnMessage
    public void writeGenesisBlock (GenesisBlock genesisBlock) {
        try {
            String encodedGenesisBlock = new GenesisBlockEncoder().encode(genesisBlock);
            if (validateGenesisBlock(genesisBlock)) {
                System.out.println("Genesis block passed validation check! Encoding and adding to chain!\n");
                new BlockChain();
                BlockChain.blockChain.add(encodedGenesisBlock);
                mc.writeBlockChain();
                WalletLogger.logEvent("info", WalletLogger.getLogTimeStamp() + "Genesis block passed validation check!\n");
            } else {
                System.out.println("Genesis block not passing validation!\n");
                WalletLogger.logEvent("warning", WalletLogger.getLogTimeStamp() + " Genesis block failed validation!\n");
            }
        } catch (EncodeException ee) {
            WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp()
                    + " An encode exception occurred while trying to encode genesis block! See details below:\n"
                    + WalletLogger.exceptionStacktraceToString(ee));
        }
    }

    public void initChain() {
        mc.generatePrivateKey();
        System.out.println("Generated private key: " + KeyRing.keyRing.get(0) + " Generated address: " +  mc.generateAddress(0));
    }

    private static boolean validateGenesisBlock(GenesisBlock genesisBlock) {
        byte[] genesisHeaderBytes = MainChain.swapEndianness((Long.parseLong(genesisBlock.getIndex()) + Long.parseLong(genesisBlock.getTimeStamp())
                + genesisBlock.getPszTimestamp() + genesisBlock.getFromAddress() + genesisBlock.getToAddress() + genesisBlock.getTxHash()
                + genesisBlock.getMerkleRoot() + Long.parseLong(genesisBlock.getNonce()) + genesisBlock.getPreviousBlockHash() + genesisBlock.getAlgo()
                + genesisBlock.getTarget() + Double.parseDouble(genesisBlock.getDifficulty()) + Float.parseFloat(genesisBlock.getAmount())).getBytes());
        String hashedGenesisBytes = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(genesisHeaderBytes)));
        return hashedGenesisBytes.contentEquals(Constants.GENESIS_HASH);
    }
}
