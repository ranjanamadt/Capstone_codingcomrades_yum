package madt.capstone_codingcomrades_yum.utils;

public class FirebaseConstants {

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
    }



}
