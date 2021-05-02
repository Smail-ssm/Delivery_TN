package com.xdev.deliverytn.reclamations;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.models.OrderData;
import com.xdev.deliverytn.models.ReclamationObject;
import com.xdev.deliverytn.models.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;

public class createReclamation extends AppCompatActivity {
    String OrdererId;
    String DeliverId;
    String RoomId;
    UserDetails orderer, deliverer;
    OrderData order;
    DatabaseReference root;
    Snackbar snackbar;
    ReclamationObject rec = new ReclamationObject();
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout2;
    private CardView reclamationCard;
    private TextView textView20;
    private TextView textView18;
    private EditText reclamationEditText;
    private Button cancle;
    private Button reclame;
    private CheckBox ordertimeoutReason;
    private CheckBox badorderstatueReason;
    private CheckBox badBehaviour;
    private TextInputLayout textInputLayout8;
    private TextInputEditText otherReason;
    private TextView time;
    private TextView date;
    private CardView cardView;
    private ImageButton showordererdata;
    private TextInputEditText orderercin;
    private TextInputLayout textInputLayout4;
    private TextInputEditText ordererID;
    private TextView textView16;
    private TextInputLayout ordrcin;
    private TextInputLayout ordrid;
    private TextInputLayout ordrnam;
    private TextInputLayout delcin;
    private TextInputLayout deliid;
    private TextInputLayout delname;
    private TextInputLayout textInputLayout3;
    private TextInputEditText orderrName;
    private CardView cardView3;
    private ImageButton showdelivereinfo;
    private TextInputEditText deliverercin;
    private TextInputLayout textInputLayout6;
    private TextInputEditText delivID;
    private TextView textView17;
    private TextInputLayout textInputLayout5;
    private TextInputEditText delivname;
    private FloatingActionButton fab;
    private OrderData Order;
    private CardView orderercard;
    private CardView delivcrd;
    private int reclamnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reclamation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        root = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getTitle());
        OrdererId = i.getStringExtra("OrdererId");
        DeliverId = i.getStringExtra("DeliverId");
        RoomId = i.getStringExtra("RoomId");
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        reclamationCard = findViewById(R.id.reclamationCard);
        textView20 = findViewById(R.id.textView20);
        textView18 = findViewById(R.id.textView18);
        reclamationEditText = findViewById(R.id.reclamationEditText);
        cancle = findViewById(R.id.cancle);
        reclame = findViewById(R.id.reclame);
        ordertimeoutReason = findViewById(R.id.ordertimeoutReason);
        badorderstatueReason = findViewById(R.id.badorderstatueReason);
        badBehaviour = findViewById(R.id.badBehaviour);
        textInputLayout8 = findViewById(R.id.textInputLayout8);
        otherReason = findViewById(R.id.otherReason);
        time = findViewById(R.id.time);
        cardView = findViewById(R.id.orderercard);
        showordererdata = findViewById(R.id.showordererdata);
        orderercin = findViewById(R.id.orderercin);
        textInputLayout4 = findViewById(R.id.ordrid);
        ordererID = findViewById(R.id.ordererID);
        textView16 = findViewById(R.id.textView16);
        textInputLayout3 = findViewById(R.id.ordrnam);
        orderrName = findViewById(R.id.orderrName);
        cardView3 = findViewById(R.id.delivcrd);
        showdelivereinfo = findViewById(R.id.showdelivereinfo);
        deliverercin = findViewById(R.id.deliverercin);
        textInputLayout6 = findViewById(R.id.deliid);
        delivID = findViewById(R.id.delivID);
        textView17 = findViewById(R.id.textView17);
        textInputLayout5 = findViewById(R.id.delname);
        delivname = findViewById(R.id.delivname);
        fab = findViewById(R.id.fab);
        ordrcin = findViewById(R.id.ordrcin);
        ordrid = findViewById(R.id.ordrid);
        ordrnam = findViewById(R.id.ordrnam);
        delcin = findViewById(R.id.delcin);
        deliid = findViewById(R.id.deliid);
        delname = findViewById(R.id.delname);
        orderercard = findViewById(R.id.orderercard);
        delivcrd = findViewById(R.id.delivcrd);
        Calendar c = Calendar.getInstance();
        String timeSt = String.valueOf(System.currentTimeMillis());
        System.out.println("Current dateTime => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
        time.setText(df.format(c.getTime()));
        Intent intent = getIntent();
        if (!(intent.hasExtra("reclamationID"))) {
            root.child("deliveryApp").child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userdata : dataSnapshot.getChildren()) {
                        for (DataSnapshot orderdata : userdata.getChildren()) {
                            OrderData Order = orderdata.getValue(OrderData.class);
                            if (String.valueOf(Order.orderId).equals(RoomId)) {
                                order = Order;

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            root.child("deliveryApp").child("users").child(OrdererId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    orderer = dataSnapshot.getValue(UserDetails.class);
                    orderrName.setText(orderer.getLast() + " " + orderer.getFirst());
                    orderercin.setText(orderer.getCin());
                    ordererID.setText(orderer.getUid());

                    orderrName.setEnabled(false);
                    orderercin.setEnabled(false);
                    ordererID.setEnabled(false);

                    rec.setoCin(orderercin.getText().toString());
                    rec.setoID(ordererID.getText().toString());
                    rec.setoName(orderrName.getText().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            root.child("deliveryApp").child("users").child(DeliverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    deliverer = dataSnapshot.getValue(UserDetails.class);
                    delivname.setText(deliverer.getLast() + " " + deliverer.getFirst());
                    deliverercin.setText(deliverer.getCin());
                    delivID.setText(deliverer.getUid());

                    delivname.setEnabled(false);
                    deliverercin.setEnabled(false);
                    delivID.setEnabled(false);

                    rec.setdCin(deliverercin.getText().toString());
                    rec.setdId(delivID.getText().toString());
                    rec.setdName(delivname.getText().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else {
            int recid = intent.getIntExtra("reclamationID", 0);
            root.child("deliveryApp")
                    .child("reclamations")
                    .child(String.valueOf(recid))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReclamationObject recfire = snapshot.getValue(ReclamationObject.class);
                            delivname.setText(recfire.getdName());
                            deliverercin.setText(recfire.getdCin());
                            delivID.setText(recfire.getdId());
                            delivname.setEnabled(false);
                            deliverercin.setEnabled(false);
                            delivID.setEnabled(false);

                            orderrName.setText(recfire.getoName());
                            orderercin.setText(recfire.getoCin());
                            ordererID.setText(recfire.getoID());

                            orderrName.setEnabled(false);
                            orderercin.setEnabled(false);
                            ordererID.setEnabled(false);
                            reclamationEditText.setText(recfire.getReclamationText());
                            reclamationEditText.setTextColor(BLACK);
                            reclamationEditText.setEnabled(false);
                            if (recfire.getReason().contains("order timed out, "))
                                ordertimeoutReason.setChecked(true);
                            ordertimeoutReason.setEnabled(false);
                            ordertimeoutReason.setTextColor(BLUE);
                            if (recfire.getReason().contains("bad behaviour, "))
                                badBehaviour.setChecked(true);
                            badBehaviour.setEnabled(false);
                            badBehaviour.setTextColor(BLUE);
                            if (recfire.getReason().contains("bad order statue, "))
                                badorderstatueReason.setChecked(true);
                            badorderstatueReason.setEnabled(false);
                            badorderstatueReason.setTextColor(BLUE);
                            String currentString = recfire.getReason();
                            String[] separated = currentString.split(",");
                            otherReason.setText(separated[separated.length - 1]);
                            otherReason.setTextColor(BLACK);
                            otherReason.setEnabled(false);

                            reclame.setVisibility(View.INVISIBLE);
                            cancle.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


        showordererdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderercin.isShown()) {
                    orderercard.setCardBackgroundColor(RED);
                    ordrcin.setVisibility(View.GONE);
                    ordrid.setVisibility(View.GONE);
                    ordrnam.setVisibility(View.GONE);
                } else {
                    orderercard.setCardBackgroundColor(TRANSPARENT);

                    ordrnam.setVisibility(View.VISIBLE);
                    ordrid.setVisibility(View.VISIBLE);
                    ordrcin.setVisibility(View.VISIBLE);
                }
            }
        });
        showdelivereinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deliverercin.isShown()) {
                    delivcrd.setCardBackgroundColor(RED);

                    delcin.setVisibility(View.GONE);
                    deliid.setVisibility(View.GONE);
                    delname.setVisibility(View.GONE);
                } else {
                    delivcrd.setCardBackgroundColor(TRANSPARENT);

                    delcin.setVisibility(View.VISIBLE);
                    deliid.setVisibility(View.VISIBLE);
                    delname.setVisibility(View.VISIBLE);
                }
            }
        });
        reclame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rec.setTimestamp(timeSt);
                String reason = "";
                if (ordertimeoutReason.isChecked()) reason = reason + "order timed out, ";
                if (badBehaviour.isChecked()) reason = reason + "bad behaviour,";
                if (badorderstatueReason.isChecked()) reason = reason + "bad order statue,";
                reason = reason + otherReason.getEditableText().toString() + ".";
                if (reason.equals("")) {
                    int color;
                    color = Color.WHITE;
                    snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.please_set_the_reason_of_reclamation, Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(color);
                    snackbar.show();
                    return;
                } else {
                    rec.setReason(reason);
                }
                rec.setReclamationText(reclamationEditText.getEditableText().toString());
                rec.setSenderId(FirebaseAuth.getInstance().getUid());
                DatabaseReference recRef = root.child("deliveryApp");
                recRef.keepSynced(true);
                recRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("totalReclamation")) {
                            root.child("deliveryApp").child("totalReclamation").setValue(1);
                            reclamnumber = 1;
                            rec.setRecId(reclamnumber);
                            root.child("deliveryApp").child("reclamations").child(String.valueOf(reclamnumber)).setValue(rec);
                        } else {
                            reclamnumber = dataSnapshot.child("totalReclamation").getValue(Integer.class);
                            reclamnumber++;
                            rec.setRecId(reclamnumber);

                            root.child("deliveryApp").child("totalReclamation").setValue(reclamnumber);
                            root.child("deliveryApp").child("reclamations").child(Integer.toString(reclamnumber)).setValue(rec);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

}
