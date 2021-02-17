package com.xdev.deliverytn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xdev.deliverytn.user.order.OrderData;

public class syncLocation extends Service {
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference deliveryApp;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public syncLocation(Intent intent , OrderData myOrder) {

        deliveryApp = root.child("deliveryApp").child("orders").child(myOrder.acceptedBy.delivererID).child(myOrder.acceptedBy.currentLocation);
        deliveryApp.keepSynced(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}