package com.xdev.deliverytn.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.recyclerview.RecyclerViewOrderAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Profile extends AppCompatActivity {
    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST_profile = 1887;
    private static final int CAMERA_REQUEST_profile_change = 1886;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 234;
    public static RecyclerViewOrderAdapter adapter;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private final int SELECT_FILE = 1;
    private final Handler handler = new Handler();
    public List<OrderData> orderList;
    public Uri photoURI;
    MenuItem mPreviousMenuItem = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean isRefreshing = false;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textViewUserName;
    boolean rectoExists = false;
    GridLayout mainGrid;
    File imageFilePath;
    String cinphoto;
    AnimationDrawable animationDrawable;
    Button recto;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    FirebaseStorage storage;
    boolean profileexsists = false;
    String name;
    String url;
    ProgressBar p;
    TextView textViewEmail;
    boolean havelocation;
    ImageView profilei;
    private FirebaseAuth auth;
    private Address address;
    private UserDetails userDetails = new UserDetails();
    private TextInputEditText cin;
    private TextInputEditText birth;
    private TextInputEditText mobile;
    private TextInputEditText rate;
    private TextInputEditText ratenumber;
    private TextInputEditText addresss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i = getIntent();
        String userType = i.getStringExtra("type");
        String userId = user.getUid();
        profilei = findViewById(R.id.imageViewUserImage);


        cin = findViewById(R.id.cin);
        birth = findViewById(R.id.birth);
        mobile = findViewById(R.id.mobile);
        rate = findViewById(R.id.rate);
        ratenumber = findViewById(R.id.ratenumber);
        addresss = findViewById(R.id.address);
        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(userId);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(UserDetails.class);
//                mHeaderView = navigationView.getHeaderView(0);
                forUserData.child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String photoUrl = dataSnapshot.getValue(String.class);
                        try {
                            if (photoUrl.equals("nophoto"))
                                Picasso.get().load(R.drawable.smiley).into(profilei);
                            else
                                Picasso.get().load(photoUrl).into(profilei);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                profilei.setImageBitmap(loadImageFromStorage("cin"));
                textViewUserName = findViewById(R.id.headerUserName);
                textViewEmail = findViewById(R.id.headerUserEmail);
                Integer wallet = Integer.valueOf(userDetails.getWallet());
                ImageView walletBalance = findViewById(R.id.walletBalance);
                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(Integer.toString(wallet), Color.WHITE, 100);
                walletBalance.setImageDrawable(drawable);
                textViewUserName.setText(userDetails.getLast() + "" + userDetails.getFirst());
                textViewEmail.setText(userDetails.getEmail());
                cin.setText(userDetails.getCin());
                birth.setText(userDetails.getBirth());
                mobile.setText(userDetails.getMobile());
                rate.setText(String.valueOf(userDetails.getRate()));
                ratenumber.setText(String.valueOf(userDetails.getRate()));
                addresss.setText(userDetails.getAddress());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profilei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilei.setOnClickListener(v1 -> {
//                    capturimage();
                });


            }


        });
        if (havelocation) {
            View mHeaderView = navigationView.getHeaderView(0);
            ImageView currentLocation = findViewById(R.id.currentLocation);
            TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.BLACK).bold().endConfig().buildRoundRect(address.getLocality(), Color.WHITE, 100);
            currentLocation.setImageDrawable(drawable);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == CAMERA_REQUEST
                && data != null
                && data.getData() != null) {
            Dialog builder1 = new Dialog(this);
            builder1.create();
            builder1.setContentView(R.layout.imgdialo);
            ImageView a = builder1.findViewById(R.id.a);
            builder1.setOnShowListener(dialogInterface -> {
                Button btn1 = builder1.findViewById(R.id.btn1);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestCode == CAMERA_REQUEST_profile) {
                            if (data == null || data.getData() == null) {
                                uploadProfile(photoURI);
                                builder1.dismiss();
                            } else {
                                uploadProfile(data.getData());
                                builder1.dismiss();
                            }


                        }
                    }
                });
                Button btn2 = builder1.findViewById(R.id.btn2);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder1.dismiss();
                    }
                });


                a.setImageURI(photoURI);


            });

            builder1.show();


        }

    }

    private String saveToStorage(Uri data, String pictype) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

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
                        root.child("deliveryApp").child("users").child(user.getUid()).child("profile").setValue(downloadUri);
                        showSnacks("Done ☺ ");
                        Picasso.get()
                                .load(downloadUri).into(profilei);

                    } else {
                        showSnacks(String.valueOf(R.string.erroruploading));
                    }
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, R.string.nofilesemcted, Toast.LENGTH_SHORT).show();
        }

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
}
