package com.thc.blockchain.network.decoders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.blockchain.consensus.Consensus;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.wallet.HashArray;
import com.thc.blockchain.wallet.MainChain;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public final class BlockDecoder implements Decoder.Text<Block> {

    private MainChain mc = new MainChain();

    @Override
    public void destroy() {
    }

    @Override
    public void init(final EndpointConfig arg0) {
    }

    @Override
    public Block decode(final String arg0) throws DecodeException {
        try {
            System.out.println("Block Decoder activated!\n");
            System.out.println("arg0 says: " + arg0);
            System.out.println(Constants.OBJECT_MAPPER.readValue(arg0, Block.class));
            mc.readBlockChain();
            Consensus consensus = new Consensus();
            JsonElement checkIndex = new JsonParser().parse(arg0);
            JsonObject checkIndexObj = checkIndex.getAsJsonObject();
            JsonElement parseIndex = checkIndexObj.get("index");
            long parsedIndex = parseIndex.getAsLong();
            boolean verifyIndex = consensus.isBlockOrphan(parsedIndex);
            if (verifyIndex) {
                HashArray.hashArray.add(arg0);
                mc.writeBlockChain();
                System.out.println("size says: " + HashArray.hashArray.size());
            } else {
                System.out.println("A consensus error occurred! Block will not be added!\n");
            }
            return Constants.OBJECT_MAPPER.readValue(arg0, Block.class);
        } catch (IOException e) {
            throw new DecodeException(arg0, "Unable to decode text to Block", e);
        }
    }

    @Override
    public boolean willDecode(final String arg0) {
        return true;
    }

}

