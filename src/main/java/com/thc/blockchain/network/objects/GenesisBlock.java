package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class GenesisBlock {

    @JsonProperty("index")
    private final String index;
    @JsonProperty("time stamp")
    private final String timeStamp;
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
    public GenesisBlock(@JsonProperty("index") String index, @JsonProperty("time stamp") String timeStamp,
                        @JsonProperty("pszTimestamp") String pszTimestamp, @JsonProperty("from address") String fromAddress,
                        @JsonProperty("to address") String toAddress, @JsonProperty("tx hash") String txHash,
                        @JsonProperty("merkle root") String merkleRoot, @JsonProperty("nonce") String Nonce,
                        @JsonProperty("previous block hash") String previousBlockHash, @JsonProperty("algo") String algo,
                        @JsonProperty("block hash") String blockHash, @JsonProperty("target") String target,
                        @JsonProperty("amount") String amount) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(timeStamp);
        Objects.requireNonNull(pszTimestamp);
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txHash);
        Objects.requireNonNull(merkleRoot);
        Objects.requireNonNull(Nonce);
        Objects.requireNonNull(previousBlockHash);
        Objects.requireNonNull(algo);
        Objects.requireNonNull(blockHash);
        Objects.requireNonNull(target);
        Objects.requireNonNull(amount);
        this.index = index;
        this.timeStamp = timeStamp;
        this.pszTimestamp = pszTimestamp;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.txHash = txHash;
        this.merkleRoot = merkleRoot;
        this.Nonce = Nonce;
        this.previousBlockHash = previousBlockHash;
        this.algo = algo;
        this.blockHash = blockHash;
        this.target = target;
        this.amount = amount;
    }

    @SuppressWarnings("unused")
    public String getIndex() {
        return this.index;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
    @SuppressWarnings("unused")
    public String getPszTimestamp() {
        return this.pszTimestamp;
    }
    @SuppressWarnings("unused")
    public String getFromAddress() {
        return this.fromAddress;
    }
    @SuppressWarnings("unused")
    public String getToAddress() { return this.toAddress; }
    @SuppressWarnings("unused")
    public String getTxHash() {
        return this.txHash;
    }
    @SuppressWarnings("unused")
    public String getMerkleRoot() {
        return this.merkleRoot;
    }
    @SuppressWarnings("unused")
    public String getNonce() {
        return this.Nonce;
    }
    @SuppressWarnings("unused")
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    @SuppressWarnings("unused")
    public String getAlgo() {
        return this.algo;
    }
    public String getBlockHash() { return this.blockHash; }
    @SuppressWarnings("unused")
    public String getTarget() {
        return this.target;
    }
    @SuppressWarnings("unused")
    public String getAmount() {
        return this.amount;
    }
}
