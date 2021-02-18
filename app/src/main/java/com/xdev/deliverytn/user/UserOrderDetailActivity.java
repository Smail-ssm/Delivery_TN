package com.xdev.deliverytn.user;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.xdev.deliverytn.Chat.chatacti.ChatMain;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.login.user_details.UserDetails;
import com.xdev.deliverytn.order_form.EditOrderForm;
import com.xdev.deliverytn.user.order.OrderData;

import java.io.IOException;
import java.util.List;


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
        fetchDelivererdetail();
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

        deliverer_details = "Name: \t\t\t" + myOrder.acceptedBy.name + "\nMobile: \t\t\t" + myOrder.acceptedBy.mobile;
        if (!myOrder.acceptedBy.alt_mobile.equals("")) {
            deliverer_details += "\nAlt. Mobile: \t\t\t" + myOrder.acceptedBy.alt_mobile;

        }
        deliverer_details += "\nE-mail: \t\t\t" + myOrder.acceptedBy.email;
        acceptedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callDeliverer();

                new AlertDialog.Builder(UserOrderDetailActivity.this)
                        .setTitle("Deliverer Details")
                        .setMessage(deliverer_details)
                        .setPositiveButton(getString(R.string.dialog_ok), null)
                        .setNeutralButton("chat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserOrderDetailActivity.this, myOrder.acceptedBy.delivererID, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(UserOrderDetailActivity.this, ChatMain.class);
                                i.putExtra("Delivererid", myOrder.acceptedBy.delivererID);
                                startActivity(i);
                            }
                        }).setNegativeButton("call", new DialogInterface.OnClickListener() {
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
        userDetails.getRate();
        Button rankBtn = (Button) findViewById(R.id.ratebutton);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dialog rankDialog = new Dialog(UserOrderDetailActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.ratingbar);
                DatabaseReference ratingRef = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID);
//                ratingRef.addValueEventListener(new ValueEventListener() {
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
                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);

                DatabaseReference rate = ratingRef.child("rate");

                rate.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                            ratingBar.setRating(rating);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                DatabaseReference rate_number = ratingRef.child("rate_number");

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(
                            RatingBar ratingBar,
                            float rating,
                            boolean fromUser) {
                        rate_number.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot != null && snapshot.getValue() != null) {
                                    float ratenumber = Float.parseFloat(snapshot.getValue().toString());
                                    if (fromUser)
                                        rate_number.setValue(String.valueOf(ratenumber + 1));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                rankDialog.show();
            }
        });
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
                time += " AM";
            } else {
                time += " PM";
            }
        }
        expiryTime_Time.setText(time);

    }

    private void syncDelLocation() {
        //todo cal this method to update deliverer location on map
        Toast.makeText(this, "accepted by " + myOrder.acceptedBy, Toast.LENGTH_SHORT).show();
        deliveryApp = root.child("deliveryApp").child("orders").child(myOrder.acceptedBy.delivererID).child(myOrder.acceptedBy.currentLocation);
        deliveryApp.keepSynced(true);
        deliveryApp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AlertDialog alertDialog = new AlertDialog.Builder(UserOrderDetailActivity.this).create();
                alertDialog.setTitle("Hotel Info");
                alertDialog.setIcon(R.drawable.ic_location);
                LayoutInflater layoutInflater = LayoutInflater.from(UserOrderDetailActivity.this);
                View promptView = layoutInflater.inflate(R.layout.delmap, null);
                alertDialog.setView(promptView);

                MapView mMapView = promptView.findViewById(R.id.mapView);
                MapsInitializer.initialize(UserOrderDetailActivity.this);

                mMapView.onCreate(alertDialog.onSaveInstanceState());
                mMapView.onResume();


                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        LatLng p1 = getLocationFromAddress(UserOrderDetailActivity.this, myOrder.acceptedBy.currentLocation);
                        LatLng posisiabsen = new LatLng(p1.latitude, p1.longitude); ////your lat lng
                        googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(myOrder.acceptedBy.name + " current location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                    }
                });

//
//                final RatingBar rb = (RatingBar) promptView.findViewById(R.id.ratingBar);
//                rb.setRating(3);
//
//                final TextView hoteltitle= (TextView)promptView.findViewById(R.id.HotelInfoTitle);
//                hoteltitle.setText("Hotel " + hotelname);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UserOrderDetailActivity.this, "update", Toast.LENGTH_SHORT).show();

                    }

                });
                alertDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    //    public GeoPoint getLocationFromAddress(String strAddress){
//
//        Geocoder coder = new Geocoder(this);
//        List<Address> address;
//        GeoPoint p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress,5);
//            if (address==null) {
//                return null;
//            }
//            Address location=address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
//                    (double) (location.getLongitude() * 1E6));
//
//            return p1;
//        }
//    }
    void fetchDelivererdetail() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                delevirerdetails = dataSnapshot.getValue(UserDetails.class);
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
                userDetails = dataSnapshot.getValue(UserDetails.class);
                userName.setText(userDetails.getLast() + "" + userDetails.getFirst());
                userPhoneNumber.setText(userDetails.getMobile());
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
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
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

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

