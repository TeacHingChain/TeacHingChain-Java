package com.thc.blockchain.network.nodes;

import com.thc.blockchain.network.nodes.client.endpoints.*;
import com.thc.blockchain.util.NetworkConfigFields;
import com.thc.blockchain.util.WalletLogger;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.thc.blockchain.network.Constants.GENESIS_NODE_FQN;

public final class EndpointManager {

    private final static CountDownLatch messageLatch = new CountDownLatch(1);
    private static String uri;
    private final NetworkConfigFields configFields = new NetworkConfigFields();

    public void connectAsClient(String reason) {
        WebSocketContainer container;
        if (reason.contentEquals("update")) {
            if (!configFields.updateNode1FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.updateNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
            if (!configFields.updateNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.updateNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }
        } else if (reason.contentEquals("hello")) {
            if (!configFields.helloNode1FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.helloNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
            if (!configFields.helloNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.helloNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }

        } else if (reason.contentEquals("sync")) {
            if (!configFields.syncNode1FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.syncNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncAlertClient.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
            if (!configFields.syncNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.syncNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncAlertClient.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }
        } else if (reason.contentEquals("init chain")) {
            try {
                container = ContainerProvider.getWebSocketContainer();
                uri = GENESIS_NODE_FQN;
                System.out.println("Connecting to " + uri);
                container.connectToServer(GenesisChainClientEndpoint.class, URI.create(uri));
                messageLatch.await(1, TimeUnit.SECONDS);
            } catch (DeploymentException de) {
                WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
            } catch (InterruptedException ie) {
                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        } else if (reason.contentEquals("sync-block")) {
            if (!configFields.pushChainNode1FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.pushChainNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncBlockClient.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
            if (!configFields.pushChainNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.pushChainNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncBlockClient.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }
        } else if (reason.contentEquals("push tx")) {
            if (!configFields.pushTxNode1FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.pushTxNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(TxClientEndpoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
            if (!configFields.pushTxNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.pushTxNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(TxClientEndpoint.class, URI.create(uri));
                    messageLatch.await(1, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }
        }
    }

    public boolean isNodeConnected(int node) {
        WebSocketContainer container;
        if (node == 1) {
            if (!configFields.helloNode1FQN.isEmpty())
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.helloNode1FQN;
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(500, TimeUnit.MILLISECONDS);
                    return container.connectToServer(HelloClientEndpoint.class, URI.create(uri)).isOpen();
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }

        if (node == 2) {
            if (!configFields.helloNode2FQN.isEmpty()) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = configFields.helloNode2FQN;
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(500, TimeUnit.MILLISECONDS);
                    return container.connectToServer(HelloClientEndpoint.class, URI.create(uri)).isOpen();
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured!\n");
                return false;
            }
        }
        return false;
    }
}



