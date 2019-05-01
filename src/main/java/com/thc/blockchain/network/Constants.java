package com.thc.blockchain.network;


public final class Constants {

    public static final byte[] magicStr = "0xtesttestest0420".getBytes(); // will be used for client verification
    public static final int maxTxPoolTxs = 10;
    public static final int maxBlockSize = 819200000; // will need to redesign serialized objects to be blk.dat files instead of a single chain.dat
    public static final String pubKey = "109348381146730432945387474263032604320659227953931582747864448786837442830597";
    public static final int maxClientConnections = 8;
}

/*
TODO: Add constants for tx pool max size, block max size
 */