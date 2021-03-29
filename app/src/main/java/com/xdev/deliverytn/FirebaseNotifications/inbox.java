package com.xdev.deliverytn.FirebaseNotifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.models.FBNotification;
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.user.UserViewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.models.usertype.usertype;

public class inbox extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference allnotif = root.child("deliveryApp").child("Notifications");
    public List<String> notiflist;
    public List<FBNotification> fbobjectList;
    MenuItem mPreviousMenuItem = null;
    NavigationView navigationView;
    Toolbar toolbar;
    ListView notiflv;
    AlertDialog.Builder builder;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView dynamic;
    private DrawerLayout mDrawerLayout;
    private DatabaseReference notifref, totalnotif;
    private Integer notifcount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        navigationView = findViewById(R.id.nav_view_deliverer);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        refresh();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(inbox.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        notiflist = new ArrayList<>();
        fbobjectList = new ArrayList<>();
        dynamic = findViewById(R.id.notiflv);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setTitle(R.string.getinformed);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notiflist.clear();
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }


        });

        dynamic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog alertDialog = new AlertDialog.Builder(inbox.this).create();
                alertDialog.setMessage(fbobjectList.get(position).getMessage());
                alertDialog.setTitle(fbobjectList.get(position).getTitle());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });


    }

    private void refresh() {
        allnotif.keepSynced(true);
        allnotif.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FBNotification value = dataSnapshot.getValue(FBNotification.class);
                notiflist.add(value.getTitle());
                fbobjectList.add(value);
                arrayAdapter = new ArrayAdapter<String>(inbox.this, android.R.layout.simple_list_item_1, notiflist);
                dynamic.setAdapter(arrayAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


}