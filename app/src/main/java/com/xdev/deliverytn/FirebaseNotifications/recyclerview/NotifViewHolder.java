package com.xdev.deliverytn.FirebaseNotifications.recyclerview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.R;


public class NotifViewHolder extends RecyclerView.ViewHolder {

    public boolean isClickable = true;
    CardView cv;
    LinearLayout swipeLayout;
    TextView title;
    TextView description;


    NotifViewHolder(View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.notifCarda);
        title = itemView.findViewById(R.id.title);
        description = itemView.findViewById(R.id.description);

    }
}
