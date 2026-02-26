package com.betuince.borealis.service;

import com.betuince.borealis.model.BlogPost;
import com.betuince.borealis.model.Content;
import com.betuince.borealis.model.Note;
import com.betuince.borealis.model.Project;
import com.betuince.borealis.repository.ContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final MarkdownService markdownService;
    private final MetadataParser metadataParser;
    private ExternalContentService externalContentService;

    private final Map<String, List<? extends Content>> cache = new ConcurrentHashMap<>();

    public ContentService(ContentRepository contentRepository,
                         MarkdownService markdownService,
                         MetadataParser metadataParser) {
        this.contentRepository = contentRepository;
        this.markdownService = markdownService;
        this.metadataParser = metadataParser;
    }

    // Setter for ExternalContentService to avoid circular dependency
    public void setExternalContentService(ExternalContentService externalContentService) {
        this.externalContentService = externalContentService;
    }

    @PostConstruct
    public void loadContent() {
        System.out.println("Loading content from filesystem...");
        try {
            List<BlogPost> blogPosts = loadBlogPosts();
            List<Project> projects = loadProjects();
            List<Note> notes = loadNotes();

            cache.put("blog", blogPosts);
            cache.put("projects", projects);
            cache.put("notes", notes);

            System.out.println("Content loaded successfully: " +
                    blogPosts.size() + " blog posts, " +
                    projects.size() + " projects, " +
                    notes.size() + " notes");
        } catch (Exception e) {
            System.err.println("Error loading content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<BlogPost> getAllBlogPosts() {
        @SuppressWarnings("unchecked")
        List<BlogPost> localPosts = (List<BlogPost>) cache.getOrDefault("blog", new ArrayList<>());

        // Merge with external posts if available
        List<BlogPost> allPosts = new ArrayList<>(localPosts);
        if (externalContentService != null) {
            allPosts.addAll(externalContentService.getAllExternalPosts());
        }

        return allPosts.stream()
                .filter(Content::isPublished)
                .sorted(Comparator.comparing(Content::getDate).reversed())
                .collect(Collectors.toList());
    }

    public BlogPost getBlogPost(String slug) {
        return getAllBlogPosts().stream()
                .filter(post -> post.getSlug().equals(slug))
                .findFirst()
                .orElse(null);
    }

    public List<BlogPost> getRecentBlogPosts(int limit) {
        return getAllBlogPosts().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Project> getAllProjects() {
        @SuppressWarnings("unchecked")
        List<Project> projects = (List<Project>) cache.getOrDefault("projects", new ArrayList<>());
        return projects.stream()
                .filter(Content::isPublished)
                .sorted(Comparator.comparing(Content::getDate).reversed())
                .collect(Collectors.toList());
    }

    public Project getProject(String slug) {
        return getAllProjects().stream()
                .filter(project -> project.getSlug().equals(slug))
                .findFirst()
                .orElse(null);
    }

    public List<Project> getFeaturedProjects(int limit) {
        return getAllProjects().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Note> getAllNotes() {
        @SuppressWarnings("unchecked")
        List<Note> notes = (List<Note>) cache.getOrDefault("notes", new ArrayList<>());
        return notes.stream()
                .filter(Content::isPublished)
                .sorted(Comparator.comparing(Content::getDate).reversed())
                .collect(Collectors.toList());
    }

    private List<BlogPost> loadBlogPosts() throws IOException {
        List<Resource> resources = contentRepository.findAllBlogPosts();
        List<BlogPost> blogPosts = new ArrayList<>();

        for (Resource resource : resources) {
            try {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                BlogPost post = new BlogPost();
                populateContent(post, resource, content);

                // Set type from metadata (default to "blog")
                Map<String, Object> metadata = post.getMetadata();
                post.setType((String) metadata.getOrDefault("type", "blog"));

                blogPosts.add(post);
            } catch (Exception e) {
                System.err.println("Error loading blog post " + resource.getFilename() + ": " + e.getMessage());
            }
        }

        return blogPosts;
    }

    private List<Project> loadProjects() throws IOException {
        List<Resource> resources = contentRepository.findAllProjects();
        List<Project> projects = new ArrayList<>();

        for (Resource resource : resources) {
            try {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                Project project = new Project();
                populateContent(project, resource, content);

                // Set project-specific fields from metadata
                Map<String, Object> metadata = project.getMetadata();
                project.setGithubUrl((String) metadata.get("githubUrl"));
                project.setLiveUrl((String) metadata.get("liveUrl"));

                @SuppressWarnings("unchecked")
                List<String> technologies = (List<String>) metadata.get("technologies");
                project.setTechnologies(technologies);

                projects.add(project);
            } catch (Exception e) {
                System.err.println("Error loading project " + resource.getFilename() + ": " + e.getMessage());
            }
        }

        return projects;
    }

    private List<Note> loadNotes() throws IOException {
        List<Resource> resources = contentRepository.findAllNotes();
        List<Note> notes = new ArrayList<>();

        for (Resource resource : resources) {
            try {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                Note note = new Note();
                populateContent(note, resource, content);
                notes.add(note);
            } catch (Exception e) {
                System.err.println("Error loading note " + resource.getFilename() + ": " + e.getMessage());
            }
        }

        return notes;
    }

    private void populateContent(Content content, Resource resource, String fileContent) {
        // Extract metadata and markdown content
        Map<String, Object> metadata = metadataParser.parseMetadata(fileContent);
        String markdown = metadataParser.extractContent(fileContent);

        // Generate slug from filename
        String filename = resource.getFilename();
        String slug = filename != null ? filename.replace(".md", "") : "untitled";

        // Remove date prefix from slug if present (e.g., "2026-02-25-hello-world" -> "hello-world")
        slug = slug.replaceAll("^\\d{4}-\\d{2}-\\d{2}-", "");

        content.setSlug(slug);
        content.setTitle((String) metadata.getOrDefault("title", "Untitled"));
        content.setAuthor((String) metadata.get("author"));
        content.setExcerpt((String) metadata.get("excerpt"));
        content.setPublished((Boolean) metadata.getOrDefault("published", false));
        content.setMetadata(metadata);
        content.setRawMarkdown(markdown);

        // Convert markdown to HTML
        String html = markdownService.convertToHtml(markdown);
        content.setHtmlContent(html);

        // Parse date
        Object dateObj = metadata.get("date");
        if (dateObj != null) {
            try {
                if (dateObj instanceof Date) {
                    content.setDate(((Date) dateObj).toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate());
                } else if (dateObj instanceof String) {
                    content.setDate(LocalDate.parse((String) dateObj, DateTimeFormatter.ISO_DATE));
                }
            } catch (Exception e) {
                System.err.println("Error parsing date for " + slug + ": " + e.getMessage());
                content.setDate(LocalDate.now());
            }
        } else {
            content.setDate(LocalDate.now());
        }

        // Parse tags
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) metadata.get("tags");
        content.setTags(tags != null ? tags : new ArrayList<>());
    }
}
