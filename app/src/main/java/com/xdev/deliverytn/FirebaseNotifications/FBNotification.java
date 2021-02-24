package com.xdev.deliverytn.FirebaseNotifications;

public class FBNotification {
    String title, message;
    int id;

    public FBNotification() {
    }

    public FBNotification(String title, String message, int id) {
        this.title = title;
        this.message = message;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
