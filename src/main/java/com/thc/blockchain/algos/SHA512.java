package com.thc.blockchain.algos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 {
    public static String generateSHA512Hash(String value) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0x00ff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm! Check algorithm parameter\n");
        }
        return hash;
    }
}


