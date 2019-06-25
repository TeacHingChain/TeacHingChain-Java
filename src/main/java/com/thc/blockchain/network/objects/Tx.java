package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.web3j.crypto.Sign;

import java.util.Objects;

public final class Tx {

    @JsonProperty("time stamp")
    private final long timeStamp;
    @JsonProperty("from address")
    private final String fromAddress;
    @JsonProperty("to address")
    private final String toAddress;
    @JsonProperty("tx amount")
    private final double amount;
    @JsonProperty("tx hash")
    private final String txHash;
    @JsonProperty("signature data")
    private final Sign.SignatureData sigData;

    @JsonCreator
    public Tx(@JsonProperty("time stamp") long timeStamp, @JsonProperty("from address") String fromAddress, @JsonProperty("to address")
            String toAddress, @JsonProperty("tx amount") double amount, @JsonProperty("tx hash") String txHash,
            @JsonProperty("signature data") Sign.SignatureData sigData){
        Objects.requireNonNull(fromAddress);
        Objects.requireNonNull(toAddress);
        Objects.requireNonNull(txHash);
        Objects.requireNonNull(sigData);
        this.timeStamp = timeStamp;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.txHash = txHash;
        this.sigData = sigData;
    }

    public long getTimeStamp() { return this.timeStamp; }
    public String getFromAddress() { return this.fromAddress; }
    public String getToAddress() { return this.toAddress; }
    public double getAmount() { return this.amount; }
    public String getTxHash() { return this.txHash; }
    public Sign.SignatureData getSigData() { return this.sigData; }
}
