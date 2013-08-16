package com.jreddit.botkernel;

import java.io.*;
import java.util.*;

import com.omrlnr.jreddit.*;

/**
 *
 * Reddit Bot Crawler Event
 *
 */
public class CrawlerEvent {
  
    public static final int CRAWLER_START = 0;
    public static final int CRAWLER_COMPLETE = 1;
    public static final int CRAWLER_MATCH = 2;

    private int _type;
    private Thing _thing;
    private CrawlerMatchCriteria _criteria;
    private Crawler _crawler;

    public CrawlerEvent(int type, Crawler crawler) {
        _type = type;
        _crawler = crawler;
    }

    public CrawlerEvent(int type, 
                        Thing thing, 
                        CrawlerMatchCriteria criteria,
                        Crawler crawler) {
        this(type, crawler);
        _thing = thing;
        _criteria = criteria;
    }

    public int getType() { return _type; }
    public Thing getSource() { return _thing; }
    public CrawlerMatchCriteria getCriteria() { return _criteria; }
    public Crawler getCrawler() { return _crawler; }

}
