package com.betuince.borealis.controller;

import com.betuince.borealis.exception.ContentNotFoundException;
import com.betuince.borealis.model.BlogPost;
import com.betuince.borealis.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final ContentService contentService;

    public BlogController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("title", "Blog");
        model.addAttribute("posts", contentService.getAllBlogPosts());
        return "blog/list";
    }

    @GetMapping("/{slug}")
    public String viewPost(@PathVariable String slug, Model model) {
        BlogPost post = contentService.getBlogPost(slug);

        if (post == null) {
            throw new ContentNotFoundException("Blog post not found: " + slug);
        }

        model.addAttribute("title", post.getTitle());
        model.addAttribute("description", post.getExcerpt());
        model.addAttribute("post", post);
        return "blog/post";
    }
}
