package com.jreddit.botkernel;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.omrlnr.jreddit.*;
import com.omrlnr.jreddit.utils.Utils;

/**
 *
 * A bot to administer the bot kernel
 *
 */
public class AdminBot extends BaseBot implements Bot {
    
    public static final String BOT_NAME = "ADMIN_BOT";

    private static final String CONFIG_FILE = "scratch/adminbot.properties";
    private static final String REPLIES_FILE = "scratch/adminbot.replies";

    private String _owner = null;   // Load this from config. We will
                                    // accept PM'ed commands from this user.

    //
    // Commands
    //
    public static final String SHUTDOWN = "shutdown";   // Shutdown botkernel
    public static final String LOAD_BOT = "loadbot";    // Load a bot
    public static final String STOP_BOT = "stopbot";    // Stop a bot
    public static final String ADD_CRAWLER      = "addcrawler";
    public static final String REMOVE_CRAWLER   = "removecrawler";  

    //
    // Time to sleep in seconds
    //
    private static final int SLEEP_TIME = 30;   // Time to sleep between
                                                // checking for commands 
                                                // sent to the admin bot,

    //
    // Workaround markAsRead issues.
    //
    // This is ugly. Shouldn't need this, as we *should* just be able to
    // mark any received messages as READ. Not sure if there is a bug in the
    // underlying reddit API wrapper, but sometimes we seem to mark messages
    // as read but they still appear new??
    //
    // Anyway for now persist messages we have replied to, to avoid
    // replying multiple times.
    //
    private Properties _replies = null;

    /**
     *
     * The API wrapper user for this botkernel admin bot.
     *
     */
    private User _user;

    public String getName() { 
        return BOT_NAME;
    }

    public void init() {

        //
        // Load config files
        //
        
        log("Attempting to load admin bot configuration.");
        Properties props = new Properties();
        loadProperties(CONFIG_FILE, props);

        //
        // The bot owner (user from which we will accept PM commands)
        //
        _owner = props.getProperty("owner");

        String username = props.getProperty("username");
        String password = props.getProperty("password");

        _user = new User(username, password);

        //
        // Load our replied messages
        //
        _replies = new Properties();
        loadProperties(REPLIES_FILE, _replies);

        //
        // Register the default crawler, which any bots can use.
        //
        Crawler crawler = CrawlerFactory.getCrawler(CrawlerFactory.DEFAULT_CRAWLER);
        crawler.setShuffle(true);
        BotKernel.getBotKernel().addCrawler(crawler); 

    }


    public void run() {

        while(true) {

            if(_shutdown) {
                break;
            }
           
            try {

                _user.connect();

                //
                // Check for stuff to do here.
                //

                log("Checking for command messages for admin bot \n" + _user);

                //
                // Look for and handle new messages 
                //
                List<Message> messages = Messages.getMessages(
                                                _user,
                                                Messages.MessageType.UNREAD,
                                                100 );

                if(messages.size() == 0) {

                    log("INFO No new admin bot messages.");

                    //
                    // No new messages. This means all messages have been
                    // successfully marked as read. Clear our workaround
                    // cache of replies.
                    //
                    _replies.clear();
                    saveProperties(REPLIES_FILE, _replies);
                }

                for(Message message: messages) {
           
                    //
                    // Only accept private messages as commands.
                    // I.e. no public posts.
                    //
                    if(message.getKind().equals(Thing.KIND_MESSAGE)) {

                        String author = message.getAuthor();
                   
                        //
                        // Only accept commands from "owner" or from myself.
                        //
                        if( author.equals(_owner) ||
                            author.equals(_user.getUsername()) ) {
   
                            String body = message.getBody().trim();

                            //
                            // Ensure we have not already replied to this
                            // message.
                            //
                            String fullname = message.getName();
                            if(_replies.getProperty(fullname) != null) {
                                log("Skipping already replied message: " 
                                    + body);
                                Messages.markAsRead(_user, message);
                                continue;
                            }

                           

                            String[] commands = body.split(" ");

                            if(commands.length == 1) {
                                
                                if(commands[0].equals(SHUTDOWN)) {
                                    log("Executing command: "  + SHUTDOWN);
                                    markAsReplied(message);
                                    BotKernel.getBotKernel().shutdownKernel();
                                }

                                continue;
                            }

                            if(commands.length == 2) {
     
                                if(commands[0].equals(ADD_CRAWLER)) {
                                    Crawler crawler = 
                                        CrawlerFactory.getCrawler(commands[1]);
                                    BotKernel.getBotKernel().addCrawler(
                                                                    crawler); 
                                    Messages.markAsRead(_user, message);
                                    continue;
                                }

                                if(commands[0].equals(REMOVE_CRAWLER)) {
                                    Crawler crawler = 
                                        CrawlerFactory.getCrawler(commands[1]);
                                    BotKernel.getBotKernel().removeCrawler(
                                                                    crawler); 
                                    Messages.markAsRead(_user, message);
                                    continue;
                                }

                                if(commands[0].equals(LOAD_BOT)) {
                                    String classname = commands[1];

                                    log("Executing command: "  + LOAD_BOT);

                                    Comments.comment(
                                        _user, 
                                        message, 
                                        "Loading bot " + classname);
                                
                                    markAsReplied(message);

                                    BotKernel.getBotKernel().loadBot(classname);
                                    continue;
                                }
    
                                if(commands[0].equals(STOP_BOT)) {
                                    String name = commands[1];

                                    if(name.equals(BOT_NAME)) {
                                        //
                                        //
                                        //
                                        log("INFO Refusing request to " +
                                            "stop myself. Will not stop " +
                                            "admin bot.");
                                        
                                        Comments.comment(
                                            _user, 
                                            message, 
                                            "Not stopping admin bot.");

                                        markAsReplied(message);
                                        continue;
                                    }

                                    log("Executing command: "  + STOP_BOT);

                                    Comments.comment(
                                        _user, 
                                        message, 
                                        "Stopping bot " + name);
                                
                                    markAsReplied(message);

                                    BotKernel.getBotKernel().stopBot(name);
                                    continue;
                                }
    
                                continue;

                            } 

                            log("Ignoring command: " + body);
                        }
    
                    } else {
                        log("Ignoring message: " + message);
                    }
    
                }

            } catch(IOException ioe) {
                ioe.printStackTrace();
                log("Error AdminBot caught " + ioe);
            }

            BotKernel.getBotKernel().logInfo();

            log("Sleeping...");

            //
            // Sleep
            //
            sleep(SLEEP_TIME);
        }
    }

    public void markAsReplied(Message message) {
        _replies.setProperty(message.getName(), "");
        saveProperties(REPLIES_FILE, _replies);

        try {
            Messages.markAsRead(_user, message);
        } catch (IOException ioe) {
            //
            //  This will be okay, as we have out _replies cache 
            //  as a workaround. 
            //
            log("ERROR Error marking message " + 
                message.getName() + " as read.");
        }
    }

    /**
     *
     * DEBUG only. 
     * Should run this bot through botkernel.
     *
     */
    public static void main(String[] args) throws Exception {
        AdminBot bot = new AdminBot();
        bot.init();

        // Thread thread = new Thread(bot);
        // thread.start();
        bot.run();
    }



}
