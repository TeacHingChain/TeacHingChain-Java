package com.thc.blockchain.algos;

import com.thc.blockchain.util.WalletLogger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    public static String generateSHA256Hash(String value) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0x00ff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException nsae) {
            WalletLogger.logException(nsae, "severe", "No such algorithm exception occurred while trying to generate a SHA512 hash! See below:\n");
            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(nsae);
            WalletLogger.logException(nsae, "severe", stacktraceAsString);
        }
        return hash;
    }
}

/*
TODO: Add additional algorithms, play with append method for diff adjustment?
 */
