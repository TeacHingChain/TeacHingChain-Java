package com.thc.blockchain.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.blockchain.network.nodes.EndpointManager;
import com.thc.blockchain.wallet.MainChain;
import com.thc.blockchain.util.addresses.Base58;


public final class Constants {

    public static final int maxTxPoolTxs = 40; // 10 tx's (with 4 params each)
    public static final int maxBlockSize = 819200000; // will need to redesign serialized objects to be blk.dat files instead of a single chain.dat
    public static final String pubKey = "042d19b2d3538855f7fe63ec5c614300ac606412df779cd5d0c55e0fee798820d99eba1e244aa27c26c7966cbb8bf451600f303d691f572f4fdcd1325cba997177"; // write a check for the private key (ensure was derived from pubkey?)
    public static final String cbPubKey = "04e8a058194f40a50775e3e189006b953f617419889c4c339edfeb35811ddeb19acc8e1feae801d785102e8780904b78c28042b9b65143633e6e63b44a0dedab3c";
    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    public static final char ENCODED_ZERO = ALPHABET[0];
    public static final int[] INDEXES = new int[128];
    public static final String cbAddress = Base58.encode(cbPubKey.getBytes());
    public static final int maxClientConnections = 8;
    public static final String commPort = "7777";
    private static final String localNodeIP = "localhost:" + commPort;
    public static String programDataDir;
    public static String baseDir = System.getProperty("user.dir");
    public static final String syncKey = "sync";
    public static String syncNode1FQN = "ws://" + EndpointManager.node1ConfigIP + ":" + commPort + "/server/" + syncKey;
    public static final String updateKey = "update";
    public static String updateNode1FQN = "ws://" + EndpointManager.node1ConfigIP + ":" + commPort + "/server/" + updateKey;
    public static final String helloKey = "hello";
    public static  String helloNode1FQN = "ws://" + EndpointManager.node1ConfigIP + ":" + commPort + "/server/" + helloKey;
    public static  String syncNode2FQN = "ws://" + EndpointManager.node2ConfigIP + ":" + commPort + "/server/" + syncKey;
    public static  String updateNode2FQN = "ws://" + EndpointManager.node2ConfigIP + ":" + commPort + "/server/" + updateKey;
    public static  String helloNode2FQN = "ws://" + EndpointManager.node2ConfigIP + ":" + commPort + "/server/" + helloKey;
    public static final String pushChainKey = "push";
    public static  String pushChainNode1FQN = "ws://" + EndpointManager.node1ConfigIP + ":" + commPort + "/server/" + pushChainKey;
    public static  String pushChainNode2FQN = "ws://" + EndpointManager.node2ConfigIP + ":" + commPort + "/server/" + pushChainKey;
    public static final String genesisServerKey = "genesis";
    public static final String genesisNodeFQN = "ws://" + localNodeIP + "/server/" + genesisServerKey;
    public static final String genesisIndex = "0";
    public static final String genesisTimestamp = "1558813055732";
    public static final String genesisPszTimestamp = "TeacHingChain, a very simple crypto-currency implementation written in java for illustrative purposes and to help learn some of the nuances of blockchain!";
    public static final String genesisFromAddress = "";
    public static final String genesisToAddress = "";
    public static final String genesisTxHash = "5d0b0f61a978470869b78136bf5acc40e07ec361ad3faeb5feb28597a644de53";
    public static final String genesisMerkleHash = "c433108d65576596d46ae7840a06cb9f2bce102e0d5b2bfa32157d7675123d0f";
    public static final String genesisNonce = "2402952";
    public static final String genesisPreviousBlockHash = "none";
    public static final String genesisAlgo = "sha256";
    public static final String genesisHash = "000009c2024118e1c49e11b8d8f7aea2591f40d073dfcc53e5fe87fd2de81cd4";
    public static final String genesisDifficulty = "5";
    public static final String genesisAmount = String.valueOf(MainChain.nSubsidy);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

}

/*
TODO: Add constants for tx pool max size, block max size
 */