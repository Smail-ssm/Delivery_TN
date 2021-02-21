package com.xdev.deliverytn.order;

public class ExpiryDate {

    public int year;
    public int month;
    public int day;
    public Long timeStamp;
    public ExpiryDate() {

    }

    public ExpiryDate(int year, int month, int day, Long timeStamp) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.timeStamp = timeStamp;
    }
}
