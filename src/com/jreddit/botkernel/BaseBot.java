package com.jreddit.botkernel;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.omrlnr.jreddit.*;
import com.omrlnr.jreddit.utils.Utils;

/**
 *
 * Some common utils bots can extend and use.
 *
 */
public abstract class BaseBot implements Bot {

    protected boolean _shutdown;

    protected void sleep(int seconds) {
        try {
            for(int i = 0; i < seconds; i++) {
                Thread.sleep(1000);
                if(_shutdown) {
                    log("We are shutting down. wake up.");
                    return;
                }
            }
        } catch (InterruptedException ie) {
            //
            // Unsure how to handle this.
            //
            throw new RuntimeException(ie);
        }
    }

    /**
     *
     *
     */
    public void shutdown() {
        _shutdown = true;
    }

    protected void log(String s) {
        BotKernel.getBotKernel().log(s);
    }

    protected void loadProperties(String filename, Properties props) {
        try {
            log("Loading properties file " + filename);
            FileInputStream in = new FileInputStream(filename);
            props.load(in);
            in.close();
        } catch( IOException ioe) {
            log("ERROR loading properties file " + filename);
        }
    }

    protected void saveProperties(String filename, Properties props) {
        try {
            log("Saving properties file " + filename);
            FileOutputStream fos = new FileOutputStream(filename);
            props.store(fos, null);
            fos.close();
        } catch( IOException ioe) {
            log("ERROR saving properties file " + filename);
        }
    }


}
