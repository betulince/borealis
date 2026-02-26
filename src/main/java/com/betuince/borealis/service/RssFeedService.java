package com.betuince.borealis.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Collections;
import java.util.List;

@Service
public class RssFeedService {

    private static final Logger logger = LoggerFactory.getLogger(RssFeedService.class);

    /**
     * Fetch and parse RSS feed from a given URL
     * @param feedUrl The RSS feed URL
     * @return List of feed entries, or empty list if fetch fails
     */
    public List<SyndEntry> fetchFeed(String feedUrl) {
        try {
            logger.info("Fetching RSS feed from: {}", feedUrl);

            URL url = new URL(feedUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(url));

            List<SyndEntry> entries = feed.getEntries();
            logger.info("Successfully fetched {} entries from {}", entries.size(), feedUrl);

            return entries;

        } catch (Exception e) {
            logger.error("Failed to fetch RSS feed from {}: {}", feedUrl, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Extract plain text from HTML content
     * @param html HTML string
     * @return Plain text
     */
    public String extractPlainText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        // Remove HTML tags and decode entities
        String text = html.replaceAll("<[^>]+>", "");
        text = text.replaceAll("&nbsp;", " ");
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");

        // Trim and limit length for excerpt
        text = text.trim();
        if (text.length() > 300) {
            text = text.substring(0, 297) + "...";
        }

        return text;
    }
}
