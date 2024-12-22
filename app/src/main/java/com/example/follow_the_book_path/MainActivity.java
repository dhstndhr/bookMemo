package com.example.follow_the_book_path;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "내 서재로 이동" 버튼 참조
        Button goToLibraryButton = findViewById(R.id.go_to_library_button);
        Button goToMemoButton = findViewById(R.id.go_to_memo_button);
        // 버튼 클릭 이벤트 처리
        goToLibraryButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     // LibraryActivity로 이동
                                                     Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                                                     startActivity(intent);
                                                 }
                                             });
        goToMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MemoActivity.class);
                startActivity(intent);
            }
        });
    }
}
