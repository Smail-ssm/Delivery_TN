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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
import com.xdev.deliverytn.profile.Profile;
import com.xdev.deliverytn.user.UserViewActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    int notif;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

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

    //todo add icon to call deliverer
    //todo in app messeging in get informed
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView username = findViewById(R.id.username);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        root = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        animation();
        checkConnection();
        requestLocationPermissions();
        requestcamera();
        profilepic = findViewById(R.id.profile_image);
        getinfo = findViewById(R.id.getinfo);
        getinfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, com.xdev.deliverytn.FirebaseNotifications.inbox.class)));
        Button logOutButton = findViewById(R.id.btnlogout);
//        Button setting = findViewById(R.id.settings);
        auth = FirebaseAuth.getInstance();
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
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("first").getValue(String.class) + " , " + dataSnapshot.child("last").getValue(String.class);
                showSnacks(getString(R.string.welcom) + name);
                username.setText(getString(R.string.welcom) + name);

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
                                Picasso
                                        .get()
                                        .load(dataSnapshot.child("cinPhoto").getValue(String.class))
                                        .into(a);


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
//        totalnotif =;
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
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
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
                                // if (!(data == null || data.getData() == null)) {
                                uploadCin(photoURI);
//                                a.setImageURI(data.getData());
                                isCin = false;
                                builder1.dismiss();

                                // }
                            }
                            if (isProfile) {
                                //   if (!(data == null || data.getData() == null)) {
                                uploadProfile(photoURI);
//                                a.setImageURI(photoURI);
                                isProfile = false;
                                builder1.dismiss();
                                // }
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


                        Picasso.get()
                                .load(downloadUri)
                                .into(profilepic);


                    } else {
                        showSnacks(getString(R.string.erroruploading));
                    }
                }
            });
            // [END upload_get_download_url]
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
        int color;
        color = Color.WHITE;
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
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