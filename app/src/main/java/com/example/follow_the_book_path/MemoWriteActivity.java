package com.example.follow_the_book_path;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MemoWriteActivity extends AppCompatActivity {
    DatabaseHelper database;
    SQLiteDatabase db;
    Button memoSubmitBtn,findBookBtn;
    TextView memoBookName;
    EditText memoTitle,memoPageNumber,memoContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_write);

        //db
        database = new DatabaseHelper(MemoWriteActivity.this);
        db = database.getWritableDatabase();

        //연결
        memoBookName=findViewById(R.id.memoBookName);
        memoTitle = findViewById(R.id.memoTitle);
        memoPageNumber = findViewById(R.id.memoPageNumber);
        memoContent= findViewById(R.id.memoContent);
        findBookBtn = findViewById(R.id.findBookBtn);
        memoSubmitBtn = findViewById(R.id.memoSubmitBtn);

        memoSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = memoTitle.getText().toString();
                String bookName = memoBookName.getText().toString();
                String content = memoContent.getText().toString();
                String pageNum = memoPageNumber.getText().toString();
                // 빈 값 확인
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 실행 중단
                }

                if (bookName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "책을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 실행 중단
                }
                if (pageNum.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "페이지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 실행 중단
                }
                if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 실행 중단
                }

                Intent outIntent = new Intent();

                outIntent.putExtra("memo_title",title);
                outIntent.putExtra("memo_bookName",bookName);
                outIntent.putExtra("memo_content",content);
                outIntent.putExtra("memo_pageNumber",Integer.parseInt(pageNum));
                setResult(RESULT_OK,outIntent);

                finish();
            }
        });
        findBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> bookList =new ArrayList<>();
                // 책 제목 리스트 가져오기
                database.getBookTitle(bookList);
                // 대화상자에 표시
                showBookDialog(bookList);
            }
        });

    }

    private void showBookDialog(ArrayList<String> bookTitles) {
        String[] bookTitlesArray = bookTitles.toArray(new String[0]);
        AlertDialog.Builder dlg = new AlertDialog.Builder(MemoWriteActivity.this);
        dlg.setTitle("책 선택");
        dlg.setItems(bookTitlesArray, (dialog, which) -> {
                    // 사용자가 선택한 책 처리
                    String selectedBook = bookTitlesArray[which];
                    // 처리 로직 (예: 선택한 책을 TextView에 표시)
                    TextView memoBookName = findViewById(R.id.memoBookName);
                    memoBookName.setText(selectedBook);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
