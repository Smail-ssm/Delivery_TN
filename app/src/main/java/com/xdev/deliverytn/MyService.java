package com.xdev.deliverytn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xdev.deliverytn.user.order.OrderData;

public class MyService extends Service {
    public OrderData myOrder;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference deliveryApp;

    public MyService(DatabaseReference deliveryApp) {
        this.deliveryApp = deliveryApp;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myOrder = intent.getParcelableExtra("MyOrder");
        String location = intent.getStringExtra("location");

        //TODO do something useful
        deliveryApp = root.child("deliveryApp").child("orders").child(myOrder.acceptedBy.delivererID).child(myOrder.acceptedBy.currentLocation);
        deliveryApp.setValue(location);
        deliveryApp.keepSynced(true);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}