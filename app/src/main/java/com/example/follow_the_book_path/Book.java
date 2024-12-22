package com.example.follow_the_book_path;
import java.util.Date;

public class Book {
    private String title; //책제목
    private String author; //저자
    private String genre; // 장르
    private Date startDate; // 읽기 시작 날짜
    private Date endDate; // 완독 날짜
    private String status; //읽은 상태
    private int imageResId; //책 이미지

    public Book(String title, String author, String genre, Date startDate, Date endDate, String status, int imageResId) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.imageResId = imageResId;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }

    public String getStatus() {
        return status;
    }

    public int getImageResId() {
        return imageResId;
    }
}
