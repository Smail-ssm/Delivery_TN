package com.xdev.deliverytn.Chat.chatroom;

public class chatrrom {
    public String roomId;
    public String ordererId;
    public String deliverId;

    public chatrrom(String roomId, String ordererId, String deliverId) {
        this.roomId = roomId;
        this.ordererId = ordererId;
        this.deliverId = deliverId;
    }

    public chatrrom() {

    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOrdererId() {
        return ordererId;
    }

    public void setOrdererId(String ordererId) {
        this.ordererId = ordererId;
    }

    public String getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(String deliverId) {
        this.deliverId = deliverId;
    }
}
