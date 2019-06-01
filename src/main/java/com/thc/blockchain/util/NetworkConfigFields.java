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
            if (baseDir.contains("apache-tomcat-8.5.23")) {
                configPath = Constants.baseDir + "/../../config/config.properties";
            } else {
                configPath = Constants.baseDir + "/config/config.properties";
            }
            configProps.load(new FileInputStream(configPath));
            syncNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + commPort + "/server/" + syncKey;
            syncNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + commPort + "/server/" + syncKey;
            updateNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + commPort + "/server/" + updateKey;
            updateNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + commPort + "/server/" + updateKey;
            helloNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + commPort + "/server/" + helloKey;
            helloNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + commPort + "/server/" + helloKey;
            pushChainNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + commPort + "/server/" + pushChainKey;
            pushChainNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + commPort + "/server/" + pushChainKey;
            pushTxNode1FQN = "ws://" + configProps.getProperty("node1") + ":" + commPort + "/server/" + pushTxKey;
            pushTxNode2FQN = "ws://" + configProps.getProperty("node2") + ":" + commPort + "/server/" + pushTxKey;
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " Failed to parse config properties! See details below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }
}


