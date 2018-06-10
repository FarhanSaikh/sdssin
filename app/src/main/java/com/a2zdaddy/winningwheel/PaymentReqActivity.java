package com.a2zdaddy.winningwheel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentReqActivity extends AppCompatActivity {


 RecyclerView mReqList;
    LinearLayoutManager mLayoutManager;
      DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_req);

        mReqList=findViewById(R.id.requestlist);
        mReqList.setHasFixedSize(true);
        // mpostlist.setLayoutManager(new LinearLayoutManager(PostList.this));
        mLayoutManager = new LinearLayoutManager(PaymentReqActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Now set the layout manager and the adapter to the RecyclerView
        mReqList.setLayoutManager(mLayoutManager);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("paymentreq");


    }
    @Override
    protected void onStart() {
        super.onStart();
        final ProgressDialog progressDialog = new ProgressDialog(PaymentReqActivity.this);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseRecyclerAdapter<RequestModel,PostViewFolder> PSVF=new FirebaseRecyclerAdapter<RequestModel, PostViewFolder>(
                RequestModel.class,
                R.layout.reqlistformat,
                PostViewFolder.class,
                mDatabase) {



            @Override
            protected void populateViewHolder(PostViewFolder viewHolder, RequestModel model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setUserid(model.getUserid());
                viewHolder.setPaymentmethod(model.getPaymentmethod());
                viewHolder.setPaymentno(model.getPaymentno());
                viewHolder.setWithdrawlstat(model.getWithdrawstat());

                progressDialog.cancel();



                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            Intent intent = new Intent(PaymentReqActivity.this, Main2Activity.class);
                            startActivity(intent);


                    }
                });

            }
        };

        mReqList.setAdapter(PSVF);


    }
    public static class PostViewFolder extends RecyclerView.ViewHolder{

        View mview;
        public PostViewFolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setName(String name){
            TextView nameof=(TextView) mview.findViewById(R.id.nameuser);
            nameof.setText(name);

        }

        public void setPaymentno(String paymentno ){
            TextView paymentview=(TextView) mview.findViewById(R.id.number);
            paymentview.setText(paymentno);


        }
        public void setUserid(String userid ){
            TextView useridser=(TextView) mview.findViewById(R.id.userid);
            useridser.setText(userid);


        }
        public void setWithdrawlstat(String withdrawstat ){
            TextView stat=(TextView) mview.findViewById(R.id.statusofpayment);
            stat.setText(withdrawstat);


        }
        public void setPaymentmethod(String paymentmethod ){
            TextView method=(TextView) mview.findViewById(R.id.methodofpayment);
            method.setText(paymentmethod);


        }






    }
}
