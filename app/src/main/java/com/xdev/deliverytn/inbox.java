package com.xdev.deliverytn;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.login.user_details.UserDetails;
import com.xdev.deliverytn.recyclerview.RecyclerViewOrderAdapter;
import com.xdev.deliverytn.user.UserViewActivity;
import com.xdev.deliverytn.user.order.OrderData;

import java.util.List;

import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.login.usertype.usertype;

public class inbox extends AppCompatActivity {
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
    ImageView imageViewUserImage;
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
        setContentView(R.layout.activity_inbox);
        setUpNavigationView();
        setUpDrawerLayout();
        setUpToolBarAndActionBar();
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

                if (id == R.id.use_as_deliverer) {
                    startActivity(new Intent(inbox.this, DelivererViewActivity.class));
                    usertype(root, "deliverer");


                } else if (id == R.id.use_as_user) {
                    startActivity(new Intent(inbox.this, UserViewActivity.class));
                    usertype(root, "orderer");

                    finish();
                } else if (id == R.id.profile) {
                    Intent i = new Intent(inbox.this, Profile.class);
                    FirebaseAuth authx = FirebaseAuth.getInstance();
                    String user = authx.getCurrentUser().toString();

                    i.putExtra("UserDetail", user);
                    startActivity(i);

                } else if (id == R.id.chatroom) {
                    Intent i = new Intent(inbox.this, chatRooms.class);

                    startActivity(i);

                } else if (id == R.id.info) {
                    mDrawerLayout.closeDrawers();

                } else if (id == R.id.sign_out_deliverer) {
                    signOut();

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
        Intent loginIntent = new Intent(inbox.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    void setUpDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(inbox.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                        userId = user.getUid();

                        forUserData = root.child("deliveryApp").child("users").child(userId);
                        forUserData.keepSynced(true);
                        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userDetails = dataSnapshot.getValue(UserDetails.class);
                                mHeaderView = navigationView.getHeaderView(0);
                                textViewUserName = mHeaderView.findViewById(R.id.headerUserName);
                                textViewEmail = mHeaderView.findViewById(R.id.headerUserEmail);
                                imageViewUserImage = mHeaderView.findViewById(R.id.imageViewUserImage);
                                int wallet = userDetails.getWallet();
                                ImageView walletBalance = mHeaderView.findViewById(R.id.walletBalance);
                                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(Integer.toString(wallet), Color.WHITE, 100);
                                walletBalance.setImageDrawable(drawable);
                                textViewUserName.setText(userDetails.getFirst() + " " + userDetails.getLast());
                                textViewEmail.setText(userDetails.getEmail());
//                                String photoUrl = userDetails.getCinPhoto();
                                try {
                                    Picasso.get()
                                            .load(userDetails.getProfile())
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
                            TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(address.toString(), Color.WHITE, 100);
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
}