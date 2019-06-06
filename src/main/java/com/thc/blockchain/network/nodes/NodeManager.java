package com.thc.blockchain.network.nodes;

import com.thc.blockchain.network.objects.Alert;
import com.thc.blockchain.network.objects.Block;
import com.thc.blockchain.network.objects.GenesisBlock;
import com.thc.blockchain.network.objects.Tx;
import com.thc.blockchain.util.WalletLogger;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class NodeManager {

    private static final Lock LOCK = new ReentrantLock();
    private static final Set<Session> NODES = new CopyOnWriteArraySet<>();
    private static Session session;

    public static void pushBlock(final Block block, final Session sid) {
        assert !Objects.isNull(block) && !Objects.isNull(sid);
        NODES.forEach(session -> {
            if (sid.getUserProperties().get("id").toString().contentEquals("update-chain-client") || sid.getUserProperties().get("id").toString().contentEquals("update-chain-server") || sid.getUserProperties().get("id").toString().contentEquals("sync-block-client") || sid.getUserProperties().get("id").toString().contentEquals("sync-block-server")) {
                try {
                    sid.getBasicRemote().sendObject(block);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe", WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to push a block to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "severe", WalletLogger.getLogTimeStamp() + " Encode exception occurred while trying to push a block to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
        });
    }

    public static void pushGenesisBlock(final GenesisBlock genesisBlock, final Session sid) {
        assert !Objects.isNull(genesisBlock) && !Objects.isNull(sid);
        NODES.forEach(session -> {
            if (sid.getUserProperties().get("id").toString().contentEquals("genesis-chain-client")) {
                try {
                    sid.getBasicRemote().sendObject(genesisBlock);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe",  WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to push genesis block! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "severe",  WalletLogger.getLogTimeStamp() + " Encode exception occurred while trying to push genesis block! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
        });
    }

    public static void pushAlert(final Alert alert, final Session sid) {
        assert !Objects.isNull(alert) && !Objects.isNull(sid);
        System.out.println(sid.getUserProperties().get("id").toString());
        NODES.forEach(session -> {
            if (sid.getUserProperties().get("id").toString().contentEquals("sync-client") || sid.getUserProperties().get("id").toString().contentEquals("sync-server")) {
                try {
                    sid.getBasicRemote().sendObject(alert);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe",  WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to push an alert to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "severe",  WalletLogger.getLogTimeStamp() + " Encode exception occurred while trying to push an alert to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
        });
    }

    public static void pushTx(final Tx tx, final Session sid) {
        assert !Objects.isNull(tx) && !Objects.isNull(sid);
        NODES.forEach(session -> {
            if (sid.getUserProperties().get("id").toString().contentEquals("tx-client") || sid.getUserProperties().get("id").toString().contentEquals("tx-server")) {
                try {
                    sid.getBasicRemote().sendObject(tx);
                } catch (IOException ioe) {
                    WalletLogger.logException(ioe, "severe",  WalletLogger.getLogTimeStamp() + " IO exception occurred while trying to push an alert to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ioe));
                } catch (EncodeException ee) {
                    WalletLogger.logException(ee, "severe",  WalletLogger.getLogTimeStamp() + " Encode exception occurred while trying to push an alert to a peer! See below:\n" + WalletLogger.exceptionStacktraceToString(ee));
                }
            }
        });
    }

    public static int getPeerCount() {
        int connectedNodes = 0;
        if (new EndpointManager().getIsNode1Connected()) {
            connectedNodes++;
        }
        if (new EndpointManager().getIsNode2Connected()) {
            connectedNodes++;
        }

        return connectedNodes;
    }

    public static boolean registerNode(final Session session, final String id) {
        assert !Objects.isNull(session);
        assert !Objects.isNull(id);
        NodeManager.session = session;
        try {
            LOCK.lock();
            session.getUserProperties().put("id", id);
            NODES.add(session);
            setSession(session);
        } finally {
            LOCK.unlock();
        }
        return NODES.contains(session);
    }

    public static void close(final Session session, final CloseCodes closeCode, final String message) {
        assert !Objects.isNull(session) && !Objects.isNull(closeCode);
        try {
            session.close(new CloseReason(closeCode, message));
        } catch (IOException e) {
            throw new RuntimeException("Unable to close session", e);
        }
    }

    public static boolean remove(final Session session) {
        assert !Objects.isNull(session);
        return NODES.remove(session);
    }

    public static void setSession(Session session) {
        NodeManager.session = session;
    }

    public static Session getSession() {
        return session;
    }

}
