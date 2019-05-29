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

    public void connectAsClient(String reason) {
        if (reason.contentEquals("update")) {
            if (node1ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = updateNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            } else {
                System.out.println("No node 1 configured in config file!\n");
            }
            if (node2ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = updateNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(UpdateClientEndPoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            } else {
                System.out.println("No node 2 configured in config file!\n");
            }
        } else if (reason.contentEquals("hello")) {
            if (node1ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = helloNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            } else {
                System.out.println("No node 1 configured in config file!\n");
            }
            if (node2ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = helloNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(5, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            }

        } else if (reason.contentEquals("sync")) {
            if (node1ConfigIP != null) {
                if (isNodeConnected(1)) {
                    try {
                        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                        String uri = syncNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncAlertClient.class, URI.create(uri));
                        messageLatch.await(5, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                        WalletLogger.logException(de, "severe", stacktraceAsString);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                        WalletLogger.logException(ie, "severe", stacktraceAsString);
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                        WalletLogger.logException(ioe, "severe", stacktraceAsString);
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
                        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                        String uri = syncNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncAlertClient.class, URI.create(uri));
                        messageLatch.await(5, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                        WalletLogger.logException(de, "severe", stacktraceAsString);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                        WalletLogger.logException(ie, "severe", stacktraceAsString);
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                        WalletLogger.logException(ioe, "severe", stacktraceAsString);
                    }
                } else {
                    System.out.println("Unable to reach hello server, please check your network connection!\n");
                }
            } else {
                System.out.println("No node 2 configured in config file\n");
            }
        } else if (reason.contentEquals("init chain")) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                String uri = genesisNodeFQN;
                System.out.println("Connecting to " + uri);
                container.connectToServer(GenesisChainClientEndpoint.class, URI.create(uri));
                messageLatch.await(10, TimeUnit.SECONDS);
            } catch (DeploymentException de) {
                WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                WalletLogger.logException(de, "severe", stacktraceAsString);
            } catch (InterruptedException ie) {
                WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                WalletLogger.logException(ie, "severe", stacktraceAsString);
            } catch (IOException ioe) {
                WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                WalletLogger.logException(ioe, "severe", stacktraceAsString);
            }
        } else if (reason.contentEquals("push-chain")) {
            if (node1ConfigIP != null) {
                if (isNodeConnected(1)) {
                    try {
                        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                        String uri = pushChainNode1FQN;
                        System.out.println("Connecting to " + uri);
                        container.connectToServer(SyncBlockClient.class, URI.create(uri));
                        messageLatch.await(10, TimeUnit.SECONDS);
                    } catch (DeploymentException de) {
                        WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                        WalletLogger.logException(de, "severe", stacktraceAsString);
                    } catch (InterruptedException ie) {
                        WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                        WalletLogger.logException(ie, "severe", stacktraceAsString);
                    } catch (IOException ioe) {
                        WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                        String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                        WalletLogger.logException(ioe, "severe", stacktraceAsString);
                    }
                }
            }
        }
        if (node2ConfigIP != null) {
            if (isNodeConnected(2)) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = pushChainNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(SyncBlockClient.class, URI.create(uri));
                    messageLatch.await(10, TimeUnit.SECONDS);
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            }
        }
    }

    public boolean isNodeConnected(int node) {
        if (node == 1) {
            if (node1ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = helloNode1FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(500, TimeUnit.MILLISECONDS);
                    return container.connectToServer(HelloClientEndpoint.class, URI.create(uri)).isOpen();
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            } else {
                System.out.println("No node 1 configured!\n");
            }
        }
        if (node == 2) {
            if (node2ConfigIP != null) {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String uri = helloNode2FQN;
                    System.out.println("Connecting to " + uri);
                    container.connectToServer(HelloClientEndpoint.class, URI.create(uri));
                    messageLatch.await(500, TimeUnit.MILLISECONDS);
                    return container.connectToServer(HelloClientEndpoint.class, URI.create(uri)).isOpen();
                } catch (DeploymentException de) {
                    WalletLogger.logException(de, "severe", "Deployment exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(de);
                    WalletLogger.logException(de, "severe", stacktraceAsString);
                } catch (InterruptedException ie) {
                    WalletLogger.logException(ie, "severe", "Interrupted exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ie);
                    WalletLogger.logException(ie, "severe", stacktraceAsString);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", "IO exception occurred while trying to connect to a peer as a client! See below:\n");
                    String stacktraceAsString = WalletLogger.exceptionStacktraceToString(ioe);
                    WalletLogger.logException(ioe, "severe", stacktraceAsString);
                }
            } else {
                System.out.println("No node 2 configured!\n");
            }
        }
        return false;
    }
}



