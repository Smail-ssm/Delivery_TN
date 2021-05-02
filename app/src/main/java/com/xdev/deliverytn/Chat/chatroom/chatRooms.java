package com.xdev.deliverytn.Chat.chatroom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatacti.ChatMain;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.models.chatrrom;

import java.util.ArrayList;

import static com.xdev.deliverytn.R.string.Chat_rooms;

public class chatRooms extends AppCompatActivity {

    ArrayList<String> chats = new ArrayList<>();
    ListView chatlist;
    Toolbar toolbar;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<chatrrom> chatrooms = new ArrayList<>();
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms2);
        chatlist = findViewById(R.id.listchat);
        swipe = findViewById(R.id.swipechat);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Chat_rooms);
        getrooms();
        chatlist.setOnItemClickListener((arg0, v, position, arg3) -> {
            chatrrom chatrrom = chatrooms.get(position);
            Intent intent = new Intent(chatRooms.this, ChatMain.class);
            intent.putExtra("DeliverId", chatrrom.getDeliverId());
            intent.putExtra("OrdererId", chatrrom.getOrdererId());
            intent.putExtra("RoomId", chatrrom.getRoomId());
            startActivity(intent);
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayAdapter.clear();
                getrooms();
                swipe.setRefreshing(false);
            }
        });

    }


    private void getrooms() {
        FirebaseDatabase.getInstance().getReference("deliveryApp").child("chatRooms").child("roomId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        chatrrom l = npsnapshot.getValue(chatrrom.class);
                        findViewById(R.id.txt).setVisibility(View.GONE);

                        if (!(FirebaseAuth.getInstance().getUid().equals(l.ordererId))) {
                            findViewById(R.id.txt).setVisibility(View.VISIBLE);

                        } else {

                            chats.add(getString(R.string.commndnumber) + l.getRoomId());
                            chatrooms.add(l);
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


}
