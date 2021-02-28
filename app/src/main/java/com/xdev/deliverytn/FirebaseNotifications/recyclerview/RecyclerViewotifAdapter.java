package com.xdev.deliverytn.FirebaseNotifications.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.FirebaseNotifications.FBNotification;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.order.OrderData;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewotifAdapter extends RecyclerView.Adapter<NotifViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1500; // 3sec
    private final Handler handler = new Handler(); // handler for running delayed runnables
    private final HashMap<OrderData, Runnable> pendingRunables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    public List<FBNotification> list;
    Context context;
    String title;
    String description;


    public RecyclerViewotifAdapter(List<FBNotification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_content, parent, false);
        NotifViewHolder holder = new NotifViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {


        final FBNotification data = list.get(position);


        /** {show regular layout} and {hide swipe layout} */
        holder.cv.setVisibility(View.VISIBLE);
        holder.isClickable = true;
        FBNotification notif = list.get(position);
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(data.getTitle());
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

    // Remove a RecyclerView item containing a specified OrderData object
    public void remove(int notif) {

        int position = list.indexOf(notif);
        if (position != -1) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int getPosition(OrderData orderData) {
        return list.indexOf(orderData);
    }


}
