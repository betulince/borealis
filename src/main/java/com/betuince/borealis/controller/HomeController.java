package com.betuince.borealis.controller;

import com.betuince.borealis.service.ContentService;
import com.betuince.borealis.service.ExternalContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ContentService contentService;
    private final ExternalContentService externalContentService;

    public HomeController(ContentService contentService, ExternalContentService externalContentService) {
        this.contentService = contentService;
        this.externalContentService = externalContentService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        model.addAttribute("substackPosts", externalContentService.getSubstackPosts(3));
        model.addAttribute("mediumPosts", externalContentService.getMediumPosts(3));
        return "index";
    }
}
