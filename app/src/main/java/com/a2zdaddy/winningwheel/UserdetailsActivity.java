package com.a2zdaddy.winningwheel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class UserdetailsActivity extends AppCompatActivity {


    EditText name, emal, reffercode;
    Button bttn;
    String promocode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);
        name = (EditText) findViewById(R.id.name1);
        emal = (EditText) findViewById(R.id.email1);
        reffercode = (EditText) findViewById(R.id.reffercode);
        bttn = (Button) findViewById(R.id.save1);
        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               if (name.getText().toString().equals("") || emal.getText().toString().equals(""))
               {

                   Toast.makeText(UserdetailsActivity.this, "Please Provide Name and Mobile Number", Toast.LENGTH_LONG).show();



               }else {

                   final ProgressDialog progressDialog = new ProgressDialog(UserdetailsActivity.this);
                   progressDialog.setMessage("Please wait..");
                   progressDialog.getActionBar();
                   progressDialog.setCancelable(false);
                   progressDialog.show();

                   FirebaseAuth mAuth = FirebaseAuth.getInstance();


                   if (mAuth.getCurrentUser() != null) {

                       String userid = mAuth.getCurrentUser().getUid();
                       String emailid = mAuth.getCurrentUser().getEmail();
                       int index = emailid.indexOf("@");
                       String resultpromo = emailid.substring(0, index);
                       resultpromo = resultpromo.replaceAll("[.]", "");
                       DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference(userid);
                       DatabaseReference createpromo = FirebaseDatabase.getInstance().getReference("reffercode");
                       String username = name.getText().toString();
                       String mobileno = emal.getText().toString();
                       String reffered = reffercode.getText().toString();
                       int walletcoin = 2000;

                       promocode = resultpromo;

                       Map<String, String> newPost = new HashMap<String, String>();

                       newPost.put("name", username);
                       newPost.put("mobileno", mobileno);
                       newPost.put("email", emailid);
                       newPost.put("reffered", reffered);
                       newPost.put("walletcoin", String.valueOf(walletcoin));
                       newPost.put("refferal", "notcredited");
                       newPost.put("promocode",promocode);
                       newPost.put("withdrawstat","norequest");
                       newPost.put("userid",userid);
                       current_user_db.setValue(newPost);
                       createpromo.child(promocode).setValue(userid);
                       progressDialog.cancel();
                       checkreffercode();
                       expert();

                   } else {
                       Toast.makeText(UserdetailsActivity.this, "Please Login Again", Toast.LENGTH_LONG).show();


                   }
               }
            }
        });


    }

    public void expert(){

        Intent intent=new Intent(UserdetailsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }




    public void checkreffercode() {

        final String refferalcode = reffercode.getText().toString();
        if (refferalcode.equals("") || refferalcode.equals(promocode)) {

            Toast.makeText(UserdetailsActivity.this, "Referral code is empty or not valid", Toast.LENGTH_LONG).show();


        } else {
            final DatabaseReference reffercoderef = FirebaseDatabase.getInstance().getReference().child("reffercode");
            final DatabaseReference agentwallet = FirebaseDatabase.getInstance().getReference();

            final DatabaseReference userwalletstat = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("refferal");
            final DatabaseReference userwallet = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walletcoin");

            userwalletstat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String statous = dataSnapshot.getValue(String.class);
                    if (statous == null) {

                        Toast.makeText(UserdetailsActivity.this, "data is null in Refferal", Toast.LENGTH_LONG).show();


                    } else if (statous.equals("notcredited")) {
                        //here check all the feild....
                        reffercoderef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(refferalcode).exists()) {
                                    final String Uid = dataSnapshot.child(refferalcode).getValue(String.class);
                                    userwallet.setValue(String.valueOf(40000));
                                    agentwallet.child(Uid).child("walletcoin").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {


                                            String agentbalance = dataSnapshot.getValue(String.class);
                                            Integer agentbalinint = Integer.parseInt(agentbalance);
                                            Integer updatedbalu = (agentbalinint + 40000);
                                            agentwallet.child(Uid).child("walletcoin").setValue(String.valueOf(updatedbalu));
                                            userwalletstat.setValue("credited");

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(UserdetailsActivity.this, "error on updating agent balance", Toast.LENGTH_LONG).show();


                                        }
                                    });


                                } else {

                                    Toast.makeText(UserdetailsActivity.this, "Refferal code not found", Toast.LENGTH_LONG).show();


                                }

                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(UserdetailsActivity.this, "Database error on checking refferal code", Toast.LENGTH_LONG).show();


                            }
                        });


                        //last

                    } else {


                        Toast.makeText(UserdetailsActivity.this, "Both wallet has been credited", Toast.LENGTH_LONG).show();


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(UserdetailsActivity.this, "Database error on checking account status", Toast.LENGTH_LONG).show();


                }
            });


        }


    }



}






