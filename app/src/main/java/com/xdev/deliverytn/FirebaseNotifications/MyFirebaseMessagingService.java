package com.xdev.deliverytn.FirebaseNotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xdev.deliverytn.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference notifref, totalnotif;
    private Integer notifcount;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        shownotification(remoteMessage.getNotification());
        FBNotification notif = new FBNotification();
        notif.setMessage(remoteMessage.getNotification().getBody());
        notif.setTitle(remoteMessage.getNotification().getTitle());
        notifref = root.child("deliveryApp").child("Notifications");
        totalnotif = root.child("deliveryApp").child("totalNotifications");
        totalnotif.keepSynced(true);
        totalnotif.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifcount = dataSnapshot.getValue(Integer.class);
                totalnotif.setValue(notifcount + 1);
                notifref.keepSynced(true);
                notifref = root.child("deliveryApp").child("Notifications").child(String.valueOf(notifcount + 1));
                addnotif(notif);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private void addnotif(FBNotification notif) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", notif.getMessage());
        map.put("title", notif.getTitle());
        notifref.setValue(map, (databaseError, databaseReference) -> {
            if (databaseError != null) {
            } else {
            }
        });
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN", s);
    }


    public void shownotification(RemoteMessage.Notification message) {


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.xdev.binidik"; //your app package name

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notification),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_info_24)
                .setContentTitle(message.getTitle())
                .setContentText(message.getBody())
                .setContentInfo(getString(R.string.info));

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }

}