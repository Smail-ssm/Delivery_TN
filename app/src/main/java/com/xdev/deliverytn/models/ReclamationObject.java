package com.xdev.deliverytn.models;

public class ReclamationObject {

    String oName, oCin, oID, dName, dCin, dId, reclamationText, reason, reclamationPic, senderId, timestamp;
    int recId;

    public ReclamationObject() {
    }

    public ReclamationObject(String oName, String oCin, String oID, String dName, String dCin, String dId, String reclamationText, String reason, String reclamationPic, String senderId, String timestamp, int recId) {
        this.oName = oName;
        this.oCin = oCin;
        this.oID = oID;
        this.dName = dName;
        this.dCin = dCin;
        this.dId = dId;
        this.reclamationText = reclamationText;
        this.reason = reason;
        this.reclamationPic = reclamationPic;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.recId = recId;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
    }

    public String getoCin() {
        return oCin;
    }

    public void setoCin(String oCin) {
        this.oCin = oCin;
    }

    public String getoID() {
        return oID;
    }

    public void setoID(String oID) {
        this.oID = oID;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdCin() {
        return dCin;
    }

    public void setdCin(String dCin) {
        this.dCin = dCin;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getReclamationText() {
        return reclamationText;
    }

    public void setReclamationText(String reclamationText) {
        this.reclamationText = reclamationText;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReclamationPic() {
        return reclamationPic;
    }

    public void setReclamationPic(String reclamationPic) {
        this.reclamationPic = reclamationPic;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }
}
