package com.xdev.deliverytn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;
import com.xdev.deliverytn.login.LoginActivity;


public class SplashSceen extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private final int SPLASH_DISPLAY_LENGTH = 1000; //spla


    // Declare a button
    Button visitbtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sceen);
        ImageView imageView = findViewById(R.id.imageView);
        ProgressBar pb = findViewById(R.id.progressbar);

//        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
//        imageView.startAnimation(animation);

        pb.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(0, pb.getMax());
                animator.setDuration(SPLASH_DISPLAY_LENGTH);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        pb.setProgress((Integer) animation.getAnimatedValue());
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!ConnectivityReceiver.isConnected()) {
                            showSnack(false);

                        }
                        new Handler().postDelayed(new Runnable() {
                                                      @Override
                                                      public void run() {

                                                          Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                          startActivity(i);
                                                          finish();


                                                      }
                                                  },
                                SPLASH_DISPLAY_LENGTH);
                        // start your activity here
                    }
                });
                animator.start();


            }

        }, SPLASH_DISPLAY_LENGTH);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}