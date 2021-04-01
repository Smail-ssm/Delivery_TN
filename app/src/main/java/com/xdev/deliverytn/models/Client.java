package com.xdev.deliverytn.models;

public class Client {
    public String email, mobile, name, photo, uid;

    public Client(String email, String mobile, String name, String photo, String uid) {
        this.email = email;
        this.mobile = mobile;
        this.name = name;
        this.photo = photo;
        this.uid = uid;
    }

    public Client() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
