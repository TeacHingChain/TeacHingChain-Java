package com.thc.blockchain.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WalletLogger {

    private static final Logger logger=Logger.getLogger("WalletLog");

    public static void logException(Exception ex, String level, String msg) {

        FileHandler fh = null;
        try {
            fh = new FileHandler("log.xml",true);
            logger.addHandler(fh);
            switch (level) {
                case "severe":
                    logger.log(Level.SEVERE, msg, ex);
                    break;
                case "warning":
                    logger.log(Level.WARNING, msg, ex);
                    break;
                case "info":
                    logger.log(Level.INFO, msg, ex);
                    break;
                case "config":
                    default:
                        logger.log(Level.CONFIG, msg, ex);
                        break;
            }
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally{
            if(fh!=null)fh.close();
        }
    }

    public static String exceptionStacktraceToString(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

    public static void logEvent(String level, String msg) {
        FileHandler fh = null;
        try {
            fh = new FileHandler("log.xml", true);
            logger.addHandler(fh);
            switch (level) {
                case "warning":
                    logger.log(Level.WARNING, msg);
                    break;
                case "info":
                    logger.log(Level.INFO, msg);
                    break;
                case "config":
                default:
                    logger.log(Level.CONFIG, msg);
                    break;
            }
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally {
            if (fh != null) fh.close();
        }
    }

    public static String getLogTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }
}