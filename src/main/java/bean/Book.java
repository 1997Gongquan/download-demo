package bean;

import java.util.Date;

public class Book {
    private String category;
    private String name;
    private String bookHref;
    private String lastChapter;
    private String lastChapterHref;
    private String author;
    private Date lastUpdate;
    private String status;

    public String getLastChapterHref() {
        return lastChapterHref;
    }

    public void setLastChapterHref(String lastChapterHref) {
        this.lastChapterHref = lastChapterHref;
    }

    public String getBookHref() {
        return bookHref;
    }

    public void setBookHref(String bookHref) {
        this.bookHref = bookHref;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book{" +
                "category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", bookHref='" + bookHref + '\'' +
                ", lastChapter='" + lastChapter + '\'' +
                ", lastChapterHref='" + lastChapterHref + '\'' +
                ", author='" + author + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", status='" + status + '\'' +
                '}';
    }
}
