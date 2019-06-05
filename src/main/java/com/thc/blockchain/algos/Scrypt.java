package com.thc.blockchain.algos;

import com.lambdaworks.crypto.SCryptUtil;
import java.nio.charset.StandardCharsets;

class Scrypt {

    public static String generateScryptHash(String value) {
        String scryptHash = SCryptUtil.scrypt(value, 100, 50, 50);
        byte[] bytes = scryptHash.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0x00ff) + 0x100, 16).substring(1));
        }
        String hash;
        hash = sb.toString();
        return hash;
    }
}
