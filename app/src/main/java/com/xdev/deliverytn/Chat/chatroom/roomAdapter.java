package com.xdev.deliverytn.Chat.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.R;

import java.util.List;

public class roomAdapter extends RecyclerView.Adapter<roomAdapter.ViewHolder> {
    private static ClickListener clickListener;
    private final List<chatrrom> RoomList;

    public roomAdapter(List<chatrrom> listData) {
        this.RoomList = listData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        chatrrom ld = RoomList.get(position);
        holder.txtid.setText(ld.getRoomId());

    }

    @Override
    public int getItemCount() {
        return RoomList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView txtid;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            txtid = itemView.findViewById(R.id.orderId);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            roomAdapter.clickListener = clickListener;
        }

    }

}