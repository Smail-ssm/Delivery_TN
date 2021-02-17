package com.xdev.deliverytn.login;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class getRooms {
    public static ArrayList<String> getRooms(String userid, String type, DatabaseReference root) {
        root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db;
        String userId;
        FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();
        userId = userr.getUid();
        ArrayList<String> roomList = new ArrayList<>();
        if (type.equals("orderer")) {
            String orderid = root.child("deliveryApp").child("users").child(userId).child("orderId").toString();
            DatabaseReference deleverirID = root.child("deliveryApp").child("users")
                    .child(userId)
                    .child("orderid").child("acceptedBy").child("delivererID");
            if (orderid.equals(deleverirID.toString())) {

            }
        } else {

        }


        return roomList;
    }
}
