package com.xdev.deliverytn.order;

public class OrderObject {

    public String category, description, userId, status, otp;
    public int orderId, min_range, max_range, final_price, deliveryCharge;
    public UserLocation userLocation = new UserLocation();
    public ExpiryDate expiryDate = new ExpiryDate();
    public ExpiryTime expiryTime = new ExpiryTime();
    public AcceptedBy acceptedBy = new AcceptedBy();

    public OrderObject() {
        //For DataSnapshot.getValue()
        //Don't ever try to delete it.
    }
//
//    public OrderObject(Parcel in) {
//        category = in.readString();
//        description = in.readString();
//        orderId = in.readInt();
//        min_range = in.readInt();
//        max_range = in.readInt();
//        status = in.readString();
//        deliveryCharge = in.readInt();
//        userId = in.readString();
//
//        userLocation.Name = in.readString();
//        userLocation.Location = in.readString();
//        userLocation.PhoneNumber = in.readString();
//        userLocation.Lat = in.readDouble();
//        userLocation.Lon = in.readDouble();
//        userLocation.addr = in.readString();
//        userLocation.city = in.readString();
//        userLocation.state = in.readString();
//        userLocation.country = in.readString();
//        userLocation.postalCode = in.readString();
//
//        expiryTime.hour = in.readInt();
//        expiryTime.minute = in.readInt();
//        expiryTime.t = in.readLong();
//
//        expiryDate.year = in.readInt();
//        expiryDate.month = in.readInt();
//        expiryDate.day = in.readInt();
//        expiryDate.timeStamp = in.readLong();
//
//        acceptedBy.name = in.readString();
//        acceptedBy.mobile = in.readString();
//        acceptedBy.alt_mobile = in.readString();
//        acceptedBy.email = in.readString();
//        acceptedBy.delivererID = in.readString();
//
//        otp = in.readString();
//
//        final_price = in.readInt();
//    }
//
//    public OrderObject(String category, String description, int orderId, int max_range, int min_range,
//                       UserLocation location, ExpiryDate expiryDate, ExpiryTime expiryTime, String status,
//                       int deliveryCharge, AcceptedBy acceptedBy, String userId, String otp, int final_price) {
//        this.category = category;
//        this.description = description;
//        this.orderId = orderId;
//        this.min_range = min_range;
//        this.max_range = max_range;
//        this.userLocation = location;
//        this.expiryDate = expiryDate;
//        this.expiryTime = expiryTime;
//        this.status = status;
//        this.deliveryCharge = deliveryCharge;
//        this.acceptedBy = acceptedBy;
//        this.userId = userId;
//        this.otp = otp;
//        this.final_price = final_price;
//
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(category);
//        dest.writeString(description);
//        dest.writeInt(orderId);
//        dest.writeInt(min_range);
//        dest.writeInt(max_range);
//        dest.writeString(status);
//        dest.writeInt(deliveryCharge);
//        dest.writeString(userId);
//
//        dest.writeString(userLocation.Name);
//        dest.writeString(userLocation.Location);
//        dest.writeDouble(userLocation.Lat);
//        dest.writeDouble(userLocation.Lon);
//        dest.writeString(userLocation.PhoneNumber);
//        dest.writeString(userLocation.addr);
//        dest.writeString(userLocation.city);
//        dest.writeString(userLocation.state);
//        dest.writeString(userLocation.country);
//        dest.writeString(userLocation.postalCode);
//
//        dest.writeInt(expiryTime.hour);
//        dest.writeInt(expiryTime.minute);
//        dest.writeLong(expiryTime.t);
//
//        dest.writeInt(expiryDate.year);
//        dest.writeInt(expiryDate.month);
//        dest.writeInt(expiryDate.day);
//        dest.writeLong(expiryDate.timeStamp);
//
//        dest.writeString(acceptedBy.name);
//        dest.writeString(acceptedBy.mobile);
//        dest.writeString(acceptedBy.alt_mobile);
//        dest.writeString(acceptedBy.email);
//        dest.writeString(acceptedBy.delivererID);
//
//        dest.writeString(otp);
//
//        dest.writeInt(final_price);
//    }

    public OrderObject(String category, String description, String userId, String status, String otp, int orderId, int min_range, int max_range, int final_price, int deliveryCharge, UserLocation userLocation, ExpiryDate expiryDate, ExpiryTime expiryTime, AcceptedBy acceptedBy) {
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
        this.expiryDate = expiryDate;
        this.expiryTime = expiryTime;
        this.acceptedBy = acceptedBy;
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
}
