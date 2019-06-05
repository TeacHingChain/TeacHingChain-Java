package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class Tx {

    @JsonProperty("from address")
    private String fromAddress;
    @JsonProperty("to address")
    private String toAddress;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("tx hash")
    private String txHash;

    @JsonCreator
    public Tx(@JsonProperty("from address") String fromAddress, @JsonProperty("to address") String toAddress, @JsonProperty("amount")float amount, @JsonProperty String txHash) {
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txHash);
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.txHash = txHash;
    }

    public String getFromAddress() { return this.fromAddress; }
    public String getToAddress() { return this.toAddress; }
    public float getAmount() { return this.amount; }
    public String getTxHash() { return this.txHash; }
}
