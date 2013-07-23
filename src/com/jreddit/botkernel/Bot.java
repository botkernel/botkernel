package com.jreddit.botkernel;

/**
 *
 * Reddit Bot.
 * Bot implementations should implement at least this interface
 *
 */
public interface Bot extends Runnable {

    /**
     * This should return the unique name of the bot.
     *
     * @return A unique name for this bot.
     *
     */
    public String getName(); 

    /**
     *
     * Implement Runnable so this Bot can run as it's own thread.
     *
     */
    public void run();

    /**
     *
     * Called when a bot is loaded and initialized for the first time.
     *
     * @param botKernel     The BotKernel instance which is loading the bot.
     *
     */
    public void init();

    /**
     *
     * Called by the BotKernel to indicate that the bot needs to 
     * shut down.
     *
     */
    public void shutdown();

}
