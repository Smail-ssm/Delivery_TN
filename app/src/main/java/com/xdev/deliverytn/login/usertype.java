package com.xdev.deliverytn.login;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class usertype {
    public static void usertype(DatabaseReference root,String type) {
        root = FirebaseDatabase.getInstance().getReference();
        String userId;
        FirebaseUser userr =  FirebaseAuth.getInstance().getCurrentUser();
        userId = userr.getUid();
        root.child("deliveryApp").child("users") //NON-NLS //NON-NLS
                .child(userId)
                .child("usertype").setValue(type); //NON-NLS
    }
}
