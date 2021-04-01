package com.xdev.deliverytn.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderWeb implements Parcelable {
    public static final Parcelable.Creator<OrderWeb> CREATOR = new Parcelable.Creator<OrderWeb>() {
        public OrderWeb createFromParcel(Parcel in) {
            return new OrderWeb(in);
        }

        public OrderWeb[] newArray(int size) {
            return new OrderWeb[size];
        }
    };
    public String category,
            description,
            userId,
            status,
            otp;
    public int orderId,
            min_range,
            max_range,
            final_price,
            deliveryCharge;
    public UserLocation userLocation = new UserLocation();
    public double earnings;

    public ExpiryDate expiryDate = new ExpiryDate();
    public ExpiryTime expiryTime = new ExpiryTime();
    public AcceptedBy acceptedBy = new AcceptedBy();
    public Client client = new Client();
    public Time tt = new Time();
    public Deliverer delivere = new Deliverer();

    public OrderWeb() {

    }

    public OrderWeb(String category, String description, String userId, String status, String otp,
                    int orderId, int min_range, int max_range, int final_price, int deliveryCharge,
                    UserLocation userLocation, ExpiryDate expiryDate, ExpiryTime expiryTime,
                    AcceptedBy acceptedBy, Client client, Time tt, Deliverer delivere) {
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
        this.client = client;
        this.tt = tt;
        this.delivere = delivere;
    }

    public OrderWeb(Parcel in) {
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

        tt.accepted = in.readLong();
        tt.delivered = in.readLong();
        tt.ordered = in.readLong();

        expiryDate.year = in.readInt();
        expiryDate.month = in.readInt();
        expiryDate.day = in.readInt();
        expiryDate.timeStamp = in.readLong();
        client.email = in.readString();
        client.mobile = in.readString();
        client.name = in.readString();
        client.photo = in.readString();
        delivere.email = in.readString();
        delivere.mobile = in.readString();
        delivere.name = in.readString();
        delivere.photo = in.readString();


        acceptedBy.name = in.readString();
        acceptedBy.mobile = in.readString();
        acceptedBy.alt_mobile = in.readString();
        acceptedBy.email = in.readString();
        acceptedBy.delivererID = in.readString();

        otp = in.readString();

        final_price = in.readInt();
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

        dest.writeString(client.email);
        dest.writeString(client.mobile);
        dest.writeString(client.name);
        dest.writeString(client.photo);
        dest.writeString(delivere.email);
        dest.writeString(delivere.mobile);
        dest.writeString(delivere.name);
        dest.writeString(delivere.photo);

        dest.writeInt(expiryTime.hour);
        dest.writeInt(expiryTime.minute);
        dest.writeLong(expiryTime.t);
        dest.writeLong(tt.accepted);
        dest.writeLong(tt.delivered);
        dest.writeLong(tt.ordered);


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
