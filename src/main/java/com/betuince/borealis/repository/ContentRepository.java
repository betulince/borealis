package com.betuince.borealis.repository;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ContentRepository {

    /**
     * Find all blog post markdown files
     * @return List of Resource objects representing markdown files
     * @throws IOException if reading fails
     */
    List<Resource> findAllBlogPosts() throws IOException;

    /**
     * Find all project markdown files
     * @return List of Resource objects representing markdown files
     * @throws IOException if reading fails
     */
    List<Resource> findAllProjects() throws IOException;

    /**
     * Find all note markdown files
     * @return List of Resource objects representing markdown files
     * @throws IOException if reading fails
     */
    List<Resource> findAllNotes() throws IOException;

    /**
     * Find a specific content file by slug and type
     * @param slug The slug (filename without extension)
     * @param type The content type ("blog", "projects", or "notes")
     * @return Resource object or null if not found
     * @throws IOException if reading fails
     */
    Resource findBySlug(String slug, String type) throws IOException;
}
