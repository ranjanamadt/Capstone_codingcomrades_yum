package madt.capstone_codingcomrades_yum.chat;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

import madt.capstone_codingcomrades_yum.utils.FSConstants;

public class ChatDetail {

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String firstName;
    String chatRoomId;
    String lastMessage;
    String profileImage;
    String senderId;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    String deviceToken;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    String receiverId;

    public ChatDetail(String firstName, String lastName, String lastMessageTimeStamp, String lastMessage, String profileImage, String senderId, String receiverId, String deviceToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessage = lastMessage;
        this.profileImage = profileImage;
        this.chatRoomId = null;
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.deviceToken=deviceToken;
    }

    String lastName;
    String lastMessageTimeStamp;

    public String getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(String lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    public ChatDetail(DocumentSnapshot document) {
        this.chatRoomId = document.getId();
        HashMap<String, Object> userDetail = (HashMap<String, Object>) document.get(FSConstants.CHAT_List.USER_DETAIL);
        this.firstName = userDetail.get(FSConstants.USER.FIRST_NAME).toString();
        this.lastName = userDetail.get(FSConstants.USER.LAST_NAME).toString();
        this.lastMessageTimeStamp = userDetail.get(FSConstants.USER.LAST_MESSAGE_TIMESTAMP).toString();
        this.lastMessage = userDetail.get(FSConstants.USER.LAST_MESSAGE).toString();
        this.profileImage = userDetail.get(FSConstants.USER.PROFILE_IMAGE).toString();
        this.senderId = userDetail.get(FSConstants.CHAT_List.SENDER_ID).toString();
        this.receiverId = userDetail.get(FSConstants.CHAT_List.RECEIVER_ID).toString();
        this.deviceToken = userDetail.get(FSConstants.USER.DEVICE_TOKEN).toString();

    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
