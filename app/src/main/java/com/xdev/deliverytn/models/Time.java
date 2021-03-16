package com.xdev.deliverytn.models;

public class Time {
    private long accepted;
    private long delivered;
    private long ordered;

    public Time() {

    }

    public Time(long accepted, long delivered, long ordered) {
        this.accepted = accepted;
        this.delivered = delivered;
        this.ordered = ordered;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getDelivered() {
        return delivered;
    }

    public void setDelivered(long delivered) {
        this.delivered = delivered;
    }

    public long getOrdered() {
        return ordered;
    }

    public void setOrdered(long ordered) {
        this.ordered = ordered;
    }
}
