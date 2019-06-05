package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Block {

    @JsonProperty("index")
    private final String index;
    @JsonProperty("time stamp")
    private final String timeStamp;
    @JsonProperty("from address")
    private final String fromAddress;
    @JsonProperty("to address")
    private final String toAddress;
    @JsonProperty("transactions")
    private final String[] txs;
    @JsonProperty ("merkle root")
    private final String merkleRoot;
    @JsonProperty("nonce")
    private final String Nonce;
    @JsonProperty("previous block hash")
    private final String previousBlockHash;
    @JsonProperty("algo")
    private final String algo;
    @JsonProperty("block hash")
    private final String blockHash;
    @JsonProperty("target")
    private final String target;
    @JsonProperty("amount")
    private final String amount;

    @JsonCreator
    public Block(@JsonProperty("index") String index, @JsonProperty("time stamp") String timeStamp,
                 @JsonProperty("from address") String fromAddress, @JsonProperty("to address") String toAddress,
                 @JsonProperty("transactions") String[] txs, @JsonProperty("merkle root") String merkleRoot,
                 @JsonProperty("nonce") String Nonce, @JsonProperty("previous block hash") String previousBlockHash,
                 @JsonProperty("algo") String algo, @JsonProperty("block hash") String blockHash,
                 @JsonProperty("target") String target, @JsonProperty("amount") String amount) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(timeStamp);
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txs);
        Objects.requireNonNull(merkleRoot);
        Objects.requireNonNull(Nonce);
        Objects.requireNonNull(previousBlockHash);
        Objects.requireNonNull(algo);
        Objects.requireNonNull(blockHash);
        Objects.requireNonNull(target);
        Objects.requireNonNull(amount);
        this.index = index;
        this.timeStamp = timeStamp;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.txs = txs;
        this.merkleRoot = merkleRoot;
        this.Nonce = Nonce;
        this.previousBlockHash = previousBlockHash;
        this.algo = algo;
        this.blockHash = blockHash;
        this.target = target;
        this.amount = amount;
    }

    public String getIndex() {
        return this.index;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
    public String getFromAddress() { return this.fromAddress; }
    public String getToAddress() { return this.toAddress; }
    public String[] getTransactions() {
        return this.txs;
    }
    public String getMerkleRoot() {
        return this.merkleRoot;
    }
    public String getNonce() {
        return this.Nonce;
    }
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    public String getAlgo() {
        return this.algo;
    }
    public String getBlockHash() { return this.blockHash; }
    public String getTarget() {
        return this.target;
    }
    public String getAmount() {
        return this.amount;
    }



}

/*
long index, long currentTimeMillis, String sendKey, String recvKey, String coinbaseAddress, String txHash, long Nonce, String previousBlockHash, String algo, int targetAsBigDec, float amount
 */