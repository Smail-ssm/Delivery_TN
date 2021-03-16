package com.xdev.deliverytn.models;

/**
 * Class that represent the message.
 */
public class Chat {
    public String message;
    public String id;
    public int msgN;

    public Chat() {

    }

    public String getMessage() {
        return message;
    }

    public Chat(String message, String id, int msgN) {
        this.message = message;
        this.id = id;
        this.msgN = msgN;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMsgN() {
        return msgN;
    }

    public void setMsgN(int msgN) {
        this.msgN = msgN;
    }
}