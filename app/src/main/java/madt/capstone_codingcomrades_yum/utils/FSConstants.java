package madt.capstone_codingcomrades_yum.utils;

public class FSConstants {

    public interface Collections{
        String USERS="users";
        String PREFERENCES="preferences";
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



}
