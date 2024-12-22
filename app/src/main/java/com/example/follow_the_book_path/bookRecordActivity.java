package com.example.follow_the_book_path;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class bookRecordActivity extends AppCompatActivity {

    private EditText edtBookName, edtAuthor, edtGenre, edtStartDate, edtEndDate;
    private Spinner spinnerStatus;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private int userId;
    private int bookId = -1; // 신규 추가를 나타냄
    private String[] statusOptions = {"읽는 중", "완독", "중단"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_record);

        // UI 초기화
        edtBookName = findViewById(R.id.edtBookName);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtGenre = findViewById(R.id.edtGenre);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        Button btnBookRecord = findViewById(R.id.btnBookRecord);

        // 데이터베이스 연결
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Spinner 초기화
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Intent에서 데이터 받기
        Intent inIntent = getIntent();
        userId = inIntent.getIntExtra("userId", -1);
        bookId = inIntent.getIntExtra("bookId", -1);

        // 기존 책 정보 불러오기
        if (bookId != -1) {
            loadBookData(bookId);
        }

        // 완료 버튼 클릭 -> 데이터 저장
        btnBookRecord.setOnClickListener(v -> saveBookData());
    }

    // 기존 책 데이터 로드
    private void loadBookData(int bookId) {
        Cursor cursor = db.rawQuery("SELECT * FROM book WHERE bookId = ?", new String[]{String.valueOf(bookId)});

        if (cursor.moveToFirst()) {
            edtBookName.setText(cursor.getString(cursor.getColumnIndexOrThrow("bookName")));
            edtAuthor.setText(cursor.getString(cursor.getColumnIndexOrThrow("author")));
            edtGenre.setText(cursor.getString(cursor.getColumnIndexOrThrow("genre")));
            edtStartDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
            edtEndDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            // Spinner 상태 설정
            for (int i = 0; i < statusOptions.length; i++) {
                if (statusOptions[i].equals(status)) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }
        cursor.close();
    }

    // 데이터 저장
    private void saveBookData() {
        String bookName = edtBookName.getText().toString();
        String author = edtAuthor.getText().toString();
        String genre = edtGenre.getText().toString();
        String startDate = edtStartDate.getText().toString();
        String endDate = edtEndDate.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (bookName.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "책 제목과 저자는 필수 입력 항목입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bookId == -1) {
            // 신규 추가
            db.execSQL("INSERT INTO book (bookName, author, genre, startDate, endDate, status, userId) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{bookName, author, genre, startDate, endDate, status, userId});
            Toast.makeText(this, "책이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 기존 데이터 수정
            db.execSQL("UPDATE book SET bookName=?, author=?, genre=?, startDate=?, endDate=?, status=? WHERE bookId=?",
                    new Object[]{bookName, author, genre, startDate, endDate, status, bookId});
            Toast.makeText(this, "책이 수정되었습니다.", Toast.LENGTH_SHORT).show();
        }

        // 결과 전달 후 종료
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "success");
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
