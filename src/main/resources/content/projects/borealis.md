---
title: "Borealis"
date: 2026-02-26
tags: ["java", "spring boot", "web", "thymeleaf"]
excerpt: "A minimal, polished personal website with Obsidian-inspired design built using Spring Boot."
published: true
githubUrl: "https://github.com/yourusername/borealis"
liveUrl: "https://yourdomain.com"
technologies: ["Java 17", "Spring Boot 4.0", "Thymeleaf", "Flexmark", "CSS3"]
---

# Borealis

Borealis is my personal website - a place to share my thoughts, projects, and book reviews. The name comes from the Aurora Borealis (Northern Lights), reflecting the wonder and discovery I hope to share here.

## Features

- **Blog System** - Write posts in Markdown with YAML front matter
- **Project Showcase** - Display portfolio items with links
- **Personal Notes** - Quick thoughts and ideas
- **Dark/Light Themes** - Obsidian-inspired design with theme toggle
- **Responsive Design** - Works beautifully on all devices
- **No Database** - File-based content management with Git

## Technical Architecture

### Backend
- **Spring Boot 4.0.3** - Main application framework
- **Thymeleaf** - Server-side template engine for SEO-friendly rendering
- **Flexmark** - Markdown-to-HTML conversion with GitHub Flavored Markdown support
- **SnakeYAML** - YAML front matter parsing

### Frontend
- **Custom CSS** - Obsidian-inspired design system
- **Vanilla JavaScript** - Lightweight interactions and theme toggling
- **Glass-morphism** - Modern UI effects with backdrop blur

### Content Management
- Markdown files with YAML front matter
- In-memory content caching for performance
- Git-based version control
- No database overhead

## Design Philosophy

The design draws inspiration from Obsidian's polished crystal aesthetic:

- **Deep Blue Theme** - Dark backgrounds (#0a0e27, #151B2E) evoking the night sky
- **Blue Accents** - Vibrant highlights (#3b82f6, #60a5fa) for interactive elements
- **Smooth UI** - Rounded corners, subtle shadows, and smooth transitions
- **Clean Typography** - Modern sans-serif fonts for excellent readability
- **Crystal Effects** - Glass-morphism and subtle gradients

## Lessons Learned

Building this project taught me:

1. **Simplicity matters** - File-based content is often enough
2. **Server-side rendering** - Still valuable for content-heavy sites
3. **Design consistency** - A clear design system makes development easier
4. **Performance** - In-memory caching provides instant page loads

## Future Enhancements

- RSS feed for blog posts
- Search functionality
- Tag filtering
- Reading time calculation
- Table of contents for long posts
- Comments via utterances.es

## Try It Yourself

Feel free to fork this project and customize it for your own needs. The architecture is simple, the code is clean, and the design is yours to modify!
