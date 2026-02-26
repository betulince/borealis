package com.betuince.borealis.controller;

import com.betuince.borealis.model.Book;
import com.betuince.borealis.service.ExternalContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BooksController {

    private final ExternalContentService externalContentService;
    private static final int BOOKS_PER_PAGE = 12;

    public BooksController(ExternalContentService externalContentService) {
        this.externalContentService = externalContentService;
    }

    @GetMapping("/reading")
    public String reading(Model model) {
        // Get all currently reading books
        List<Book> currentlyReading = externalContentService.getCurrentlyReadingBooks(100);

        // Get only 3 read books
        List<Book> readBooks = externalContentService.getRecentlyReadBooks(3);

        // Combine them
        List<Book> booksToDisplay = new java.util.ArrayList<>();
        booksToDisplay.addAll(currentlyReading);
        booksToDisplay.addAll(readBooks);

        model.addAttribute("title", "Reading");
        model.addAttribute("books", booksToDisplay);

        return "reading";
    }
}
