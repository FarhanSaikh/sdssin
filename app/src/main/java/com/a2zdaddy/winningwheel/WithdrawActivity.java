package com.a2zdaddy.winningwheel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class WithdrawActivity extends AppCompatActivity {
    TextView coin,ruppes,paymentmode,paymentstatus,payno,contact;
    EditText mobileno;
    Button submitbtn;
    Double mBalinr;
    Spinner spinner;
    String spinnervalue,stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        coin=findViewById(R.id.coin);
        ruppes=findViewById(R.id.rupees);
        submitbtn=findViewById(R.id.submitrequest);
        spinner=findViewById(R.id.paymentspin);
        mobileno=findViewById(R.id.phoneno);
        paymentmode=findViewById(R.id.paymentmode);
        paymentstatus=findViewById(R.id.paymentstat);
        payno=findViewById(R.id.paymentno);
        contact=findViewById(R.id.contact);
        paymentstatc();
        updatedatas();
//        loadAd();

        //submit buton request

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendfeedback();
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnervalue=spinner.getSelectedItem().toString();


                if (mobileno.getText().toString().equals("")){
                    Toast.makeText(WithdrawActivity.this, "Enter number to get paid", Toast.LENGTH_SHORT).show();
                }

                else if (mBalinr<30){

                    Toast.makeText(WithdrawActivity.this, "Minimum  balance $30 to redeem", Toast.LENGTH_SHORT).show();
                }



                else if (mBalinr>30 ){
                    withdrawbal();
                    Toast.makeText(WithdrawActivity.this, "Request Submitted, takes upto 5 days", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void updatedatas() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference(mUid);
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String wallcoin = dataSnapshot.child("walletcoin").getValue(String.class);
                coin.setText(wallcoin);
                Double walleint = Double.parseDouble(wallcoin);
                mBalinr = ((walleint / 20000));
                ruppes.setText(String.valueOf(mBalinr));
                progressDialog.cancel();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WithdrawActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void withdrawbal(){
        final ProgressDialog progressDialog = new ProgressDialog(WithdrawActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference pDb=FirebaseDatabase.getInstance().getReference("paymentreq").child(mUid);

        final DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference(mUid);
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String wallcoin = dataSnapshot.child("walletcoin").getValue(String.class);
                //Double walleint = Double.parseDouble(wallcoin);
                //mBalinr = ((walleint / 1000));
                //Double updatedcoin=(mBalinr-30.0)*1000;
                //mUserDb.child("walletcoin").setValue(String.valueOf(updatedcoin.intValue()));
                pDb.child("withdrawstat").setValue("Request is Pending");
                pDb.child("paymentmethod").setValue(spinnervalue);
                pDb.child("paymentno").setValue(mobileno.getText().toString());
                pDb.child("name").setValue(String.valueOf(mBalinr));
                pDb.child("userid").setValue(mUid);
                mUserDb.child("withdrawstat").setValue("Request is PENDING");
                mUserDb.child("paymentmethod").setValue(spinnervalue);
                mUserDb.child("paymentno").setValue(mobileno.getText().toString());
                progressDialog.cancel();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WithdrawActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void paymentstatc(){

        String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference(mUid);
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("paymentno").exists()){
                    stat=dataSnapshot.child("withdrawstat").getValue(String.class);
                    String number=dataSnapshot.child("paymentno").getValue(String.class);
                    String payment=dataSnapshot.child("paymentmethod").getValue(String.class);
                    paymentmode.setText(payment);
                    paymentstatus.setText(stat);
                    payno.setText(number);


                }
                else {

                    paymentstatus.setText("No Request yet");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WithdrawActivity.this, "Error loading", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void sendfeedback(){
        Intent mIntent = new Intent(Intent.ACTION_SENDTO);
        mIntent.setData(Uri.parse("mailto:"));
        mIntent.putExtra(Intent.EXTRA_EMAIL  , new String[] {"techdev@sds.consulting@gmail.com"});
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "I am facing some problem in Winning wheel");
        startActivity(Intent.createChooser(mIntent, "Send Email Using..."));
    }

}

