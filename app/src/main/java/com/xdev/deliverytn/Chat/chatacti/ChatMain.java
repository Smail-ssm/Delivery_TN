package com.xdev.deliverytn.Chat.chatacti;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatroom.chatrrom;
import com.xdev.deliverytn.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatMain extends AppCompatActivity {
    private static final String TAG = ChatMain.class.getName();
    private static final int Orderer = 1;
    DatabaseReference msgs;
    private EditText metText;
    private Button mbtSent;
    private DatabaseReference mFirebaseRef;
    private List<String> mChats;
    private RecyclerView mRecyclerView;
    private chatadapter mAdapter;
    private String mId;
    private DatabaseReference msgss;
    private LinearLayout chatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainchat);
        Intent i = getIntent();
        chatrrom c = new chatrrom();
        c.setOrdererId(i.getStringExtra("OrdererId"));
        c.setDeliverId(i.getStringExtra("DeliverId"));
        c.setRoomId(i.getStringExtra("RoomId"));
        metText = findViewById(R.id.queryEditText);
        mbtSent = findViewById(R.id.sendBtn); mChats = new ArrayList<String>();
        chatLayout = findViewById(R.id.chatLayout);
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("deliveryApp").child("chatRooms").child("roomId").child(c.getRoomId());
        msgs = FirebaseDatabase.getInstance().getReference("deliveryApp").child("chatRooms").child("roomId").child(c.getRoomId()).child("message");
        mFirebaseRef = database.child("message");

        mbtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = metText.getText().toString();

                if (!message.isEmpty()) {

                    mFirebaseRef.setValue(new Chat(message, FirebaseAuth.getInstance().getUid()));
                } else {
                    Toast.makeText(ChatMain.this, "message can not be empty", Toast.LENGTH_SHORT).show();
                }

                showTextView(message, 1);
                metText.setText(null);
                metText.requestFocus();
            }
        });

          getallmessages(c);
        msgs.keepSynced(true);
        database.keepSynced(true);
        msgs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        Chat model = dataSnapshot.getValue(Chat.class);
if (!model.getId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
    showTextView(model.getMessage(), 0);

}
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        msgs.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        Chat model = dataSnapshot.getValue(Chat.class);
                        if (model.getId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
                            showTextView(model.getMessage(), 0);
                        }

                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void sendMessage(View view) {

    }

    void getallmessages(chatrrom c) {
        msgss = FirebaseDatabase.getInstance().getReference("deliveryApp")
                .child("chatRooms").child("roomId")
                .child(c.getRoomId());
        msgss.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Map<Chat, Object> map
                                = (Map<Chat, Object>) dataSnapshot.getValue();
//                        if (map.get(0)getId()
//                                .equals(FirebaseAuth.getInstance()
//                                        .getUid())) {
//                            showTextView(c.getMessage(), 1);
//                        } else {
//                            showTextView(c.getMessage(), 0);
//
//                        }
                        Toast.makeText(ChatMain.this, map.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }


    private void showTextView(String message, int type) {
        FrameLayout layout;
        if (type == Orderer) {
            layout = getUserLayout();
        } else {
            layout = getBotLayout();
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.notif);
            mp.start();
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout);
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }
}
