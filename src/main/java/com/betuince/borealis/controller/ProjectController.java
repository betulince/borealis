package com.betuince.borealis.controller;

import com.betuince.borealis.exception.ContentNotFoundException;
import com.betuince.borealis.model.Project;
import com.betuince.borealis.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ContentService contentService;

    public ProjectController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public String listProjects(Model model) {
        model.addAttribute("title", "Projects");
        model.addAttribute("projects", contentService.getAllProjects());
        return "projects/list";
    }

    @GetMapping("/{slug}")
    public String viewProject(@PathVariable String slug, Model model) {
        Project project = contentService.getProject(slug);

        if (project == null) {
            throw new ContentNotFoundException("Project not found: " + slug);
        }

        model.addAttribute("title", project.getTitle());
        model.addAttribute("description", project.getExcerpt());
        model.addAttribute("project", project);
        return "projects/detail";
    }
}
