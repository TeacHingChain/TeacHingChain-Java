package com.thc.blockchain.network.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Alert {

    @JsonProperty("alert type")
    private final String alertType;
    @JsonProperty("alert message")
    private final String alertMessage;

    @JsonCreator
    public Alert(@JsonProperty("alert type") final String alertType, @JsonProperty("alert message") final String alertMessage) {
        Objects.requireNonNull(alertType);
        Objects.requireNonNull(alertMessage);
        this.alertType = alertType;
        this.alertMessage = alertMessage;
    }

    public String getAlertType() {
        return this.alertType;
    }

    public String getAlertMessage() {
        return this.alertMessage;
    }
}
