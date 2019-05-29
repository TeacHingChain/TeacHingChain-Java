package com.thc.blockchain.network.nodes.client.endpoints;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.network.decoders.BlockDecoder;
import com.thc.blockchain.network.encoders.BlockEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.nodes.server.endpoints.SyncAlertServer;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.HashArray;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

@ClientEndpoint(encoders = { BlockEncoder.class }, decoders = { BlockDecoder.class })
public class SyncBlockClient {

    private int size;

    @OnOpen
    private void initSyncBlock(Session session) {
        try {
            System.out.println("null path hit\n");
            FileInputStream fis = new FileInputStream("/home/dev-environment/Desktop/java_random/TeacHingChain/chain.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashArray.hashArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to read chain.dat in SyncBlockClient! See below:\n");
            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
            WalletLogger.logException(ioe, "severe", stacktraceAsString);
        } catch (ClassNotFoundException cnfe) {
            WalletLogger.logException(cnfe, "severe", "Class not found exception occurred while trying to read chain.dat in SyncBlockClient! See below:\n");
            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(cnfe);
            WalletLogger.logException(cnfe, "severe", stacktraceAsString);
        }
        if (session.getUserProperties().get("id").toString().contentEquals("sync client")) {
            size = SyncAlertClient.remoteChainSize;
        } else if (session.getUserProperties().get("id").toString().contentEquals("sync server")) {
            size =  SyncAlertServer.remoteChainSize;
        }
        for (int i = size; i < HashArray.hashArray.size(); i++) {
            System.out.println("Block " + i + " in flight!\n");
            String blockAsString = HashArray.hashArray.get(i).toString();
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
            JsonElement txHashElement = jsonObject.get("tx hash");
            String txHash = txHashElement.getAsString();
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
        System.out.println("Processing block: " + block.getIndex() + " for session: " + session.getUserProperties().get("id"));
    }
}
