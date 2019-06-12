package com.thc.blockchain.network.decoders;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public final class BlockDecoder implements Decoder.Text<Block> {


    @Override
    public void destroy() {
    }

    @Override
    public void init(final EndpointConfig arg0) {
    }

    @Override
    public Block decode(final String arg0) throws DecodeException {
        try {
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

