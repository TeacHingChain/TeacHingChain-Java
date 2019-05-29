package com.thc.blockchain.network.nodes;

import com.thc.blockchain.network.nodes.client.endpoints.*;
import com.thc.blockchain.util.WalletLogger;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static com.thc.blockchain.network.Constants.*;

public class ClientManager {

    private final static CountDownLatch messageLatch = new CountDownLatch(1);
    public static String node1ConfigIP;
    public static String node2ConfigIP;
    private static String uri;

    public void connectAsClient(String reason) {
        WebSocketContainer container;
        if (reason.contentEquals("update")) {
            if (node1ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = updateNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured in config file!\n");
            }
            if (node2ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = updateNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 2 configured in config file!\n");
            }
        } else if (reason.contentEquals("hello")) {
            if (node1ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = helloNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            } else {
                System.out.println("No node 1 configured in config file!\n");
            }
            if (node2ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = helloNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            }

        } else if (reason.contentEquals("sync")) {
            if (node1ConfigIP != null) {
                if (isNodeConnected(1)) {
                    try {
                        container = ContainerProvider.getWebSocketContainer();
                        uri = syncNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncAlertClient.class, URI.create(uri));
                        messageLatch.await(5, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                    }
                } else {
                    System.out.println("Unable to reach hello server, please check your network connection!\n");
                }
            } else {
                System.out.println("No node 1 configured in config file!\n");
            }
            if (node2ConfigIP != null) {
                if (isNodeConnected(2)) {
                    try {
                        container = ContainerProvider.getWebSocketContainer();
                        uri = syncNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncAlertClient.class, URI.create(uri));
                        messageLatch.await(5, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                    }
                } else {
                    System.out.println("Unable to reach hello server, please check your network connection!\n");
                }
            } else {
                System.out.println("No node 2 configured in config file\n");
            }
        } else if (reason.contentEquals("init chain")) {
            try {
                container = ContainerProvider.getWebSocketContainer();
                uri = genesisNodeFQN;
                System.out.println("Connecting to " + uri);
                container.connectToServer(GenesisChainClientEndpoint.class, URI.create(uri));
                messageLatch.await(5, TimeUnit.SECONDS);
            } catch (DeploymentException de) {
                WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
            } catch (InterruptedException ie) {
                WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
            }
        } else if (reason.contentEquals("push-chain")) {
            if (node1ConfigIP != null) {
                if (isNodeConnected(1)) {
                    try {
                        container = ContainerProvider.getWebSocketContainer();
                        uri = pushChainNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncBlockClient.class, URI.create(uri));
                        messageLatch.await(5, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                    }
                }
            }
        }
        if (node2ConfigIP != null) {
            if (isNodeConnected(2)) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = pushChainNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncBlockClient.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", WalletLogger.getLogTimeStamp() + " Deployment exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(de));
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", WalletLogger.getLogTimeStamp() + " Interrupted exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ie));
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to connect to a peer as a client! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                }
            }
        }
    }

    public boolean isNodeConnected(int node) {
        WebSocketContainer container;
        if (node == 1) {
            if (node1ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = helloNode1FQN;
                    System.out.println("Connecting to " + uri);
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
        }
        if (node == 2) {
            if (node2ConfigIP != null) {
                try {
                    container = ContainerProvider.getWebSocketContainer();
                    uri = helloNode2FQN;
                    System.out.println("Connecting to " + uri);
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
            }
        }
        return false;
    }
}



