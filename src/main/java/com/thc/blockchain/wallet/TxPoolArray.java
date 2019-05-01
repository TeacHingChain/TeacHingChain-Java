package com.thc.blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class TxPoolArray {

    public TxPoolArray() {
        initTxPoolArray();

    }

    public static List TxPool;

    private static void initTxPoolArray() {
        TxPool = new ArrayList<>();

    }
}

