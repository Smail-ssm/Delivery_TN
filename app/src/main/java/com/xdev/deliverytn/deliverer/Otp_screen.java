package com.xdev.deliverytn.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.models.OrderData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Otp_screen extends AppCompatActivity {

    private static DatabaseReference SOCEARNINGS;
    private EditText f1, f2, f3, f4, f5;
    private String otp;
    private String userId;
    private DatabaseReference root, wallet_ref, deliverer_ref;
    private OrderData myOrder;
    private int final_price_int, balance;
    private DatabaseReference topay_ref;
    private DatabaseReference socwallet_ref;
    private String OrdererId;
    private String DeliverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);
        Intent intent = getIntent();
        OrdererId = intent.getStringExtra("OrdererId");
        DeliverId = intent.getStringExtra("DeliverId");
        String final_price = intent.getStringExtra("Final Price");
        final_price_int = Integer.parseInt(final_price);
        otp = intent.getStringExtra("OTP");
        myOrder = intent.getParcelableExtra("MyOrder");
        root = FirebaseDatabase.getInstance().getReference();
        f1 = findViewById(R.id.f1);
        f2 = findViewById(R.id.f2);
        f3 = findViewById(R.id.f3);
        f4 = findViewById(R.id.f4);
        f5 = findViewById(R.id.f5);
        OrdererId = myOrder.userId;
        DeliverId = myOrder.acceptedBy.delivererID;
        Button btn_mark_delivered = findViewById(R.id.btn_mark_delivered);
        Button payment = findViewById(R.id.paymentmethod);

        f1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    f2.requestFocus();
                }
            }
        });
        f2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    f3.requestFocus();
                } else {
                    f1.requestFocus();
                }
            }
        });
        f3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    f4.requestFocus();
                } else {
                    f2.requestFocus();
                }
            }
        });
        f4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    f5.requestFocus();
                } else {
                    f3.requestFocus();
                }
            }
        });
        f5.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    f4.requestFocus();
                }
            }
        });

        payment.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Otp_screen.this);
            builder1.setMessage(R.string.pp);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "E-dinar smart",
                    (dialog, id) -> {
                        root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)).child("paimentMethod").setValue("Cash");
                        Toast.makeText(Otp_screen.this, "Edinar paiment saved ", Toast.LENGTH_SHORT).show();
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Otp_screen.this);
                        final androidx.appcompat.app.AlertDialog dialog1 = builder.create();
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.imgdialog, null);
                        dialog1.setView(dialogLayout);
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog1.setOnShowListener(dialog23 -> {

                            androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(Otp_screen.this);
                            final androidx.appcompat.app.AlertDialog dialog11 = builder2.create();
                            LayoutInflater inflater1 = getLayoutInflater();
                            View dialogLayout1 = inflater1.inflate(R.layout.imgdialog, null);
                            dialog11.setView(dialogLayout1);
                            dialog11.setOnShowListener(d -> {
                                ProgressBar p = dialog11.findViewById(R.id.progressimage);

                                int SDK_INT = Build.VERSION.SDK_INT;
                                if (SDK_INT > 8) {
                                    URL u = null;
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                            .permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                    ImageView image = dialog11.findViewById(R.id.goProDialogImage);
                                    try {
                                        u = new URL("https://firebasestorage.googleapis.com/v0/b/deliverytn-423ca.appspot.com/o/paimentQRcode%2Fqr.png?alt=media&token=64463ca4-b46b-4c71-a8b3-ac34cd29a026");
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    InputStream content = null;
                                    try {
                                        content = (InputStream) u.getContent();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Drawable d1 = Drawable.createFromStream(content, "src");
                                    image.setImageDrawable(d1);
                                    if (content != null) {
                                        p.setVisibility(View.GONE);
                                    }

                                }
                            });
                            dialog11.setCancelable(true);

                            dialog11.show();


                        });
                        builder.setPositiveButton(R.string.ok, (dialog22, which) -> dialog22.cancel()).

                                setNegativeButton(R.string.cancel, (dialog2, which) -> {
                                });
                        dialog1.show();


                    });

            builder1.setNegativeButton(
                    "Cash",
                    (dialog, id) -> {
                        Toast.makeText(Otp_screen.this, "Cash payment saved", Toast.LENGTH_SHORT).show();
                        root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)).child("paimentMethod").setValue("Cash");
                    });
            builder1.setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();

        });



        btn_mark_delivered.setOnClickListener(v -> {
            if (!ConnectivityReceiver.isConnected()) {
                showSnack(false);
            } else {
                String secret = f1.getText().toString() + f2.getText().toString() + f3.getText().toString() + f4.getText().toString() + f5.getText().toString();
                if (secret.equals(otp)) {
                    root.child("deliveryApp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild("SOCEARNINGS")) {
                                root.child("deliveryApp").child("SOCEARNINGS").setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    root.child("deliveryApp").child("orders").child(myOrder.userId).child(Integer.toString(myOrder.orderId)).child("status").setValue("FINISHED");

                    wallet_ref = root.child("deliveryApp").child("users").child(myOrder.userId).child("wallet");
                    wallet_ref.keepSynced(true);
                    wallet_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer wal_bal = dataSnapshot.getValue(Integer.class);
                            balance = wal_bal;
                            wallet_ref.setValue(balance + (myOrder.max_range - final_price_int));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    topay_ref = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID).child("topay"); //NON-NLS //NON-NLS
                    topay_ref.keepSynced(true);
                    topay_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Integer wal_bal = dataSnapshot.getValue(Integer.class);
                                balance = wal_bal;
                                topay_ref.setValue(balance + ((myOrder.deliveryCharge * 30) / 100));
                            }else{
                                topay_ref.setValue(0);

                            }


                        }

                        //todo fix null when getting to pay of the deliverer
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    SOCEARNINGS = root.child("deliveryApp").child("SOCEARNINGS");
                    SOCEARNINGS.keepSynced(true);
                    SOCEARNINGS.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer wal_bal = dataSnapshot.getValue(Integer.class);
                            SOCEARNINGS.setValue(
                                    wal_bal +
                                            ((myOrder.deliveryCharge * 30) / 100));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userId = user.getUid();

                    deliverer_ref = root.child("deliveryApp").child("users").child(myOrder.acceptedBy.delivererID).child("wallet");
                    deliverer_ref.keepSynced(true);

                    deliverer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer wal_bal = dataSnapshot.getValue(Integer.class);
                            deliverer_ref.setValue(wal_bal + myOrder.deliveryCharge);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(Otp_screen.this, "Delivery Finished!!", Toast.LENGTH_LONG).show();
                    setUpDeliveredNotif(myOrder);
                    Intent intent1 = new Intent(Otp_screen.this, DelivererViewActivity.class);
                    startActivity(intent1);
                    finish();

                } else {
                    Toast.makeText(Otp_screen.this, "Wrong OTP! Enter correct OTP", Toast.LENGTH_LONG).show();
                }
            }
        });


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

    public void setUpDeliveredNotif(final OrderData order) {
        String userId = order.userId;
        root.child("deliveryApp").child("users").child(userId).child("playerId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String player_id = dataSnapshot.getValue(String.class);
                //TOAST
                try {
                    JSONObject notificationContent = new JSONObject("{'contents': {'en': '" + order.description + "'}," +
                            "'include_player_ids': ['" + player_id + "'], " +
                            "'headings': {'en': 'Your Order got delivered\nOrder Id : " + order.orderId + " '} " +
                            "}");
                    JSONObject order = new JSONObject();
                    order.put("userId", myOrder.userId);
                    order.put("orderId", myOrder.orderId);
                    notificationContent.putOpt("data", order);
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
