package com.example.follow_the_book_path;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);

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

        memoList.add(new Memo("title1", "context1", "2022-11-11", "2022-11-12", 66, "안녕자두야"));
        memoList.add(new Memo("title2", "context2", "2022-11-10", "2022-11-15", 76, "안녕자두"));
        memoList.add(new Memo("title3", "context3", "2022-11-12", "2022-11-17", 96, "안녕자야"));



        // 버튼 클릭 이벤트
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

                        addMemoToDb(title, bookName, content, pageNumber);
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
       db.execSQL("INSERT INTO user (email, password, name) VALUES ('on@naver.com','on123','onon')");
       db.execSQL("INSERT INTO book (bookName, author, userId) VALUES ('Book Title 1', 'Author 1', 1)");
       db.execSQL("INSERT INTO memo (memoTitle,content,pageNumber,bookId) VALUES('BOOK1','whow',33,1)");
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
    private void addMemoToDb(String title, String bookName, String content, int pageNumber) {
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

        long memoId = db.insert("memo", null, memoValues);
        Log.d("MemoApp", "Inserted Memo, Memo ID: " + memoId);

        // UI 업데이트
        memoList.add(new Memo(title, content, null, null, pageNumber, bookName));
        memoAdapter.notifyDataSetChanged();
        Log.d("MemoApp", "UI Updated");
    }
}

//
//        addMemoBtn = findViewById(R.id.addMemoBtn);
//        listView = findViewById(R.id.memoListView);
//        memoAdapter = new MemoAdapter(this, memoList);
//
//        listView.setAdapter(memoAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView parent, View v, int position, long id){
//                Toast.makeText(getApplicationContext(),
//                        memoAdapter.getItem(position).getTitle(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        //sample data
//        database.insertMemo("Sample Memo", "This is a test memo.", 10, "Sample Book");
//
//        //database.insertMemo("메모1","정말 재밌어요",22,"안녕자두야");
//    //    loadMemoData();
//
//
//
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == 1 && data != null) {
//            String title = data.getStringExtra("memo_title");
//            String bookName = data.getStringExtra("memo_book");
//            String field = data.getStringExtra("memo_field");
//            int bookPage = data.getIntExtra("memo_bookpage", 0);
//
//           // database.insertMemo(title,field,bookPage,);
//
//          //  memoList.add(new Memo(title, field, bookPage, bookName));
//            memoAdapter.notifyDataSetChanged();
//        }
//
//    }
//    private void loadMemoData() {
//        SQLiteDatabase db = database.getReadableDatabase();
//        memoList.clear();
//        Cursor cursor = db.rawQuery(
//                "SELECT m.memoTitle, m.content, m.pageNumber, b.bookName " +
//                        "FROM memo m " +
//                        "LEFT JOIN book b ON m.bookId = b.bookId",
//                null);
//        while (cursor.moveToNext()) {
//            String title = cursor.getString(0);
//            String field = cursor.getString(1);
//            int page = cursor.getInt(2);
//            String book = cursor.getString(3) != null ? cursor.getString(3) : "알 수 없음";
//            Log.d("hi","한개");
//            memoList.add(new Memo(title, field, page, book));
//
//        }
//
//        memoAdapter.notifyDataSetChanged();
//        cursor.close();
//    }

