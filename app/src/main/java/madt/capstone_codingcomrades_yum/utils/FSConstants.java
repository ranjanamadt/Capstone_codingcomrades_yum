package madt.capstone_codingcomrades_yum.utils;

public class FSConstants {

    public interface Collections{
        String USERS="users";
        String PREFERENCES="preferences";
        String CHATROOM="chatroom";
    }

    public interface USER{
        String FIRST_NAME="firstName";
        String LAST_NAME="lastName";
        String DOB="dob";
        String GENDER="gender";
        String SEX_PREFER="sePref";
        String DEVICE_TOKEN="deviceToken";
        String LATITUDE="latitude";
        String LONGITUDE="longitude";
        String ABOUT_ME="aboutMe";
        String PROFILE_IMAGE="profileImage";
    }

    public interface PREFERENCE{
        String PREFERENCE_NAME="preference_name";
        String PREFERENCE_TYPE="preference_type";
        String USER_UID="user_uid";
    }
    public interface PREFERENCE_TYPE{
        String ENJOY_EATING="enjoy_eating";
        String TASTE="taste";
        String INTEREST="interest";
        String NOT_EAT="not_eat";
        String NOT_TALK="not_talk";
    }

    public  interface  CHAT_List{
        String USER_NAME= "user_name";
        String USER_IMAGE= "user_image";
        String MESSAGES="messages";

    }

    public interface MATCHES_DETAIL{
        String OTHER_USER_ID="otherUserID";
        String ACTION="Action";

    }

    public interface MESSAGE_DETAIL{
        String TEXT = "messageText";
        String SEND_BY = "sendBy";
        String SENDER_ID = "senderId";
        String TIMESTAMP = "timestamp";
        String IMAGE = "userImage";
    }

}
