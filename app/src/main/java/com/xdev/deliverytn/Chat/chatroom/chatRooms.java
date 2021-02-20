package com.xdev.deliverytn.Chat.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatacti.ChatMain;
import com.xdev.deliverytn.R;

import java.util.ArrayList;

public class chatRooms extends AppCompatActivity {

    ArrayList<String> chats = new ArrayList<>();
    ListView chatlist;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<chatrrom> chatrooms = new ArrayList<>();
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms2);
        chatlist = findViewById(R.id.listchat);
        swipe = findViewById(R.id.swipechat);
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
                        chats.add(l.getRoomId());
                        chatrooms.add(l);

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


}
