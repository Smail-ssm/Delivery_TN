package com.xdev.deliverytn.Chat.chatacti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMain extends AppCompatActivity {
    private static final String TAG = ChatMain.class.getName();
    private static final int Orderer = 1;

    String userid;
    private EditText metText;
    private Button mbtSent;
    private DatabaseReference mFirebaseRef;
    private List<String> mChats;
    private RecyclerView mRecyclerView;
    private chatadapter mAdapter;
    private String mId;
    private DatabaseReference msgss;
    private LinearLayout chatLayout;
    ImageView delevimg, userimg;
    private DatabaseReference root;

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
        mbtSent = findViewById(R.id.sendBtn);
//        delevimg=findViewById(R.id.deleveririmg);
//        userimg=findViewById(R.id.userimg);
        mChats = new ArrayList<String>();
        chatLayout = findViewById(R.id.chatLayout);
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));
        userid = FirebaseAuth.getInstance().getUid();
        msgss = FirebaseDatabase.getInstance().getReference("deliveryApp")
                .child("chatRooms").child("roomId").child(c.roomId)
                .child("messages");
        msgss.keepSynced(true);
        root = FirebaseDatabase.getInstance().getReference("deliveryApp").child("users");
        keepitup();
        getallmessages();
        mbtSent.setOnClickListener(v -> {
            String message = metText.getText().toString();

            if (!message.isEmpty()) {
                sendmsg(message);
            } else {
                Toast.makeText(ChatMain.this, "message can not be empty", Toast.LENGTH_SHORT).show();
            }

            metText.setText("");
            metText.requestFocus();
        });
    }

    void keepitup() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Chat c = dataSnapshot.getValue(Chat.class);
                setupMsgNotif(c);
                getallmessages();
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());


                Chat c = dataSnapshot.getValue(Chat.class);
                Toast.makeText(ChatMain.this, "changed" + c.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                Chat c = dataSnapshot.getValue(Chat.class);
                Toast.makeText(ChatMain.this, "moved" + c.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ChatMain.this, "Failed to load messages.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        msgss.addChildEventListener(childEventListener);
    }

    void getallmessages() {

        msgss.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Chat message = postSnapshot.getValue(Chat.class);
                            if (message.getId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
                                showTextView(message.getMessage(), 1);
                            } else {
                                showTextView(message.getMessage(), 0);
                            }
                            setupMsgNotif(message);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }



    public Boolean sendmsg(String msg) {

        DatabaseReference childRoot = msgss.push();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("id", userid);
        map.put("message", msg);

        childRoot.updateChildren(map);
        return true;
    }

    private void setupMsgNotif(Chat c) {

        // prepare intent which is triggered if the
// notification is selected

        Intent intent = new Intent(this, chatrrom.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        DatabaseReference sender = root.child(c.getId());
        Notification n = new Notification.Builder(this)
                .setContentTitle("New Message ")
                .setContentText(c.getMessage())
                .setSmallIcon(R.drawable.bg_button_send_message)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
//                .addAction(R.drawable.icon, "Call", pIntent)
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        if (type == Orderer) {
            layout = getUserLayout();
        } else {
            layout = getBotLayout();
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout);
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
//        playnotifsound();

    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    public void playnotifsound() {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notif);
        mp.start();
    }
}
