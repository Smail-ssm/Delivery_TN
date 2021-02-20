package com.xdev.deliverytn.Chat.chatroom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatacti.Chat;
import com.xdev.deliverytn.Chat.chatacti.ChatMain;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.login.user_details.UserDetails;

import java.util.ArrayList;

public class chatRooms extends AppCompatActivity {

    ArrayList<String> chats = new ArrayList<>();
    ListView chatlist;
    Toolbar toolbar;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<chatrrom> chatrooms = new ArrayList<>();
    SwipeRefreshLayout swipe;
    private UserDetails currentUserDetails = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms2);
        chatlist = findViewById(R.id.listchat);
        swipe = findViewById(R.id.swipechat);
        setUpToolBarAndActionBar();
        getrooms();
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                chatrrom chatrrom = chatrooms.get(position);
                Intent intent = new Intent(chatRooms.this, ChatMain.class);
                intent.putExtra("DeliverId",
                        chatrrom.getDeliverId());
                intent.putExtra("OrdererId",
                        chatrrom.getOrdererId());
                intent.putExtra("RoomId",
                        chatrrom.getRoomId());
                startActivity(intent);
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayAdapter.clear();
                getrooms();
                swipe.setRefreshing(false);
            }
        });
//        getcurrentuserUId();
    }


    private void getrooms() {

        final DatabaseReference nm = FirebaseDatabase.getInstance().getReference("deliveryApp")
                .child("chatRooms")
                .child("roomId");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        chatrrom l = npsnapshot.
                                getValue(chatrrom.class);

                        if (FirebaseAuth.getInstance().getUid().equals(l.ordererId) || FirebaseAuth.getInstance().getUid().equals(l.deliverId)) {
                            chats.add(l.getRoomId());
                            chatrooms.add(l);

                        } else {
                            showsnacks("no rooms");
                        }

                    }
                    arrayAdapter = new ArrayAdapter<String>(chatRooms.this, android.R.layout.simple_list_item_1, chats);
                    chatlist.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setUpToolBarAndActionBar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setTitle("Chat rooms");

    }

    private void showsnacks(String msg) {
        int color;
        color = Color.WHITE;
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    public void getcurrentuserUId(Chat c) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(c.getId());
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserDetails = dataSnapshot.getValue(UserDetails.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
