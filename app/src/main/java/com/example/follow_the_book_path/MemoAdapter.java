package com.example.follow_the_book_path;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends ArrayAdapter<Memo> {
    Context context;
    ArrayList<Memo>memoList;
    public MemoAdapter(Context context, ArrayList<Memo> memos) {
        super(context,0,memos);
        this.context = context;
        this.memoList = memos;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_memo, parent, false);
        }

        // 현재 Memo 객체 가져오기
        Memo currentMemo = memoList.get(position);

        TextView titleView = convertView.findViewById(R.id.memoTitle);
        TextView bookView = convertView.findViewById(R.id.memoBook);
        TextView pageView = convertView.findViewById(R.id.memoBookPage);
        TextView createdAtView = convertView.findViewById(R.id.memoCreatedAt);
        TextView updatedAtView = convertView.findViewById(R.id.memoUpdatedAt);

        if(currentMemo!=null) {
            titleView.setText("제목: " + currentMemo.getTitle());
            bookView.setText("책: " + currentMemo.getBookName());
            pageView.setText("페이지: " + currentMemo.getPageNumber());
            createdAtView.setText("생성시간: " + currentMemo.getCreatedAt());
            updatedAtView.setText("수정시간: " + currentMemo.getUpdatedAt());
        }
        return convertView;
    }
}
