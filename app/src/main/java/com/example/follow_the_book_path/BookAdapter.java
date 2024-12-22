package com.example.follow_the_book_path;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;
    private OnBookDeleteListener deleteListener;

    // 클릭 리스너 인터페이스 정의
    public interface OnBookClickListener {
        void onBookClick(int position);
    }

    // 삭제 리스너 인터페이스 정의
    public interface OnBookDeleteListener {
        void onBookDelete(int position);
    }

    // 생성자: 클릭 리스너와 삭제 리스너를 받음
    public BookAdapter(List<Book> bookList, OnBookClickListener listener, OnBookDeleteListener deleteListener) {
        this.bookList = bookList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view, listener, deleteListener); // 삭제 리스너 전달
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.genreTextView.setText(book.getGenre());
        holder.statusTextView.setText(book.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        holder.startDateTextView.setText(book.getStartDate() != null ? sdf.format(book.getStartDate()) : "N/A");
        holder.endDateTextView.setText(book.getEndDate() != null ? sdf.format(book.getEndDate()) : "N/A");

        holder.bookImageView.setImageResource(book.getImageResId());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // ViewHolder 클래스
    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, genreTextView, statusTextView;
        TextView startDateTextView, endDateTextView;
        ImageView bookImageView;
        Button deleteButton;

        public BookViewHolder(@NonNull View itemView, OnBookClickListener listener, OnBookDeleteListener deleteListener) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_text_view);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            genreTextView = itemView.findViewById(R.id.genre_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            startDateTextView = itemView.findViewById(R.id.start_date_text_view);
            endDateTextView = itemView.findViewById(R.id.end_date_text_view);
            bookImageView = itemView.findViewById(R.id.book_image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);

            // 삭제 버튼 클릭 리스너 연결
            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onBookDelete(getAdapterPosition());
                }
            });

            // 항목 클릭 리스너 연결
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(getAdapterPosition());
                }
            });
        }
    }
}
