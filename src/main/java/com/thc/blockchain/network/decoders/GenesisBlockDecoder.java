package com.thc.blockchain.network.decoders;
/*
{"index":"0","time stamp":"1556849753564","pszTimestamp":"test","receive key":"","miner key":"","tx hash":"a96e0beb59a16b085a7d2b3b5ffd6e5971870aa2903c6df86f26fa908ded2e21","merkle hash":"519f2132ebe4ede7fc41fe3f64497cf819ecbd5176a05a5be68e416a54558a85","nonce":"279442","previous block hash":"null","algo":"sha256","difficulty":"5","amount":"50.0","genesisBlockIndex":"0","genesisBlockTime":"1556849753564","genesisBlockPszTimestamp":"test","genesisBlockRecvKey":"","genesisBlockMinerKey":"","genesisBlockTxHash":"a96e0beb59a16b085a7d2b3b5ffd6e5971870aa2903c6df86f26fa908ded2e21","genesisBlockMerkleHash":"519f2132ebe4ede7fc41fe3f64497cf819ecbd5176a05a5be68e416a54558a85","genesisBlockNonce":"279442","genesisBlockAlgo":"sha256","genesisBlockDifficulty":"5","genesisBlockAmount":"50.0"}
 */

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.wallet.BlockChain;
import com.thc.blockchain.wallet.MainChain;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;


public final class GenesisBlockDecoder implements Decoder.Text<GenesisBlock> {

    private MainChain mc = new MainChain();

    @Override
    public GenesisBlock decode(String arg0) throws DecodeException {
        System.out.println("Genesis Block Decoder activated!\n");
        System.out.println("arg0 says: " + arg0);
        try {
            System.out.println(Constants.OBJECT_MAPPER.readValue(arg0, GenesisBlock.class));
            BlockChain blockChain = new BlockChain();
            BlockChain.blockChain.add(arg0);
            mc.writeBlockChain();
            return Constants.OBJECT_MAPPER.readValue(arg0, GenesisBlock.class);
        } catch (IOException e) {
            throw new DecodeException(arg0, "Unable to decode text to Block", e);
        }
    }

    @Override
    public boolean willDecode(String arg0) {
        return true;
    }

    @Override
    public void init(final EndpointConfig arg0) {

    }

    @Override
    public void destroy() {

    }
}
