package com.thc.blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class KeyRing {

    public KeyRing() { initKeyRing();}

    public static List<String> keyRing;

    private static void initKeyRing() { keyRing = new ArrayList<>(); }
}
