package com.example.follow_the_book_path;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "followTheBookPath.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //외래키 활성화
        db.execSQL("PRAGMA foreign_keys=ON;");

        // 사용자 테이블
        db.execSQL("CREATE TABLE user (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "name TEXT NOT NULL);");

        // 책 테이블
        db.execSQL("CREATE TABLE book (" +
                "bookId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bookName TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "genre TEXT, " +
                "startDate DATE, " +  // 시작 날짜
                "endDate DATE, " +    // 완료 날짜
                "status TEXT, " +
                "imageResId INTEGER, " +  // 이미지 리소스 추가
                "userId INTEGER NOT NULL, " +
                "FOREIGN KEY (userId) REFERENCES user(userId));");

        // 카테고리(장렬) 테이블
        db.execSQL("CREATE TABLE category (" +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoryName TEXT NOT NULL);");

        // 책-카테고리 연결 테이블
        db.execSQL("CREATE TABLE bookCategory (" +
                "bookId INTEGER NOT NULL, " +
                "categoryId INTEGER NOT NULL, " +
                "PRIMARY KEY (bookId, categoryId)," +
                "FOREIGN KEY (bookId) REFERENCES book(bookId)," +
                "FOREIGN KEY (categoryId) REFERENCES category(categoryId));");



        // 메모 테이블
        db.execSQL("CREATE TABLE memo (" +
                "memoId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "memoTitle TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "createdAt DATETIME DEFAULT (datetime('now')), " +
                "updatedAt DATETIME DEFAULT (datetime('now')), " +
                "pageNumber INTEGER, " +
                "bookId INTEGER, " +
                "FOREIGN KEY (bookId) REFERENCES book(bookId));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS book");
        db.execSQL("DROP TABLE IF EXISTS category");
        db.execSQL("DROP TABLE IF EXISTS bookCategory");
        db.execSQL("DROP TABLE IF EXISTS memo");

        // 새 테이블 생성
        onCreate(db);
    }
    public void getBookTitle(ArrayList<String> list){
        Cursor cursor = db.rawQuery("SELECT bookname FROM book",null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();

    }
}