package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class Block {

    @JsonProperty("index")
    private String index;
    @JsonProperty("time stamp")
    private String timeStamp;
    @JsonProperty("from address")
    private String fromAddress;
    @JsonProperty("to address")
    private String toAddress;
    @JsonProperty("tx hash")
    private String txHash;
    @JsonProperty ("merkle hash")
    private String merkleHash;
    @JsonProperty("nonce")
    private String Nonce;
    @JsonProperty("previous block hash")
    private String previousBlockHash;
    @JsonProperty("algo")
    private String algo;
    @JsonProperty("block hash")
    private String blockHash;
    @JsonProperty("difficulty")
    private String difficulty;
    @JsonProperty("amount")
    private String amount;

    @JsonCreator
    public Block(@JsonProperty("index") String index, @JsonProperty("time stamp") String timeStamp,
                 @JsonProperty("from address") String fromAddress, @JsonProperty("to address") String toAddress,
                 @JsonProperty("tx hash") String txHash, @JsonProperty("merkle hash") String merkleHash,
                 @JsonProperty("nonce") String Nonce, @JsonProperty("previous block hash") String previousBlockHash,
                 @JsonProperty("algo") String algo, @JsonProperty("block hash") String blockHash,
                 @JsonProperty("difficulty") String difficulty, @JsonProperty("amount") String amount) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(timeStamp);
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txHash);
        Objects.requireNonNull(merkleHash);
        Objects.requireNonNull(Nonce);
        Objects.requireNonNull(previousBlockHash);
        Objects.requireNonNull(algo);
        Objects.requireNonNull(blockHash);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(amount);
        this.index = index;
        this.timeStamp = timeStamp;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.txHash = txHash;
        this.merkleHash = merkleHash;
        this.Nonce = Nonce;
        this.previousBlockHash = previousBlockHash;
        this.algo = algo;
        this.blockHash = blockHash;
        this.difficulty = difficulty;
        this.amount = amount;
    }

    public Block() {
        super();
    }

    public String getIndex() {
        return this.index;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
    public String getFromAddress() { return this.fromAddress; }
    public String getToAddress() { return this.toAddress; }
    public String getTxHash() {
        return this.txHash;
    }
    public String getMerkleHash() {
        return this.merkleHash;
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
    public String getDifficulty() {
        return this.difficulty;
    }
    public String getAmount() {
        return this.amount;
    }



}

/*
long index, long currentTimeMillis, String sendKey, String recvKey, String coinbaseAddress, String txHash, long Nonce, String previousBlockHash, String algo, int difficulty, float amount
 */