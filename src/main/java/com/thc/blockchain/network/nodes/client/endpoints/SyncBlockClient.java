package com.thc.blockchain.network.nodes.client.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.server.endpoints.SyncAlertServer;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint(encoders = { BlockEncoder.class }, decoders = { BlockDecoder.class })
public class SyncBlockClient {

    private int size;
    private MainChain mc = new MainChain();

    @OnOpen
    private void initSyncBlock(Session session) {
        mc.readBlockChain();
        if (session.getUserProperties().get("id").toString().contentEquals("sync client")) {
            size = SyncAlertClient.remoteChainSize;
        } else if (session.getUserProperties().get("id").toString().contentEquals("sync server")) {
            size =  SyncAlertServer.remoteChainSize;
        }
        for (int i = size; i < BlockChain.blockChain.size(); i++) {
            System.out.println("Block " + i + " in flight!\n");
            String blockAsString = BlockChain.blockChain.get(i);
            JsonElement jsonElement = new JsonParser().parse(blockAsString);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement indexElement = jsonObject.get("index");
            String blockIndex = indexElement.getAsString();
            JsonElement timestampElement = jsonObject.get("time stamp");
            String timeStamp = timestampElement.getAsString();
            JsonElement fromAddressElement = jsonObject.get("from address");
            String fromAddress = fromAddressElement.getAsString();
            JsonElement toAddressElement = jsonObject.get("to address");
            String toAddress = toAddressElement.getAsString();
            JsonElement txHashElement = jsonObject.get("transactions");
            JsonArray txHashJSON = txHashElement.getAsJsonArray();
            String[] txHash = new String[txHashJSON.size()];
            for (int j = 0; j < txHashJSON.size(); j++) {
                txHash[i] = txHashJSON.getAsString();
            }
            JsonElement merkleHashElement = jsonObject.get("merkle hash");
            String merkleHash = merkleHashElement.getAsString();
            JsonElement nonceElement = jsonObject.get("nonce");
            String nonce = nonceElement.getAsString();
            JsonElement previousBlockElement = jsonObject.get("previous block hash");
            String previousBlockHash = previousBlockElement.getAsString();
            JsonElement algoElement = jsonObject.get("algo");
            String algo = algoElement.getAsString();
            JsonElement blockHashElement = jsonObject.get("block hash");
            String blockHash = blockHashElement.getAsString();
            JsonElement difficultyElement = jsonObject.get("difficulty");
            String difficulty = difficultyElement.getAsString();
            JsonElement amountElement = jsonObject.get("amount");
            String amount = amountElement.getAsString();
            Block block = new Block(blockIndex, timeStamp, fromAddress, toAddress, txHash, merkleHash, nonce, previousBlockHash, algo, blockHash, difficulty, amount);
            NodeManager.pushBlock(block, session);
        }
    }

    @OnMessage
    public void onMessage(Session session, Block block) {
        System.out.println("Processing block: " + block.getBlockHash() + " At height: " + block.getIndex() + " for session: " + session.getUserProperties().get("id"));
    }
}
