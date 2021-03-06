package madt.capstone_codingcomrades_yum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import madt.capstone_codingcomrades_yum.utils.FSConstants;

public class User {
    DocumentSnapshot document;
    String uuid;
    String firstName;
    String lastName;
    String dob;
    String gender;
    String sePref;
    String aboutMe;
    Double latitude;
    Double longitude;
    int minAge;
    int maxAge;
    String lookingFor="";
    int maxDistance;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String phoneNumber;
    List<String> profileImage;

    String deviceToken;

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    Boolean activeStatus;
    List<String> interest;
    List<String> not_eat;
    List<String> not_talk;
    List<String> taste;
    List<String> enjoy_eating;
    List<String> report_list;
    List<String> matched_users;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    List<String> preferences;

    public List<String> getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(List<String> profileImage) {
        this.profileImage = profileImage;
    }

    public User() {

    }

    public User(DocumentSnapshot document) {
        this.document = document;

        try {
            this.uuid = document.getId();
            this.firstName = document.get(FSConstants.USER.FIRST_NAME).toString();
            this.lastName = document.get(FSConstants.USER.LAST_NAME).toString();
            this.dob = document.get(FSConstants.USER.DOB).toString();
            this.gender = document.get(FSConstants.USER.GENDER).toString();
            this.sePref = document.get(FSConstants.USER.SEX_PREFER).toString();
            this.deviceToken = document.get(FSConstants.USER.DEVICE_TOKEN).toString();
            this.phoneNumber = document.get(FSConstants.USER.PHONE_NUMBER).toString();
            this.aboutMe = document.get(FSConstants.USER.ABOUT_ME) != null ? document.get(FSConstants.USER.ABOUT_ME).toString() : "";
            this.profileImage = (List<String>) document.get(FSConstants.USER.PROFILE_IMAGE);
            this.interest = (List<String>) document.get(FSConstants.PREFERENCE_TYPE.INTEREST);
            this.not_eat = (List<String>) document.get(FSConstants.PREFERENCE_TYPE.NOT_EAT);
            this.not_talk = (List<String>) document.get(FSConstants.PREFERENCE_TYPE.NOT_TALK);
            this.taste = (List<String>) document.get(FSConstants.PREFERENCE_TYPE.TASTE);
            this.enjoy_eating = (List<String>) document.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING);
            this.preferences = (List<String>) document.get(FSConstants.USER.PREFERENCES);
            this.activeStatus = (Boolean) document.get(FSConstants.USER.ACTIVE_STATUS);
            this.report_list = (List<String>) document.get(FSConstants.USER.REPORT_LIST) != null ? (List<String>) document.get(FSConstants.USER.REPORT_LIST) : new ArrayList<>();
            this.matched_users = (List<String>) document.get(FSConstants.USER.MATCHED_USERS) != null ? (List<String>) document.get(FSConstants.USER.MATCHED_USERS) : new ArrayList<>();
            this.latitude = Double.parseDouble(document.get(FSConstants.USER.LATITUDE).toString());
            this.longitude = Double.parseDouble(document.get(FSConstants.USER.LONGITUDE).toString());
            this.minAge = Integer.parseInt(document.get(FSConstants.USER.MIN_AGE_PREFERENCE).toString());
            this.maxAge = Integer.parseInt(document.get(FSConstants.USER.MAX_AGE_PREFERENCE).toString());
            this.lookingFor = document.get(FSConstants.USER.LOOKING_FOR) != null ? document.get(FSConstants.USER.LOOKING_FOR).toString() : "Any";
            this.maxDistance = Integer.parseInt(document.get(FSConstants.USER.MAX_DISTANCE).toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getMatched_users() {
        return matched_users;
    }

    public void setMatched_users(List<String> matched_users) {
        this.matched_users = matched_users;
    }

    public String getUuid() {
        return uuid;
    }

    public List<String> getReport_list() {
        return report_list;
    }

    public void setReport_list(List<String> report_list) {
        this.report_list = report_list;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public List<String> getInterest() {
        return interest;
    }

    public void setInterest(List<String> interest) {
        this.interest = interest;
    }

    public List<String> getNot_eat() {
        return not_eat;
    }

    public void setNot_eat(List<String> not_eat) {
        this.not_eat = not_eat;
    }

    public List<String> getNot_talk() {
        return not_talk;
    }

    public void setNot_talk(List<String> not_talk) {
        this.not_talk = not_talk;
    }

    public List<String> getTaste() {
        return taste;
    }

    public void setTaste(List<String> taste) {
        this.taste = taste;
    }

    public List<String> getEnjoy_eating() {
        return enjoy_eating;
    }

    public void setEnjoy_eating(List<String> enjoy_eating) {
        this.enjoy_eating = enjoy_eating;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getAge() {
        Date dobObj = null;
        try {
            dobObj = new SimpleDateFormat("MMM d, yyyy").parse(this.getDob());
            Date today = new Date();
            long difference_In_Time = today.getTime() - dobObj.getTime();
            return (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Bitmap getProfileBitmapImage() {
        byte[] decodedString = Base64.decode(this.getProfileImage().get(0), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSePref() {
        return sePref;
    }

    public void setSePref(String sePref) {
        this.sePref = sePref;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Location getLocationObject(){
        Location locationLoginUser = new Location("locationA");// creates the location a object
        locationLoginUser.setLatitude(latitude != null ? latitude : -180);// sets the location a latitude
        locationLoginUser.setLongitude(longitude != null ? longitude : -180);// sets the location a longitude
        return locationLoginUser;
    }
}
