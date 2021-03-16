package com.xdev.deliverytn.models;

public class UserLocation {
    public String Name;//
    public String Location;//
    public String PhoneNumber;//
    public double Lat;//
    public double Lon;//
    public String addr, city, state, country, postalCode;

    public UserLocation(String name, String location, String phoneNumber, double lat, double lon, String addr,  String city, String state, String country, String postalCode) {
        Name = name;
        Location = location;
        PhoneNumber = phoneNumber;
        Lat = lat;
        Lon = lon;
        this.addr = addr;

        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public UserLocation() {

    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "Name='" + Name + '\'' +
                ", Location='" + Location + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Lat=" + Lat +
                ", Lon=" + Lon +
                ", addr='" + addr + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}