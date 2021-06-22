package com.xdev.deliverytn.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xdev.deliverytn.R;
import com.xdev.deliverytn.check_connectivity.CheckConnectivityMain;
import com.xdev.deliverytn.check_connectivity.ConnectivityReceiver;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.xdev.deliverytn.R.string.coodConnectedTOinternet;
import static com.xdev.deliverytn.R.string.entremailaddress;
import static com.xdev.deliverytn.R.string.entrepass;
import static com.xdev.deliverytn.R.string.faildtologin;
import static com.xdev.deliverytn.R.string.loggedsucc;
import static com.xdev.deliverytn.R.string.pasdinternet;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int RC_SIGN_IN = 101;
    private static final String TAG = "FacebookLogin";
    private static final int RC_SIGN_IN_FB = 12345;
    public static GoogleApiClient mGoogleApiClient;
    public static String user_email, user_name, userId;
    AnimationDrawable animationDrawable;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    CircularProgressButton btnLogin;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private DatabaseReference root, database_users;
    private View fbLogin;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        animation();
        checkConnection();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, VerifyEmailScreen.class);
                startActivity(intent);
            }
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        Button btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        Button btnReset = findViewById(R.id.btn_reset_password);
        signInButton = findViewById(R.id.mygooglebutton);
        //////////////////////////////////////////////////
        //facebook login

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById(R.id.fblogin_button);

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }


            @Override
            public void onError(FacebookException error) {

            }

        });


//----------------
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectivityReceiver.isConnected()) {
                    showSnack(false);
                } else {
                    String email = inputEmail.getText().toString();
                    final String password = inputPassword.getText().toString();


                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), entremailaddress, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), entrepass, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnLogin.startAnimation();
                    //authenticate com.thedeliveryapp.thedeliveryapp.user.user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                            btnLogin.revertAnimation();

                                        } else {
                                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                            btnLogin.revertAnimation();

                                        }
                                    } else {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, VerifyEmailScreen.class);
                                            startActivity(intent);
                                        }
                                        finish();
                                        btnLogin.revertAnimation();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            userId = user.getUid();
                            root = FirebaseDatabase.getInstance().getReference();
                            database_users = root.child("deliveryApp").child("users");
                            database_users.keepSynced(true);
                            database_users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild(userId)) {
                                        Intent intent = new Intent(LoginActivity.this, OtherSignup.class);
                                        intent.putExtra("username", user_name);
                                        intent.putExtra("email", user_email);
                                        intent.putExtra("fb", token);
                                        intent.putExtra("currentuser", user);

                                        startActivity(intent);

                                    } else {
                                        btnLogin.revertAnimation();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(LoginActivity.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign-in fails, a message will display to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.\n " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    progressBar.setVisibility(View.VISIBLE);
                    GoogleSignInAccount acct = result.getSignInAccount();
                    // Get account information
                    user_name = acct.getDisplayName();
                    user_email = acct.getEmail();
                }
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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
            message = coodConnectedTOinternet;
            color = Color.WHITE;
        } else {
            message = pasdinternet;
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
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
                                        Intent intent = new Intent(LoginActivity.this, OtherSignup.class);
                                        intent.putExtra("username", user_name);
                                        intent.putExtra("email", user_email);
                                        intent.putExtra("account", account);
                                        intent.putExtra("currentuser", user);

                                        startActivity(intent);

                                    } else {
                                        btnLogin.revertAnimation();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(LoginActivity.this, loggedsucc, Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            if (!ConnectivityReceiver.isConnected()) {
                                showSnack(false);
                            } else {
                                Toast.makeText(LoginActivity.this, faildtologin, Toast.LENGTH_SHORT).show();
                            }
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        }

                        // ...
                    }
                });
    }

    void animation() {
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_login,null);*/
        ConstraintLayout linearLayout = findViewById(R.id.scrollView);
        animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
        CheckConnectivityMain.getInstance().setConnectivityListener(LoginActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}