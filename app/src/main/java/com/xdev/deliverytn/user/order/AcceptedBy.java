package com.xdev.deliverytn.user.order;

public class AcceptedBy {
    public String name, mobile, alt_mobile, email, delivererID,currentLocation;

    public AcceptedBy() {

    }

    public AcceptedBy(String name, String mobile, String alt_mobile, String email, String delivererID,String currentLocation) {
        this.name = name;
        this.mobile = mobile;
        this.alt_mobile = alt_mobile;
        this.email = email;
        this.delivererID = delivererID;
        this.currentLocation = currentLocation;
    }
    public AcceptedBy(String name, String mobile, String alt_mobile, String email, String delivererID) {
        this.name = name;
        this.mobile = mobile;
        this.alt_mobile = alt_mobile;
        this.email = email;
        this.delivererID = delivererID;
    }
}