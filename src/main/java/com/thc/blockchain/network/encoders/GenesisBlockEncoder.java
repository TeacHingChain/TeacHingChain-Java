package com.thc.blockchain.network.encoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.GenesisBlock;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public final class GenesisBlockEncoder implements Encoder.Text<GenesisBlock> {
    @Override
    public String encode(GenesisBlock genesisBlock) throws EncodeException {
        try {
            System.out.println("Encoder activated!\n");
            System.out.println(genesisBlock);
            return Constants.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(genesisBlock);
        } catch (JsonProcessingException e) {
            throw new EncodeException(genesisBlock, "Unable to encode block", e);
        }
    }

    @Override
    public void init(final EndpointConfig arg0) {

    }

    @Override
    public void destroy() {

    }
}
