package com.thc.blockchain.util;

import com.thc.blockchain.network.Constants;
import com.thc.blockchain.network.nodes.ClientManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ConfigParser {

    public void readConfigFile() {

        File configFile = new File(Constants.baseDir + "/thc.conf");
        try {
            if (configFile.exists()) {
                List<String> configList = Files.readAllLines(Paths.get(Constants.baseDir + "/thc.conf"));
                for (String configLine : configList) {
                    if (configLine.contains("node1")) {
                        String[] configLineSplit = configLine.split("node1=");
                        ClientManager.node1ConfigIP = configLineSplit[1];
                        Constants.syncNode1FQN = "ws://" + ClientManager.node1ConfigIP + ":" + Constants.commPort + "/server/" + Constants.syncKey;
                        Constants.updateNode1FQN = "ws://" + ClientManager.node1ConfigIP + ":" + Constants.commPort + "/server/" + Constants.updateKey;
                        Constants.helloNode1FQN = "ws://" + ClientManager.node1ConfigIP + ":" + Constants.commPort + "/server/" + Constants.helloKey;
                    } else if (configLine.contains("node2")) {
                        String[] configLineSplit = configLine.split("node2=");
                        ClientManager.node2ConfigIP = configLineSplit[1];
                        Constants.syncNode2FQN = "ws://" + ClientManager.node2ConfigIP + ":" + Constants.commPort + "/server/" + Constants.syncKey;
                        Constants.updateNode2FQN = "ws://" + ClientManager.node2ConfigIP + ":" + Constants.commPort + "/server/" + Constants.updateKey;
                        Constants.helloNode2FQN = "ws://" + ClientManager.node2ConfigIP + ":" + Constants.commPort + "/server/" + Constants.helloKey;
                    } else if (configLine.contains("datadir")) {
                        String[] configLineSplit = configLine.split("datadir=");
                        Constants.programDataDir = configLineSplit[1];
                    }
                }
            } else {
                Constants.programDataDir = Constants.baseDir;
            }
        } catch (IOException ioe) {
            WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while parsing config file! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
        }
    }
}
