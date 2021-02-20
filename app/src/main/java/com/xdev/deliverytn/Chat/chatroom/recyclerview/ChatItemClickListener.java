package com.xdev.deliverytn.Chat.chatroom.recyclerview;

import android.view.View;

public interface ChatItemClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
