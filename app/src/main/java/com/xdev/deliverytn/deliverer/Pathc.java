package com.xdev.deliverytn.deliverer;//package com.xdev.deliverytn.deliverer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xdev.deliverytn.R;

import org.w3c.dom.Document;

public class Pathc extends AppCompatActivity {

    GoogleMap map = null;
    Googledir gd;
    SupportMapFragment mapFragment;

    LatLng start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        Intent i = getIntent();
//
//        start = new LatLng(13.744246499553903, 100.53428772836924);
//        end = new LatLng(13.751279688694071, 100.54316081106663);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

                gd = new Googledir(Pathc.this);
                gd.setOnDirectionResponseListener(new Googledir.OnDirectionResponseListener() {

                    public void onResponse(String status, Document doc, Googledir gd) {
                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                        gd.animateDirection(map, gd.getDirection(doc), Googledir.SPEED_FAST
                                , true, true, true, false, null, false, true, new PolylineOptions().width(8).color(Color.RED));

                        map.addMarker(new MarkerOptions().position(start).icon(bitmapDescriptorFromVector(Pathc.this,R.drawable.marker_a)));

                        map.addMarker(new MarkerOptions().position(end).icon(bitmapDescriptorFromVector(Pathc.this,R.drawable.marker_b)));

                        String TotalDistance = gd.getTotalDistanceText(doc);
                        String TotalDuration = gd.getTotalDurationText(doc);
                    }
                });

                gd.request(start, end, Googledir.MODE_DRIVING);
            }
        });


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}