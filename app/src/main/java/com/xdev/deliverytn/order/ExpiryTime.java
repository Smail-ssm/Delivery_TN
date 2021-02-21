package com.xdev.deliverytn.order;

public class ExpiryTime {
    public int hour = -1;
    public int minute;
    public Long t;

    public ExpiryTime() {

    }

    public ExpiryTime(int hour, int minute, Long t) {
        this.hour = hour;
        this.minute = minute;
        this.t = t;
    }
}
