package com.xdev.deliverytn.models;

public class OrderedBy {
    public String name, mobile, alt_mobile, email, ordererID, currentLocation;

    public OrderedBy() {

    }

    public OrderedBy(String name, String mobile, String alt_mobile, String email, String ordererID, String currentLocation) {
        this.name = name;
        this.mobile = mobile;
        this.alt_mobile = alt_mobile;
        this.email = email;
        this.ordererID = ordererID;
        this.currentLocation = currentLocation;
    }

    public OrderedBy(String name, String mobile, String alt_mobile, String email, String ordererID) {
        this.name = name;
        this.mobile = mobile;
        this.alt_mobile = alt_mobile;
        this.email = email;
        this.ordererID = ordererID;
    }
}