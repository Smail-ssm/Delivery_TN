package com.xdev.deliverytn.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDataWeb implements Parcelable {
    public static final Creator<OrderDataWeb> CREATOR = new Creator<OrderDataWeb>() {
        public OrderDataWeb createFromParcel(Parcel in) {
            return new OrderDataWeb(in);
        }

        public OrderDataWeb[] newArray(int size) {
            return new OrderDataWeb[size];
        }
    };
    public String category, description, userId, status, otp;
    public int orderId, min_range, max_range, final_price, deliveryCharge;
    public double earnings;
    public UserLocation userLocation = new UserLocation();
    public ExpiryDate expiryDate = new ExpiryDate();
    public ExpiryTime expiryTime = new ExpiryTime();
    public AcceptedBy acceptedBy = new AcceptedBy();
    public Time time=new Time();
public Client client = new Client();    public OrderDataWeb() {
        //For DataSnapshot.getValue()
        //Don't ever try to delete it.
    }

    public OrderDataWeb(Parcel in) {
        category = in.readString();
        description = in.readString();
        orderId = in.readInt();
        min_range = in.readInt();
        max_range = in.readInt();
        status = in.readString();
        deliveryCharge = in.readInt();
        userId = in.readString();

        userLocation.Name = in.readString();
        userLocation.Location = in.readString();
        userLocation.PhoneNumber = in.readString();
        userLocation.Lat = in.readDouble();
        userLocation.Lon = in.readDouble();
        earnings = in.readDouble();
        userLocation.addr = in.readString();
        userLocation.city = in.readString();
        userLocation.state = in.readString();
        userLocation.country = in.readString();
        userLocation.postalCode = in.readString();

        expiryTime.hour = in.readInt();
        expiryTime.minute = in.readInt();
        expiryTime.t = in.readLong();

        expiryDate.year = in.readInt();
        expiryDate.month = in.readInt();
        expiryDate.day = in.readInt();
        expiryDate.timeStamp = in.readLong();

        acceptedBy.name = in.readString();
        acceptedBy.mobile = in.readString();
        acceptedBy.alt_mobile = in.readString();
        acceptedBy.email = in.readString();
        acceptedBy.delivererID = in.readString();

        otp = in.readString();

        final_price = in.readInt();
    }

    public OrderDataWeb(String category, String description, int orderId, int max_range, int min_range,
                        UserLocation location, ExpiryDate expiryDate, ExpiryTime expiryTime, String status,
                        int deliveryCharge, AcceptedBy acceptedBy, String userId, String otp, int final_price) {
        this.category = category;
        this.description = description;
        this.orderId = orderId;
        this.min_range = min_range;
        this.max_range = max_range;
        this.userLocation = location;
        this.expiryDate = expiryDate;
        this.expiryTime = expiryTime;
        this.status = status;
        this.deliveryCharge = deliveryCharge;
        this.acceptedBy = acceptedBy;
        this.userId = userId;
        this.otp = otp;
        this.final_price = final_price;


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(description);
        dest.writeInt(orderId);
        dest.writeInt(min_range);
        dest.writeInt(max_range);
        dest.writeString(status);
        dest.writeInt(deliveryCharge);
        dest.writeString(userId);

        dest.writeString(userLocation.Name);
        dest.writeString(userLocation.Location);
        dest.writeDouble(userLocation.Lat);
        dest.writeDouble(userLocation.Lon);
        dest.writeDouble(earnings);

        dest.writeString(userLocation.PhoneNumber);
        dest.writeString(userLocation.addr);
        dest.writeString(userLocation.city);
        dest.writeString(userLocation.state);
        dest.writeString(userLocation.country);
        dest.writeString(userLocation.postalCode);

        dest.writeInt(expiryTime.hour);
        dest.writeInt(expiryTime.minute);
        dest.writeLong(expiryTime.t);

        dest.writeInt(expiryDate.year);
        dest.writeInt(expiryDate.month);
        dest.writeInt(expiryDate.day);
        dest.writeLong(expiryDate.timeStamp);

        dest.writeString(acceptedBy.name);
        dest.writeString(acceptedBy.mobile);
        dest.writeString(acceptedBy.alt_mobile);
        dest.writeString(acceptedBy.email);
        dest.writeString(acceptedBy.delivererID);

        dest.writeString(otp);

        dest.writeInt(final_price);
    }

}