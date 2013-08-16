package com.jreddit.botkernel;

import java.io.*;
import java.util.*;

import com.omrlnr.jreddit.*;

/**
 *
 * CrawlerFactory 
 *
 * Provides some default crawlers which any bot can use. 
 *
 * If any bots do not with to create their only crawlers, they 
 * can use these, which are registered with automatically by the  admin bot.
 *
 * Bots can register listeners with these crawlers to receive 
 * notifications of criteria matches for responding.
 *
 */
public class CrawlerFactory {

    //
    // The singleton crawler instance.
    //
    private static Map<String, Crawler> _crawlers = 
                                new HashMap<String, Crawler>();;
    
    //
    // Unique crawler names
    //
    public static final String DEFAULT_CRAWLER = "DEFAULT_CRAWLER";
    public static final String TEST_CRAWLER    = "TEST_CRAWLER";


    private static class CrawlerSpec {

        private String _name;
        private String _propfile;
        private String _redditsfile;
        private int _count;
        private int _sleep;
        private int _limit;

        public CrawlerSpec( String name, 
                            String propfile, 
                            String redditsfile,
                            int count,
                            int sleep,
                            int limit) {
            _name = name;
            _propfile = propfile;
            _redditsfile = redditsfile;
            _count = count;
            _sleep = sleep;
            _limit = limit;
        }

        public String getName() { return _name; }
        public String getPropFile() { return _propfile; }
        public String getRedditsFile() { return _redditsfile; }
        public int getCount() { return _count; }
        public int getSleep() { return _sleep; }
        public int getLimit() { return _limit; }

    }

    private static CrawlerSpec[] CRAWLER_SPECS = new CrawlerSpec[] {
        new CrawlerSpec(    "DEFAULT_CRAWLER",
                            "scratch/defaultcrawler.properties",
                            "scratch/reddits.txt",
                            200,
                            60 * 60 * 2,
                            10),
        new CrawlerSpec(    "TEST_CRAWLER",
                            "scratch/testcrawler.properties",
                            "scratch/testreddits.txt",
                            1,
                            30,
                            10),
    };
        
    /**
     *
     * Get the Crawler singleton instance
     *
     */
    public static synchronized Crawler getCrawler(String name) {

        //
        // See if we already have this crawler instance created.
        //
        Crawler crawler = _crawlers.get(name);
        if(crawler != null) {
            return crawler;
        } 

        //
        // Crawler instance not present. Get the spec and create it.
        //
        CrawlerSpec spec = null;

        for(CrawlerSpec s: CRAWLER_SPECS) {
            if(s.getName().equals(name)) {
                spec = s;
                break;
            }
        }

        //
        // No default crawler spec found for this name?
        //
        if(spec == null) {
            return null;
        }

        Properties props = new Properties();
        try {
            BotKernel.getBotKernel().log("Loading default crawler config properties...");
            FileInputStream in = new FileInputStream(spec.getPropFile());
            props.load(in);
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            BotKernel.getBotKernel().log("ERROR init()'ing " + name);
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
            BotKernel.getBotKernel().log("ERROR conecting user for " + name);
        }

        List<String> subReddits = new ArrayList<String>();
        Utils.loadList(spec.getRedditsFile(), subReddits, spec.getCount());

        crawler = new Crawler( 
                                user,
                                name,
                                subReddits,
                                new Submissions.ListingType[] {
                                        Submissions.ListingType.HOT,
                                        Submissions.ListingType.NEW },
                                spec.getLimit(),
                                spec.getSleep());

        _crawlers.put(name, crawler);

        return crawler;
    }

}
