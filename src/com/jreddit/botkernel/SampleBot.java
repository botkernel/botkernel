package com.jreddit.botkernel;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.omrlnr.jreddit.*;

/**
 *
 * A sample bot
 *
 */
public class SampleBot extends BaseBot implements Bot, CrawlerListener {
    
    private static final String BOT_NAME    = "SAMPLE_BOT";
    private static final String CONFIG_FILE = "scratch/samplebot.properties";
    private static final String REPLIES_FILE = "scratch/samplebot.replies";

    /**
     *
     * The subreddit to test on.
     *
     */
    private String _subreddit;

    /**
     *
     *  The credentials used for this bot to login.
     *
     */
    private User    _user;

    /**
     *
     * The Crawler that this bot will register with the bot kernel
     * in order to listen and reply to posts.
     *
     */
    private Crawler _crawler;

    /**
     *
     *  A list of comment we have replied to. 
     *  To keep track, so we don't reply to them again.
     *
     */
    private Properties _repliedComments = new Properties();

    /**
     *
     * Return the unique name of this bot.
     *
     */
    public String getName() { 
        return BOT_NAME;
    }

    /**
     *
     * Initialize this bot. Called after the class is loaded and before
     * the thread for it is started.
     *
     */
    public void init() {

        //
        // Load username and password from config file.
        //
        try {

            log("Attempting to load sample bot configuration.");

            log("Loading config properties...");
            FileInputStream in = new FileInputStream(CONFIG_FILE);
            Properties props = new Properties();
            props.load(in);
            in.close();

            log("Loading replied comments...");
            _repliedComments = new Properties();
            if( (new File(REPLIES_FILE)).exists() ) {
                in = new FileInputStream(REPLIES_FILE);
                _repliedComments.load(in);
                in.close();
            }

            String username = props.getProperty("username");
            String password = props.getProperty("password");

            _subreddit = props.getProperty("subreddit");

            _user = new User( username, password );
            
            _user.connect();

        } catch(IOException ioe) {
            ioe.printStackTrace();
            log("Error loading sample bot config file " + CONFIG_FILE);
        }


        //
        // The subreddits for which this bot will create a Crawler.
        //
        List<String> subreddits = new ArrayList<String>();
        subreddits.add(_subreddit);

        //
        // Create a Crawler for the sample bot
        //
        _crawler = new Crawler( 

            _user,                              // Sample bot user, put your
                                                // credentials in 
                                                // the ./scratch/ config file

            "SAMPLE_CRAWLER",                   // Sample crawler name

            subreddits,                         // Subreddits to crawl

            new Submissions.ListingType[] {     // Listing types to look at.
                Submissions.ListingType.HOT,
                Submissions.ListingType.NEW },

            5,                                  // Number of items to retrieve

            60                                  // Seconds to sleep before
                                                // running again.
        );
        
        
        //
        // Register ourselves with the Crawler, so we will get called
        // if any match criteria are met.
        //
        _crawler.addListener(this);

        CrawlerMatchCriteria criteria = new CrawlerMatchCriteria() {

                public boolean match(Thing thing) {

                    String body = null;

                    //
                    // The "Thing" we are matching against could either be
                    // a Comment, or it could be a Submission possibly
                    // a Submission with selftext. We'll try to match against
                    // either of those cases.
                    //

                    if(thing instanceof Comment) {
                        Comment comment = (Comment)thing;
                        if(comment.getBody() != null) {
                            body = ((Comment)comment).getBody().toLowerCase();
                        }
                    }
   
                    if(thing instanceof Submission) {
                        Submission submission = (Submission)thing;
                        if( submission.isSelfPost() &&
                            submission.getSelftext() != null) {

                            body = submission.getSelftext().toLowerCase();
                        }
                    }
                    
                    if(body == null) {
                        return false;
                    }

                    if( body.indexOf("samplebot say hello") != -1 ) {
                        return true;
                    }

                    return false;
                }

                public CrawlerListener getCrawlerListener() {
                    return SampleBot.this;
                }

            };

        //
        // Add our match criteria to the crawler.
        //
        _crawler.addMatchCriteria(criteria);

        //
        // Register the crawler with the bot kernel.
        //
        BotKernel.getBotKernel().addCrawler(_crawler);


    }

    public void run() {
        while(true) {

            //
            // Check for shutdown requests. 
            //
            if(_shutdown) {

                //
                // We are shutting down.
                // Be nice and remove the crawlers we are responsible 
                // for adding.
                //
                log("Removing crawler...");
                BotKernel.getBotKernel().removeCrawler(_crawler);

                //
                // Save replies
                //
                log("Saving replied IDs...");
                saveReplies();
                return;
            }

            //
            // Check for stuff to do here.
            // For this example, we do nothing.
            //
            log("SampleBot sleeping ...");

            //
            // Sleep for 30 seconds.
            //
            sleep(60 * 60);
        }
    }

    /**
     *
     * Handle a crawler hit.
     *
     */
    public void handleCrawlerEvent(Thing thing) {
      
        //
        // Make sure we haven't already replied to this.
        //
        if(_repliedComments.containsKey(thing.getName())) {
            log("Ignoring already replied comment: " + thing.getName() );
            return;
        }
 
        String body = null;

        //
        // Check if it is a Comment or a Submission
        //

        if(thing instanceof Comment) {
            Comment comment = (Comment)thing;
            if(comment.getBody() != null) {
                body = ((Comment)comment).getBody().toLowerCase();
            }
        }

        if(thing instanceof Submission) {
            Submission submission = (Submission)thing;
            if( submission.isSelfPost() &&
                submission.getSelftext() != null) {

                body = submission.getSelftext().toLowerCase();
            }
        }
                    
        if(body == null) {
            return;
        }

        String text = "Hello " + thing.getAuthor() + " to you too!";

        try { 

            Comments.comment(_user, thing, text);

            //
            // Let ourselves know that we have replied to this comment. 
            //
            _repliedComments.put(thing.getName(), "");

        } catch(IOException ioe) {
            ioe.printStackTrace();
            log("SampleBot caught: " + ioe);

            //
            // Mark message as read so we don't keep replying to it.
            //
            _repliedComments.put(thing.getName(), "");
        }

        saveReplies(); 
    }


    private void saveReplies() {
        try {
            FileOutputStream fos = new FileOutputStream(REPLIES_FILE); 
            _repliedComments.store(fos, null); 
            fos.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            log("Error saving replied IDs. " + ioe.getMessage());
        }
    }


}
