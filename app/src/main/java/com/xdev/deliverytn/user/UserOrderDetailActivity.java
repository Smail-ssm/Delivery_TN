package com.xdev.deliverytn.user;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.order_form.EditOrderForm;


public class UserOrderDetailActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    Button acceptedBy;
    OrderData myOrder;
    private TextView userName;
    private TextView userPhoneNumber;
    private String deliverer_details;
    private UserDetails userDetails = new UserDetails();
    private UserDetails delevirerdetails = new UserDetails();
    private DatabaseReference deliveryApp;
    private RatingBar mRatingBar;

    public UserOrderDetailActivity() {
        myOrder = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        checkConnection();
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        TextView category = findViewById(R.id.category);
        TextView description = findViewById(R.id.description);
        TextView orderId = findViewById(R.id.orderId);
        TextView min_range = findViewById(R.id.price_range_min);
        TextView max_range = findViewById(R.id.price_range_max);
        userName = findViewById(R.id.userName);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        TextView userLocationName = findViewById(R.id.userLocationName);
        TextView userLocationLocation = findViewById(R.id.userLocationLocation);
        TextView expiryTime_Date = findViewById(R.id.expiryTime_Date);
        TextView expiryTime_Time = findViewById(R.id.expiryTime_Time);
        TextView final_item_price = findViewById(R.id.final_item_price);
        TextView deliveryCharge = findViewById(R.id.deliveryCharge);
        TextView final_total = findViewById(R.id.final_total);
        acceptedBy = findViewById(R.id.btn_accepted_by);
        TextView status = findViewById(R.id.status);
        TextView otp_h = findViewById(R.id.otp_h);
        TextView otp = findViewById(R.id.otp);

        FloatingActionButton fab = findViewById(R.id.fab);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        myOrder = intent.getParcelableExtra("MyOrder");
        if (myOrder.acceptedBy.delivererID != null) {
            fetchDelivererdetail();
        }
        CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(myOrder.category);
        }


        otp_h.setVisibility(View.GONE);
        otp.setVisibility(View.GONE);

        if (myOrder.status.equals("PENDING")) {
            fab.setVisibility(View.VISIBLE);
        }
        if (myOrder.status.equals("ACTIVE")) {
//            syncDelLocation();

        }

        if (myOrder.status.equals("PENDING") || myOrder.status.equals("CANCELLED") || myOrder.status.equals("EXPIRED")) {
            acceptedBy.setEnabled(false);
            acceptedBy.setVisibility(View.GONE);
        } else if (myOrder.status.equals("FINISHED")) {
            acceptedBy.setText("Delivered By");
        }

        if (!myOrder.otp.equals("")) {
            otp_h.setVisibility(View.VISIBLE);
            otp.setVisibility(View.VISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    Intent intent = new Intent(UserOrderDetailActivity.this, EditOrderForm.class);
                    intent.putExtra("MyOrder", myOrder);
                    startActivity(intent);
                    finish();
                }
            }
        });

        deliverer_details = getString(R.string.nom) + " \t\t\t" + myOrder.acceptedBy.name + "\n" + getString(R.string.mobile) + " \t\t\t" + myOrder.acceptedBy.mobile;
        if (!myOrder.acceptedBy.alt_mobile.equals("")) {
            deliverer_details += "\n" + getString(R.string.alt_mobile) + " \t\t\t" + myOrder.acceptedBy.alt_mobile;

        }
        deliverer_details += "\n" + getString(R.string.emaill) + " \t\t\t" + myOrder.acceptedBy.email;
        acceptedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callDeliverer();

                new AlertDialog.Builder(UserOrderDetailActivity.this)
                        .setTitle(R.string.delevirerDetails)
                        .setMessage(deliverer_details)
                        .setPositiveButton(getString(R.string.dialog_ok), null)
                        .setNeutralButton("chat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserOrderDetailActivity.this, myOrder.acceptedBy.delivererID, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(UserOrderDetailActivity.this, chatRooms.class);
//                                i.putExtra("Delivererid", myOrder.acceptedBy.delivererID);
                                startActivity(i);
                            }
                        }).setNegativeButton(R.string.call, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!userPhoneNumber.getText().toString().equals("-")) {
                            Intent mIntent = new Intent(Intent.ACTION_CALL);
                            String phone = myOrder.acceptedBy.mobile;
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                            startActivity(intent);
//                    mIntent.setData(Uri.parse(number));
// Here, thisActivity is the current activity
                            if (ContextCompat.checkSelfPermission(UserOrderDetailActivity.this,
                                    Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(UserOrderDetailActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

                                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            } else {
                                //You already have permission
                                try {
                                    startActivity(mIntent);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                    }
                })
                        .show();
            }
        });
//        rankBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Dialog rankDialog = new Dialog(UserOrderDetailActivity.this, R.style.FullHeightDialog);
//                rankDialog.setContentView(R.layout.rank_dialog);
//                rankDialog.setCancelable(true);
//                RatingBar ratingBar = rankDialog.findViewById(R.id.ratingbar);
//
//                Button updateButton = rankDialog.findViewById(R.id.rank_dialog_button);
//
//                DatabaseReference rate = ratingRef.child("rate");
//
//                rate.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
//                            float rating = Float.parseFloat(dataSnapshot.getValue().toString());
//                            ratingBar.setRating(rating);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//                DatabaseReference rate_number = ratingRef.child("rate_number");
//
//                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                    @Override
//                    public void onRatingChanged(
//                            RatingBar ratingBar,
//                            float rating,
//                            boolean fromUser) {
//                        rate_number.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot != null && snapshot.getValue() != null) {
//                                    float ratenumber = Float.parseFloat(snapshot.getValue().toString());
//                                    if (fromUser)
//                                        rate_number.setValue(String.valueOf(ratenumber + 1));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    }
//                });
//                rankDialog.show();
//            }
//        });
        category.setText(myOrder.category);
        description.setText(myOrder.description);
        status.setText((myOrder.status));
        orderId.setText(myOrder.orderId + "");
        min_range.setText(myOrder.min_range + "");
        max_range.setText(myOrder.max_range + "");
        fetchUserDetails();
        userLocationName.setText(myOrder.userLocation.Name);
        userLocationLocation.setText(myOrder.userLocation.Location);
        deliveryCharge.setText((myOrder.deliveryCharge + ""));
        otp.setText(myOrder.otp);

        if (myOrder.final_price == -1) {
            final_item_price.setText("- - - - -");
            final_total.setText("- - - - -");
        } else {
            final_item_price.setText(myOrder.final_price + "");
            final_total.setText((myOrder.deliveryCharge + myOrder.final_price) + "");
        }

        String date;
        if (myOrder.expiryDate.day == 0) {
            date = "-";
        } else {
            date = myOrder.expiryDate.day + "/" + myOrder.expiryDate.month + "/" + myOrder.expiryDate.year;
        }
        expiryTime_Date.setText(date);


        String time;
        if (myOrder.expiryTime.hour == -1) {
            time = "-";
        } else {
            if ((Integer.toString(myOrder.expiryTime.hour)).length() == 1) {
                time = "0" + myOrder.expiryTime.hour;
            } else {
                time = myOrder.expiryTime.hour + "";
            }
            time += ":";
            if ((Integer.toString(myOrder.expiryTime.minute)).length() == 1) {
                time += "0" + (myOrder.expiryTime.minute);
            } else {
                time += myOrder.expiryTime.minute + "";
            }

            if (myOrder.expiryTime.hour < 12) {
                time += " AM"; //NON-NLS
            } else {
                time += " PM";
            }
        }
        expiryTime_Time.setText(time);
        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {

            DatabaseReference ratingRef = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID);
            ratingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
//                    for (DataSnapshot child : dataSnapshot.child("rate").getChildren()) {
                        ratingSum = ratingSum + Integer.valueOf(dataSnapshot.child("rate").getValue().toString());
                        ratingsTotal++;
//                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum / ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                        ratingRef.child("rate").setValue(ratingsAvg).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UserOrderDetailActivity.this, "rate successfull", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    mRatingBar.setRating(ratingsAvg);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        });
    }



    void fetchDelivererdetail() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                delevirerdetails = dataSnapshot.getValue(UserDetails.class);
                int ratingSum = 0;
                float ratingsTotal = 0;
                float ratingsAvg = 0;
                for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                    ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                    ratingsTotal++;
                }
                if (ratingsTotal != 0) {
                    ratingsAvg = ratingSum / ratingsTotal;
                    mRatingBar.setRating(ratingsAvg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void fetchUserDetails() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(userID);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                userDetails = dataSnapshot.getValue(UserDetails.class);
                userName.setText(dataSnapshot.child("last").getValue(String.class) + "" + dataSnapshot.child("first").getValue(String.class));
                userPhoneNumber.setText(dataSnapshot.child("mobile").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void callDeliverer() {

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            //navigateUpTo(new Intent(this, ItemListActivity.class));
            //return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
            showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        int message;
        int color;
        if (isConnected) {
            message = R.string.coodConnectedTOinternet;
            color = Color.WHITE;
        } else {
            message = R.string.pasdinternet;
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckConnectivityMain.getInstance().setConnectivityListener(UserOrderDetailActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent mIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userPhoneNumber.getText().toString()));


                    startActivity(mIntent);
                } else {

                }
                return;
            }

        }
    }
}

