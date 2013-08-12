package com.jreddit.botkernel;

import java.io.*;
import java.util.*;

import com.omrlnr.jreddit.*;

/**
 *
 * DefaultCrawler 
 *
 * A crawler which any bot can use. It is configurable, but by default
 * crawls the top subreddits specified in config/reddits.txt, which is a
 * list of all subreddits in most popular order.
 *
 * Bots can register listeners with this crawler to receive 
 * notifications of criteria matches for responding.
 *
 */
public class DefaultCrawler extends Crawler {

    //
    // Unique crawler name
    //
    private static final String CRAWLER_NAME = "DEFAULT_CRAWLER";

    //
    // The sleep time for the Crawler 
    //
    private static final int SLEEP = 60;

    //
    // Limit of items to retrieve at a time.
    //
    private static final int LIMIT = 10;

    //
    // Number of reddits to load from file.
    //
    private static final int TOP_REDDITS = 200;

    //
    // The singleton crawler instance.
    //
    private static DefaultCrawler _crawler;

    //
    // Config file for our default crawler
    //
    private static final String CONFIG_FILE = 
                                    "scratch/defaultcrawler.properties";
   
    //
    // File containing subreddits
    //
    private static final String SUBREDDITS_FILE = "scratch/reddits.txt";

    /**
     *
     * Get the Crawler singleton instance
     *
     */
    public static synchronized DefaultCrawler getCrawler() {

        if(_crawler != null) {
            return _crawler;
        } 

        Properties props = new Properties();
        try {
            log("Loading default crawler config properties...");
            FileInputStream in = new FileInputStream(CONFIG_FILE);
            props.load(in);
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            log("ERROR init()'ing " + CRAWLER_NAME);
        }

        //
        // Get user info from properties file
        //
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        User user   = new User(username, password);        

        //
        // Connect
        //
        try {
            user.connect();
        } catch(IOException ioe) {
            log("ERROR conecting user for " + CRAWLER_NAME);
        }

        List<String> subReddits = new ArrayList<String>();
        Utils.loadList(SUBREDDITS_FILE, subReddits, TOP_REDDITS);

        _crawler = new DefaultCrawler( 
                                user,
                                CRAWLER_NAME,
                                subReddits,
                                new Submissions.ListingType[] {
                                        Submissions.ListingType.HOT,
                                        Submissions.ListingType.NEW },
                                LIMIT,
                                SLEEP);
        return _crawler;
    }

    private DefaultCrawler( User user,
                            String name,
                            List<String> subs,
                            Submissions.ListingType[] listingTypes,
                            int limit,
                            int sleepTime ) {
 
        super(  user,
                name,
                subs,
                listingTypes,
                limit,
                sleepTime );
    }

}
