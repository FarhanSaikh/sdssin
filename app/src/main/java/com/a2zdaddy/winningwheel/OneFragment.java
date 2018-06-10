package com.a2zdaddy.winningwheel;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

public class OneFragment extends Fragment implements RewardedVideoAdListener {

    Button button,playvideo,coinbtn;
    TextView textView;
    ImageView wheelRoul;
    public int reward = 0;
    InterstitialAd interstitialAd;
    private RewardedVideoAd mAd;
    Random r;

    MediaPlayer mp;
    int degree = 0, degree_old = 0;
    //his was 37 but i had an extra zero
    //becau[se there is 38 sectors on the wheel (9.47 degrees each)
    private static final float FACTOR = 4.7368f;


    public OneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        button = (Button) view.findViewById(R.id.btn_spin);
        textView = (TextView) view.findViewById(R.id.textview);
        wheelRoul = (ImageView) view.findViewById(R.id.imRoulette);
        playvideo=view.findViewById(R.id.playvideobutton);
        //listbtn=view.findViewById(R.id.msgg);
         mp = MediaPlayer.create(getContext(), R.raw.notifymusic);
        coinbtn=view.findViewById(R.id.coin);
        r = new Random();
        //Rewarded video add
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        //check video ad availiblity
        checkadavail();
        //banner ad code is here
        //creating native ad on single post.





        //interstitial ad code is here
        interstitialAd=new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-6712537962798152/5194288065");

        interstitialAd.loadAd( new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please wait..");
                progressDialog.getActionBar();
                progressDialog.setCancelable(false);
                progressDialog.show();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference mref = FirebaseDatabase.getInstance().getReference(uid).child("walletcoin");
                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String walletvalue = dataSnapshot.getValue(String.class);
                        Integer walletcoininint = Integer.parseInt(walletvalue);
                        Integer updatedcoin = (walletcoininint + reward);
                        mref.setValue(String.valueOf(updatedcoin));
                        coinbtn.setText(String.valueOf(updatedcoin));

                        progressDialog.cancel();
                        Toast.makeText(getContext(), "Rewards added", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();


                    }
                });

                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


/*
       listbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),PaymentReqActivity.class);
                startActivity(intent);
            }
        });*/

    //video play button clicked

        playvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bttnclicked();
            }
        });
             //play now button clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(getContext())){
                    Toast.makeText(getContext(),"No internet connection, Please turn on internet to Play ", Toast.LENGTH_LONG).show();


                }
                else {
                degree_old = degree % 360;
                degree = r.nextInt(360) + 720;
                RotateAnimation rotate = new RotateAnimation(degree_old, degree,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3000);
                rotate.setFillAfter(true);
                rotate.setInterpolator(new DecelerateInterpolator());
                rotate.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        textView.setText("");
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textView.setText(currentNumber(360 - (degree % 360)));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                });
                wheelRoul.startAnimation(rotate);
            }}
        });
       return view;

    }

    private String currentNumber(int degrees) {
        String text = "Keep Spinning";
        if (degrees >= (FACTOR * 1) && degrees < (FACTOR * 3)) {
            text = "27 x 1 = 27";
            reward = 32;
            mp.start();
            rewards();
        }
        if (degrees >= (FACTOR * 3) && degrees < (FACTOR * 5)) {
            text = "10 x 1 = 10";
            reward = 15;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 5) && degrees < (FACTOR * 7)) {
            text = "35 x 1 = 35";
            reward = 19;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 7) && degrees < (FACTOR * 9)) {
            mp.start();

            text = "29 x 1 = 29";
            reward = 4;

            rewards();


        }
        if (degrees >= (FACTOR * 9) && degrees < (FACTOR * 11)) {
            text = "12 x 1 = 12";
            reward = 21;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 11) && degrees < (FACTOR * 13)) {
            text = "8 x 1 = 8";
            reward = 2;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 13) && degrees < (FACTOR * 15)) {
            text = "19 x 1 = 19";
            reward = 25;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 15) && degrees < (FACTOR * 17)) {
            text = "31 x 1 = 31";
            reward = 17;

            rewards();

        }
        if (degrees >= (FACTOR * 17) && degrees < (FACTOR * 19)) {
            text = "18 x 1 = 18";
            reward = 37;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 19) && degrees < (FACTOR * 21)) {
            text = "6 x 1 = 6";
            mp.start();

            reward = 6;
            rewards();

        }
        if (degrees >= (FACTOR * 21) && degrees < (FACTOR * 23)) {
            text = "21 x 1 = 21";
            mp.start();

            reward = 34;
            rewards();

        }
        if (degrees >= (FACTOR * 23) && degrees < (FACTOR * 25)) {

            text = "33 x 1 = 33";
            reward = 13;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 25) && degrees < (FACTOR * 27)) {
            text = "16 x 1 = 16";
            reward = 36;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 27) && degrees < (FACTOR * 29)) {
            text = "4 x 1 = 4";
            reward =11;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 29) && degrees < (FACTOR * 31)) {
            text = "23 x 1 = 23";
            reward = 30;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 31) && degrees < (FACTOR * 33)) {
            text = "35 x 1 = 35";
            reward = 8;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 33) && degrees < (FACTOR * 35)) {
            text = "14 x 1 = 14";
            reward = 23;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 35) && degrees < (FACTOR * 37)) {
            text = "2 x 1 = 2";
            reward = 10;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 37) && degrees < (FACTOR * 39)) {
            text = "zero";
            reward = 5;
            //Toast.makeText(getContext(), "Better Luck Next Time", Toast.LENGTH_SHORT).show();
            mp.start();
            rewards();

        }
        if (degrees >= (FACTOR * 39) && degrees < (FACTOR * 41)) {
            text = "28 x 1 = 28";
            reward = 24;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 41) && degrees < (FACTOR * 43)) {
            text = "9 x 1 = 9";
            reward = 16;

            mp.start();
            rewards();
        }
        if (degrees >= (FACTOR * 43) && degrees < (FACTOR * 45)) {
            text = "26 x 1 = 26";
            reward = 33;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 45) && degrees < (FACTOR * 47)) {
            text = "30 x 1 = 30";
            reward = 1;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 47) && degrees < (FACTOR * 49)) {
            text = "11 x 1 = 11";
            reward = 20;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 49) && degrees < (FACTOR * 51)) {
            text = "7 x 1 = 7";
            reward = 14;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 51) && degrees < (FACTOR * 53)) {
            text = "20 x 1 = 20";
            reward = 31;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 53) && degrees < (FACTOR * 55)) {
            text = "32 x 1  = 32";
            reward = 9;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 55) && degrees < (FACTOR * 57)) {
            text = "17 x 1 = 17";
            reward = 22;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 57) && degrees < (FACTOR * 59)) {
            text = "5 x 1 = 5";
            reward = 18;
            mp.start();

            rewards();

        }
        if (degrees >= (FACTOR * 59) && degrees < (FACTOR * 61)) {
            text = "22 x 1 = 22";
            reward = 29;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 61) && degrees < (FACTOR * 63)) {
            text = "34 x 1 = 34";
            reward = 7;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 63) && degrees < (FACTOR * 65)) {
            text = "15 x 1 = 15";
            reward = 28;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 65) && degrees < (FACTOR * 67)) {
            text = "3 x 1 = 3";
            reward = 12;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 67) && degrees < (FACTOR * 69)) {
            text = "24 x 1 = 24";
            reward = 35;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 69) && degrees < (FACTOR * 71)) {
            text = "36 x 1 = 36";
            reward = 3;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 71) && degrees < (FACTOR * 73)) {
            text = "13 x 1 = 13";
            reward = 26;
            mp.start();

            rewards();
        }
        if (degrees >= (FACTOR * 73) && degrees < (FACTOR * 75)) {
            text = "1 x 4 = 4 ";
            reward = 0;
           // mp.start();

            //rewards();
            Toast.makeText(getContext(), "Better Luck Next Time", Toast.LENGTH_SHORT).show();

        }
        if (degrees >= (FACTOR * 73) && degrees < 360 || degrees >= 0 && degrees < (FACTOR * 1)) {
            text = "zero";
            reward = 0;
            Toast.makeText(getContext(), "Better Luck Next Time", Toast.LENGTH_SHORT).show();

        }
        return text;
    }

    public void rewards() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.aleartdialog);


        TextView title = dialog.findViewById(R.id.title);
        Button claim=dialog.findViewById(R.id.claim);
        Button cancel=dialog.findViewById(R.id.cancel);

//        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        TextView rewardpoints = dialog.findViewById(R.id.coin);
        title.setText("You have Won");

  //      imageButton.setImageResource(R.drawable.ic_style_black_24dp);
        rewardpoints.setText(String.valueOf(reward));
       claim.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               dialog.dismiss();
             if (reward>20 && mAd.isLoaded()){
                 mAd.show();
             }

                else if (interstitialAd.isLoaded()){
                 //checkadavail();
                 interstitialAd.show();

                }
                else {

                    //add rewards to firebase database to fetch it later in wallet section


                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Please wait..");
                    progressDialog.getActionBar();
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    final DatabaseReference mref = FirebaseDatabase.getInstance().getReference(uid).child("walletcoin");
                    mref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String walletvalue = dataSnapshot.getValue(String.class);
                            Integer walletcoininint = Integer.parseInt(walletvalue);
                            reward=0;
                            Integer updatedcoin = (walletcoininint + reward);
                            mref.setValue(String.valueOf(updatedcoin));

                            progressDialog.cancel();
                            Toast.makeText(getContext(), "Rewards added", Toast.LENGTH_SHORT).show();

                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                    });
                }

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getContext(), "Ok", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });


        dialog.show();

        dialog.setCancelable(false);



    }

    private void loadRewardedVideoAd() {

        if(!mAd.isLoaded())

        {

            mAd.loadAd("ca-app-pub-6712537962798152/5984625553", new AdRequest.Builder().build());

        }

    }


    public void rewardtheuser(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference mref = FirebaseDatabase.getInstance().getReference(uid).child("walletcoin");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String walletvalue = dataSnapshot.getValue(String.class);
                Integer walletcoininint = Integer.parseInt(walletvalue);
                Integer updatedcoin = (walletcoininint + reward);
                mref.setValue(String.valueOf(updatedcoin));

                progressDialog.cancel();
                Toast.makeText(getContext(), "Rewards added", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();


            }


        });
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
      rewardtheuser();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }


    @Override
    public void onResume() {
        mAd.resume(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        mAd.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(getActivity());
        super.onDestroy();
    }
    public void checkadavail(){

        if (mAd.isLoaded()){
        playvideo.setVisibility(View.VISIBLE);

        }
        }

    public void bttnclicked(){
        if (mAd.isLoaded()){

          mAd.show();
          reward=50;
          playvideo.setVisibility(View.INVISIBLE);
    }
    else {
            Toast.makeText(getContext(), "No video to watch", Toast.LENGTH_SHORT).show();


        }
    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else {
                return false;
            }
        } else
            return false;
    }

}
