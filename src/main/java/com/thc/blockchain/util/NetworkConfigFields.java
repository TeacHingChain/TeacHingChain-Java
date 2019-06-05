package com.thc.blockchain.util;

import com.thc.blockchain.network.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.thc.blockchain.network.Constants.*;

public class NetworkConfigFields {

    public NetworkConfigFields() { setConfigProperties(); }

    public String syncNode1FQN;
    public String updateNode1FQN;
    public String helloNode1FQN;
    public String syncNode2FQN;
    public String updateNode2FQN;
    public String helloNode2FQN;
    public String pushChainNode1FQN;
    public String pushChainNode2FQN;
    public String pushTxNode1FQN;
    public String pushTxNode2FQN;
    private static Properties configProps = new Properties();

    private void setConfigProperties() {
        String configPath;
        try {
            if (BASEDIR.contains("apache-tomcat-8.5.23")) {
                configPath = Constants.BASEDIR + "/../../config/config.properties";
            } else {
                configPath = Constants.BASEDIR + "/config/config.properties";
            }
            configProps.load(new FileInputStream(configPath));
            syncNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + COMM_PORT + "/server/" + SYNC_KEY;
            syncNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + COMM_PORT + "/server/" + SYNC_KEY;
            updateNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + COMM_PORT + "/server/" + UPDATE_KEY;
            updateNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + COMM_PORT + "/server/" + UPDATE_KEY;
            helloNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + COMM_PORT + "/server/" + HELLO_KEY;
            helloNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + COMM_PORT + "/server/" + HELLO_KEY;
            pushChainNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + COMM_PORT + "/server/" + PUSH_CHAIN_KEY;
            pushChainNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + COMM_PORT + "/server/" + PUSH_CHAIN_KEY;
            pushTxNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + COMM_PORT + "/server/" + PUSH_TX_KEY;
            pushTxNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + COMM_PORT + "/server/" + PUSH_TX_KEY;
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " Failed to parse config properties! See details below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }
}


