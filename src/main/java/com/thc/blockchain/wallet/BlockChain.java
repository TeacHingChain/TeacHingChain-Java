package com.thc.blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class BlockChain {

    public BlockChain() {
        initHashArray();
    }

    public static List blockChain;

    private static void initHashArray() {
        blockChain = new ArrayList<>();
    }
}

