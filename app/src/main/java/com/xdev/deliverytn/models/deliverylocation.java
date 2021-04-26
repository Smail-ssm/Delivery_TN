package com.xdev.deliverytn.models;

public class deliverylocation {
    public String Namel;//
    public String Locationl;//
    public String PhoneNumberl;//
    public double Latl;//
    public double Lonl;//
    public String addrl, cityl, statel, countryl, postalCodel;

    public deliverylocation() {
    }

    public deliverylocation(String namel, String locationl, String phoneNumberl, double latl, double lonl, String addrl, String cityl, String statel, String countryl, String postalCodel) {
        Namel = namel;
        Locationl = locationl;
        PhoneNumberl = phoneNumberl;
        Latl = latl;
        Lonl = lonl;
        this.addrl = addrl;
        this.cityl = cityl;
        this.statel = statel;
        this.countryl = countryl;
        this.postalCodel = postalCodel;
    }

    @Override
    public String toString() {
        return "deliverylocation{" +
                "Namel='" + Namel + '\'' +
                ", Locationl='" + Locationl + '\'' +
                ", PhoneNumberl='" + PhoneNumberl + '\'' +
                ", Latl=" + Latl +
                ", Lonl=" + Lonl +
                ", addrl='" + addrl + '\'' +
                ", cityl='" + cityl + '\'' +
                ", statel='" + statel + '\'' +
                ", countryl='" + countryl + '\'' +
                ", postalCodel='" + postalCodel + '\'' +
                '}';
    }
}