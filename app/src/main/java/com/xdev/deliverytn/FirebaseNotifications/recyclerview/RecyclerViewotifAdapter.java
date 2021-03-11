package com.xdev.deliverytn.FirebaseNotifications.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.FirebaseNotifications.FBNotification;
import com.xdev.deliverytn.R;

import java.util.List;

public class RecyclerViewotifAdapter extends RecyclerView.Adapter<NotifViewHolder> {
    public List<FBNotification> list;
    Context context;



    public RecyclerViewotifAdapter(List<FBNotification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_content, parent, false);
        NotifViewHolder holder = new NotifViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {
        holder.cv.setVisibility(View.VISIBLE);
        holder.isClickable = true;
        FBNotification notif = list.get(position);
        holder.title.setText(notif.getTitle());
        if (notif.getMessage().length() > 40)
            holder.description.setText(notif.getMessage().substring(0, 39) + "...");
        else
            holder.description.setText(notif.getMessage());
    }


    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, FBNotification notif) {
        list.add(position, notif);
        notifyItemInserted(position);
    }


}