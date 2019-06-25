package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class GenesisBlock {

    @JsonProperty("index")
    private final long index;
    @JsonProperty("time stamp")
    private final long timeStamp;
    @JsonProperty("pszTimestamp")
    private final String pszTimestamp;
    @JsonProperty("from address")
    private final String fromAddress;
    @JsonProperty("to address")
    private final String toAddress;
    @JsonProperty("tx hash")
    private final String txHash;
    @JsonProperty ("merkle root")
    private final String merkleRoot;
    @JsonProperty("nonce")
    private final long nonce;
    @JsonProperty("previous block hash")
    private final String previousBlockHash;
    @JsonProperty("algo")
    private final String algo;
    @JsonProperty("block hash")
    private final String blockHash;
    @JsonProperty("target")
    private final String target;
    @JsonProperty("difficulty")
    private final double difficulty;
    @JsonProperty("amount")
    private final double amount;
    
    @JsonCreator
    public GenesisBlock(@JsonProperty("index") long index, @JsonProperty("time stamp") long timeStamp,
                        @JsonProperty("pszTimestamp") String pszTimestamp, @JsonProperty("from address") String fromAddress,
                        @JsonProperty("to address") String toAddress, @JsonProperty("tx hash") String txHash,
                        @JsonProperty("merkle root") String merkleRoot, @JsonProperty("nonce") long nonce,
                        @JsonProperty("previous block hash") String previousBlockHash, @JsonProperty("algo") String algo,
                        @JsonProperty("block hash") String blockHash, @JsonProperty("target") String target,
                        @JsonProperty("difficulty") double difficulty, @JsonProperty("amount") double amount) {
        Objects.requireNonNull(pszTimestamp);
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txHash);
        Objects.requireNonNull(merkleRoot);
        Objects.requireNonNull(previousBlockHash);
        Objects.requireNonNull(algo);
        Objects.requireNonNull(blockHash);
        Objects.requireNonNull(target);
        this.index = index;
        this.timeStamp = timeStamp;
        this.pszTimestamp = pszTimestamp;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.txHash = txHash;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
        this.previousBlockHash = previousBlockHash;
        this.algo = algo;
        this.blockHash = blockHash;
        this.target = target;
        this.difficulty = difficulty;
        this.amount = amount;
    }

    public long getIndex() {
        return this.index;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public String getPszTimestamp() {
        return this.pszTimestamp;
    }
    public String getFromAddress() {
        return this.fromAddress;
    }
    public String getToAddress() {
        return this.toAddress;
    }
    public String getTxHash() {
        return this.txHash;
    }
    public String getMerkleRoot() {
        return this.merkleRoot;
    }
    public long getNonce() {
        return this.nonce;
    }
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    public String getAlgo() {
        return this.algo;
    }
    public String getBlockHash() {
        return this.blockHash;
    }
    public String getTarget() {
        return this.target;
    }
    public double getDifficulty() {
        return this.difficulty;
    }
    public double getAmount() {
        return this.amount;
    }
}
