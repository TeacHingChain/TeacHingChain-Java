package com.thc.blockchain.network.encoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Alert;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public final class AlertEncoder implements Encoder.Text<Alert> {
    @Override
    public String encode(Alert alert) throws EncodeException {
        try {
            return Constants.OBJECT_MAPPER.writeValueAsString(alert);
        } catch (JsonProcessingException e) {
            throw new EncodeException(alert, "Unable to encode alert", e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
