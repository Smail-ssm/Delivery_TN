package com.xdev.deliverytn.recyclerview;

import android.view.View;

public interface UserOrderItemClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
