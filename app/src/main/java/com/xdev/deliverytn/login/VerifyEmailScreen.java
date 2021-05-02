package com.xdev.deliverytn.login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.xdev.deliverytn.R;


public class VerifyEmailScreen extends AppCompatActivity {

    TextView email_to_verify;
    Button btn_resend_email, btngmail, btn_refresh, btn_logout;
    AnimationDrawable animationDrawable;
    FirebaseUser user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email_screen);
        //  animation();
        email_to_verify = findViewById(R.id.email_to_verify);
        btn_resend_email = findViewById(R.id.btn_resend_email);
        btn_refresh = findViewById(R.id.btn_refresh);
        btngmail = findViewById(R.id.btngmail);
        btn_logout = findViewById(R.id.btn_logout);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email_to_verify.setText(user.getEmail());
        } else email_to_verify.setText(getIntent().getStringExtra("email"));

        btngmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);
            }
        });
        btn_resend_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_resend_email.setEnabled(false);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseAuth.getInstance().getCurrentUser()
                            .sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    btn_resend_email.setEnabled(true);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.emailsentto) + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.faildtosend, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().reload();
                if (user != null) {
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reload()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(VerifyEmailScreen.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.completverification, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyEmailScreen.this, "logout succefull", Toast.LENGTH_LONG).show();
                signOut();
            }
        });

    }

    public void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        sendToLogin();
    }

    void animation() {
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_login,null);*/
        RelativeLayout verifyEmail = findViewById(R.id.verifyEmail);
        animationDrawable = (AnimationDrawable) verifyEmail.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }

    public void sendToLogin() {
        Intent loginIntent = new Intent(VerifyEmailScreen.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
