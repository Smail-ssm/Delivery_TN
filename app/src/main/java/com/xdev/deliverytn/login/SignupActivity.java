package com.xdev.deliverytn.login;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.UserDetails;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static com.xdev.deliverytn.R.string.Enter8digitnumber;
import static com.xdev.deliverytn.R.string.aaa;
import static com.xdev.deliverytn.R.string.azazz;
import static com.xdev.deliverytn.R.string.faildreg;
import static com.xdev.deliverytn.R.string.passwordshort;
import static com.xdev.deliverytn.R.string.succesregistr;

public class SignupActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final int REQUEST_CHECK_SETTINGS = 20;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    DatePickerDialog datepicker;
    String gender;
    Calendar calendar;
    String URIrecto;
    boolean resumed;
    boolean havelocation;
    double latitude, longitude;
    private EditText first;
    private EditText last;
    private RadioButton male;
    private Spinner gov;
    private Button datn;
    private EditText mobile;
    private EditText address;
    private EditText cp;
    private EditText email;
    private EditText password;
    private EditText Confirmpass;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private LocationCallback mLocationCallback;
    private Address adddress;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        checkConnection();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLatAndLong();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(SignupActivity.this, "Latitude = " + latitude + "\nLongitude = " + longitude, Toast.LENGTH_SHORT).show();
                    getAddressFromLatAndLong(latitude, longitude);
                    // Logic to handle location object
                } else {
                    //Toast.makeText(SignupActivity.this, "location has NULL value", Toast.LENGTH_SHORT).show();
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
        datn = findViewById(R.id.datn);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.address);
        cp = findViewById(R.id.cp);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Confirmpass = findViewById(R.id.Confirmpass);
        EditText cin = findViewById(R.id.cin);
        ImageButton pin = findViewById(R.id.pin);
        Button sign_up_button = findViewById(R.id.sign_up_button);
        Button sign_in_button = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        EditText cina = findViewById(R.id.cin);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getaddress();
            }
        });
        datn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datepicker = new DatePickerDialog(SignupActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                datn.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datepicker.show();
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
                    final String name = first.getText().toString().trim() + " " + last.getText().toString().trim();
                    final String Mobile = mobile.getText().toString().trim();
                    final String cin = cina.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), R.string.entrename, Toast.LENGTH_SHORT).show();
                        first.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(Mobile)) {
                        Toast.makeText(getApplicationContext(), R.string.entremobile, Toast.LENGTH_SHORT).show();
                        mobile.requestFocus();
                        return;
                    }
                    if (mobile.length() != 8) {
                        Toast.makeText(getApplicationContext(), Enter8digitnumber, Toast.LENGTH_SHORT).show();
                        mobile.requestFocus();
                        return;
                    }

                    if (cin.length() != 8) {
                        Toast.makeText(getApplicationContext(), R.string.cin8, Toast.LENGTH_SHORT).show();
                        cina.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(Email)) {
                        Toast.makeText(getApplicationContext(), R.string.emailem, Toast.LENGTH_SHORT).show();
                        email.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(Password) || TextUtils.isEmpty(Confirmpassword)) {
                        Toast.makeText(getApplicationContext(), aaa, Toast.LENGTH_SHORT).show();
                        password.requestFocus();
                        return;
                    }
                    if (password.equals(Confirmpass)) {
                        Toast.makeText(getApplicationContext(), azazz, Toast.LENGTH_SHORT).show();
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
                    auth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, succesregistr, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignupActivity.this, faildreg, Toast.LENGTH_SHORT).show();
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the com.thedeliveryapp.thedeliveryapp.user.user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in com.thedeliveryapp.thedeliveryapp.user.user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                        switch (errorCode) {

                                            case "ERROR_INVALID_EMAIL":
                                                email.requestFocus();
                                                Toast.makeText(SignupActivity.this, R.string.emailtyperror, Toast.LENGTH_SHORT).show();
                                                break;

                                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                                email.requestFocus();
                                                Toast.makeText(SignupActivity.this, R.string.emailused, Toast.LENGTH_SHORT).show();
                                                break;

                                            case "ERROR_WEAK_PASSWORD":
                                                password.requestFocus();
                                                Toast.makeText(SignupActivity.this, passwordshort, Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    } else {

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
                                        u.setProfile("nophoto");
                                        u.setRate("0");
                                        u.setTopay(0);
                                        u.setUsertype("");
                                        u.setAccountstatue("enabled");
                                        u.setDisplayName(first.getText().toString() + " " + last.getText().toString());
                                        update_userdetails_database(u);
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (!user.isEmailVerified()) {
                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                            Intent i = new Intent(SignupActivity.this, VerifyEmailScreen.class);
                                            i.putExtra("email", u.getEmail());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        }
                                        finish();
                                    }
                                }
                            });

                }
            }
        });


    }

    void getAddressFromLatAndLong(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            adddress = geocoder.getFromLocation(lat, lng, 1).get(0);
            havelocation = true;
            if (!resumed) {
                resumed = true;
//                refreshOrders();
            }
            String add = "";
            add = add + adddress.getLocality() + "," + adddress.getSubLocality(); //City
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(SignupActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
            setGpsOn();
        }
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
                if (ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.requestLocationUpdates
                        (getLocationRequest(), mLocationCallback,
                                null /* Looper */);

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
                        resolvable.startResolutionForResult(SignupActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void getaddress() {

        if (havelocation) {
//                    refreshadds();
            Toast.makeText(SignupActivity.this, adddress.getLocality() + "," + adddress.getSubLocality() + "," + adddress.getThoroughfare(), Toast.LENGTH_SHORT).show();
            address.setText(String.format("%s,%s,%s", adddress.getLocality(), adddress.getSubLocality(), adddress.getThoroughfare()));
            cp.setText(adddress.getPostalCode());
        } else {
            getLatAndLong();

        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
            showSnack(isConnected);
    }

    // Showing the status in Snackbar
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        CheckConnectivityMain.getInstance().setConnectivityListener(SignupActivity.this);
    }

    //Method to upload details in database;
    void update_userdetails_database(UserDetails ud) {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String playerId = status.getSubscriptionStatus().getUserId();
//        UserDetails Details = new UserDetails(ud.getMobile(), ud.getCin(), ud.getEmail(), ud.getWallet(), playerId, ud.getAddress(), ud.getBirth(), ud.getRecto(),  ud.getCin(), ud.getFirst(), ud.getGender(), ud.getLast(), ud.getRole(), ud.getZip());
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        ud.setPlayerId(playerId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        ud.setUid(userId);
        root.child("deliveryApp").child("users").child(userId).setValue(ud);
        root.child("web").child("users").child(userId).setValue(ud);


    }


}