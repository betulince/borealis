package com.betuince.borealis.model;

import java.time.LocalDate;

public class Book {
    private String title;
    private String author;
    private String goodreadsUrl;
    private String coverImageUrl;
    private Integer rating;
    private String status; // "reading" or "read"
    private LocalDate dateRead;
    private String review;

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGoodreadsUrl() {
        return goodreadsUrl;
    }

    public void setGoodreadsUrl(String goodreadsUrl) {
        this.goodreadsUrl = goodreadsUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDateRead() {
        return dateRead;
    }

    public void setDateRead(LocalDate dateRead) {
        this.dateRead = dateRead;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public boolean isCurrentlyReading() {
        return "reading".equals(status);
    }
}
