package com.xdev.deliverytn.recyclerview;

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
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.order.ExpiryDate;
import com.xdev.deliverytn.order.ExpiryTime;
import com.xdev.deliverytn.order.OrderData;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewOrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1500; // 3sec
    private final Handler handler = new Handler(); // handler for running delayed runnables
    private final HashMap<OrderData, Runnable> pendingRunables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    public List<OrderData> list;
    Context context;
    String status;
    String price;
    String charge;
    Calendar calendar = Calendar.getInstance();
    List<OrderData> pendingRemovalList;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    public RecyclerViewOrderAdapter(List<OrderData> list, Context context) {
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


        final OrderData data = list.get(position);

        if (pendingRemovalList.contains(data)) {
            /** {show swipe layout} and {hide regular layout} */
            holder.cv.setVisibility(View.GONE);
            holder.isClickable = false;
            holder.swipeLayout.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoOpt(data);
                }
            });
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.cv.setVisibility(View.VISIBLE);
            holder.isClickable = true;
            holder.swipeLayout.setVisibility(View.GONE);
            OrderData order = list.get(position);
            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            status = String.valueOf(order.status.charAt(0));
            price = Integer.toString(order.max_range);
            charge = Integer.toString(order.deliveryCharge);
            holder.category.setText(order.category);
            setExpiry(order.expiryDate, order.expiryTime);
            String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

            holder.expiry.setText(date.substring(0, date.length() - 4) + " " + time);
            if (order.description.length() > 40)
                holder.description.setText(order.description.substring(0, 39) + "...");
            else
                holder.description.setText(order.description);

            TextDrawable drawable = TextDrawable.builder().buildRound(status, Color.parseColor(getColor(status)));
            holder.imageView.setImageDrawable(drawable);
            //primary color
            drawable = TextDrawable.builder().buildRoundRect("TND" + price, Color.parseColor("#5D4037"), 20);
            holder.displayPrice.setImageDrawable(drawable);
            drawable = TextDrawable.builder().beginConfig().textColor(Color.WHITE).endConfig().buildRound("TND" + charge, Color.BLACK);
            holder.displayCharge.setImageDrawable(drawable);


        }

    }

    void setExpiry(ExpiryDate expiryDate, ExpiryTime expiryTime) {

        calendar.set(Calendar.YEAR, expiryDate.year);
        calendar.set(Calendar.MONTH, expiryDate.month);
        calendar.set(Calendar.DAY_OF_MONTH, expiryDate.day);
        calendar.set(Calendar.HOUR_OF_DAY, expiryTime.hour);
        calendar.set(Calendar.MINUTE, expiryTime.minute);

    }

    private void undoOpt(OrderData delOrder) {
        Runnable pendingRemovalRunnable = pendingRunables.get(delOrder);
        pendingRunables.remove(delOrder);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        pendingRemovalList.remove(delOrder);
        // this will rebind the row in "normal" state
        notifyItemChanged(list.indexOf(delOrder));
    }

    public void pendingRemoval(int position) {

        final OrderData data = list.get(position);
        if (!pendingRemovalList.contains(data)) {
            pendingRemovalList.add(data);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    setStatusCancelled(data.orderId);
                    remove(list.indexOf(data));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunables.put(data, pendingRemovalRunnable);
        }
    }

    void setStatusCancelled(final int orderId) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference stat_ref = root.child("deliveryApp").child("orders").child(userId).child(Integer.toString(orderId)).child("status");
        stat_ref.keepSynced(true);
        stat_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String curr_status = dataSnapshot.getValue(String.class);
                if (curr_status.equals("PENDING"))
                    root.child("deliveryApp").child("orders").child(userId).child(Integer.toString(orderId)).child("status").setValue("CANCELLED");
                else {
                    Toast.makeText(context, "Can not cancel , this order is active", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    public void insert(int position, OrderData OrderData) {
        list.add(position, OrderData);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified OrderData object
    public void remove(OrderData OrderData) {

        int position = list.indexOf(OrderData);
        if (position != -1) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int getPosition(OrderData orderData) {
        return list.indexOf(orderData);
    }

    public void remove(int position) {
        if (list.isEmpty())
            return;
        OrderData data = list.get(position);
        pendingRemovalList.remove(data);
        if (list.contains(data)) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        OrderData data = list.get(position);
        return pendingRemovalList.contains(data);
    }

    String getColor(String st) {

        if (st.equals("P"))
            return "#ffa000";
        else if (st.equals("A"))
            return "#8bc34a";
        else if (st.equals("E"))
            return "#f44336";
        else if (st.equals("F"))
            return "#673AB7";
        else
            return "#9e9e9e";

    }


}
