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
     * Called when a crawler has matched some crawl criteria.
     *
     * @param event     The crawler event. This will contain data
     *                  about the type of the event (match, finished, etc.)
     *                  and additional context such as 
     *                  the Thing (comment/submission) which matched or the
     *                  crawler criteria which matched it.
     *
     */
    public void handleCrawlerEvent(CrawlerEvent event);

}
