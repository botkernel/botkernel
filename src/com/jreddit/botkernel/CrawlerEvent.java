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

    public CrawlerEvent(int type) {
        _type = type;
    }

    public CrawlerEvent(int type, Thing thing, CrawlerMatchCriteria criteria) {
        this(type);
        _thing = thing;
        _criteria = criteria;
    }

    public int getType() { return _type; }
    public Thing getSource() { return _thing; }
    public CrawlerMatchCriteria getCriteria() { return _criteria; }

}
