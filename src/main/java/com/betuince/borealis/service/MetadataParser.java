package com.betuince.borealis.service;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MetadataParser {

    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile(
            "^---\\s*\\n(.*?)\\n---\\s*\\n(.*)$",
            Pattern.DOTALL
    );

    private final Yaml yaml;

    public MetadataParser() {
        this.yaml = new Yaml();
    }

    /**
     * Parse YAML front matter from markdown content
     * @param content The full markdown content with front matter
     * @return Map of metadata key-value pairs
     */
    public Map<String, Object> parseMetadata(String content) {
        if (content == null || content.isEmpty()) {
            return new HashMap<>();
        }

        Matcher matcher = FRONT_MATTER_PATTERN.matcher(content);
        if (matcher.matches()) {
            String yamlContent = matcher.group(1);
            try {
                Map<String, Object> metadata = yaml.load(yamlContent);
                return metadata != null ? metadata : new HashMap<>();
            } catch (Exception e) {
                System.err.println("Error parsing YAML front matter: " + e.getMessage());
                return new HashMap<>();
            }
        }

        return new HashMap<>();
    }

    /**
     * Extract content body (markdown without front matter)
     * @param fullContent The full markdown content with front matter
     * @return The markdown content without the front matter
     */
    public String extractContent(String fullContent) {
        if (fullContent == null || fullContent.isEmpty()) {
            return "";
        }

        Matcher matcher = FRONT_MATTER_PATTERN.matcher(fullContent);
        if (matcher.matches()) {
            return matcher.group(2).trim();
        }

        return fullContent;
    }

    /**
     * Check if content has valid front matter
     * @param content The content to check
     * @return true if content has valid YAML front matter
     */
    public boolean hasFrontMatter(String content) {
        return content != null && FRONT_MATTER_PATTERN.matcher(content).matches();
    }
}
