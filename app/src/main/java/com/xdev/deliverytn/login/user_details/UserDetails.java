package com.xdev.deliverytn.login.user_details;

public class UserDetails {
    private String Mobile;
    private String cin;
    private String email;
    private int wallet;
    private String playerId;
    private String address;
    private String birth;
    private String cinPhoto;
    private String profilepic;
    private String city;
    private String first;
    private String gender;
    private String last;
    private String role;
    private String zip;
    private float rate;

    public UserDetails() {
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

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
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

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
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

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getRate_number() {
        return rate_number;
    }

    public void setRate_number(int rate_number) {
        this.rate_number = rate_number;
    }

    private int rate_number;

    public UserDetails(String mobile, String cin, String email, int wallet, String playerId, String address, String birth, String cinPhoto, String profilepic, String city, String first, String gender, String last, String role, String zip, float rate, int rate_number) {
        Mobile = mobile;
        this.cin = cin;
        this.email = email;
        this.wallet = wallet;
        this.playerId = playerId;
        this.address = address;
        this.birth = birth;
        this.cinPhoto = cinPhoto;
        this.profilepic = profilepic;
        this.city = city;
        this.first = first;
        this.gender = gender;
        this.last = last;
        this.role = role;
        this.zip = zip;
        this.rate = rate;
        this.rate_number = rate_number;
    }
}
