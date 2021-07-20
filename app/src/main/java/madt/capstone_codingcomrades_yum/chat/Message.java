package madt.capstone_codingcomrades_yum.chat;

public class Message {
    String sendBy;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Message(String sendBy, String senderId, String timestamp, String messageText) {
        this.sendBy = sendBy;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageText = messageText;
    }

    String senderId;



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

    String timestamp;
    String messageText;
}
