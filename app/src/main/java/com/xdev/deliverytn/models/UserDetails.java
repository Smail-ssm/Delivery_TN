package com.xdev.deliverytn.models;

public class UserDetails {
    public String usertype;
    float topay;
    private String Mobile;
    private String cin;
    private String email;
    private float wallet;
    private String playerId;
    private String address;
    private String birth;
    private String cinPhoto;
    private String profile;
    private String city;
    private String first;
    private String displayName;
    private String gender;
    private String last;
    private String role;
    private String zip;
    private String uid;
    private String accountstatue;
    private String rate;
    private String ratenumber;


    public UserDetails() {

    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public float getTopay() {
        return topay;
    }

    public void setTopay(float topay) {
        this.topay = topay;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCinPhoto() {
        return cinPhoto;
    }

    public void setCinPhoto(String cinPhoto) {
        this.cinPhoto = cinPhoto;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccountstatue() {
        return accountstatue;
    }

    public void setAccountstatue(String accountstatue) {
        this.accountstatue = accountstatue;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRatenumber() {
        return ratenumber;
    }

    public void setRatenumber(String ratenumber) {
        this.ratenumber = ratenumber;
    }

    public UserDetails(String usertype, float topay, String mobile, String cin, String email, float wallet, String playerId, String address, String birth, String cinPhoto, String profile, String city, String first, String displayName, String gender, String last, String role, String zip, String uid, String accountstatue, String rate, String ratenumber) {
        this.usertype = usertype;
        this.topay = topay;
        Mobile = mobile;
        this.cin = cin;
        this.email = email;
        this.wallet = wallet;
        this.playerId = playerId;
        this.address = address;
        this.birth = birth;
        this.cinPhoto = cinPhoto;
        this.profile = profile;
        this.city = city;
        this.first = first;
        this.displayName = displayName;
        this.gender = gender;
        this.last = last;
        this.role = role;
        this.zip = zip;
        this.uid = uid;
        this.accountstatue = accountstatue;
        this.rate = rate;
        this.ratenumber = ratenumber;
    }
}