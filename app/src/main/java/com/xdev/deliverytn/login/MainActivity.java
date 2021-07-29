package com.xdev.deliverytn.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.models.ReclamationObject;
import com.xdev.deliverytn.payments;
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.reclamations.createReclamation;
import com.xdev.deliverytn.user.UserViewActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.xdev.deliverytn.R.string.addcinpic;
import static com.xdev.deliverytn.R.string.camerapermesiongranted;
import static com.xdev.deliverytn.R.string.deletedoldpic;
import static com.xdev.deliverytn.R.string.locationpermessiongranted;
import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.models.usertype.usertype;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static Context mContext;
    private final String lang = "";
    public Uri photoURI;
    GridLayout mainGrid;
    File imageFilePath;
    Boolean isCin = false, isProfile = false;
    AnimationDrawable animationDrawable;
    Button recto;
    LinearLayout getinfo;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    FirebaseStorage storage;
    DatabaseReference root;
    String userId;
    FirebaseUser user;
    String name;
    Snackbar snackbar;
    ImageView profilepic;
    TextView ReclamationList;
    CardView clientcard, delivererCard;
    int notif;
    ReclamationObject rec;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button accountStatue;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        MainActivity.mContext = mContext;
    }

    public static void restart(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        System.exit(0);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView username = findViewById(R.id.username);
        TextView earningss = findViewById(R.id.earnings);
        animation();
        checkConnection();
        requestLocationPermissions();
        requestcamera();
        mainGrid = findViewById(R.id.mainGrid);
        profilepic = findViewById(R.id.profile_image);
        getinfo = findViewById(R.id.getinfo);
        getinfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, com.xdev.deliverytn.FirebaseNotifications.inbox.class)));
        Button logOutButton = findViewById(R.id.btnlogout);
//        Button setting = findViewById(R.id.settings);
        auth = FirebaseAuth.getInstance();
        accountStatue = findViewById(R.id.accountStatue);
        recto = findViewById(R.id.recto);
        root = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(mContext, "Current account is disabled login again to fix the problem ", Toast.LENGTH_SHORT).show();
            new Intent(MainActivity.this, LoginActivity.class);
            finish();
        } else {
            userId = user.getUid();
        }
        //selected date from calender
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy"); //Date and time
        Calendar myCalendar = Calendar.getInstance();
        String currentDate = sdf.format(myCalendar.getTime());

//selcted_day name
        SimpleDateFormat sdf_ = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf_ = new SimpleDateFormat("YYYY");
        }
        String dayofweek = sdf_.format(myCalendar.getTime());

        if (dayofweek.equalsIgnoreCase("Saturday") || dayofweek.equalsIgnoreCase("Sunday") || dayofweek.equalsIgnoreCase("monday")) {
        earningss.setVisibility(View.VISIBLE);
        }
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        root = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // setup the alert builder
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                String[] items = {"Francais", "Englais", "عربية"};
//
//// cow
//                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        checkedItem[0] = items[which];
//                        switch (which) {
//                            case 0:
//                                Toast.makeText(MainActivity.this, "FR", Toast.LENGTH_LONG).show();
//                                checkedItem[0] = "FR";
//                                break;
//                            case 1:
//                                Toast.makeText(MainActivity.this, "EN", Toast.LENGTH_LONG).show();
//                                checkedItem[0] = "EN";
//                                break;
//                            case 2:
//                                Toast.makeText(MainActivity.this, "TN", Toast.LENGTH_LONG).show();
//                                checkedItem[0] = "ar";
//                                break;
//
//                        }
//
//                    }
//                });
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        restart(MainActivity.this, checkedItem[0]);
//
//                    }
//                });
//                builder.setNegativeButton(R.string.cancel, null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        TextView notifNmbr = findViewById(R.id.notifNmbr);
        root.child("deliveryApp").child("users").child(userId).child("first");
        DatabaseReference userinfo = root.child("deliveryApp").child("users").child(userId);
        userinfo.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("first").getValue(String.class) + " , " + dataSnapshot.child("last").getValue(String.class);
                if (dataSnapshot.child("accountstatue").getValue(String.class).equalsIgnoreCase("enabled")) {

                    findViewById(R.id.alert1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.alert2).setVisibility(View.INVISIBLE);
                    accountStatue.setText(R.string.enabled);
                } else {
                    mainGrid.setClickable(false);
                    mainGrid.setBackgroundColor(Color.RED);
                    mainGrid.setVisibility(View.INVISIBLE);
                    findViewById(R.id.alert1).setVisibility(View.VISIBLE);
                    findViewById(R.id.alert2).setVisibility(View.VISIBLE);
                    accountStatue.setText(R.string.unlock);
                    accountStatue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, payments.class));
                        }
                    });

                }
                showSnacks(getString(R.string.welcom) + " " + name);
                username.setText(getString(R.string.welcom) + " " + name);
                String usertypo = dataSnapshot.child("usertype").getValue(String.class);
                if (!(usertypo.isEmpty())) {
                    if (usertypo.equalsIgnoreCase("deliverer")) {
                        findViewById(R.id.ordererCard).setVisibility(View.GONE);
                        if (!(dataSnapshot.child("topay").exists())) {
                            if ((dataSnapshot.child("topay").getValue(String.class)) == "0") {
                                earningss.setText("0");
                                userinfo.child("topay").setValue(0);
                            }
                        } else {

                            earningss.setText("to pay :" + dataSnapshot.child("topay").getValue(Float.class) + " TND.");

                        }

                    }
                    if ((dataSnapshot.child("topay").getValue(Float.class)) >= 10) {
                        mainGrid.setClickable(false);
                        mainGrid.setBackgroundColor(Color.RED);
                        mainGrid.setVisibility(View.GONE);
                        findViewById(R.id.alert1).setVisibility(View.VISIBLE);
                        findViewById(R.id.alert2).setVisibility(View.VISIBLE);
                        accountStatue.setText(R.string.unlock);
                        accountStatue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, payments.class));
                            }
                        });
                    }
                    if (dataSnapshot.child("usertype").getValue(String.class).equalsIgnoreCase("orderer")) {
                        findViewById(R.id.delivererCard).setVisibility(View.GONE);
                        earningss.setVisibility(View.GONE);
                    }
                } else return;


                if (dataSnapshot.child("cinPhoto").getValue(String.class).equalsIgnoreCase("nophoto")) {
                    recto.setText(R.string.verifie);
                    recto.setBackgroundColor(Color.RED);
                    showSnacks(getString(R.string.enattentverification));
                } else {
//
                    recto.setText(R.string.verified);
                    recto.setBackgroundColor(Color.TRANSPARENT);

                    recto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSnacks(getString(R.string.noneedtoverify));
                            showSnacks(getString(R.string.longclicktoviewimage));
                            return;
                        }
                    });
                    recto.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Dialog builder1 = new Dialog(MainActivity.this);
                            builder1.create();
                            builder1.setContentView(R.layout.imgdialo);
                            Button btn1 = builder1.findViewById(R.id.btn1);
                            Button btn2 = builder1.findViewById(R.id.btn2);
                            TextView t = builder1.findViewById(R.id.text_dialog);
                            ImageView a = builder1.findViewById(R.id.a);
                            builder1.setOnShowListener(dialogInterface -> {
                                Picasso.get().load(dataSnapshot.child("cinPhoto").getValue(String.class)).into(a);
                                btn1.setVisibility(View.GONE);
                                t.setVisibility(View.GONE);
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder1.dismiss();
                                    }
                                });
                                btn1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder1.dismiss();
                                    }
                                });
                            });
                            builder1.show();
                            return false;
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("deliveryApp").child("totalNotifications").keepSynced(true);
        FirebaseDatabase.getInstance().getReference().child("deliveryApp").child("totalNotifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer notifa = dataSnapshot.getValue(Integer.class);
                notifNmbr.setText(String.valueOf(notifa));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

        DatabaseReference allrec = root.child("deliveryApp").child("reclamations");
        allrec.keepSynced(true);
        allrec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ReclamationObject reca = data.getValue(ReclamationObject.class);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(Long.parseLong(reca.getTimestamp())));
                    arrayAdapter.add("Order number " + reca.getRecId() + " at " + dateString);
                    ReclamationList.setText("Reclamation: " + arrayAdapter.getCount());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

        ReclamationList = findViewById(R.id.ReclamationList);
        ReclamationList.setOnClickListener(v -> {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("list des reclamation que vous avez passé");

            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                String strName = arrayAdapter.getItem(which);
                String recid = strName + " : ";
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                builderInner.setMessage(recid);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", (dialog1, which1) -> {
                    Intent i = new Intent(MainActivity.this, createReclamation.class);
                    i.putExtra("reclamationID", which);
                    startActivity(i);
                });
                builderInner.show();
            });
            builderSingle.show();
        });
        profilepic.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Profile.class);
            startActivity(i);

        });
        userinfo.child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoUrl = dataSnapshot.getValue(String.class);
                if (!photoUrl.equalsIgnoreCase("noPhoto") || photoUrl.equalsIgnoreCase(""))
                    try {
                        Picasso.get()
                                .load(photoUrl)
                                .into(profilepic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else Picasso.get().load(R.drawable.smiley).into(profilepic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profilepic.setOnLongClickListener(v -> {
            isProfile = true;
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.choisireimageSource)
                    .setCancelable(true)
                    .setPositiveButton(R.string.camera, (dialog, id) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {

                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                try {
                                    imageFilePath = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                photoURI = FileProvider.getUriForFile(MainActivity.this, "com.xdev.pfe.utils.fileprovider", imageFilePath);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    })
                    .setNegativeButton(R.string.gallery, (dialog, id) -> chooseImage());
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle(getString(R.string.addprofilepic));
            alert.show();
            return false;
        });


        logOutButton.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        auth.signOut();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(MainActivity.this, "Loged out from facebook", Toast.LENGTH_SHORT).show();
                        }
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    })
                    .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss());
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle(getString(R.string.logout));
            alert.show();

        });
        authListener = firebaseAuth -> {
            FirebaseUser usera = FirebaseAuth.getInstance().getCurrentUser();
            if (usera == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {

                DatabaseReference forUserData = root.child("deliveryApp").child("users").child(userId);
                forUserData.keepSynced(true);
                forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot
                                                     dataSnapshot) {
//                        UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                        String type = dataSnapshot.child("usertype").getValue(String.class);
                        if (!(type.isEmpty())) {
                            if (type.equalsIgnoreCase("deliverer")) {
                                findViewById(R.id.delivererCard).setVisibility(View.VISIBLE);
                                findViewById(R.id.ordererCard).setVisibility(View.GONE);
                            }
                            if (dataSnapshot.child("usertype").getValue(String.class).equalsIgnoreCase("orderer")) {
                                findViewById(R.id.ordererCard).setVisibility(View.VISIBLE);
                                findViewById(R.id.delivererCard).setVisibility(View.GONE);
                            }
                        } else {
                            findViewById(R.id.ordererCard).setVisibility(View.VISIBLE);
                            findViewById(R.id.delivererCard).setVisibility(View.VISIBLE);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        recto.setOnClickListener(v -> {
            isCin = true;
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(addcinpic).setCancelable(false)
                    .setPositiveButton(R.string.camera, (dialog, id) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {

                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                try {
                                    imageFilePath = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                photoURI = FileProvider.getUriForFile(MainActivity.this, "com.xdev.pfe.utils.fileprovider", imageFilePath);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    })
                    .setNegativeButton(R.string.gallery, (dialog, id) -> chooseImage());
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("CIN");
            alert.show();
        });
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        mainGrid = findViewById(R.id.mainGrid);
        ordererMode(mainGrid);
        delivererMode(mainGrid);
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

    private void ordererMode(GridLayout mainGrid) {
        CardView cardView = (CardView) mainGrid.getChildAt(0);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserViewActivity.class));
                usertype(root, "orderer");

            }
        });
    }

    private void delivererMode(GridLayout mainGrid) {
        CardView cardView = (CardView) mainGrid.getChildAt(1);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DelivererViewActivity.class));
                usertype(root, "deliverer");
            }
        });
    }

    void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Toast.makeText(MainActivity.this, locationpermessiongranted, Toast.LENGTH_SHORT).show();
            setGpsOn();
        }
    }

    void requestcamera() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_CAMERA = 0;
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            Toast.makeText(MainActivity.this, camerapermesiongranted, Toast.LENGTH_SHORT).show();

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectpic)), PICK_IMAGE_REQUEST);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
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

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
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

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //cin
                        if ((requestCode == CAMERA_REQUEST)) {
                            if (isCin) {
                                uploadCin(photoURI);
                                isCin = false;
                                builder1.dismiss();
                            }
                            if (isProfile) {
                                uploadProfile(photoURI);
                                isProfile = false;
                                builder1.dismiss();
                            }
                        }
                        if (requestCode == PICK_IMAGE_REQUEST) {
                            if (isCin) {
                                uploadCin(data.getData());
//                                a.setImageURI(photoURI);
                                isCin = false;
                                builder1.dismiss();
                            }
                            if (isProfile) {
                                uploadProfile(data.getData());
//                                a.setImageURI(data.getData());
                                builder1.dismiss();
                                isProfile = false;

                            }
                        }


                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder1.dismiss();
                    }
                });


            });
            builder1.show();
        }
    }


    private void uploadCin(Uri filePath) {
        if (filePath != null) {

            StorageReference sRef = storageReference.child("uploads/" + auth.getUid() + "/" + "cin/" + auth.getCurrentUser().getEmail() + ".jpg");       //adding the file to reference
            storageReference.child("uploads/" + auth.getUid() + "/" + "cin/").delete();
            UploadTask uploadTask = sRef.putFile(filePath);
            int color;
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.uploading, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        update_user_cin(downloadUri);
                        showSnacks(getString(R.string.donecin));

                    } else {
                        showSnacks(getString(R.string.errorwhileuploading));
                    }
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, R.string.nofilesemcted, Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadProfile(Uri filePath) {
        if (filePath != null) {

            StorageReference sRef = storageReference.child("uploads/" + auth.getUid() + "/" + "profile/" + "profilePic_" + System.currentTimeMillis() + ".jpg");       //adding the file to reference

            StorageReference desertRef = storageReference.child("uploads/" + auth.getUid() + "/profile/");


            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, deletedoldpic, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, "exception" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            UploadTask uploadTask = sRef.putFile(filePath);
            int color;
            color = Color.WHITE;
            snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.uploading, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        update_user_profile(downloadUri);
                        snackbar.dismiss();
                        showSnacks(getString(R.string.Doneprofile));
                        Picasso.get().load(downloadUri).into(profilepic);


                    } else {
                        showSnacks(getString(R.string.erroruploading));
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.nofilesemcted, Toast.LENGTH_SHORT).show();
        }

    }

    void update_user_cin(Uri test) {
        root.child("deliveryApp").child("users").child(userId).child("cinPhoto").setValue(test.toString());
    }

    void update_user_profile(Uri test) {
        root.child("deliveryApp").child("users").child(userId).child("profile").setValue(test.toString());

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
            showSnack(isConnected);
    }

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

    private void showSnacks(String msg) {

    }

    private void showSnackUploading(String msg) {
        int color;
        color = Color.WHITE;
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    void animation() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_login, null);
        LinearLayout linearLayout = findViewById(R.id.mainLinearLayout);
        animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
        CheckConnectivityMain.getInstance().setConnectivityListener(MainActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}