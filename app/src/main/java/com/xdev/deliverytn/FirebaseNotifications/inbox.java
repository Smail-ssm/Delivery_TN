package com.xdev.deliverytn.FirebaseNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.FirebaseNotifications.recyclerview.RecyclerViewotifAdapter;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.user.UserViewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.login.usertype.usertype;

public class inbox extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static RecyclerViewotifAdapter adapter;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference allnotif = root.child("deliveryApp").child("Notifications");
    public List<FBNotification> notiflist;
    MenuItem mPreviousMenuItem = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    NavigationView navigationView;
    boolean isRefreshing = false;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        navigationView = findViewById(R.id.nav_view_deliverer);
        recyclerView = findViewById(R.id.notif_list);

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
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    auth.signOut();
                    Intent loginIntent = new Intent(inbox.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(inbox.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        notiflist = new ArrayList<>();
        adapter = new RecyclerViewotifAdapter(notiflist, getApplication());
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setTitle(getTitle());
        allnotif.keepSynced(true);
        allnotif.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//
                for (DataSnapshot orderdata : dataSnapshot.getChildren()) {
                    FBNotification notif = orderdata.getValue(FBNotification.class);
                    notiflist.add(notif);
                    adapter.insert(0, notif);
                }


//                swipeRefreshLayout.setRefreshing(false);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//
//        setUpSwipeRefresh();

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


}