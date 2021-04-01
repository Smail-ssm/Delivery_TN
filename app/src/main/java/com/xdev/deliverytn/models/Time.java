package com.xdev.deliverytn.models;

public class Time {
    public long accepted;
    public long delivered;
    public long ordered;

    public Time() {

    }

    public Time(long accepted, long delivered, long ordered) {
        this.accepted = accepted;
        this.delivered = delivered;
        this.ordered = ordered;
    }


}
