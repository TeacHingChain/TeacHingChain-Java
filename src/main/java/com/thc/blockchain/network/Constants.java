package com.thc.blockchain.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.blockchain.algos.SHA256;
import com.thc.blockchain.util.addresses.Base58;
import com.thc.blockchain.wallet.MainChain;
import java.math.BigInteger;


public final class Constants {

    private static final int TARGET_WINDOW = 5;
    public static final int MAX_TX_POOL_TXS = 10; // 10 tx's
    public static final double TX_FEE = 0.004;
    public static final int MAX_BLOCK_SIZE = 819200000; // Maybe use .getBytes() and .size() to get this value?
    public static final char[] BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    public static final String HEXES = "0123456789abcdef";
    public static final char ENCODED_ZERO = BASE58_ALPHABET[0];
    public static final int[] INDEXES = new int[128];
    public static final int MAX_CLIENT_CONNECTIONS = 8;
    public static final String COMM_PORT = "7777";
    private static final String LOCAL_NODE_IP = "localhost:" + COMM_PORT;
    public static final String BASEDIR = System.getProperty("user.dir");
    public static final String SYNC_KEY = "sync";
    public static final String UPDATE_KEY = "update";
    public static final String HELLO_KEY = "hello";
    public static final String PUSH_CHAIN_KEY = "push";
    public static final String PUSH_TX_KEY = "tx";
    public static final String GENESIS_SERVER_KEY = "genesis";
    public static final String GENESIS_NODE_FQN = "ws://" + LOCAL_NODE_IP + "/server/" + GENESIS_SERVER_KEY;
    public static final long GENESIS_INDEX = 0;
    public static final long GENESIS_TIMESTAMP = 1560037287230L;
    public static final String GENESIS_PSZ_TIMESTAMP = "TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!";
    public static final String GENESIS_FROM_ADDRESS = "";
    public static final String GENESIS_TO_ADDRESS = "";
    public static final String GENESIS_TX_HASH = "78a29d3f5cea2a77434630fb5cd081ad19b63a61fba6c3658f4feba62660e82d";
    public static final String GENESIS_MERKLE_ROOT = "78a29d3f5cea2a77434630fb5cd081ad19b63a61fba6c3658f4feba62660e82d";
    public static final long GENESIS_NONCE = 162007;
    public static final String GENESIS_PREVIOUS_BLOCK_HASH = "none";
    public static final String GENESIS_ALGO = "sha256";
    public static final String GENESIS_HASH = "000002d774d436d9275f9c546e718e3958b936599cc1a55aa39eba925b5af142";
    public static final String GENESIS_TARGET = "00000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    public static final double GENESIS_DIFFICULTY = 1.0;
    public static final double GENESIS_AMOUNT = MainChain.nSubsidy;
    private static final int TARGET_BLOCK_TIME = 60;
    public static final int TARGET_WINDOW_DURATION = TARGET_BLOCK_TIME * TARGET_WINDOW;
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}

/*
TODO: Add constants for tx pool max size, block max size


--------------------------BLOCK DETAILS--------------------------


Index:
0


Unix time stamp:
1560037287230


Data:
TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!


Previous:
none


Nonce:
162007


Tx hash:
78a29d3f5cea2a77434630fb5cd081ad19b63a61fba6c3658f4feba62660e82d


Merkle root:
78a29d3f5cea2a77434630fb5cd081ad19b63a61fba6c3658f4feba62660e82d

Target:
00000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee


Difficulty:
1.0



 */