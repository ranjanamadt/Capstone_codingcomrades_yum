package madt.capstone_codingcomrades_yum.chat;

import java.util.HashMap;

import madt.capstone_codingcomrades_yum.utils.FSConstants;

public class ChatUserDetail {

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

    public ChatUserDetail(String firstName, String lastName, String lastMessageTimeStamp, String lastMessage, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessage = lastMessage;
        this.profileImage = profileImage;
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

    String lastMessage;
    String profileImage;

    public ChatUserDetail(HashMap<String, Object> userDetail) {
        this.firstName = userDetail.get(FSConstants.USER.FIRST_NAME).toString();
        this.lastName = userDetail.get(FSConstants.USER.FIRST_NAME).toString();

    }


}
