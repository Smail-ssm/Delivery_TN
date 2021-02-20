package com.xdev.deliverytn.Chat.chatroom.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatroom.chatrrom;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.user.order.ExpiryDate;
import com.xdev.deliverytn.user.order.ExpiryTime;
import com.xdev.deliverytn.user.order.OrderData;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewChatAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1500; // 3sec
    private final Handler handler = new Handler(); // handler for running delayed runnables
    private final HashMap<OrderData, Runnable> pendingRunables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    public List<String> list;
    Context context;
    String status;
    String price;
    String charge;
    Calendar calendar = Calendar.getInstance();
    List<OrderData> pendingRemovalList;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    public RecyclerViewChatAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        pendingRemovalList = new ArrayList<>();
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
        OrderViewHolder holder = new OrderViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {


        final String data = list.get(position);

        if (pendingRemovalList.contains(data)) {
            /** {show swipe layout} and {hide regular layout} */
            holder.cv.setVisibility(View.GONE);
            holder.isClickable = false;
            holder.swipeLayout.setVisibility(View.VISIBLE);

        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.cv.setVisibility(View.VISIBLE);
            holder.isClickable = true;
            holder.swipeLayout.setVisibility(View.GONE);
            String chat = list.get(position);
            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView

            holder.Order_id.setText(chat);


        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }


//    @Override
//    public int getItemCount() {
//        //returns the number of elements the RecyclerView will display
//        return list.size();
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }






}
