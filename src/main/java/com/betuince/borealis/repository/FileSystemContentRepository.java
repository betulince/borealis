package com.betuince.borealis.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class FileSystemContentRepository implements ContentRepository {

    @Value("${content.blog-path}")
    private String blogPath;

    @Value("${content.projects-path}")
    private String projectsPath;

    @Value("${content.notes-path}")
    private String notesPath;

    private final ResourcePatternResolver resourcePatternResolver;

    public FileSystemContentRepository() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public List<Resource> findAllBlogPosts() throws IOException {
        return findMarkdownFiles(blogPath);
    }

    @Override
    public List<Resource> findAllProjects() throws IOException {
        return findMarkdownFiles(projectsPath);
    }

    @Override
    public List<Resource> findAllNotes() throws IOException {
        return findMarkdownFiles(notesPath);
    }

    @Override
    public Resource findBySlug(String slug, String type) throws IOException {
        String basePath = switch (type.toLowerCase()) {
            case "blog" -> blogPath;
            case "projects" -> projectsPath;
            case "notes" -> notesPath;
            default -> throw new IllegalArgumentException("Unknown content type: " + type);
        };

        // Try to find files that match the slug (with or without date prefix)
        Resource[] resources = resourcePatternResolver.getResources(basePath + "*" + slug + ".md");

        if (resources.length > 0) {
            return resources[0];
        }

        return null;
    }

    private List<Resource> findMarkdownFiles(String path) throws IOException {
        try {
            Resource[] resources = resourcePatternResolver.getResources(path + "*.md");
            return new ArrayList<>(Arrays.asList(resources));
        } catch (IOException e) {
            System.err.println("Error reading markdown files from " + path + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
