package com.thc.blockchain.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.util.addresses.Base58;
import com.thc.blockchain.wallet.MainChain;


public final class Constants {


    public static final int maxTxPoolTxs = 40; // 10 tx's (with 4 params each)
    public static final int maxBlockSize = 819200000; // will need to redesign serialized objects to be blk.dat files instead of a single chain.dat
    public static final String pubKey = "042d19b2d3538855f7fe63ec5c614300ac606412df779cd5d0c55e0fee798820d99eba1e244aa27c26c7966cbb8bf451600f303d691f572f4fdcd1325cba997177"; // write a check for the private key (ensure was derived from pubkey?)
    public static final String cbPubKey = "04e8a058194f40a50775e3e189006b953f617419889c4c339edfeb35811ddeb19acc8e1feae801d785102e8780904b78c28042b9b65143633e6e63b44a0dedab3c";
    public static final char[] BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    public static final String HEXES = "0123456789abcdef";
    public static final char ENCODED_ZERO = BASE58_ALPHABET[0];
    public static final int[] INDEXES = new int[128];
    public static final String cbAddress = Base58.encode(SHA256.SHA256HashByteArray(SHA256.SHA256HashByteArray(cbPubKey.getBytes())));
    public static final int maxClientConnections = 8;
    public static final String commPort = "7777";
    private static final String localNodeIP = "localhost:" + commPort;
    public static String baseDir = System.getProperty("user.dir");
    public static final String syncKey = "sync";
    public static final String updateKey = "update";
    public static final String helloKey = "hello";
    public static final String pushChainKey = "push";
    public static String pushTxKey = "tx";
    public static final String genesisServerKey = "genesis";
    public static final String genesisNodeFQN = "ws://" + localNodeIP + "/server/" + genesisServerKey;
    public static final String genesisIndex = "0";
    public static final String genesisTimestamp = "1559271704446";
    public static final String genesisPszTimestamp = "TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!";
    public static final String genesisFromAddress = "";
    public static final String genesisToAddress = "";
    public static final String genesisTxHash = "5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53";
    public static final String genesisMerkleRoot = "5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53";
    public static final String genesisNonce = "540875";
    public static final String genesisPreviousBlockHash = "none";
    public static final String genesisAlgo = "sha256";
    public static final String genesisHash = "00000c754ed334ced901c1be18403baffc34ff9512dcdce9b2b947e90cfa2e41";
    public static final String genesisDifficulty = "5";
    public static final String genesisAmount = String.valueOf(MainChain.nSubsidy);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}

/*
TODO: Add constants for tx pool max size, block max size
 */