package com.xdev.deliverytn.Chat.chatroom.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.R;


public class OrderViewHolder extends RecyclerView.ViewHolder {

    public boolean isClickable = true;
    CardView cv;
    LinearLayout swipeLayout;
    TextView Order_id;

    OrderViewHolder(View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.cardView);
        swipeLayout = itemView.findViewById(R.id.swipeLayout);
        Order_id = itemView.findViewById(R.id.category);

    }
}
