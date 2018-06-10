package com.a2zdaddy.winningwheel;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JackpotFragment extends AppCompatActivity {
TextView nameofwinner,timetext;

InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jackpot_fragment);
        nameofwinner=findViewById(R.id.winnername);
        timetext=findViewById(R.id.date);

updatedatas();

    }

    public void updatedatas() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("jackpotwinner");
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 String winname = dataSnapshot.child("name").getValue(String.class);
                String time = dataSnapshot.child("time").getValue(String.class);

                nameofwinner.setText(winname);
                 timetext.setText(time);
                     progressDialog.cancel();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(JackpotFragment.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

