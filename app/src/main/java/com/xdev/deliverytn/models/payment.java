package com.xdev.deliverytn.models;

public class payment {
    public int year;
    public int month;
    public int day;
    public int hour = -1;
    public int minute;
    public Long timeStamp;
    String userID, payID, transfertnumber, paymentmethod, sendername, cin, paymentfacture, value;

    public payment() {
    }


    public payment(int year, int month, int day, int hour, int minute, String payID, Long timeStamp, String userID, String transfertnumber, String paymentmethod, String sendername, String cin, String paymentfacture, String value) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.payID = payID;
        this.timeStamp = timeStamp;
        this.userID = userID;
        this.transfertnumber = transfertnumber;
        this.paymentmethod = paymentmethod;
        this.sendername = sendername;
        this.cin = cin;
        this.paymentfacture = paymentfacture;
        this.value = value;
    }

    @Override
    public String toString() {
        return "payment{" +
                "userID='" + userID + '\n' +
                ", transfertnumber='" + transfertnumber + '\n' +
                ", paymentmethod='" + paymentmethod + '\n' +
                ", sendername='" + sendername + '\n' +
                ", cin='" + cin + '\n' +
                ", paymentfacture='" + paymentfacture + '\n' +
                ", value='" + value + '\n' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", timeStamp=" + timeStamp +
                '}';
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getPayID() {
        return payID;
    }

    public void setPayID(String payID) {
        this.payID = payID;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTransfertnumber() {
        return transfertnumber;
    }

    public void setTransfertnumber(String transfertnumber) {
        this.transfertnumber = transfertnumber;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getPaymentfacture() {
        return paymentfacture;
    }

    public void setPaymentfacture(String paymentfacture) {
        this.paymentfacture = paymentfacture;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
