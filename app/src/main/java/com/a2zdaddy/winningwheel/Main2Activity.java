package com.a2zdaddy.winningwheel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {
    SignInButton googlesigninbutton;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthlistener;
    private final static int RC_SIGN_IN =2;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthlistener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        googlesigninbutton=findViewById(R.id.googlebttn);
        mAuth = FirebaseAuth.getInstance();
        if(!isConnected(Main2Activity.this)){
            Toast.makeText(Main2Activity.this,"No internet connection, Please turn on internet to Sign in", Toast.LENGTH_SHORT).show();


        }

        else {
            Toast.makeText(Main2Activity.this,"Welcome", Toast.LENGTH_SHORT);
        }
        googlesigninbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mAuthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    //first
                    final ProgressDialog progressDialog = new ProgressDialog(Main2Activity.this);
                    progressDialog.setMessage("Please wait..");
                    progressDialog.getActionBar();
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(uid)) {
                                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent1);
                                finish();
                                progressDialog.cancel();


                            } else {
                                progressDialog.cancel();
                                Intent intent = new Intent(getApplicationContext(), UserdetailsActivity.class);

                                startActivity(intent);
                                finish();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(Main2Activity.this, "Databse eroor", Toast.LENGTH_LONG).show();

                        }
                    });


                }

                else{



                    Toast.makeText(Main2Activity.this, "Click on Sign in to continue", Toast.LENGTH_LONG).show();




                }


                //last

            }
        };

// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
// Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(),"Something went wrong, try again",Toast.LENGTH_SHORT).show();

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        final ProgressDialog progressDialog = new ProgressDialog(Main2Activity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressDialog.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(Main2Activity.this,"Auth failed, Try again",Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Main2Activity.this, "Successfully Logged in",
                                Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Main2Activity.this, "Please Try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);



            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))

                return true;

            else

                {

                    return false;
            }

        }

        else
            return false;

    }
}
