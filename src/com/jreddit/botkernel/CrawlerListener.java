package com.jreddit.botkernel;

import com.omrlnr.jreddit.Thing;

/**
 *
 * Reddit Bot Crawler listener.
 *
 * Any bot which register for a crawling service will need to implement
 * this interface in order to be notified when there is a matching 
 * crawl result.
 *
 */
public interface CrawlerListener  {

    /**
     *
     * Called then a crawler has matched some crawl criteria.
     *
     * @param thing     The com.omrlnr.jreddit.Thing which matched
     *                  the crawl criteria.
     *                  This will be either a
     *                  com.omrlnr.jreddit.Comment or a 
     *                  com.omrlnr.jreddit.Submission (with selftext)
     */
    public void handleCrawlerEvent(Thing comment);

}
