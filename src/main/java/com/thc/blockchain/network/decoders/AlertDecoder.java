package com.thc.blockchain.network.decoders;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Alert;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public final class AlertDecoder implements Decoder.Text<Alert> {

    @Override
    public Alert decode(final String arg0) throws DecodeException {
        try {
            return Constants.OBJECT_MAPPER.readValue(arg0, Alert.class);
        } catch (IOException e) {
            throw new DecodeException(arg0, "Unable to decode text to Block", e);
        }
    }

    @Override
    public boolean willDecode(String arg0) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
