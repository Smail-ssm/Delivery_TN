package com.xdev.deliverytn;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.xdev.deliverytn.Chat.chatroom.chatRooms;
import com.xdev.deliverytn.login.LoginActivity;
import com.xdev.deliverytn.models.FBNotification;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.models.payment;
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.user.UserViewActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.xdev.deliverytn.R.string.deletedoldpic;
import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.models.usertype.usertype;

public class payments extends AppCompatActivity {
    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static Context mContext;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference allpaiments = root.child("deliveryApp").child("users");
    private final String lang = "";
    public List<String> notiflist;
    public List<FBNotification> fbobjectList;
    public Uri photoURI;
    public List<OrderData> orderList;
    MenuItem mPreviousMenuItem = null;
    NavigationView navigationView;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    ListView dynamic;
    File imageFilePath;
    StorageReference storageReference;
    Snackbar snackbar;
    String userId;
    boolean active;
    boolean finished;
    boolean havelocation;
    FloatingActionButton fab;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean isRefreshing = false;
    View mHeaderView;
    TextView textViewUserName;
    TextView textViewEmail;
    boolean pending;
    ArrayList orderedList = new ArrayList();
    double latitude, longitude;
    ImageView imageViewUserImage;
    FirebaseStorage storage;
    ArrayList<String> paymentsid = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView listView;
    UserDetails p;
    private AppBarLayout app_bar;
    private FrameLayout frameLayout;
    private ConstraintLayout linearLayout6;
    private TextView textView11;
    private ImageView factureimage;
    private TextView textView7;
    private ConstraintLayout mandatPayment;
    private ImageButton imageButton;
    private LinearLayout history;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private TextView numcartedinar;
    private EditText transfertnumber;
    private EditText sendername;
    private EditText sendersin;
    private Button confirmpayment;
    private TextView totalernings;
    private TextView textView9;
    private TextView textView10;
    private DatabaseReference forUserData;
    private DatabaseReference childOrders;
    private UserDetails userDetails = new UserDetails();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Address address;
    private TextView totalToPay;
    private LinearLayout linearLayout8;
    private CardView mandat;
    private TextView textView13;
    private CardView edinar;
    private TextView textView15;
    private CardView banctransfert;
    private NavigationView nav_view_deliverer;
    private DatabaseReference notifref, totalnotif;
    private Integer notifcount;
    private DatabaseReference curentuserspaimnets;
    private int paymentnumber, paymentid;
    private payment payment;
    private EditText paymentvalue;
    private String paymentmethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //////////////////////
        app_bar = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frameLayout);
        linearLayout6 = findViewById(R.id.linearLayout6);
        factureimage = findViewById(R.id.factureimg);
        mandatPayment = findViewById(R.id.mandatPayment);
        paymentvalue = findViewById(R.id.paymentvalue);
        imageButton = findViewById(R.id.imageButton);
        numcartedinar = findViewById(R.id.numcartedinar);
        transfertnumber = findViewById(R.id.transfertnumber);
        sendername = findViewById(R.id.sendername);
        sendersin = findViewById(R.id.sendersin);
        totalernings = findViewById(R.id.totalernings);

//        root.child("deliveryApp").child("users").child(userId).child("first");
        DatabaseReference userinfo = root.child("deliveryApp").child("users").child(FirebaseAuth.getInstance().getUid());
        userinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p = dataSnapshot.getValue(UserDetails.class);
                if (dataSnapshot.child("topay") != null) {
                    if ((dataSnapshot.child("topay").getValue(Integer.class)) != 0) {

                        totalToPay.setText((String.valueOf(dataSnapshot.child("topay").getValue(Integer.class))));
                        totalernings.setText((String.valueOf(dataSnapshot.child("wallet").getValue(Integer.class))));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        Long time = System.currentTimeMillis();
        calendar.setTimeInMillis(time);

        //dd=day, MM=month, yyyy=year, hh=hour, mm=minute, ss=second.
        String date = DateFormat.format("dd-MM-yyyy-hh-mm-ss", calendar).toString();
        String[] items = date.split("-");
        confirmpayment = findViewById(R.id.confirmpayment);
        confirmpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curentuserspaimnets = allpaiments.child(mAuth.getCurrentUser().getUid());
                curentuserspaimnets.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("paymentNumber")) {
                            curentuserspaimnets.child("totalpayments").setValue(1);
                            paymentnumber = 1;
                            paymentid = paymentnumber;
                            payment = new payment();
                            payment.setPayID(paymentid + "_" + p.getUid() + "_" + p.getCin());
                            payment.setUserID(p.getUid());
                            payment.setTransfertnumber(transfertnumber.getEditableText().toString());
                            payment.setPaymentmethod(paymentmethod);
                            payment.setSendername(p.getFirst() + p.getLast());
                            payment.setCin(p.getCin());
                            payment.setPaymentfacture("downloaduri");
                            payment.setValue(paymentvalue.getEditableText().toString());
                            payment.setYear(Integer.parseInt(items[2]));
                            payment.setMonth(Integer.parseInt(items[1]));
                            payment.setDay(Integer.parseInt(items[0]));
                            payment.setHour(Integer.parseInt(items[3]));
                            payment.setMinute(Integer.parseInt(items[4]));
                            payment.setTimeStamp(time);
                            root.child("deliveryApp").child("users").child(mAuth.getCurrentUser().getUid()).child("payments").child(Integer.toString(paymentnumber)).setValue(payment);

//                            root.child("web").child("orders").child(Integer.toString(paymentnumber)).setValue(orderweb);
                        } else {
                            paymentnumber = dataSnapshot.child("totalOrders").getValue(Integer.class);
                            paymentnumber++;
                            paymentid = paymentnumber;
                            payment = new payment();
                            payment.setPayID(paymentid + "_" + p.getUid() + "_" + p.getCin());
                            payment.setUserID(p.getUid());
                            payment.setTransfertnumber(transfertnumber.getEditableText().toString());
                            payment.setPaymentmethod(paymentmethod);
                            payment.setSendername(p.getFirst() + p.getLast());
                            payment.setCin(p.getCin());
                            payment.setPaymentfacture("downloaduri");
                            payment.setValue(paymentvalue.getEditableText().toString());
                            payment.setYear(Integer.parseInt(items[2]));
                            payment.setMonth(Integer.parseInt(items[1]));
                            payment.setDay(Integer.parseInt(items[0]));
                            payment.setHour(Integer.parseInt(items[3]));
                            payment.setMinute(Integer.parseInt(items[4]));
                            payment.setTimeStamp(time);

//                            orderweb = new OrderWeb(
//                                    order.category,
//                                    order.description,
//                                    order.userId,
//                                    order.status,
//                                    order.otp,
//                                    order.orderId,
//                                    order.min_range,
//                                    order.max_range,
//                                    order.final_price,
//                                    order.deliveryCharge,
//                                    order.userLocation,
//                                    order.expiryDate,
//                                    order.expiryTime,
//                                    order.acceptedBy,
//                                    client,
//                                    time,
//                                    deliverer);
                            curentuserspaimnets.child("totalpayments").setValue(paymentnumber);
                            root.child("deliveryApp").child("users").child(mAuth.getCurrentUser().getUid()).child("payments").child(Integer.toString(paymentnumber)).setValue(payment);
                            root.child("web").child("totalOrders").setValue(paymentnumber);

//                            root.child("web").child("orders").child(Integer.toString(paymentnumber)).setValue(orderweb);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });
            }
        });
        totalernings = findViewById(R.id.totalernings);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        totalToPay = findViewById(R.id.totalToPay);
        history = findViewById(R.id.history);
        mandat = findViewById(R.id.mandat);
        textView13 = findViewById(R.id.textView13);
        edinar = findViewById(R.id.edinar);
        textView15 = findViewById(R.id.textView15);
        banctransfert = findViewById(R.id.banctransfert);
        nav_view_deliverer = findViewById(R.id.nav_view_deliverer);
        navigationView = findViewById(R.id.nav_view_deliverer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mPreviousMenuItem = menuItem;// close drawer when item is tapped
                mDrawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                if (id == R.id.use_as_deliverer) {
                    startActivity(new Intent(payments.this, payments.class));
                    usertype(root, "deliverer");


                } else if (id == R.id.use_as_user) {
                    startActivity(new Intent(payments.this, UserViewActivity.class));
                    usertype(root, "orderer");
                    finish();
                } else if (id == R.id.profile) {
                    Intent i = new Intent(payments.this, Profile.class);
                    FirebaseAuth authx = FirebaseAuth.getInstance();
                    String user = authx.getCurrentUser().toString();
                    i.putExtra("UserDetail", user);
                    startActivity(i);
                } else if (id == R.id.chatroom) {
                    Intent i = new Intent(payments.this, chatRooms.class);

                    startActivity(i);

                } else if (id == R.id.info) {
                    mDrawerLayout.closeDrawers();

                } else if (id == R.id.sign_out_deliverer) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    auth.signOut();
                    Intent loginIntent = new Intent(payments.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(payments.this, android.R.layout.select_dialog_singlechoice);

        DatabaseReference allpays = root.child("deliveryApp").child("users").child(mAuth.getCurrentUser().getUid()).child("payments");
        allpays.keepSynced(true);
        allpays.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    payment reca = data.getValue(payment.class);
                    arrayAdapter.add("Payment  of " + DateFormat.format("dd-MM-yyyy hh:mm:ss", reca.getTimeStamp()).toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
        mandat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numcartedinar.setVisibility(View.INVISIBLE);
            }
        });
        edinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numcartedinar.setVisibility(View.VISIBLE);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder alertDialog = new
//                        AlertDialog.Builder(payments.this);
//                View rowList = getLayoutInflater().inflate(R.layout.row, null);
//                listView = rowList.findViewById(R.id.listView);
//                adapter = new ArrayAdapter<String>(payments.this, android.R.layout.simple_list_item_1, paymentsid);
//                listView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//                alertDialog.setView(rowList);
//                AlertDialog dialog = alertDialog.create();
//                dialog.show();


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(payments.this);
                builderSingle.setTitle("list des payment que vous avez passé");

                builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

                builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                    String strName = arrayAdapter.getItem(which);
                    String recid = strName + " : ";
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(payments.this);
                    builderInner.setMessage(recid);
                    builderInner.setTitle("Your Selected Item is");
                    builderInner.setPositiveButton("Ok", (dialog1, which1) -> {
                        dialog.dismiss();
                    });
                    builderInner.show();
                });
                builderSingle.show();
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(payments.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        notiflist = new ArrayList<>();
        fbobjectList = new ArrayList<>();
        dynamic = findViewById(R.id.notiflv);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setTitle("Payments");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(mContext, "Current account is disabled login again to fix the problem ", Toast.LENGTH_SHORT).show();
            new Intent(payments.this, LoginActivity.class);
            finish();
        } else {
            userId = user.getUid();
        }
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes

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
                            TextView currentLocation = mHeaderView.findViewById(R.id.currentLocation);
//                            TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(, Color.WHITE, 100);
                            if (address != null) {
                                currentLocation.setText(address.getLocality());
                            } else {
                                currentLocation.setText("can't get current adresss");
                            }
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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageButton.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(payments.this);
            builder.setMessage("Add facture image").setCancelable(false)
                    .setPositiveButton(R.string.camera, (dialog, id) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                try {
                                    imageFilePath = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                photoURI = FileProvider.getUriForFile(payments.this, "com.xdev.pfe.utils.fileprovider", imageFilePath);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    })
                    .setNegativeButton(R.string.gallery, (dialog, id) -> chooseImage());
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Payment");
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        });
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = new File(image.getAbsolutePath());
        return image;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectpic)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == CAMERA_REQUEST && data != null && data.getData() != null) {
            Dialog builder1 = new Dialog(this);
            builder1.create();
            builder1.setContentView(R.layout.imgdialo);
            Button btn1 = builder1.findViewById(R.id.btn1);
            Button btn2 = builder1.findViewById(R.id.btn2);
            ImageView a = builder1.findViewById(R.id.a);
            builder1.setOnShowListener(dialogInterface -> {
                if (data == null || data.getData() == null) {
                    a.setImageURI(photoURI);
                } else {
                    a.setImageURI(data.getData());
                }
                btn1.setOnClickListener(v -> {
                    if ((requestCode == CAMERA_REQUEST)) {
                        builder1.dismiss();
                        uploadfacture(photoURI);
                        builder1.dismiss();
                    }
                    if (requestCode == PICK_IMAGE_REQUEST) {
                        uploadfacture(data.getData());
//                         a.setImageURI(data.getData());
                        builder1.dismiss();
                    }
                });
                btn2.setOnClickListener(v -> builder1.dismiss());
            });
            builder1.show();
        }
    }

    private void uploadfacture(Uri photoURI) {
        if (photoURI != null) {
            StorageReference sRef = storageReference.child("uploads/" + auth.getUid() + "/" + "payments/" + "payments_" + System.currentTimeMillis() + ".jpg");       //adding the file to reference
            StorageReference desertRef = storageReference.child("uploads/" + auth.getUid() + "/profile/");
            desertRef.delete().addOnSuccessListener(aVoid -> Toast.makeText(payments.this, deletedoldpic, Toast.LENGTH_SHORT).show()).addOnFailureListener(exception -> Toast.makeText(payments.this, "exception" + exception.getMessage(), Toast.LENGTH_SHORT).show());
            UploadTask uploadTask = sRef.putFile(photoURI);
            int color;
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.uploading, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return sRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(payments.this, String.valueOf(downloadUri), Toast.LENGTH_SHORT).show();
                    snackbar.dismiss();
                    showSnacks(getString(R.string.Doneprofile));
                    Picasso.get().load(downloadUri).into(factureimage);

                } else {
                    showSnacks(getString(R.string.erroruploading));
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, R.string.nofilesemcted, Toast.LENGTH_SHORT).show();
        }
    }


    private void showSnacks(String string) {
        int color;
        color = Color.WHITE;
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), string, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}