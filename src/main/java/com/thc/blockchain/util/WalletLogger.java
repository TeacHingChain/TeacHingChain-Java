package com.thc.blockchain.util;

import com.thc.blockchain.network.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.*;

public class WalletLogger {

    private static final Logger logger=Logger.getLogger("WalletLog");

    public static void logException(Exception ex, String level, String msg) {
        LogManager.getLogManager().reset();
        FileHandler fh = null;
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            fh = new FileHandler(configProps.getProperty("datadir") + "/debug.log",true);
            fh.setFormatter(new SimpleFormatter());
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
        return Arrays.toString(e.getStackTrace());
    }

    public static void logEvent(String level, String msg) {
        LogManager.getLogManager().reset();
        FileHandler fh = null;
        String configPath;
        if (Constants.BASEDIR.contains("apache-tomcat-8.5.23")) {
            configPath = Constants.BASEDIR + "/../../config/config.properties";
        } else {
            configPath = Constants.BASEDIR + "/config/config.properties";
        }
        Properties configProps = new Properties();
        try {
            configProps.load(new FileInputStream(configPath));
            fh = new FileHandler(configProps.getProperty("datadir") + "/debug.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            switch (level) {
                case "warning":
                    logger.log(Level.WARNING, msg);
                    break;
                case "config":
                    logger.log(Level.CONFIG, msg);
                case "severe":
                    logger.log(Level.SEVERE, msg);
                default:
                    logger.log(Level.INFO, msg);
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
