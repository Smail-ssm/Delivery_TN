package com.xdev.deliverytn.deliverer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.Profile;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.inbox;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.login.user_details.UserDetails;
import com.xdev.deliverytn.recyclerview.OrderViewHolder;
import com.xdev.deliverytn.recyclerview.RecyclerViewOrderAdapter;
import com.xdev.deliverytn.recyclerview.UserOrderItemClickListener;
import com.xdev.deliverytn.recyclerview.UserOrderTouchListener;
import com.xdev.deliverytn.user.UserViewActivity;
import com.xdev.deliverytn.user.order.ExpiryDate;
import com.xdev.deliverytn.user.order.ExpiryTime;
import com.xdev.deliverytn.user.order.OrderData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.login.usertype.usertype;


public class DelivererViewActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    public static RecyclerViewOrderAdapter adapter;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    public List<OrderData> orderList;
    MenuItem mPreviousMenuItem = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean isRefreshing = false;
    NavigationView navigationView;
    View mHeaderView;
    Toolbar toolbar;
    TextView textViewUserName;
    TextView textViewEmail;
    boolean pending;
    boolean active;
    boolean finished;
    boolean havelocation;
    boolean resumed;
    double latitude, longitude;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference forUserData;
    private DatabaseReference childOrders;
    private String userId;
    private UserDetails userDetails = new UserDetails();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverer_view);
        checkConnection();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLatAndLong();
        setUpToolBarAndActionBar();
        setUpNavigationView();
        setUpDrawerLayout();
        setDefaultFlags();
        setUpSwipeRefresh();
        setUpRecyclerView();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(DelivererViewActivity.this, "Latitude = " + latitude + "\nLongitude = " + longitude, Toast.LENGTH_SHORT).show();
                    getAddressFromLatAndLong(latitude, longitude);
                    // Logic to handle location object
                } else {
                    //Toast.makeText(DelivererViewActivity.this, "location has NULL value", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    void getAddressFromLatAndLong(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address = geocoder.getFromLocation(lat, lng, 1).get(0);
            havelocation = true;
            if (!resumed) {
                resumed = true;
                refreshOrders();
            }
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
address.toString();
            //add = add + "\n" + address.getSubThoroughfare();
            Toast.makeText(DelivererViewActivity.this, add, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }   Address getfromLatAndLong(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        return        address = geocoder.getFromLocation(lat, lng, 1).get(0);


    }

    void getLatAndLong() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            //Toast.makeText(DelivererViewActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
            setGpsOn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGpsOn();
                } else {
                }
                return;
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
                if (ActivityCompat.checkSelfPermission(DelivererViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DelivererViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        
                    return;
                }
                mFusedLocationClient.requestLocationUpdates
                        (getLocationRequest(), mLocationCallback,
                                null /* Looper */); }
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
                        resolvable.startResolutionForResult(DelivererViewActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
        } else {
            Toast.makeText(DelivererViewActivity.this, "GPS permission Denied", Toast.LENGTH_LONG).show();
        }
    }

    void setDefaultFlags() {
        finished = false;
        active = true;
        pending = true;
    }

    void setUpNavigationView() {
        navigationView = findViewById(R.id.nav_view_deliverer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setCheckable(true);
                menuItem.setChecked(true);
                if (mPreviousMenuItem != null) {
                    mPreviousMenuItem.setChecked(false);
                }
                mPreviousMenuItem = menuItem;// close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if (id == R.id.sign_out_deliverer) {
                    Toast.makeText(DelivererViewActivity.this, "You have been successfully logged out.", Toast.LENGTH_LONG).show();
                    signOut();
                } else if (id == R.id.all_orders_deliverer) {
                    setDefaultFlags();
                    toolbar.setTitle("All Orders");
                    refreshOrders();
                } else if (id == R.id.completed_deliverer) {
                    active = false;
                    pending = false;
                    finished = true;
                    toolbar.setTitle("Finished");
                    refreshOrders();

                } else if (id == R.id.info) {
                    Intent i=new Intent(DelivererViewActivity.this, inbox.class);
                    startActivity(i);
                }else if (id == R.id.active_deliverer) {
                    active = true;
                    pending = false;
                    finished = false;
                    toolbar.setTitle("Active");
                    refreshOrders();
                } else if (id == R.id.pending_deliverer) {
                    active = false;
                    pending = true;
                    finished = false;
                    toolbar.setTitle("Pending");
                    refreshOrders();
                } else if (id == R.id.use_as_user) {
                    startActivity(new Intent(DelivererViewActivity.this, UserViewActivity.class));
                    usertype(root,"orderer");

                    finish();
                } else if (id == R.id.profile) {
                    Intent i = new Intent(DelivererViewActivity.this, Profile.class);
                    FirebaseAuth authx = FirebaseAuth.getInstance();
                    String user = authx.getCurrentUser().toString();

                    i.putExtra("UserDetail", user);
                    i.putExtra("type", "deliv");
                    startActivity(i);

                } else if (id == R.id.chatroom) {
                    Intent i = new Intent(DelivererViewActivity.this, chatRooms.class);

                    startActivity(i);

                }
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    public void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        auth.signOut();
        Intent loginIntent = new Intent(DelivererViewActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();    }


    void setUpDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout_deliverer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(DelivererViewActivity.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                        userId = user.getUid();
                        ImageView imageViewUserImage=findViewById(R.id.imageViewUserImage);

                        forUserData = root.child("deliveryApp").child("users").child(userId);
                        forUserData.keepSynced(true);
                        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userDetails = dataSnapshot.getValue(UserDetails.class);
                                mHeaderView = navigationView.getHeaderView(0);
                                textViewUserName = mHeaderView.findViewById(R.id.headerUserName);
                                textViewEmail = mHeaderView.findViewById(R.id.headerUserEmail);
                                int wallet = 1000;
//                                userDetails.wallet;
                                ImageView walletBalance = mHeaderView.findViewById(R.id.walletBalance);
                                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(Integer.toString(wallet), Color.WHITE, 100);
                                walletBalance.setImageDrawable(drawable);
                                textViewUserName.setText(userDetails.getLast()+""+userDetails.getFirst());
                                textViewEmail.setText(userDetails.getEmail());
                                String photoUrl = userDetails.getCinPhoto();
                                try {
                                    Picasso.get()
                                            .load(photoUrl)
                                            .into(imageViewUserImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if (havelocation) {
                            mHeaderView = navigationView.getHeaderView(0);
                            ImageView currentLocation = mHeaderView.findViewById(R.id.currentLocation);
                            TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(address.getLocality(), Color.WHITE, 100);
                            currentLocation.setImageDrawable(drawable);
                        }
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

    }

    void setUpToolBarAndActionBar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setTitle(getTitle());

    }

    void setUpSwipeRefresh() {
        //Swipe Refresh Layout
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if (havelocation) {
                    refreshOrders();
                } else {
                    getLatAndLong();
                }

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (havelocation && resumed) { //this if needs to be thought upon
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
        }
        Log.d("REFRESH", havelocation + " " + !resumed);
        if (havelocation && !resumed) {
            resumed = true;
            refreshOrders();
        }
        CheckConnectivityMain.getInstance().setConnectivityListener(DelivererViewActivity.this);
    }

    void setUpRecyclerView() {

        recyclerView = findViewById(R.id.item_list);
        orderList =
                new ArrayList<OrderData>();
        adapter = new RecyclerViewOrderAdapter(orderList, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator itemAnimator = new
                DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
//TODO fix show path using direction API / show more about user info and deliverer info /make contract to ensure well working processee
        recyclerView.addOnItemTouchListener(new UserOrderTouchListener(this, recyclerView, new UserOrderItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                OrderViewHolder viewHolder = (OrderViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && !viewHolder.isClickable)
                    return;
                OrderData clickedOrder = orderList.get(position);
                Intent intent = new Intent(DelivererViewActivity.this, DelivererOrderDetailActivity.class);
                intent.putExtra("MyOrder", clickedOrder);
                startActivity(intent);
                resumed = false;

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    void setExpiry(Calendar calendar, ExpiryDate expiryDate, ExpiryTime expiryTime) {

        calendar.set(Calendar.YEAR, expiryDate.year);
        calendar.set(Calendar.MONTH, expiryDate.month);
        calendar.set(Calendar.DAY_OF_MONTH, expiryDate.day);
        calendar.set(Calendar.HOUR_OF_DAY, expiryTime.hour);
        calendar.set(Calendar.MINUTE, expiryTime.minute);

    }

    void refreshOrders() {
        //TODO Add internet connectivity error
        final ProgressBar progressBar = findViewById(R.id.progressBarUserOrder);
        progressBar.setVisibility(View.VISIBLE);
        isRefreshing = true;
        userId = user.getUid();

        final int size = orderList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                adapter.remove(0);
            }
        }

        final DatabaseReference allorders = root.child("deliveryApp").child("orders");
        allorders.keepSynced(true);
        allorders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isRefreshing = true;
                Calendar curr = Calendar.getInstance();
                Calendar exp = Calendar.getInstance();
                boolean somethingExpired = false;
                for (DataSnapshot userdata : dataSnapshot.getChildren()) {
                    if (userdata.getKey().equals(userId)) {
                        continue;
                    }

                    for (DataSnapshot orderdata : userdata.getChildren()) {
                        OrderData order = orderdata.getValue(OrderData.class);
                        if (order.status.equals("PENDING")) {
                            setExpiry(exp, order.expiryDate, order.expiryTime);
                            boolean isExpired = curr.after(exp);
                            if (isExpired) {
                                order.status = "EXPIRED";
                                somethingExpired = true;
                                allorders.child(userdata.getKey()).child(Integer.toString(order.orderId)).child("status").setValue("EXPIRED");
                                continue;
                            }
                        } else if (order.status.equals("CANCELLED"))
                            continue;
                        Address order_adds = null;
                        try {
                            order_adds = getfromLatAndLong(order.userLocation.Lat,order.userLocation.Lon);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (order_adds == null) continue;
//                        String[] tokens = order_address.split(",");
//                        int size = order_address..length;
                        Log.e("ADDRESS", order.userId + " " + order_adds.getLocality() + " " + order.orderId);
                        String city;
//                        city = tokens[size - 2];
                        Toast.makeText(DelivererViewActivity.this, order_adds.toString(), Toast.LENGTH_SHORT).show();//                                 order_address;
//                        city = city.substring(1);
                        if ((order.userLocation.toString().contains(order_adds.getAddressLine(0)))
//                                if ((order_adds.toString().contains(order.userLocation.toString()))
                                        &&
                                ((order.status.equals("PENDING") && pending) ||
                                        (order.status.equals("ACTIVE") &&
                                                active && userId.equals(order.acceptedBy.delivererID)) ||
                                        (order.status.equals("FINISHED") &&
                                                finished && userId.equals(order.acceptedBy.delivererID))))
                            adapter.insert(0, order);
                    }
                }
                if (somethingExpired)
                    Toast.makeText(DelivererViewActivity.this, "Some orders expired", Toast.LENGTH_LONG).show();
                isRefreshing = false;
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.drawer_layout_deliverer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}