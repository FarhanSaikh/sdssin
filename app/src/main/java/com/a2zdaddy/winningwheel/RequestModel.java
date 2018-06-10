package com.a2zdaddy.winningwheel;

import java.util.jar.Attributes;

/**
 * Created by FARHAN SAIKH on 4/16/2018.
 */

public class RequestModel {


    String name,userid,paymentno,withdrawstat,paymentmethod;
    RequestModel(){}

    public RequestModel(String name,String userid,String paymentno,String withdrawstat,String paymentmethod){

        this.name=name;
        this.userid=userid;
        this.paymentno=paymentno;
        this.withdrawstat=withdrawstat;
        this.paymentmethod=paymentmethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPaymentno() {
        return paymentno;
    }

    public void setPaymentno(String paymentno) {
        this.paymentno = paymentno;
    }

    public String getWithdrawstat() {
        return withdrawstat;
    }

    public void setWithdrawstat(String withdrawstat) {
        this.withdrawstat = withdrawstat;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }
}



