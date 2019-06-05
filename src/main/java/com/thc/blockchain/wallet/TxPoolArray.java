package com.thc.blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class TxPoolArray {

    TxPoolArray() {
        initTxPoolArray();

    }

    public static List<String> TxPool;

    private static void initTxPoolArray() {
        TxPool = new ArrayList<>();

    }
}

