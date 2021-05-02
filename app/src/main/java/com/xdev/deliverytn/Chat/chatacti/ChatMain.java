package com.xdev.deliverytn.Chat.chatacti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.models.Chat;
import com.xdev.deliverytn.models.chatrrom;
import com.xdev.deliverytn.reclamations.createReclamation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xdev.deliverytn.R.string.filedloadingmsgs;
import static com.xdev.deliverytn.R.string.msgcantbeempty;

public class ChatMain extends AppCompatActivity {
    private static final String TAG = ChatMain.class.getName();
    private static final int Orderer = 1;

    String userid;
    ImageView delevimg, userimg;
    String OrdererId;
    String DeliverId;
    String RoomId;
    private EditText metText;
    private Button mbtSent;
    private DatabaseReference mFirebaseRef;
    private List<String> mChats;
    private RecyclerView mRecyclerView;
    private chatadapter mAdapter;
    private String mId;
    private DatabaseReference msgss;
    private LinearLayout chatLayout;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainchat);
        Intent i = getIntent();
        chatrrom c = new chatrrom();
        OrdererId = i.getStringExtra("OrdererId");
        DeliverId = i.getStringExtra("DeliverId");
        RoomId = i.getStringExtra("RoomId");
        c.setOrdererId(OrdererId); //NON-NLS
        c.setDeliverId(DeliverId); //NON-NLS
        c.setRoomId(RoomId); //NON-NLS
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setTitle("Command N° " + RoomId);
        metText = findViewById(R.id.queryEditText);
        mbtSent = findViewById(R.id.sendBtn);
        mChats = new ArrayList<String>();
        chatLayout = findViewById(R.id.chatLayout);
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));
        userid = FirebaseAuth.getInstance().getUid();
        msgss = FirebaseDatabase.getInstance().getReference("deliveryApp") //NON-NLS
                .child("chatRooms").child("roomId").child(c.roomId) //NON-NLS //NON-NLS
                .child("messages"); //NON-NLS
        msgss.keepSynced(true);
        root = FirebaseDatabase.getInstance().getReference("deliveryApp").child("users"); //NON-NLS //NON-NLS
        keepitup();
//        getallmessages();
        mbtSent.setOnClickListener(v -> {


            if (!metText.getText().toString().isEmpty()) {
                sendmsg(metText.getText().toString());
            } else { //NON-NLS
                Toast.makeText(ChatMain.this, msgcantbeempty, Toast.LENGTH_SHORT).show();
            }

            metText.setText("");
            metText.requestFocus();
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reclamation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createRec:
                Uri uri;
                Intent i = new Intent(ChatMain.this, createReclamation.class);
                i.putExtra("OrdererId", OrdererId);
                i.putExtra("DeliverId", DeliverId);
                i.putExtra("RoomId   ", RoomId);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void keepitup() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Chat c = dataSnapshot.getValue(Chat.class);
                setupMsgNotif(c);
                getallmessages();
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatMain.this, filedloadingmsgs,
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

        Map<String, Object> map = new HashMap<String, Object>(); //NON-NLS //NON-NLS

        map.put("id", userid); //NON-NLS
        map.put("message", msg); //NON-NLS

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
                .setContentTitle(getString(R.string.newmsgs))
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
