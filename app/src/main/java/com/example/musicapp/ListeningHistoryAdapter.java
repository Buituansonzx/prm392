package com.example.musicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListeningHistoryAdapter extends RecyclerView.Adapter<ListeningHistoryAdapter.ViewHolder> {

    // Hiện tại không có dữ liệu
    private List<String> listeningHistoryItems; // Có thể thay đổi để sử dụng mô hình thực tế sau này

    public ListeningHistoryAdapter() {
        // Khởi tạo mà không có dữ liệu thực tế
        this.listeningHistoryItems = List.of(); // Mẫu rỗng
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_listening_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Không có dữ liệu để hiển thị hiện tại
        holder.itemTitle.setText("Listening History Item " + (position + 1)); // Ví dụ
        holder.itemDescription.setText("Description for Item " + (position + 1)); // Ví dụ
    }

    @Override
    public int getItemCount() {
        return listeningHistoryItems.size(); // Trả về số lượng mục
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TextView itemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemDescription = itemView.findViewById(R.id.itemDescription);
        }
    }
}
