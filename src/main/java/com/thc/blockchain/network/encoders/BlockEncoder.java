package com.thc.blockchain.network.encoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Block;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public final class BlockEncoder implements Encoder.Text<Block> {

    @Override
    public String encode(final Block block) throws EncodeException {
        try {
            return Constants.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(block);
        } catch (JsonProcessingException e) {
            throw new EncodeException(block, "Unable to encode block", e);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(final EndpointConfig arg0) {
    }


}

