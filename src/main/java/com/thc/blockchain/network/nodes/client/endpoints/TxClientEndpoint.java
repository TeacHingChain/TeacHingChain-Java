package com.thc.blockchain.network.nodes.client.endpoints;

import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.network.decoders.TxDecoder;
import com.thc.blockchain.network.encoders.TxEncoder;
import com.thc.blockchain.network.nodes.NodeManager;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.util.WalletLogger;
import com.thc.blockchain.wallet.MainChain;
import com.thc.blockchain.wallet.TxPoolArray;
import javax.websocket.ClientEndpoint;
import javax.websocket.DecodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@SuppressWarnings("unused")
@ClientEndpoint(encoders = { TxEncoder.class }, decoders = { TxDecoder.class })

public class TxClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        try {
            MainChain mc = new MainChain();
            if (NodeManager.registerNode(session, "tx-client")) {
                System.out.println("Registered as: " + session.getUserProperties().get("id"));
                mc.readTxPool();
                for (String tx : TxPoolArray.TxPool) {
                    Tx decodedTx = new TxDecoder().decode(tx);
                    String fromAddress = decodedTx.getFromAddress();
                    String toAddress = decodedTx.getToAddress();
                    float amount = decodedTx.getAmount();
                    String txHash = decodedTx.getTxHash();
                    byte[] txHashBytes = MainChain.swapEndianness(MainChain.hexStringToByteArray(MainChain.getHex((fromAddress + toAddress + amount).getBytes())));
                    String txHashCheck = MainChain.getHex(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(txHashBytes)));
                    boolean isTxHashValid = txHashCheck.contentEquals(txHash);
                    if (isTxHashValid) {
                        NodeManager.pushTx(decodedTx, session);
                    }
                }
            }
        } catch (DecodeException de) {
            WalletLogger.logException(de, "warning", WalletLogger.getLogTimeStamp() + " An error occurred decoding a tx! See details below:\n" + WalletLogger.exceptionStacktraceToString(de) );
        }
    }
}
