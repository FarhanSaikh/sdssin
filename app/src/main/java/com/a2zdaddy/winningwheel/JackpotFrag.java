package com.a2zdaddy.winningwheel;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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



public class JackpotFrag extends Fragment{
    Dialog dialog;
                  EditText editText;
                  Button playbutton,resultbutton,succesbtn;
                  Integer coinstoinvest =0;
                  InterstitialAd interstitialAd;
    public JackpotFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_jackpot, container, false);

            playbutton=view.findViewById(R.id.playjackpot);
            resultbutton=view.findViewById(R.id.jackpotresult);

             succesbtn=view.findViewById(R.id.succestext);
             updatejackpot();




            playbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playjackpot();


                }
            });

        resultbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             showresult();
            }
        });


        return view;
    }

    public void playjackpot(){

        //show dialog for investing coin
        //enter coin to invest

        //deduct this coin from account and participate in jackpot

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.jackpotdialog);


         editText=dialog.findViewById(R.id.cointoinvest);
        Button participate=dialog.findViewById(R.id.participate);

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().equals("") ) {
                    editText.setError("Enter Coins");
                }
                else if (Integer.parseInt(editText.getText().toString())<2000){
                    editText.setError("Coins must be greater than 2000");


                }
                else {
                    coinstoinvest=Integer.parseInt(editText.getText().toString());

                    deductcoin();

                }
            }
        });
        dialog.show();

        dialog.setCancelable(true);



    }


    public void showresult(){
        Intent intent=new Intent(getContext(),JackpotFragment.class);
        startActivity(intent);

        }
     public void deductcoin(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         final DatabaseReference mUserjackpot = FirebaseDatabase.getInstance().getReference().child("jackpotinvester").child(mUid);

         final DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference(mUid);
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String wallcoin = dataSnapshot.child("walletcoin").getValue(String.class);
                Integer coinsinint = Integer.parseInt(wallcoin);

                //deducting coin
                if (coinsinint>coinstoinvest) {

                    Integer remainingcoin = (coinsinint - coinstoinvest);
                    mUserDb.child("walletcoin").setValue(remainingcoin.toString());
                    mUserjackpot.setValue(coinstoinvest.toString());
                    Toast.makeText(getContext(), " Your "+coinstoinvest +"  Coins has been deducted from your Wallet", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else
                    {
                        editText.setError(" You Don't have enough coins in you wallet");


                    }


                //

                progressDialog.cancel();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), " No Internet", Toast.LENGTH_SHORT).show();
            }
        });




    }


    public void updatejackpot()
    {
        final String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference jackpotDb = FirebaseDatabase.getInstance().getReference("jackpotinvester").child(mUid);
        jackpotDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String coins=dataSnapshot.getValue(String.class);
                    succesbtn.setText("Congratulations, You have Invested "+coins+" Coins in Jackpot");
                    succesbtn.setVisibility(View.VISIBLE);
                     playbutton.setEnabled(false);
                     playbutton.setAlpha(0.1f);
                }

                else {
                    succesbtn.setVisibility(View.GONE);
                    playbutton.setEnabled(true);
                    playbutton.setAlpha(1f);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
              Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
            }
        });



    }


    }
