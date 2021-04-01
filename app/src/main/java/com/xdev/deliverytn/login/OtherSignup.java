package com.xdev.deliverytn.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.pkmmte.view.CircularImageView;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.UserDetails;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.xdev.deliverytn.R.string.Enter8digitnumber;
import static com.xdev.deliverytn.R.string.cin8;
import static com.xdev.deliverytn.R.string.entremobile;
import static com.xdev.deliverytn.R.string.entrpass;
import static com.xdev.deliverytn.R.string.faildtologin;
import static com.xdev.deliverytn.R.string.loggedsucc;
import static com.xdev.deliverytn.R.string.passworddntmatch;


public class OtherSignup extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final int REQUEST_CHECK_SETTINGS = 20;
    public static final int PICK_IMAGE = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    AnimationDrawable animationDrawable;
    boolean rectoExists = false;
    boolean versoExsist = false;
    boolean resumed;
    boolean havelocation;
    double latitude, longitude;
    String gender;
    String URIrecto, URIverso;
    DatePickerDialog datepicker;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    private static final int RC_SIGN_IN = 101;
    private EditText inputName, inputMobile, inputAltMobile, inputEmail;
    private Button btnSignIn, btnSaveDetails;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private EditText name;
    public static GoogleApiClient mGoogleApiClient;
    public static String user_email, user_name, userId;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    CircularProgressButton btnLogin;
    FirebaseUser currentuser;
    GoogleSignInAccount currentAccount;
    private EditText mobile;
    private EditText email;
    private EditText password;
    private EditText Confirmpass;
    private CircularImageView recto;
    private TextView textView;
    private CircularImageView verso;
    private TextView textView3;
    private EditText first;
    private EditText inputPassword;
    private DatabaseReference root, database_users;
    private RadioButton male;
    private Spinner gov;
    private EditText address;
    private EditText cp;
    private LocationCallback mLocationCallback;
    private Address adddress;
    private FusedLocationProviderClient mFusedLocationClient;
    private LinearLayout cinLayout;
    private Button btnSelect;
    private ImageView ivImage;
    private ImageView imageView;
    private Button datn;
    private EditText last;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_signup);
        // animation();

        checkConnection();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLatAndLong();
        btnSignIn = findViewById(R.id.sign_in_button);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(getBaseContext(), "Latitude = " + latitude + "\nLongitude = " + longitude, Toast.LENGTH_SHORT).show();
                    Geocoder geocoder = new Geocoder(OtherSignup.this, Locale.getDefault());
                    try {
                        adddress = geocoder.getFromLocation(latitude, longitude, 1).get(0);
                        havelocation = true;
                        if (!resumed) {
                            resumed = true;
//                refreshOrders();
                        }
                        String add = "";
                        add = add + adddress.getLocality() + "," + adddress.getSubLocality(); //City

                        //add = add + "\n" + address.getSubThoroughfare();
                        //keepchecking location
                        // address.getLocality()+","+address.getSubLocality()+","+address.getThoroughfare()
//            Toast.makeText(getBaseContext(), add, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(OtherSignup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // Logic to handle location object
                } else {
                    //Toast.makeText(getBaseContext(), "location has NULL value", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        ImageView imageView2 = findViewById(R.id.imageView2);
        first = findViewById(R.id.first);
        last = findViewById(R.id.last);
        male = findViewById(R.id.male);
        RadioButton female = findViewById(R.id.female);
        gov = findViewById(R.id.gov);
        ImageButton pin = findViewById(R.id.pin);
        datn = findViewById(R.id.datn);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.address);
        cp = findViewById(R.id.cp);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
//        password.setVisibility(View.GONE);
        Confirmpass = findViewById(R.id.Confirmpass);
//        Confirmpass.setVisibility(View.GONE);
        Button sign_up_button = findViewById(R.id.sign_up_button);
        Button sign_in_button = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        EditText cina = findViewById(R.id.cin);

        Intent i = getIntent();
        String google_name = i.getStringExtra("username");
        currentAccount = i.getParcelableExtra("account");
        currentuser = i.getParcelableExtra("currentuser");
        String[] google_names = google_name.split(" ");
//
        String google_email = i.getStringExtra("email");
        getaddress();
        first.setText(google_names[0]);
        first.setEnabled(false);
        last.setText(google_names[1]);
        last.setEnabled(false);
        email.setText(google_email);
        email.setEnabled(false);
        datn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datepicker = new DatePickerDialog(OtherSignup.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                datn.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datepicker.show();
            }


        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                auth.signOut();
                Intent intent = new Intent(OtherSignup.this, OtherSignup.class);
                startActivity(intent);
                finish();
            }
        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getaddress();
            }
        });
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    final String Email = email.getText().toString().trim();
                    String Password = password.getText().toString().trim();
                    String Confirmpassword = Confirmpass.getText().toString().trim();
//                    final String name = first.getText().toString().trim() + last.getText().toString().trim();
                    final String Mobile = mobile.getText().toString().trim();
                    final String cin = cina.getText().toString().trim();
//                    final String alt_mobile = inputAltMobile.getText().toString().trim();

//                    if (TextUtils.isEmpty(name)) {
//                        Toast.makeText(getApplicationContext(), entrename, Toast.LENGTH_SHORT).show();
//                        first.requestFocus();
//                        return;
//                    }

                    if (TextUtils.isEmpty(Mobile)) {
                        Toast.makeText(getApplicationContext(), entremobile, Toast.LENGTH_SHORT).show();
                        mobile.requestFocus();
                        return;
                    }
                    if (mobile.length() != 8) {
                        Toast.makeText(getApplicationContext(), Enter8digitnumber, Toast.LENGTH_SHORT).show();
                        mobile.requestFocus();
                        return;
                    }

                    if ((cin.length() != 0) && (cin.length() != 8)) {
                        Toast.makeText(getApplicationContext(), cin8, Toast.LENGTH_SHORT).show();
                        cina.requestFocus();
                        return;
                    }


                    if (TextUtils.isEmpty(Password) || TextUtils.isEmpty(Confirmpassword)) {
                        Toast.makeText(getApplicationContext(), entrpass, Toast.LENGTH_SHORT).show();
                        password.requestFocus();
                        return;
                    }
                    if (password.equals(Confirmpass)) {
                        Toast.makeText(getApplicationContext(), passworddntmatch, Toast.LENGTH_SHORT).show();
                        password.requestFocus();
                        return;
                    }

                    if (male.isChecked()) {
                        gender = "m";
                    } else {
                        gender = "f";
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    //create com.thedeliveryapp.thedeliveryapp.user.user

                    auth.updateCurrentUser(currentuser).addOnCompleteListener(OtherSignup.this, task -> {
                        if (!task.isSuccessful()) {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {

                                case "ERROR_INVALID_EMAIL":
                                    email.requestFocus();
                                    Toast.makeText(OtherSignup.this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show();
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    email.requestFocus();
                                    Toast.makeText(OtherSignup.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    password.requestFocus();
                                    Toast.makeText(OtherSignup.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            Toast.makeText(OtherSignup.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        } else { // task success
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // Get auth credentials from the user for re-authentication
                            AuthCredential credential = GoogleAuthProvider.getCredential(currentAccount.getIdToken(), null); // Current Login Credentials \\
                            // Prompt the user to re-provide their sign-in credentials
                            currentuser.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            user.updatePassword(password.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                UserDetails u = new UserDetails();
                                                                u.setMobile(mobile.getText().toString());
                                                                u.setCin(cin);
                                                                u.setEmail(email.getText().toString());
                                                                u.setWallet(1000);
                                                                u.setAddress(address.getText().toString());
                                                                u.setBirth((String) datn.getText());
                                                                u.setCinPhoto("noPhoto");
                                                                u.setCity(gov.getSelectedItem().toString());
                                                                u.setFirst(first.getText().toString());
                                                                u.setGender(gender);
                                                                u.setLast(last.getText().toString());
                                                                u.setZip(cp.getText().toString());
                                                                u.setRole("null");
                                                                u.setRate(0);
                                                                u.setProfile("nophoto");
                                                                u.setRate(0);
                                                                u.setUsertype("");
                                                                u.setDisplayName(first.getText().toString() + " " + last.getText().toString());
                                                                update_userdetails_database(u);
                                                                if (!currentuser.isEmailVerified()) {
                                                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                                                    startActivity(new Intent(OtherSignup.this, VerifyEmailScreen.class));
                                                                } else {
                                                                    startActivity(new Intent(OtherSignup.this, MainActivity.class));
                                                                }

                                                                Toast.makeText(OtherSignup.this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }

                        progressBar.setVisibility(View.GONE);

                    });

                }
            }
        });


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            userId = user.getUid();
                            root = FirebaseDatabase.getInstance().getReference();
                            database_users = root.child("deliveryApp").child("users");
                            database_users.keepSynced(true);
                            database_users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild(userId)) {
                                        Intent intent = new Intent(OtherSignup.this, OtherSignup.class);
                                        intent.putExtra("username", user_name);
                                        intent.putExtra("email", user_email);
                                        intent.putExtra("account", account);

                                        startActivity(intent);

                                    } else {
                                        btnLogin.revertAnimation();

                                        Intent intent = new Intent(OtherSignup.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(OtherSignup.this, loggedsucc, Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            if (!ConnectivityReceiver.isConnected()) {
                                showSnack(false);
                            } else {
                                Toast.makeText(OtherSignup.this, faildtologin, Toast.LENGTH_SHORT).show();
                            }
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        }

                        // ...
                    }
                });
    }


    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(30 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    private void getaddress() {

        if (havelocation) {
//                    refreshadds();
            Toast.makeText(OtherSignup.this, adddress.getLocality() + "," + adddress.getSubLocality() + "," + adddress.getThoroughfare(), Toast.LENGTH_SHORT).show();
            address.setText(adddress.getLocality() + "," + adddress.getSubLocality() + "," + adddress.getThoroughfare());
            cp.setText(adddress.getPostalCode());
        } else {
            getLatAndLong();

        }
    }


    void getLatAndLong() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            LocationRequest mLocationRequest = getLocationRequest();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates
                            (getLocationRequest(), mLocationCallback,
                                    null /* Looper */);
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(OtherSignup.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        auth.signOut();
        Intent intent = new Intent(OtherSignup.this, OtherSignup.class);
        startActivity(intent);
        finish();
    }


    //Method to upload details in database;
    void update_userdetails_database(UserDetails ud) {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String playerId = status.getSubscriptionStatus().getUserId();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        ud.setPlayerId(playerId);

        ud.setUid(currentuser.getUid());
        root.child("deliveryApp").child("users").child(currentuser.getUid()).setValue(ud);
        root.child("web").child("users").child(currentuser.getUid()).setValue(ud);


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

//    void animation() {
//        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.activity_login,null);*/
//        CoordinatorLayout otherSignup = findViewById(R.id.otherSignup);
//        animationDrawable = (AnimationDrawable) otherSignup.getBackground();
//        animationDrawable.setEnterFadeDuration(5000);
//        animationDrawable.setExitFadeDuration(5000);
//
//    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}