package com.thc.blockchain.algos;
import com.lambdaworks.crypto.SCryptUtil;

import java.nio.charset.StandardCharsets;

public class Scrypt {

    public static String generateScryptHash(String value) {
        String scryptHash = SCryptUtil.scrypt(value, 100, 50, 50);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0x00ff) + 0x100, 16).substring(1));
        }
        String hash;
        hash = sb.toString();

        return hash;
    }
}
