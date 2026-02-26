package com.betuince.borealis.service;

import com.betuince.borealis.model.BlogPost;
import com.betuince.borealis.model.Book;
import com.rometools.rome.feed.synd.SyndEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExternalContentService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalContentService.class);

    // RSS Feed URLs
    private static final String SUBSTACK_FEED_URL = "https://betulince.substack.com/feed";
    private static final String MEDIUM_FEED_URL = "https://medium.com/feed/@betulince";
    private static final String GOODREADS_CURRENTLY_READING_URL = "https://www.goodreads.com/review/list_rss/67117849?shelf=currently-reading";
    private static final String GOODREADS_READ_URL = "https://www.goodreads.com/review/list_rss/67117849?shelf=read";

    private final RssFeedService rssFeedService;
    private final ContentService contentService;
    private final ConcurrentHashMap<String, List<BlogPost>> externalPostsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Book>> booksCache = new ConcurrentHashMap<>();

    @Autowired
    public ExternalContentService(RssFeedService rssFeedService, ContentService contentService) {
        this.rssFeedService = rssFeedService;
        this.contentService = contentService;
    }

    /**
     * Fetch external content on startup
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing external content service...");
        // Register this service with ContentService
        contentService.setExternalContentService(this);
        fetchAllExternalContent();
    }

    /**
     * Fetch external content every 6 hours
     */
    @Scheduled(fixedRate = 21600000) // 6 hours in milliseconds
    public void fetchAllExternalContent() {
        logger.info("Fetching external content from all sources...");

        try {
            // Fetch Substack posts
            List<BlogPost> substackPosts = fetchSubstackPosts();
            externalPostsCache.put("substack", substackPosts);
            logger.info("Cached {} Substack posts", substackPosts.size());

            // Fetch Medium posts
            List<BlogPost> mediumPosts = fetchMediumPosts();
            externalPostsCache.put("medium", mediumPosts);
            logger.info("Cached {} Medium posts", mediumPosts.size());

            // Fetch Goodreads books
            List<Book> currentlyReading = fetchGoodreadsBooks("reading");
            booksCache.put("currently-reading", currentlyReading);
            logger.info("Cached {} currently reading books", currentlyReading.size());

            List<Book> readBooks = fetchGoodreadsBooks("read");
            booksCache.put("read", readBooks);
            logger.info("Cached {} read books", readBooks.size());

        } catch (Exception e) {
            logger.error("Error fetching external content: {}", e.getMessage());
        }
    }

    /**
     * Get all external posts (Substack + Medium)
     */
    public List<BlogPost> getAllExternalPosts() {
        List<BlogPost> allPosts = new ArrayList<>();
        allPosts.addAll(externalPostsCache.getOrDefault("substack", List.of()));
        allPosts.addAll(externalPostsCache.getOrDefault("medium", List.of()));
        return allPosts;
    }

    /**
     * Get Substack posts only
     */
    public List<BlogPost> getSubstackPosts(int limit) {
        return externalPostsCache.getOrDefault("substack", List.of())
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get Medium posts only
     */
    public List<BlogPost> getMediumPosts(int limit) {
        return externalPostsCache.getOrDefault("medium", List.of())
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get all books (currently reading + read)
     */
    public List<Book> getAllBooks() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.addAll(booksCache.getOrDefault("currently-reading", List.of()));
        allBooks.addAll(booksCache.getOrDefault("read", List.of()));
        return allBooks;
    }

    /**
     * Fetch posts from Substack RSS feed
     */
    private List<BlogPost> fetchSubstackPosts() {
        List<SyndEntry> entries = rssFeedService.fetchFeed(SUBSTACK_FEED_URL);
        return convertEntriesToBlogPosts(entries, "substack");
    }

    /**
     * Fetch posts from Medium RSS feed
     */
    private List<BlogPost> fetchMediumPosts() {
        List<SyndEntry> entries = rssFeedService.fetchFeed(MEDIUM_FEED_URL);
        return convertEntriesToBlogPosts(entries, "medium");
    }

    /**
     * Convert RSS entries to BlogPost objects
     */
    private List<BlogPost> convertEntriesToBlogPosts(List<SyndEntry> entries, String source) {
        List<BlogPost> posts = new ArrayList<>();

        for (SyndEntry entry : entries) {
            try {
                BlogPost post = new BlogPost();

                // Set basic fields
                post.setTitle(entry.getTitle());
                post.setExternalUrl(entry.getLink());
                post.setSource(source);
                post.setPublished(true);
                post.setType("blog");

                // Set date
                if (entry.getPublishedDate() != null) {
                    LocalDate date = entry.getPublishedDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    post.setDate(date);
                } else {
                    post.setDate(LocalDate.now());
                }

                // Generate slug from title
                String slug = generateSlug(entry.getTitle(), source);
                post.setSlug(slug);

                // Extract excerpt from description
                String description = entry.getDescription() != null
                    ? entry.getDescription().getValue()
                    : "";
                String excerpt = rssFeedService.extractPlainText(description);
                post.setExcerpt(excerpt);

                // Set HTML content (for external posts, we'll show the excerpt)
                post.setHtmlContent("<p>" + excerpt + "</p><p><a href=\"" + entry.getLink() + "\" target=\"_blank\">Read full post on " + capitalizeFirst(source) + " →</a></p>");

                posts.add(post);

            } catch (Exception e) {
                logger.error("Error converting RSS entry to BlogPost: {}", e.getMessage());
            }
        }

        return posts;
    }

    /**
     * Generate a slug from title and source
     */
    private String generateSlug(String title, String source) {
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        // Add source prefix to avoid slug conflicts
        return source + "-" + slug;
    }

    /**
     * Capitalize first letter
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Fetch books from Goodreads
     */
    private List<Book> fetchGoodreadsBooks(String status) {
        String feedUrl = status.equals("reading") ? GOODREADS_CURRENTLY_READING_URL : GOODREADS_READ_URL;
        List<SyndEntry> entries = rssFeedService.fetchFeed(feedUrl);
        return convertEntriesToBooks(entries, status);
    }

    /**
     * Convert RSS entries to Book objects
     */
    private List<Book> convertEntriesToBooks(List<SyndEntry> entries, String status) {
        List<Book> books = new ArrayList<>();

        for (SyndEntry entry : entries) {
            try {
                Book book = new Book();

                // Parse title which is in format: "Title by Author"
                String title = entry.getTitle();
                if (title != null) {
                    String[] parts = title.split(" by ");
                    if (parts.length >= 2) {
                        book.setTitle(parts[0].trim());
                        book.setAuthor(parts[1].trim());
                    } else {
                        book.setTitle(title);
                    }
                }

                book.setGoodreadsUrl(entry.getLink());
                book.setStatus(status);

                // Extract rating and review from description
                String description = entry.getDescription() != null
                    ? entry.getDescription().getValue()
                    : "";

                // Extract book cover image URL
                if (description.contains("<img")) {
                    try {
                        String imgTag = description.substring(description.indexOf("<img"));
                        if (imgTag.contains("src=\"")) {
                            String src = imgTag.substring(imgTag.indexOf("src=\"") + 5);
                            src = src.substring(0, src.indexOf("\""));

                            // Upgrade image quality by replacing size parameters
                            // Goodreads uses _SX50_, _SY75_, etc. for small images
                            // Replace with _SX318_ for larger, higher quality images
                            src = src.replaceAll("_SX\\d+_", "_SX318_")
                                     .replaceAll("_SY\\d+_", "_SY475_");

                            book.setCoverImageUrl(src);
                        }
                    } catch (Exception e) {
                        logger.debug("Could not extract cover image: {}", e.getMessage());
                    }
                }

                // Parse rating (format: "rated it ★★★★★")
                if (description.contains("rated it")) {
                    int starCount = description.split("rated it")[1].split("<")[0].replaceAll("[^★]", "").length();
                    book.setRating(starCount);
                }

                // Extract review text (usually after rating)
                String plainText = rssFeedService.extractPlainText(description);
                if (plainText.length() > 100) {
                    book.setReview(plainText.substring(0, 300) + "...");
                } else {
                    book.setReview(plainText);
                }

                // Set date
                if (entry.getPublishedDate() != null) {
                    LocalDate date = entry.getPublishedDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    book.setDateRead(date);
                }

                books.add(book);

            } catch (Exception e) {
                logger.error("Error converting RSS entry to Book: {}", e.getMessage());
            }
        }

        return books;
    }

    /**
     * Get all currently reading books
     */
    public List<Book> getCurrentlyReadingBooks(int limit) {
        return booksCache.getOrDefault("currently-reading", List.of())
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get recently read books
     */
    public List<Book> getRecentlyReadBooks(int limit) {
        return booksCache.getOrDefault("read", List.of())
                .stream()
                .limit(limit)
                .toList();
    }
}
