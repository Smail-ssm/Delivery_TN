package com.xdev.deliverytn.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.xdev.deliverytn.Profile;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.deliverer.DelivererViewActivity;
import com.xdev.deliverytn.user.UserViewActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.xdev.deliverytn.login.LoginActivity.mGoogleApiClient;
import static com.xdev.deliverytn.login.usertype.usertype;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST_profile = 1887;
    private static final int CAMERA_REQUEST_profile_change = 1886;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 234;
    private final int SELECT_FILE = 1;
    private final Handler handler = new Handler();
    public Uri photoURI;
    boolean rectoExists = false;
    GridLayout mainGrid;
    File imageFilePath;
    String cinphoto;
    AnimationDrawable animationDrawable;
    Button recto;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    FirebaseStorage storage;
    DatabaseReference root;
    String userId;
    FirebaseUser user;
    boolean profileexsists = false;
    String name;
    String url;
    ProgressBar p;
    ImageView profilepic;
    String usertype;
    boolean profilepicexsists = false;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ImageView ivImage;

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
        profilepic.setImageBitmap(loadImageFromStorage("profile"));
        ImageButton logOutButton = findViewById(R.id.btnlogout);
        auth = FirebaseAuth.getInstance();
        recto = findViewById(R.id.recto);
        root = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        root.child("deliveryApp").child("users").child(userId).child("first");
        DatabaseReference userinfo = root.child("deliveryApp").child("users").child(userId);
        userinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("first").getValue(String.class) + " , " + dataSnapshot.child("last").getValue(String.class);
                showSnacks("Welcome " + name);
                username.setText("Welcome " + name);

                if (dataSnapshot.child("cinPhoto").getValue(String.class).equals("noPhoto")) {
                    rectoExists = false;
                    recto.setText("verify");
                    recto.setBackgroundColor(Color.RED);
                    showSnacks("account waiting verification");
                } else {
                    rectoExists = true;
                    recto.setText("verified");
                    recto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSnacks("no need for verification you r ready to go ");
                            return;
                        }
                    });
                }
                if (dataSnapshot.child("photoURL") == null) {
                    profileexsists=false;
                    Toast.makeText(MainActivity.this, "Add profile picture", Toast.LENGTH_SHORT).show();
                    profilepic.setImageResource(R.drawable.back_to_home_button); }

                return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

//        if (profileexsists == false) {
//            showSnacks("please add profile picture");
//
//        }

//            profilepic.setImageURI(Uri.parse(root.child("deliveryApp").child("users").child(userId).child("profile").toString() + "profilePic.jpg"));
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Profile.class);
                startActivity(i);

            }
        });
        profilepic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("camera ?")
                        .setCancelable(true)
                        .setPositiveButton("camera", (dialog, id) -> {
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
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST_profile);
                                }
                            }
                        })
                        .setNegativeButton("Gallery", (dialog, id) -> chooseImage());
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("add profile pic");
                alert.show();
                return false;
            }
        });


        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                auth.signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
//
//username.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        Intent i=new Intent(MainActivity.this, Profile.class);
//        startActivity(i);
//    }
//});
        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        };
        recto.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("camera ?")
                    .setCancelable(false)
                    .setPositiveButton("camera", (dialog, id) -> {
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
                    .setNegativeButton("Gallery", (dialog, id) -> chooseImage());
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
            Toast.makeText(MainActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "Camera permission granted", Toast.LENGTH_SHORT).show();

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(30 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    void setGpsOn() {
        System.out.println("Inside setGpsOn");
        LocationRequest mLocationRequest = getLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                System.out.println("task ke OnSuccess mai hoon");

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("task ke onFailure mai hoon");
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
        if (resultCode == RESULT_OK || requestCode == CAMERA_REQUEST
                && data != null
                && data.getData() != null) {
            Dialog builder = new Dialog(this);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Save picture ?")
                            .setCancelable(false)
                            .setPositiveButton("upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (
                                            requestCode
                                                    ==
                                                    CAMERA_REQUEST
                                    ) {
                                        if ((data
                                                ==
                                                null
                                                ||
                                                data.getData()
                                                        == null)) {
                                            uploadimg(photoURI);
                                            try {
                                                saveToStorage(photoURI, "CIN");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            uploadimg(data.getData());
                                            try {
                                                saveToStorage(data.getData(), "CIN");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } else if (
                                            requestCode
                                                    ==
                                                    CAMERA_REQUEST_profile) {
                                        if (data
                                                == null ||
                                                data.getData()
                                                        == null) {
                                            uploadProfile(photoURI);
                                            try {
                                                saveToStorage(photoURI, "profile");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            uploadProfile(data.getData());
                                            try {
                                                saveToStorage(data.getData(), "profile");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
            });

            ImageView imageView = new ImageView(this);
            imageView.setImageURI(photoURI);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            builder.show();


        }

    }

    private String saveToStorage(Uri data, String pictype) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, pictype + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void uploadimg(Uri filePath) {
        if (filePath != null) {

            StorageReference sRef = storageReference.child("uploads/" + auth.getUid() + "/" + auth.getCurrentUser().getEmail() + ".jpg");       //adding the file to reference
            UploadTask uploadTask = sRef.putFile(filePath);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        update_user(downloadUri);
                        showSnacks("Done ☺ ");

                    } else {
                        showSnacks("error while uploading");
                    }
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, "NO file is selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadProfile(Uri filePath) {
        if (filePath != null) {

            StorageReference sRef = storageReference.child("uploads/" + auth.getUid() + "/" + "profilePic" + ".jpg");       //adding the file to reference
            UploadTask uploadTask = sRef.putFile(filePath);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        showSnacks("Done ☺ ");
                        profilepic.setImageURI(downloadUri);

                    } else {
                        showSnacks("error while uploading");
                    }
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, "NO file is selected", Toast.LENGTH_SHORT).show();
        }

    }

    Bitmap loadImageFromStorage(String type) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        Bitmap b = null;
        try {
            File f = new File(directory, type + ".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        profileexsists = true;
        return b;
    }

    void update_user(Uri test) {
        root.child("deliveryApp").child("users").child(userId).child("cinPhoto").setValue(test.toString());
        rectoExists = true;
    }

    void update_user_profile(Uri test) {
        root.child("deliveryApp").child("users").child(userId).child("profile").setValue(test.toString());
        profilepicexsists = true;
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

    void animation() {
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_login,null);*/
        LinearLayout linearLayout = findViewById(R.id.mainLinearLayout);
        animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
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