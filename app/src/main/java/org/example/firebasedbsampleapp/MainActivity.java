package org.example.firebasedbsampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private CoordinatorLayout mCoordinatorLayout;
    private final String API_KEY = "<YOUR_API_KEY>";

    private FirebaseAuth mAuth;

    private View mSigninButton;
    private View mSignoutButton;

    private static final int RC_SIGN_IN = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        findViewById(R.id.jump_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.edit_text);
                if (!TextUtils.isEmpty(text.getText())) {
                    startActivity(
                            MessageActivity.createIntent(MainActivity.this,
                                    text.getText().toString(),
                                    ((CompoundButton) findViewById(R.id.auth_switch)).isChecked()
                            ));

                }
            }
        });

        mSigninButton = findViewById(R.id.sign_in_button);
        mSignoutButton = findViewById(R.id.sign_out_button);
        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                updateUi();
                            }
                        });
            }
        });


        setupGooglePlayService();
        mAuth = FirebaseAuth.getInstance();

    }

    void setupGooglePlayService() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(API_KEY)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Snackbar.make(mCoordinatorLayout, "Login Success", Snackbar.LENGTH_SHORT).show();
            EditText text = (EditText) findViewById(R.id.edit_text);
            text.setText(user.getUid());
        } else {
        }
        updateUi();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(mCoordinatorLayout, "login Success", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mCoordinatorLayout, "login error", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void updateUi() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mSigninButton.setVisibility(View.GONE);
            mSignoutButton.setVisibility(View.VISIBLE);
        } else {
            mSigninButton.setVisibility(View.VISIBLE);
            mSignoutButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null)
            mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Snackbar.make(mCoordinatorLayout, "login error", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


}
