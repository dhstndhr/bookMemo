package com.example.follow_the_book_path;

import android.content.ContentValues;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
import android.widget.Button;
        import android.widget.ListView;

        import androidx.activity.result.ActivityResultLauncher;
        import androidx.activity.result.contract.ActivityResultContracts;
        import androidx.appcompat.app.AppCompatActivity;

        import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {
    private ArrayList<Memo> memoList;
    private MemoAdapter memoAdapter;
    private ListView listView;
    private Button addMemoBtn;
    DatabaseHelper database;
    SQLiteDatabase db;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        //title
        setTitle("독서 메모");

        database = new DatabaseHelper(this);
        db = database.getWritableDatabase();

        listView = findViewById(R.id.memoListView);
        addMemoBtn = findViewById(R.id.addMemoBtn);


        memoList = new ArrayList<>();
        memoAdapter = new MemoAdapter(this, memoList);
        listView.setAdapter(memoAdapter);

        dumies();
        loadMemosFromDb();

        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MemoWriteActivity로 이동
                Intent intent = new Intent(getApplicationContext(), MemoWriteActivity.class);
                startActivity(intent);
            }
        });

        // ActivityResultLauncher 등록
        activityResultLauncher = registerForActivityResult(

                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String title = data.getStringExtra("memo_title");
                        String bookName = data.getStringExtra("memo_bookName");
                        String content = data.getStringExtra("memo_content");
                        int pageNumber = data.getIntExtra("memo_pageNumber", -1);
                        String createdAt = data.getStringExtra("memo_createdAt");
                        String updatedAt = data.getStringExtra("memo_updatedAt");

                        addMemoToDb(title, bookName, content,createdAt,updatedAt, pageNumber);
                    } else{
                        Log.e("ActivityResult", "Invalid result or null data.");
                    }
                }
        );
        // 버튼 클릭에서 런처 실행
        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemoWriteActivity.class);
                activityResultLauncher.launch(intent); // 런처 사용
            }
        });
    }
    public void dumies() {
        db.execSQL("INSERT INTO memo (memoTitle,content,createdAt,updatedAt,pageNumber,bookId) VALUES('최고의 작품','나만이 없는 거리',2022-01-02,2022-01-22,22,1)");
          //db.execSQL("INSERT INTO user (email, password, name) VALUES ('on@naver.com','on123','onon')");
          //db.execSQL("INSERT INTO book (bookName, author, userId) VALUES ('Book Title 1', 'Author 1', 1)");
          //db.execSQL("INSERT INTO memo (memoTitle,content,pageNumber,bookId) VALUES('정말재밌어요','바람과 함께 사라지다',33,1)");
    }
    private void loadMemosFromDb() {
        if (db == null) {
            Log.e("DB_ERROR", "Database object is null. Please initialize it before calling this function.");
            return;
        }

        memoList.clear();

        Cursor cursor = null;
        try {
            // JOIN으로 bookName 가져오기
            cursor = db.rawQuery(
                    "SELECT m.memoTitle, m.content, m.pageNumber, b.bookName " +
                            "FROM memo m " +
                            "LEFT JOIN book b ON m.bookId = b.bookId",
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(0);
                    String content = cursor.getString(1);
                    int pageNumber = cursor.getInt(2);
                    String bookName = cursor.getString(3) != null ? cursor.getString(3) : "알 수 없음";

                    // 메모 리스트에 추가
                    memoList.add(new Memo(title, content, null, null, pageNumber, bookName));
                }
            }

        } catch (Exception e) {
            Log.e("LOAD_MEMOS_ERROR", "Error occurred while loading memos from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Adapter에 데이터 업데이트
        if (memoAdapter != null) {
            memoAdapter.notifyDataSetChanged();
        } else {
            Log.e("ADAPTER_ERROR", "MemoAdapter is null. Initialize it before calling this function.");
        }
    }
    private void addMemoToDb(String title, String bookName, String content,String createdAt,String updatedAt, int pageNumber) {
        Log.d("MemoApp", "Add Memo Start");

        // Book ID 찾기
        int bookId = -1;
        Cursor cursor = db.rawQuery(
                "SELECT bookId FROM book WHERE bookName = ?",
                new String[]{bookName}
        );
        if (cursor != null && cursor.moveToFirst()) {
            bookId = cursor.getInt(0);
            Log.d("MemoApp", "Found Book ID: " + bookId);
        }
        if (cursor != null) {
            cursor.close();
        }

        // Book Name이 없을 경우 추가
        if (bookId == -1) {
            ContentValues bookValues = new ContentValues();
            bookValues.put("bookName", bookName);
            bookId = (int) db.insert("book", null, bookValues);
            Log.d("MemoApp", "Inserted New Book, Book ID: " + bookId);
        }

        // Memo 추가
        ContentValues memoValues = new ContentValues();
        memoValues.put("memoTitle", title);
        memoValues.put("bookId", bookId);
        memoValues.put("content", content);
        memoValues.put("pageNumber", pageNumber);
        memoValues.put("createdAt", createdAt);
        memoValues.put("updatedAt",updatedAt);
        long memoId = db.insert("memo", null, memoValues);

        // UI 업데이트
        memoList.add(new Memo(title, content, createdAt,updatedAt, pageNumber, bookName));
        memoAdapter.notifyDataSetChanged();
    }
}


