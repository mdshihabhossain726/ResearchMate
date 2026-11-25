package com.researchmate;

/**
 * One research paper.
 * ENCAPSULATION: the fields are private; other classes use getters and setters.
 */
public class Paper {

    private int id;
    private String title;
    private String authors;
    private int year;
    private String content;   // the text of the paper (from a PDF, or typed by the user)

    /** Used when a NEW paper is created (it has no id yet). */
    public Paper(String title, String authors, int year, String content) {
        this.title = title;
        this.authors = authors;
        this.year = year;
        this.content = content;
    }

    /** Used when a paper is read from the database (the text is loaded separately). */
    public Paper(int id, String title, String authors, int year) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    /** This text is shown in the drop-down lists. */
    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
