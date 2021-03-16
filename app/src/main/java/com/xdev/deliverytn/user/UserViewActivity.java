package com.xdev.deliverytn.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.FirebaseNotifications.inbox;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.models.ExpiryDate;
import com.xdev.deliverytn.models.ExpiryTime;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.order_form.OrderForm;
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.recyclerview.OrderViewHolder;
import com.xdev.deliverytn.recyclerview.RecyclerViewOrderAdapter;
import com.xdev.deliverytn.recyclerview.SwipeOrderUtil;
import com.xdev.deliverytn.recyclerview.UserOrderItemClickListener;
import com.xdev.deliverytn.recyclerview.UserOrderTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.xdev.deliverytn.R.string.yourordersexpired;
import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.models.usertype.usertype;


public class UserViewActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static RecyclerViewOrderAdapter adapter;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    public List<OrderData> orderList;
    MenuItem mPreviousMenuItem = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean isRefreshing = false;
    NavigationView navigationView;
    View mHeaderView;
    ImageView imageViewUserImage;

    TextView textViewUserName;
    TextView textViewEmail;
    Toolbar toolbar;
    boolean pending;
    boolean finished;
    boolean cancelled;
    boolean active;
    boolean expired;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference deliveryApp;
    private DatabaseReference forUserData;
    private String userId;
    private UserDetails userDetails = new UserDetails();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        checkConnection();

        setUpToolBarAndActionBar();
        setUpNavigationView();
        setUpDrawerLayout();
        setUpFloatingActionButton();
        setUpSwipeRefresh();
        setDefaultFlags();
        setUpRecyclerView();

    }

    void setDefaultFlags() {
        active = true;
        pending = true;
        finished = false;
        expired = false;
        cancelled = false;
    }

    public void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        auth.signOut();
        Intent loginIntent = new Intent(UserViewActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    void setUpNavigationView() {
        navigationView = findViewById(R.id.nav_view_user);
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
                if (id == R.id.sign_out_user) {
                    Toast.makeText(UserViewActivity.this, R.string.logoutsuccsess, Toast.LENGTH_LONG).show();
                    signOut();
                } else if (id == R.id.use_as_deliverer) {
                    startActivity(new Intent(UserViewActivity.this, DelivererViewActivity.class));
                    usertype(root, "deliverer");

                    finish();
                } else if (id == R.id.all_orders_user) {
                    setDefaultFlags();
                    toolbar.setTitle(R.string.myorders);
                    refreshOrders();
                } else if (id == R.id.active_user) {
                    active = true;
                    pending = false;
                    cancelled = false;
                    expired = false;
                    finished = false;
                    toolbar.setTitle(R.string.activeorders);
                    refreshOrders();
                } else if (id == R.id.info) {
                    Intent i = new Intent(UserViewActivity.this, inbox.class);
                    startActivity(i);
                } else if (id == R.id.pending_user) {
                    active = false;
                    pending = true;
                    cancelled = false;
                    expired = false;
                    finished = false;
                    toolbar.setTitle(R.string.pendingorders);
                    refreshOrders();
                } else if (id == R.id.cancelled_user) {
                    active = false;
                    pending = false;
                    cancelled = true;
                    expired = false;
                    finished = false;
                    toolbar.setTitle(R.string.cancledorders);
                    refreshOrders();
                } else if (id == R.id.expired_user) {
                    active = false;
                    pending = false;
                    cancelled = false;
                    expired = true;
                    finished = false;
                    toolbar.setTitle(R.string.expiredorders);
                    refreshOrders();
                } else if (id == R.id.completed_user) {
                    active = false;
                    pending = false;
                    cancelled = false;
                    expired = false;
                    finished = true;
                    toolbar.setTitle(R.string.finihsidorders);
                    refreshOrders();
                } else if (id == R.id.profile) {
                    Intent i = new Intent(UserViewActivity.this, Profile.class);
                    FirebaseAuth authx = FirebaseAuth.getInstance();
                    String user = authx.getCurrentUser().toString();

                    i.putExtra("UserDetail", user);
                    i.putExtra("type", "user");
                    startActivity(i);

                } else if (id == R.id.chat) {
                    Intent i = new Intent(UserViewActivity.this, chatRooms.class);

                    startActivity(i);

                } else if (id == R.id.info) {
                    Intent i = new Intent(UserViewActivity.this, inbox.class);

                    startActivity(i);

                }
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    void setUpDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(UserViewActivity.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                                imageViewUserImage = mHeaderView.findViewById(R.id.imageViewUserImage);
                                textViewEmail = mHeaderView.findViewById(R.id.headerUserEmail);
                                int wallet = userDetails.getWallet();
                                ImageView walletBalance = mHeaderView.findViewById(R.id.walletBalance);
                                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(Integer.toString(wallet), Color.WHITE, 100);
                                walletBalance.setImageDrawable(drawable);
                                textViewUserName.setText(userDetails.getLast() + " " + userDetails.getFirst());
                                textViewEmail.setText(userDetails.getEmail());
                                String photoUrl = userDetails.getProfile();
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

    void setUpFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    Intent intent = new Intent(UserViewActivity.this, OrderForm.class);
                    startActivity(intent);
                    //finish();
                }
            }
        });
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
                refreshOrders();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

            }


        });
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
        refreshOrders();
        DatabaseReference childOrders = root.child("deliveryApp").child("orders").child(userId = user.getUid());
        childOrders.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // OrderData order = dataSnapshot.getValue(OrderData.class);
                OrderData order = dataSnapshot.getValue(OrderData.class);
                OrderData temp = null;
                if (order.status == "CANCELLED")
                    return;
                for (OrderData myOrder : adapter.list) {
                    if (myOrder.orderId == order.orderId) {
                        //temp = myOrder;
                        //myOrder.status = order.status;
                        int pos = adapter.getPosition(myOrder);
                        adapter.remove(myOrder);
                        adapter.insert(pos, order);
                        break;
                    }
                }
                //  if(temp != null && orderList.contains(temp)) {
                //    adapter.remove(temp);
                //  adapter.insert(0, order);
                //}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        CheckConnectivityMain.getInstance().setConnectivityListener(UserViewActivity.this);
    }

    void setUpRecyclerView() {

        recyclerView = findViewById(R.id.item_list);
        orderList = new ArrayList<OrderData>();
        adapter = new RecyclerViewOrderAdapter(orderList, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // animation
        RecyclerView.ItemAnimator itemAnimator = new
                DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.addOnItemTouchListener(new UserOrderTouchListener(this, recyclerView, new UserOrderItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                OrderViewHolder viewHolder = (OrderViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && !viewHolder.isClickable)
                    return;
                OrderData clickedOrder = orderList.get(position);
                Intent intent = new Intent(UserViewActivity.this, UserOrderDetailActivity.class);
                intent.putExtra("MyOrder", clickedOrder);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        SwipeOrderUtil swipeHelper = new SwipeOrderUtil(0, ItemTouchHelper.LEFT, UserViewActivity.this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                RecyclerViewOrderAdapter adapter = (RecyclerViewOrderAdapter) recyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView tempRecyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                if (!orderList.get(position).status.equals("PENDING"))
                    return 0;
                else if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                    return 0;
                }

                RecyclerViewOrderAdapter adapter = (RecyclerViewOrderAdapter) recyclerView.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(tempRecyclerView, viewHolder);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //set swipe label
        swipeHelper.setLeftSwipeLable("Cancel");
        //set swipe background-Color
        swipeHelper.setLeftcolorCode(R.color.cardview_dark_background);
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

        userId = user.getUid();
        deliveryApp = root.child("deliveryApp").child("orders").child(userId);
        deliveryApp.keepSynced(true);
        isRefreshing = true;
        final int size = orderList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                adapter.remove(0);
                adapter.notifyItemRemoved(0);
            }
        }
        deliveryApp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ProgressBar progressBar = findViewById(R.id.progressBarUserOrder);
                progressBar.setVisibility(View.VISIBLE);
                isRefreshing = true;
                Calendar curr = Calendar.getInstance();
                Calendar exp = Calendar.getInstance();
                boolean somethingExpired = false;
                for (DataSnapshot orders : dataSnapshot.getChildren()) {
                    OrderData order = orders.getValue(OrderData.class);
                    if (order.status.equals("PENDING")) {
                        setExpiry(exp, order.expiryDate, order.expiryTime);
                        boolean isExpired = curr.after(exp);
                        if (isExpired) {
                            order.status = "EXPIRED";
                            somethingExpired = true;
                            deliveryApp.child(Integer.toString(order.orderId)).child("status").setValue("EXPIRED");
                        }
                    }
                    if ((order.status.equals("PENDING") && pending) ||
                            (order.status.equals("ACTIVE") && active) ||
                            (order.status.equals("FINISHED") && finished) ||
                            (order.status.equals("CANCELLED") && cancelled) ||
                            (order.status.equals("EXPIRED") && expired))
                        adapter.insert(0, order);
                    //   Toast.makeText(ItemListActivity.this,Integer.toString(order.max_range), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ItemListActivity.this,Integer.toString(adapter.getItemCount()), Toast.LENGTH_LONG).show();

                }
                if (somethingExpired)
                    Toast.makeText(UserViewActivity.this, yourordersexpired, Toast.LENGTH_LONG).show();
                isRefreshing = false;
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
}