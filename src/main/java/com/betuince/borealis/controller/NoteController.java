package com.betuince.borealis.controller;

import com.betuince.borealis.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final ContentService contentService;

    public NoteController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public String listNotes(Model model) {
        model.addAttribute("title", "Notes");
        model.addAttribute("notes", contentService.getAllNotes());
        return "notes/list";
    }
}
