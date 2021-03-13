package com.xdev.deliverytn.Chat.chatacti;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xdev.deliverytn.R;

import java.util.List;

class chatadapter extends RecyclerView.Adapter<chatadapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private final List<String> mDataSet;
    private final String mId;

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     * @param id      Device id
     */
    chatadapter(List<String> dataSet, String id) {
        mDataSet = dataSet;
        mId = id;
    }



    @Override
    public chatadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == CHAT_END) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_end, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_start, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String chat = mDataSet.get(position);
        holder.mTextView.setText(chat);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = itemView.findViewById(R.id.tvMessage);
        }
    }
}