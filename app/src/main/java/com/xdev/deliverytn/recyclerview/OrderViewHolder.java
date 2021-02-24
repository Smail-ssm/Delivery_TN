package com.xdev.deliverytn.recyclerview;

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
    TextView category;
    TextView description;
    ImageView imageView;
    ImageView displayPrice;
    ImageView displayCharge;
    TextView undo;
    TextView expiry;

    OrderViewHolder(View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.notifCard);
        swipeLayout = itemView.findViewById(R.id.swipeLayout);
        undo = itemView.findViewById(R.id.undo);
        category = itemView.findViewById(R.id.category);
        description = itemView.findViewById(R.id.description);
        imageView = itemView.findViewById(R.id.imageView);
        displayCharge = itemView.findViewById(R.id.chargeDisplay);
        displayPrice = itemView.findViewById(R.id.priceDisplay);
        expiry = itemView.findViewById(R.id.expiryDisplay);
    }
}
