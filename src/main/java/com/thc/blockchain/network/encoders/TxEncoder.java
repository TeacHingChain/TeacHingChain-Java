package com.thc.blockchain.network.encoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Tx;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public final class TxEncoder implements Encoder.Text<Tx> {
    @Override
    public String encode(Tx tx) throws EncodeException {
        try {
            return Constants.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(tx);
        } catch (JsonProcessingException e) {
            throw new EncodeException(tx, "Unable to encode tx", e);
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
