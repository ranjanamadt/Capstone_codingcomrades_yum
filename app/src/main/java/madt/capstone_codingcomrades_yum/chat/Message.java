package madt.capstone_codingcomrades_yum.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import madt.capstone_codingcomrades_yum.utils.FSConstants;

public class Message {
    String sendBy;
    String timestamp;
    String messageText;
    String senderId;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    String userImage;

    public Message(String sendBy, String senderId, String timestamp, String messageText, String userImage) {
        this.sendBy = sendBy;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageText = messageText;
        this.userImage = userImage;
    }

    public Message(HashMap<String, Object> document) {
        this.sendBy = document.get(FSConstants.MESSAGE_DETAIL.TEXT).toString();
        this.senderId = document.get(FSConstants.MESSAGE_DETAIL.SENDER_ID).toString();
        this.timestamp = document.get(FSConstants.MESSAGE_DETAIL.TIMESTAMP).toString();
        this.messageText = document.get(FSConstants.MESSAGE_DETAIL.TEXT).toString();
        this.userImage = document.get(FSConstants.MESSAGE_DETAIL.IMAGE).toString();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDateFromTimeStamp() {
        long myLong = Long.parseLong(this.timestamp);
        Date itemDate = new Date(myLong);
        return new SimpleDateFormat("dd/MM/yyyy").format(itemDate);

    }


    public String getTimeFromTimeStamp() {
        long myLong = Long.parseLong(this.timestamp);
        Date itemDate = new Date(myLong);
        return new SimpleDateFormat("HH:mm").format(itemDate);
    }


}

