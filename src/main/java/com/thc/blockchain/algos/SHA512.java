package com.thc.blockchain.algos;

import com.thc.blockchain.util.WalletLogger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 {

    private static MessageDigest digest;

    @SuppressWarnings("unused")
    public static String SHA512HashString(String value) {
        String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException nsae) {
            WalletLogger.logException(nsae, "severe", "No such algorithm exception occurred while trying to generate a SHA512 hash! See below:\n");
            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(nsae);
            WalletLogger.logException(nsae, "severe", stacktraceAsString);
        }
        return hash;
    }

    public static byte[] SHA512HashByteArray(byte[] obytes) {
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException nsae) {
            WalletLogger.logException(nsae, "severe", "No such algorithm exception occurred while trying to generate a SHA512 hash! See below:\n");
            String stacktraceAsString = WalletLogger.exceptionStacktraceToString(nsae);
            WalletLogger.logException(nsae, "severe", stacktraceAsString);
        }
        return digest.digest(obytes);

    }
}



