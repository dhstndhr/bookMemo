package com.example.follow_the_book_path;

public class Memo {
    private String title;
    private String content;
    private int pageNumber;
    private String createdAt;
    private String updatedAt;
    private String bookName;

    // 생성자
    public Memo(String title, String content,String createdAt,String updatedAt ,int pageNumber, String bookName) {
        this.title = title;
        this.content = content;
        this.pageNumber = pageNumber;
        this.bookName = bookName;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }
    public String getCreatedAt(){
        return createdAt;
    };
    public String getUpdatedAt(){
        return updatedAt;
    }

    public String getBookName() {
        return bookName;
    }
}