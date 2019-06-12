package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Block {

    @JsonProperty("index")
    private final long index;
    @JsonProperty("time stamps")
    private final long[] timeStamps;
    @JsonProperty("tx inputs")
    private final String[] txins;
    @JsonProperty("tx outputs")
    private final String[] txouts;
    @JsonProperty("transactions")
    private final String[] txs;
    @JsonProperty ("merkle root")
    private final String merkleRoot;
    @JsonProperty("nonce")
    private final long Nonce;
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
    @JsonProperty("amounts")
    private final double[] amounts;

    @JsonCreator
    public Block(@JsonProperty("index") long index, @JsonProperty("time stamps") long[] timeStamps,
                 @JsonProperty("tx inputs") String[] txins, @JsonProperty("tx outputs") String[] txouts,
                 @JsonProperty("transactions") String[] txs, @JsonProperty("merkle root") String merkleRoot,
                 @JsonProperty("nonce") Long Nonce, @JsonProperty("previous block hash") String previousBlockHash,
                 @JsonProperty("algo") String algo, @JsonProperty("block hash") String blockHash,
                 @JsonProperty("target") String target, @JsonProperty("difficulty") double difficulty, @JsonProperty("amounts") double[] amounts) {
        Objects.requireNonNull(timeStamps);
        Objects.requireNonNull(txins);
        Objects.requireNonNull(txouts);
        Objects.requireNonNull(txs);
        Objects.requireNonNull(merkleRoot);
        Objects.requireNonNull(Nonce);
        Objects.requireNonNull(previousBlockHash);
        Objects.requireNonNull(algo);
        Objects.requireNonNull(blockHash);
        Objects.requireNonNull(target);
        Objects.requireNonNull(amounts);
        this.index = index;
        this.timeStamps = timeStamps;
        this.txins = txins;
        this.txouts = txouts;
        this.txs = txs;
        this.merkleRoot = merkleRoot;
        this.Nonce = Nonce;
        this.previousBlockHash = previousBlockHash;
        this.algo = algo;
        this.blockHash = blockHash;
        this.target = target;
        this.difficulty = difficulty;
        this.amounts = amounts;
    }

    public long getIndex() {
        return this.index;
    }
    public long[] getTimeStamps() {
        return this.timeStamps;
    }
    public String[] getTxins() { return this.txins; }
    public String[] getTxouts() { return this.txouts; }
    public String[] getTransactions() {
        return this.txs;
    }
    public String getMerkleRoot() { return this.merkleRoot; }
    public Long getNonce() {
        return this.Nonce;
    }
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    public String getAlgo() { return this.algo; }
    public String getBlockHash() { return this.blockHash; }
    public String getTarget() {
        return this.target;
    }
    public double getDifficulty() { return this.difficulty; }
    public double[] getAmounts() {
        return this.amounts;
    }



}
