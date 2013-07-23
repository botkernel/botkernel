package com.jreddit.botkernel;

import com.omrlnr.jreddit.Thing;

/**
 *
 * Reddit Bot Crawler matching criteria
 *
 * Implement this interface and supply it to a crawler
 * in order for the crawler to match a Comment or Submission
 *
 */
public interface CrawlerMatchCriteria {

    /**
     *
     * Called by a crawler to determine if it found a match.
     *
     * @param thing     The com.omrlnr.jreddit.Thing which matched
     *                  the crawl criteria.
     *                  This will be either a
     *                  com.omrlnr.jreddit.Comment or a 
     *                  com.omrlnr.jreddit.Submission (with selftext)
     */
    public boolean match(Thing comment);

    /**
     *
     * Returns the CrawlerListener interested in this 
     * match criteria.
     *
     */
    public CrawlerListener getCrawlerListener();


}
