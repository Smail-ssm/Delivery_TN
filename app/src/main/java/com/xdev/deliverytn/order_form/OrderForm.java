package com.xdev.deliverytn.order_form;

import static com.xdev.deliverytn.R.string.google_maps_key;
import static com.xdev.deliverytn.R.string.locationValidation;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.app.akplacepicker.models.AddressData;
import com.app.akplacepicker.utilities.Constants;
import com.app.akplacepicker.utilities.PlacePicker;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.AcceptedBy;
import com.xdev.deliverytn.models.Client;
import com.xdev.deliverytn.models.Deliverer;
import com.xdev.deliverytn.models.ExpiryDate;
import com.xdev.deliverytn.models.ExpiryTime;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.OrderWeb;
import com.xdev.deliverytn.models.Time;
import com.xdev.deliverytn.models.UserDetails;
import com.xdev.deliverytn.models.UserLocation;
import com.xdev.deliverytn.models.deliverylocation;
import com.xdev.deliverytn.user.UserViewActivity;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class OrderForm extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final int REQUEST_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CHECK_SETTINGS = 20;
    public static LatLng clientLocation;
    private final int final_price = -1;
    private final DeliveryChargeCalculater calc = new DeliveryChargeCalculater();
    TextView category, delivery_charge, price, total_charge;
    Button date_picker, time_picker, user_location, deliverylocationbtn;
    Calendar calendar;
    EditText description, min_int_range, max_int_range;
    int flag;
    UserLocation userLocation = null;
    deliverylocation deliverylocation = null;
    ExpiryTime expiryTime = null;
    ExpiryDate expiryDate = null;
    AcceptedBy acceptedBy = null;
    OrderData order;
    OrderWeb orderweb;
    Time time = new Time();
    Client client = null;
    Deliverer deliverer = new Deliverer();
    int PLACE_PICKER_REQUEST = 1;
    int DELIVERY_PICKER_REQUEST = 2;
    UserDetails cu;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private DatabaseReference walletBalance;
    private String userId, otp;
    private int OrderNumber;
    private int order_id;
    private int value;
    private int userBalance;
    private float finaldeliverycharge;
    private LatLng l1, l2;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);
        checkConnection();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermissions();
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.NewOrder);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        acceptedBy = new AcceptedBy("-", "-", "-", "-", "-");
        otp = "";
        progressBar = findViewById(R.id.progressBar);
        category = findViewById(R.id.btn_category);
        date_picker = findViewById(R.id.btn_date_picker);
        time_picker = findViewById(R.id.btn_time_picker);
        calendar = Calendar.getInstance();
        description = findViewById(R.id.description_of_order);
        min_int_range = findViewById(R.id.min_int);
        max_int_range = findViewById(R.id.max_int);
        user_location = findViewById(R.id.user_location);
        deliverylocationbtn = findViewById(R.id.deliverylocation);
        delivery_charge = findViewById(R.id.delivery_charge);
        price = findViewById(R.id.max_price);
        total_charge = findViewById(R.id.total_amount);
        max_int_range.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String S = s.toString();
                if (!S.equals("")) {
                    value = Integer.parseInt(s.toString());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userId = user.getUid();
                    root = FirebaseDatabase.getInstance().getReference();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mcategories = new ArrayList<String>();
                mcategories.add(getString(R.string.food));
                mcategories.add(getString(R.string.medcin));
                mcategories.add(getString(R.string.houshold));
                mcategories.add(getString(R.string.electronic));
                mcategories.add(getString(R.string.toileterie));
                mcategories.add(getString(R.string.books));
                mcategories.add(getString(R.string.clothing));
                mcategories.add(getString(R.string.Shoes));
                mcategories.add(getString(R.string.Sports));
                mcategories.add(getString(R.string.Games));
                mcategories.add(getString(R.string.Others));
                //Create sequence of items
                final CharSequence[] Categories = mcategories.toArray(new String[mcategories.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OrderForm.this);
                dialogBuilder.setTitle(R.string.ChooseCategory);
                dialogBuilder.setItems(Categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedText = Categories[item].toString();  //Selected item in listview
                        category.setText(selectedText);
                    }
                });
                //Create alert dialog object via builder
                AlertDialog alertDialogObject = dialogBuilder.create();
                //Show the dialog
                alertDialogObject.show();
            }
        });

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        expiryDate = new ExpiryDate(year, monthOfYear, dayOfMonth, System.currentTimeMillis());
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                        date_picker.setText(date);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(OrderForm.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        expiryTime = new ExpiryTime(i, i1, System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        calendar.set(Calendar.MINUTE, i1);
                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                        time_picker.setText(time);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });


        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
//                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                    try {
//                        startActivityForResult(builder.build(OrderForm.this), PLACE_PICKER_REQUEST);
//
//                    } catch (Exception e) {
//                        Log.e("location error", e.getMessage());
//                    }
                    Intent intent = new PlacePicker.IntentBuilder()
                            .setGoogleMapApiKey(String.valueOf(R.string.google_api_key))
                            .setMapZoom(19.0f)
                            .setAddressRequired(true)
                            .setPrimaryTextColor(R.color.black)
                            .build(OrderForm.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
            }
        });
        deliverylocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    Intent intent = new PlacePicker.IntentBuilder()
                            .setGoogleMapApiKey(String.valueOf(google_maps_key))
                            .setMapZoom(19.0f)
                            .setAddressRequired(true)
                            .setPrimaryTextColor(R.color.black)
                            .build(OrderForm.this);
                    startActivityForResult(intent, DELIVERY_PICKER_REQUEST);
                }
            }
        });

        View bottomSheet = findViewById(R.id.confirmation_dialog);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        Button btn_proceed = findViewById(R.id.btn_proceed);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setHideable(true);



        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String order_description = description.getText().toString();
                final String order_category = category.getText().toString();
                final String order_min_range = min_int_range.getText().toString();
                final String order_max_range = max_int_range.getText().toString();
                if (expiryDate == null || expiryTime == null || userLocation == null || order_description.equals("") || order_category.equals("None") || order_min_range.equals("") || order_max_range.equals("")) {
                    new AlertDialog.Builder(OrderForm.this)
                            .setMessage(getString(R.string.dialog_save))
                            .setPositiveButton(getString(R.string.dialog_ok), null)
                            .show();
                    return;
                } else if (Integer.parseInt(order_min_range) > Integer.parseInt(order_max_range)) {
                    Toast.makeText(getApplicationContext(), "Min value cannot be more than Max value!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
//                    calc = new DeliveryChargeCalculater(Integer.parseInt(order_max_range));
                    l1 = new LatLng(deliverylocation.Latl, deliverylocation.Lonl);
                    l2 = new LatLng(userLocation.Lat, userLocation.Lon);
                    float deliveryCH = (Deliverychargemethod(l1, l2));

                    BigDecimal bigDecimal = new BigDecimal(String.valueOf(deliveryCH));
                    int intValue = bigDecimal.intValue();
                    System.out.println("Double Number: " + bigDecimal.toPlainString());
                    System.out.println("Integer Part: " + intValue);
                    System.out.println("Decimal Part: " + bigDecimal.subtract(new BigDecimal(intValue)).toPlainString());
                    int integerpart= intValue;
                   float DecimalPart =Float.parseFloat(bigDecimal.subtract(new BigDecimal(intValue)).toPlainString());

                    if (integerpart == 0) {
                        finaldeliverycharge = (DecimalPart) + (float) 1.2;
                    } else {

                        finaldeliverycharge = (float) ((integerpart+0.2)+(DecimalPart*0.2));
                    }
                    String priceo = max_int_range.getText().toString();
                    delivery_charge.setText("TND " + finaldeliverycharge);
                    price.setText(priceo);
                    total_charge.setText(String.valueOf(Integer.parseInt(max_int_range.getText().toString()) + finaldeliverycharge));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userId = user.getUid();

                    root = FirebaseDatabase.getInstance().getReference();
                    addOrder();
                }
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Location location = locationResult.getLastLocation();
            }
        };

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                Toast.makeText(OrderForm.this, data.getParcelableExtra(String.valueOf(addressData.getLatitude()))
                        , Toast.LENGTH_SHORT).show();  // your code
                LatLng latLng = new LatLng(addressData.getLatitude(), addressData.getLongitude());
                List<Address> addresses = addressData.getAddressList();
                clientLocation = latLng;
                String addr = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                if (addr.isEmpty()) {
                    Toast.makeText(this, locationValidation, Toast.LENGTH_SHORT).show();
                } else {
                    LatLng latit = latLng;
                    userLocation = new UserLocation(knownName, addr, addresses.get(0).getPhone(), latit.latitude, latit.longitude, addr, city, state, country, postalCode);
                    user_location.setText(addr);

                }


            }
        }

            if (requestCode == DELIVERY_PICKER_REQUEST) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                    Toast.makeText(OrderForm.this, data.getParcelableExtra(String.valueOf(addressData.getLatitude()))
                            , Toast.LENGTH_SHORT).show();  // your code
                    LatLng latLng = new LatLng(addressData.getLatitude(), addressData.getLongitude());
                    List<Address> addresses = addressData.getAddressList();
                    clientLocation = latLng;
                    String addr = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    if (addr.isEmpty()) {
                        Toast.makeText(this, locationValidation, Toast.LENGTH_SHORT).show();
                    } else {

                        LatLng latit = latLng;
                        deliverylocation = new deliverylocation(knownName, addr, addresses.get(0).getPhone(), latit.latitude, latit.longitude, addr, city, state, country, postalCode);
                        deliverylocationbtn.setText(addr);
                    }
//
                }
            }
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.requestLocationUpdates
                        (getLocationRequest(), mLocationCallback,
                                null /* Looper */);
            }
        }

        void requestLocationPermissions () {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                //Toast.makeText(DelivererViewActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
                setGpsOn();
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case REQUEST_LOCATION_PERMISSION:
                    // If the permission is granted, get the location,
                    // otherwise, show a Toast
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setGpsOn();
                    } else {
                        Toast.makeText(OrderForm.this, "Location permission Denied", Toast.LENGTH_LONG).show();
                    }
                    return;
            }
        }

        private LocationRequest getLocationRequest () {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(60 * 1000);
            locationRequest.setFastestInterval(30 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            return locationRequest;
        }

        void setGpsOn () {
            LocationRequest mLocationRequest = getLocationRequest();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    if (ActivityCompat.checkSelfPermission(OrderForm.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderForm.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(OrderForm.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }


        @Override
        public boolean dispatchTouchEvent (MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    Rect outRect = new Rect();
                    View bottomSheet = findViewById(R.id.confirmation_dialog);
                    bottomSheet.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
            return super.dispatchTouchEvent(event);
        }


        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            int id = item.getItemId();

            if (id == android.R.id.home) {

            }
            return super.onOptionsItemSelected(item);
        }

        private void checkConnection () {
            boolean isConnected = ConnectivityReceiver.isConnected();
            if (!isConnected)
                showSnack(isConnected);
        }

        // Showing the status in Snackbar
        private void showSnack ( boolean isConnected){
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
        public void onNetworkConnectionChanged ( boolean isConnected){
            showSnack(isConnected);
        }

        @Override
        protected void onResume () {
            super.onResume();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
            CheckConnectivityMain.getInstance().setConnectivityListener(OrderForm.this);
        }

        @Override
        protected void onPause () {
            super.onPause();
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        }


        public Boolean addOrder () {
            final String order_description = description.getText().toString();
            final String order_category = category.getText().toString();
            final String order_min_range = min_int_range.getText().toString();
            final String order_max_range = max_int_range.getText().toString();
            flag = 0;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

            DatabaseReference forUserData = root.child("deliveryApp").child("users").child(userId);
            forUserData.keepSynced(true);
            forUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cu = dataSnapshot.getValue(UserDetails.class);
                    client = new Client(dataSnapshot.child("displayName").getValue(String.class), dataSnapshot.child("mobile").getValue(String.class), dataSnapshot.child("email").getValue(String.class), dataSnapshot.child("profile").getValue(String.class), userId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            deliverer = new Deliverer("-", "-", "-", "-", "-");
            root = FirebaseDatabase.getInstance().getReference();
            time = new Time(0, 0, System.currentTimeMillis() / 1000);
            DatabaseReference deliveryApp = root.child("deliveryApp");
            deliveryApp.keepSynced(true);
//        l1 = new LatLng(order.deliverylocation.Latl, order.deliverylocation.Lonl);
//        l2 = new LatLng(order.userLocation.Lat, order.userLocation.Lon);
//        float deliveryCH = (Deliverychargemethod(l1, l2));
//        String doubleAsString = String.valueOf(deliveryCH);
//        int indexOfDecimal = doubleAsString.indexOf(".");
//        System.out.println("Double Number: " + deliveryCH);
//        System.out.println("Integer Part: " + doubleAsString.substring(0, indexOfDecimal));
//        System.out.println("Decimal Part: " + (doubleAsString.substring(indexOfDecimal)));
//        if ((Integer.parseInt(String.valueOf(doubleAsString.substring(indexOfDecimal).charAt(1)))) != 0) {
//            finaldeliverycharge = (Integer.parseInt(doubleAsString.substring(0, indexOfDecimal))) + 1;
//        } else {
//            finaldeliverycharge=(Integer.parseInt(doubleAsString.substring(0, indexOfDecimal)));
//        }
            deliveryApp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild("totalOrders")) {
                        root.child("deliveryApp").child("totalOrders").setValue(1);
                        OrderNumber = 1;
                        order_id = OrderNumber;
                        order = new OrderData(
                                order_category,
                                order_description,
                                order_id,
                                Integer.parseInt(order_max_range),
                                Integer.parseInt(order_min_range),
                                "",
                                userLocation,
                                expiryDate,
                                expiryTime,
                                "PENDING",
                                finaldeliverycharge,
                                acceptedBy,
                                userId,
                                otp,
                                final_price,
                                deliverylocation);
                    /*
//                    orderweb = new OrderWeb(
//                            order.category,
//                            order.description,
//                            order.userId,
//                            order.status,
//                            order.otp,
//                            order.orderId,
//                            order.min_range,
//                            order.max_range,
//                            order.final_price,
//                            order.deliveryCharge,
//                            order.userLocation,
//                            order.expiryDate,
//                            order.expiryTime,
//                            order.acceptedBy,
//                            client,
//                            time,
//                            deliverer);
                    */
                        root.child("deliveryApp").child("orders").child(userId).child(Integer.toString(OrderNumber)).setValue(order);
                        root.child("web").child("orders").child(Integer.toString(OrderNumber)).setValue(orderweb);
                    } else {
                        OrderNumber = dataSnapshot.child("totalOrders").getValue(Integer.class);
                        OrderNumber++;
                        order_id = OrderNumber;
                        order = new OrderData(order_category,
                                order_description,
                                order_id,
                                Integer.parseInt(order_max_range),
                                Integer.parseInt(order_min_range),
                                "",
                                userLocation,
                                expiryDate,
                                expiryTime,
                                "PENDING",
                                finaldeliverycharge,
                                acceptedBy,
                                userId,
                                otp,
                                final_price, deliverylocation);

//                    orderweb = new OrderWeb(
//                            order.category,
//                            order.description,
//                            order.userId,
//                            order.status,
//                            order.otp,
//                            order.orderId,
//                            order.min_range,
//                            order.max_range,
//                            order.final_price,
//                            order.deliveryCharge,
//                            order.userLocation,
//                            order.expiryDate,
//                            order.expiryTime,
//                            order.acceptedBy,
//                            client,
//                            time,
//                            deliverer);
                        root.child("deliveryApp").child("totalOrders").setValue(OrderNumber);
                        root.child("deliveryApp").child("orders").child(userId).child(Integer.toString(OrderNumber)).setValue(order);
                        root.child("web").child("totalOrders").setValue(OrderNumber);

                        root.child("web").child("orders").child(Integer.toString(OrderNumber)).setValue(orderweb);
                    }
                    UserViewActivity.adapter.insert(0, order);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            finish();
            Toast.makeText(getApplicationContext(), " Successful ", Toast.LENGTH_SHORT).show();
            return true;
//    Log.d("RESPONSE",inResponse.toString());

        }

        float Deliverychargemethod (LatLng latLon1, LatLng latLon2){
            if (latLon1 == null || latLon2 == null)
                return -1;
            float[] result = new float[1];
            Location.distanceBetween(latLon1.latitude, latLon1.longitude,
                    latLon2.latitude, latLon2.longitude, result);


            return (float) ((result[0]) * 0.0012);

        }

        private void getcurrentUser (String uid){

        }
    }