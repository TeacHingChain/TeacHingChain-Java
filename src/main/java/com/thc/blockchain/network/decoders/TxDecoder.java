package com.thc.blockchain.network.decoders;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.wallet.MainChain;
import com.thc.blockchain.wallet.TxPoolArray;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public final class TxDecoder implements Decoder.Text<Tx> {
    @Override
    public Tx decode(final String arg0) throws DecodeException {
        MainChain mc = new MainChain();
        try {
            mc.readTxPool();
            if (TxPoolArray.TxPool.size() >= 1) {
                for (Object o : TxPoolArray.TxPool) {
                    String tx = o.toString();
                    if (tx.contentEquals(arg0)) {
                        System.out.println("Already have this transaction in the pool, skipping..\n");
                    } else {
                        TxPoolArray.TxPool.add(arg0);
                    }
                }
            }
            return Constants.OBJECT_MAPPER.readValue(arg0, Tx.class);
        } catch (IOException ioe) {
            throw new DecodeException(arg0, "Unable to decode Tx", ioe);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
