package com.xdev.deliverytn.deliverer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.OrderObject;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.models.chatrrom;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;


public class DelivererOrderDetailActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    public OrderData myOrder;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    double latitude, longitude;
    DatabaseReference refrooms;
    DatabaseReference refroomsA;
    OrderObject myorder;
    Location location;
    String myUserId = "";
    int myOrderId = 0;
    private DatabaseReference WALET;
    private LinearLayout userName_h;
    private TextView userName;
    private TextView userPhoneNumber;
    private TextView status;
    private String userId;
    private Button btn_accept;
    private Button btn_complete_order;
    private DatabaseReference root;
    private DatabaseReference ref1;
    private DatabaseReference ref2;
    private DatabaseReference wallet_ref;
    private DatabaseReference deliverer;
    private DatabaseReference allorders;
    private UserDetails deliverer_data;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Address address;
    private UserDetails userDetails = new UserDetails();
    private OrderData order;
    private Integer balance;

    public static Double getDistanceBetween(LatLng latLon1, LatLng latLon2) {
        if (latLon1 == null || latLon2 == null)
            return null;
        float[] result = new float[1];
        Location.distanceBetween(latLon1.latitude, latLon1.longitude,
                latLon2.latitude, latLon2.longitude, result);
        return (double) result[0];
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverer_order_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        checkConnection();
        getLatAndLong();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(DelivererOrderDetailActivity.this, "Latitude = " + latitude + "\nLongitude = " + longitude, Toast.LENGTH_SHORT).show();
                    getAddressFromLatAndLong(latitude, longitude);
                    // Logic to handle location object
                } else {
                    //Toast.makeText(DelivererOrderDetailActivity.this, "location has NULL value", Toast.LENGTH_SHORT).show();
                }
            }
        };
        root = FirebaseDatabase.getInstance().getReference();
        TextView category = findViewById(R.id.category);
        TextView description = findViewById(R.id.description);
        TextView orderId = findViewById(R.id.orderId);
        TextView min_range = findViewById(R.id.price_range_min);
        TextView max_range = findViewById(R.id.price_range_max);
        userName_h = findViewById(R.id.userName_h);
        userName = findViewById(R.id.userName);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        TextView userLocationName = findViewById(R.id.userLocationName);
        TextView userLocationLocation = findViewById(R.id.userLocationLocation);
        TextView expiryTime_Date = findViewById(R.id.expiryTime_Date);
        TextView expiryTime_Time = findViewById(R.id.expiryTime_Time);
        TextView final_item_price = findViewById(R.id.final_item_price);
        TextView deliveryCharge = findViewById(R.id.deliveryCharge);
        TextView final_total = findViewById(R.id.final_total);
        status = findViewById(R.id.status);
        btn_accept = findViewById(R.id.btn_accept);
        Button btn_show_path = findViewById(R.id.btn_show_path);
        btn_complete_order = findViewById(R.id.btn_complete_order);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        myOrder = intent.getParcelableExtra("MyOrder"); //NON-NLS
        myOrderId = Integer.parseInt(intent.getStringExtra("orderid")); //NON-NLS
        myUserId = intent.getStringExtra("userid"); //NON-NLS
//        fetchorder(myOrderId, myUserId);

        CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(myOrder.category);
        }
        if (myOrder.status.equals("FINISHED")) { //NON-NLS
            btn_accept.setEnabled(false);
            btn_accept.setVisibility(View.GONE);
            btn_show_path.setEnabled(false);
            btn_show_path.setVisibility(View.GONE);
            btn_complete_order.setEnabled(false);
            btn_complete_order.setVisibility(View.GONE);
        } else if (myOrder.status.equals("ACTIVE")) { //NON-NLS
            btn_accept.setText("Reject");
        } else {
            btn_complete_order.setEnabled(false);
            btn_complete_order.setVisibility(View.GONE);
        }

        if (myOrder.status.equals("PENDING")) { //NON-NLS
            userName_h.setVisibility(View.GONE);
        }
        category.setText(myOrder.category);
        description.setText(myOrder.description);
        status.setText(myOrder.status);
        orderId.setText(myOrder.orderId + "");
        min_range.setText(myOrder.min_range + "");
        max_range.setText(myOrder.max_range + "");
        fetchUserDetails();
        userLocationName.setText(myOrder.userLocation.Name);
        userLocationLocation.setText(myOrder.userLocation.Location);
        deliveryCharge.setText((myOrder.deliveryCharge + ""));
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
        final AlertDialog alertDialog = new AlertDialog.Builder(DelivererOrderDetailActivity.this)
                .setCancelable(false)
                .setTitle(R.string.areyousure)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button yesButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button noButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


//                        Toast.makeText(DelivererOrderDetailActivity.this, "LOcation latlng=" +
//                                        myorder.userLocation.Lat +
//                                        myorder.userLocation.Lon,
//                                Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        userId = user.getUid();
                        ref1 = root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)); //NON-NLS //NON-NLS
                        ref2 = ref1.child("acceptedBy"); //NON-NLS

                        ref2.keepSynced(true);
                        wallet_ref = root.child("deliveryApp").child("users").child(myOrder.userId).child("wallet"); //NON-NLS //NON-NLS
                        wallet_ref.keepSynced(true);
                        if (myOrder.status.equals("PENDING")) {
                            deliverer = root.child("deliveryApp").child("users").child(userId);
                            deliverer.keepSynced(true);
                            deliverer.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    deliverer_data = dataSnapshot.getValue(UserDetails.class); //NON-NLS
                                    ref2.child("name").setValue(deliverer_data.getLast() + " " + deliverer_data.getFirst());
                                    ref2.child("email").setValue(deliverer_data.getEmail());
                                    ref2.child("mobile").setValue(deliverer_data.getMobile()); //NON-NLS
                                    ref2.child("cin").setValue(deliverer_data.getCin());
                                    ref2.child("delivererID").setValue(userId);
//                                    LatLng cl = new LatLng(
//                                            myorder.userLocation.Lat,
//                                            myorder.userLocation.Lon);
//                                    LatLng delivererLatlng = new LatLng(
//                                            location.getLatitude(),
//                                            location.getLongitude());
//                                    myOrder.final_price = Math.round(getDistanceBetween(delivererLatlng, cl).intValue());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }); //NON-NLS
                            allorders = root.child("deliveryApp").child("orders")
                                    .child(myOrder.userId)
                                    .child(String.valueOf(myOrder.orderId));
                            allorders.keepSynced(true);
                            allorders.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    order = snapshot.getValue(OrderData.class);
                                    if (order.orderId == myOrderId) {
                                        myorder = new OrderObject();
                                        myorder.acceptedBy = order.acceptedBy;
                                        myorder.userLocation = order.userLocation;
                                        myorder.expiryDate = order.expiryDate;
                                        myorder.expiryTime = order.expiryTime;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            }); //NON-NLS
                            ref1.child("status").setValue("ACTIVE"); //NON-NLS
                            btn_accept.setText("Reject");
                            myOrder.status = "ACTIVE";
                            status.setText((myOrder.status));
                            refrooms = root.child("deliveryApp").child("chatRooms").child("roomId"); //NON-NLS
                            WALET = root.child("deliveryApp").child("SocEarnings"); //NON-NLS
                            refroomsA = refrooms.child(String.valueOf(myOrder.orderId));
                            chatrrom c = new chatrrom();
                            c.setRoomId(String.valueOf(myOrder.orderId));
                            c.setDeliverId(user.getUid());
                            c.setOrdererId(myOrder.userId);
                            refroomsA.setValue(c);
//                            refrooms.child("ordererId").setValue(myOrder.orderId);
//                            refrooms.child("delivereId").setValue(user.getUid());
                            Toast.makeText(DelivererOrderDetailActivity.this, "Room of order N°" + myOrder.orderId + " created ", Toast.LENGTH_SHORT).show(); //NON-NLS
                            getLatAndLong();
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(DelivererOrderDetailActivity.this);
                            mLocationCallback = new LocationCallback() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location location = locationResult.getLastLocation();
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                        //Toast.makeText(DelivererOrderDetailActivity.this, "Latitude = " + latitude + "\nLongitude = " + longitude, Toast.LENGTH_SHORT).show();
                                        getAddressFromLatAndLong(latitude, longitude);
//
                                        LatLng l1 = new LatLng(location.getLatitude(), location.getLongitude());
                                        LatLng l2 = new LatLng(myorder.userLocation.Lat, myorder.userLocation.Lon);
                                        Double x = getDistanceBetween(l1, l2);
                                        Toast.makeText(DelivererOrderDetailActivity.this, String.valueOf(x), Toast.LENGTH_SHORT).show(); //NON-NLS
                                        root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)).child("deliveryCharge:").setValue((int) Math.round(x * 0.001)); //NON-NLS
                                        updateOrderPrice(Integer.sum(myOrder.max_range, myOrder.deliveryCharge));
                                        myOrder.earnings = ((((int) Math.round(x * 0.001)) * 70) / 100);
                                        updateWalet((((int) Math.round(x * 0.001)) * 30) / 100);
                                        // Logic to handle location object
                                    } else {
                                        Toast.makeText(DelivererOrderDetailActivity.this, R.string.locationhasenullvalue, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            };
                            // Deducts max_int+deliveryCharge money from orderer's wallet when order accepted
                            wallet_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Integer wal_bal = dataSnapshot.getValue(Integer.class);
                                    balance = wal_bal;
                                    wallet_ref.setValue(balance - (myOrder.max_range + myOrder.deliveryCharge));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            setUpAcceptNotif(myOrder);

                            btn_complete_order.setEnabled(true);
                            btn_complete_order.setVisibility(View.VISIBLE);
                            userName_h.setVisibility(View.VISIBLE);


                        } else if (myOrder.status.equals("ACTIVE")) { //NON-NLS //NON-NLS

                            ref1.child("status").setValue("PENDING");

                            btn_accept.setText("Accept");
                            myOrder.status = "PENDING";
                            status.setText((myOrder.status));

                            ref2.child("name").setValue("-"); //NON-NLS //NON-NLS
                            ref2.child("email").setValue("-"); //NON-NLS
                            ref2.child("mobile").setValue("-");
                            ref2.child("alt_mobile").setValue("-");
                            ref2.child("delivererID").setValue("-");

                            ref1.child("otp").setValue("");
                            ref1.child("final_price").setValue(-1);
                            // Refunds max_int+deliveryCharge money from orderer's wallet when order accepted
//                            wallet_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    Integer wal_bal = dataSnapshot.getValue(Integer.class);
//                                    balance = wal_bal;
//                                    wallet_ref.setValue(balance + (myOrder.max_range + myOrder.deliveryCharge));
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
                            setUpRejectNotif(myOrder);
                            btn_complete_order.setEnabled(false);
                            btn_complete_order.setVisibility(View.GONE);
                            userName_h.setVisibility(View.GONE);
                        }
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }

        });

        btn_show_path.setOnClickListener(new View.OnClickListener() {
            LatLng latng;

            @Override
            public void onClick(View v) {
// use this to start and trigger a service

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&daddr=" + myOrder.userLocation.Location)); //NON-NLS //NON-NLS
                intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")); //NON-NLS
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
////TODO
////                    continue distance measuring
//
//
                }

            }
        });

// todo time between 2 orders must be au min 30 min
// todo accept more if distnce is shorter then the first command

        btn_accept.setOnClickListener(v -> {
            alertDialog.show();


        });

        btn_complete_order.setOnClickListener(v -> {
//todo inapp messaging
            if (!ConnectivityReceiver.isConnected()) {
                showSnack(false);
            } else {
                Intent intent1 = new Intent(DelivererOrderDetailActivity.this, CompleteOrder.class);
                intent1.putExtra("MyOrder", myOrder);
                startActivity(intent1);
                finish();
            }
        });

    }

    private void updateWalet(int i) {
//        WALET.setValue(i);
        wallet_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer wal_bal = dataSnapshot.getValue(Integer.class);
                balance = wal_bal;
                WALET.setValue(Integer.sum(balance, i));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getAddressFromLatAndLong(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address = geocoder.getFromLocation(lat, lng, 1).get(0);
            String add = "";
            /*
            add = add + address.getAddressLine(0);
            add = add + "\n" + address.getCountryName();
            add = add + "\n" + address.getCountryCode();
            add = add + "\n" + address.getAdminArea();
            add = add + "\n" + address.getPostalCode();
            add = add + "\n" + address.getSubAdminArea();
            */
            add = add + address.getLocality() + "," + address.getSubLocality(); //City

            //add = add + "\n" + address.getSubThoroughfare();
            Toast.makeText(DelivererOrderDetailActivity.this, add, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block //NON-NLS
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show(); //NON-NLS
        }
    } //NON-NLS

    private void updateOrderPrice(double v) {
        root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)).child("final_price").setValue(v);

    }

    void getLatAndLong() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            //Toast.makeText(DelivererOrderDetailActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
            setGpsOn();
        }
    }

    private LocationRequest getLocationRequest() {
        @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(30 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    void setGpsOn() {
        LocationRequest mLocationRequest = getLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ActivityCompat.checkSelfPermission(DelivererOrderDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DelivererOrderDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.requestLocationUpdates
                        (getLocationRequest(), mLocationCallback,
                                null /* Looper */);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(DelivererOrderDetailActivity.this,
                                REQUEST_CHECK_SETTINGS); //NON-NLS
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    void fetchUserDetails() {
        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(myOrder.userId);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(UserDetails.class);
                userName.setText(userDetails.getLast() + "" + userDetails.getFirst());
                userPhoneNumber.setText(userDetails.getMobile());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //NON-NLS

            }
        });
    }

    public void setUpAcceptNotif(final OrderData order) {
        String userId = order.userId;
        root.child("deliveryApp").child("users").child(userId).child("playerId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //NON-NLS
                String player_id = dataSnapshot.getValue(String.class);
                //TOAST
                try {
                    JSONObject notificationContent = new JSONObject(
                            "{'contents': " +
                                    "{'en': '"
                                    + order.description +
                                    "'},"
                                    +
                                    "'include_player_ids': ['"
                                    + player_id +
                                    "'], " +
                                    "'headings': {'en': 'Your Order Accepted! Order Id : "
                                    + order.orderId +
                                    "'} " +
                                    "}"); //NON-NLS //NON-NLS
                    JSONObject order = new JSONObject();
                    order.put("userId", myOrder.userId); //NON-NLS
                    order.put("orderId", myOrder.orderId);
                    order.put("price", myOrder.final_price);
                    notificationContent.putOpt("data", order);
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setUpRejectNotif(final OrderData order) { //NON-NLS
        String userId = order.userId;
        root.child("deliveryApp").child("users").child(userId).child("playerId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String player_id = dataSnapshot.getValue(String.class);
                //TOAST //NON-NLS
                try {
                    JSONObject notificationContent = new JSONObject("{'contents': {'en': '" + order.description + "'}," +
                            "'include_player_ids': ['" + player_id + "'], " +
                            "'headings': {'en': 'Your Order Rejected Order Id : " + order.orderId + "'} " +
                            "}");
                    JSONObject order = new JSONObject();
                    order.put("userId", myOrder.userId);
                    order.put("orderId", myOrder.orderId);
                    notificationContent.putOpt("data", order);
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        CheckConnectivityMain.getInstance().setConnectivityListener(DelivererOrderDetailActivity.this);
    }


}