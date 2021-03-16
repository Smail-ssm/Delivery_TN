package com.xdev.deliverytn.models;

import android.widget.EditText;
import android.widget.TextView;

public class OrderWeb {
    public String category, description, userId, status, otp;
    public int orderId, min_range, max_range, final_price, deliveryCharge;
    public UserLocation userLocation = new UserLocation();
    public double earnings, socWalet;
    public ExpiryDate expiryDate = new ExpiryDate();
    public ExpiryTime expiryTime = new ExpiryTime();
    public AcceptedBy acceptedBy = new AcceptedBy();
    public Client client = new Client();
    public Time time = new Time();
    public Deliverer delivere = new Deliverer();

    public OrderWeb(TextView category, EditText description, String userId, String pending, String otp, int order_id, int parseInt, int parseInt1, int final_price, int i, UserLocation userLocation, ExpiryDate expiryDate, ExpiryTime expiryTime, AcceptedBy acceptedBy, Client client, Time time, Deliverer deliverer) {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMin_range() {
        return min_range;
    }

    public void setMin_range(int min_range) {
        this.min_range = min_range;
    }

    public int getMax_range() {
        return max_range;
    }

    public void setMax_range(int max_range) {
        this.max_range = max_range;
    }

    public int getFinal_price() {
        return final_price;
    }

    public void setFinal_price(int final_price) {
        this.final_price = final_price;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public double getSocWalet() {
        return socWalet;
    }

    public void setSocWalet(double socWalet) {
        this.socWalet = socWalet;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ExpiryDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ExpiryTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(ExpiryTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public AcceptedBy getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(AcceptedBy acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Deliverer getDelivere() {
        return delivere;
    }

    public void setDelivere(Deliverer delivere) {
        this.delivere = delivere;
    }




    public OrderWeb(String category, String description, String userId, String status, String otp, int orderId, int min_range, int max_range, int final_price, int deliveryCharge, UserLocation userLocation, double earnings, double socWalet, ExpiryDate expiryDate, ExpiryTime expiryTime, AcceptedBy acceptedBy, Client client, Time time, Deliverer delivere) {
        this.category = category;
        this.description = description;
        this.userId = userId;
        this.status = status;
        this.otp = otp;
        this.orderId = orderId;
        this.min_range = min_range;
        this.max_range = max_range;
        this.final_price = final_price;
        this.deliveryCharge = deliveryCharge;
        this.userLocation = userLocation;
        this.earnings = earnings;
        this.socWalet = socWalet;
        this.expiryDate = expiryDate;
        this.expiryTime = expiryTime;
        this.acceptedBy = acceptedBy;
        this.client = client;
        this.time = time;
        this.delivere = delivere;

    }
}
