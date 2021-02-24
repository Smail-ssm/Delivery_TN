package com.xdev.deliverytn.FirebaseNotifications.recyclerview;

import android.view.View;

public interface NotifClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
