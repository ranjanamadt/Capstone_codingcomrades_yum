package madt.capstone_codingcomrades_yum.utils;

public class FSConstants {

    public interface Collections{
        String USERS="users";
        String PREFERENCES="preferences";
        String CHATROOM="chatroom";
        String REPORTLIST="reportlist";
    }

    public interface USER{
        String FIRST_NAME="firstName";
        String LAST_NAME="lastName";
        String DOB="dob";
        String AGE="age";
        String GENDER="gender";
        String SEX_PREFER="sePref";
        String DEVICE_TOKEN="deviceToken";
        String LATITUDE="latitude";
        String LONGITUDE="longitude";
        String ABOUT_ME="aboutMe";
        String PROFILE_IMAGE="profileImage";
        String PREFERENCES="preferences";
        String LAST_MESSAGE="lastMessage";
        String LAST_MESSAGE_TIMESTAMP="lastMessageTimeStamp";
        String REPORT_LIST = "report_list";
        String MATCHED_USERS = "matched_users";
        String OTHER_LOCATIONS = "other_locations";
        String MIN_AGE_PREFERENCE = "min_age_preference";
        String MAX_AGE_PREFERENCE = "max_age_preference";
        String LOOKING_FOR = "looking_for";
        String MAX_DISTANCE = "max_distance";
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
        String USER_NAME= "userName";
        String USER_IMAGE= "userImage";
        String CHAT_ID = "chatID";
        String MESSAGES="messages";
        String USER_DETAIL="userDetail";
        String SENDER_ID="senderId";
        String RECEIVER_ID="receiverId";


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
