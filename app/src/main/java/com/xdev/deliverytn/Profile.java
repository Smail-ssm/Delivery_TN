package com.xdev.deliverytn;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import com.xdev.deliverytn.login.user_details.UserDetails;
import com.xdev.deliverytn.recyclerview.RecyclerViewOrderAdapter;
import com.xdev.deliverytn.user.order.OrderData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


        cin = (TextInputEditText) findViewById(R.id.cin);
        birth = (TextInputEditText) findViewById(R.id.birth);
        mobile = (TextInputEditText) findViewById(R.id.mobile);
        rate = (TextInputEditText) findViewById(R.id.rate);
        ratenumber = (TextInputEditText) findViewById(R.id.ratenumber);
        addresss = (TextInputEditText) findViewById(R.id.address);

//        storageReference = storage.getReference();
//        StorageReference sRef = storageReference.child("uploads/" + user.getUid() + "/" + "profilePic" + ".jpg");//adding the file to reference
//        profilei.setImageBitmap(loadImageFromStorage("profile"));
//        if (sRef==null){
//            Toast.makeText(this, "please add image ", Toast.LENGTH_SHORT).show();
//        }else {
//            Glide.with(this)
//                    .load(sRef)
//                    .into(profilei);
//

        DatabaseReference forUserData = root.child("deliveryApp").child("users").child(userId);
        forUserData.keepSynced(true);
        forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(UserDetails.class);
//                mHeaderView = navigationView.getHeaderView(0);
                forUserData.child("cinPhoto").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String photoUrl = dataSnapshot.getValue(String.class);
                        try {
                            Picasso.get()
                                    .load(photoUrl)
                                    .into(profilei);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                profilei.setImageBitmap( loadImageFromStorage("cin"));
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
                rate.setText(String.valueOf( userDetails.getRate()));
                ratenumber.setText(String.valueOf(userDetails.getRate_number()));
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
                    capturimage();
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

    private void capturimage() {
        builder = new AlertDialog.Builder(Profile.this);
        builder.setMessage("camera ?")
                .setCancelable(false)
                .setPositiveButton("camera", (dialog, id) -> {
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
                            photoURI = FileProvider.getUriForFile(Profile.this, "com.xdev.pfe.utils.fileprovider", imageFilePath);
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
    }

    Bitmap loadImageFromStorage(String type) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Bitmap b = null;
        try {
            File f = new File(directory, type + ".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Toast.makeText(cw, "no file found ", Toast.LENGTH_SHORT).show();
            profilei.setImageResource(R.drawable.smiley);

        }
        profileexsists = true;
        return b;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                    builder.setMessage("Save picture ?")
                            .setCancelable(false)
                            .setPositiveButton("upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (
                                            requestCode
                                                    ==
                                                    CAMERA_REQUEST
                                    ) if (
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
                        profilei.setImageURI(downloadUri);
                        Picasso.get().load(downloadUri).into(profilei);

                    } else {
                    }
                }
            });
            // [END upload_get_download_url]
        } else {
            Toast.makeText(this, "NO file is selected", Toast.LENGTH_SHORT).show();
        }

    }

    void update_user_profile(Uri test) {
        root.child("deliveryApp").child("users").child(user.getUid()).child("profile").setValue(test.toString());
//        profileexsists = true;
    }
}
