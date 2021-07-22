package madt.capstone_codingcomrades_yum.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginUserDetail {
    String uuid;
    String firstName;
    String lastName;
    String dob;

    public LoginUserDetail(String uuid, String firstName, String lastName, String dob, String gender, String sePref, String aboutMe, String profileImage, List<String> interest, List<String> not_eat, List<String> not_talk, List<String> taste, List<String> enjoy_eating,List<String> report_list) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.sePref = sePref;
        this.aboutMe = aboutMe;
        this.profileImage = profileImage;
        this.interest = interest;
        this.not_eat = not_eat;
        this.not_talk = not_talk;
        this.taste = taste;
        this.enjoy_eating = enjoy_eating;
        this.report_list = report_list != null ? report_list : new ArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }
    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getSePref() {
        return sePref;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public List<String> getInterest() {
        return interest;
    }

    public List<String> getNot_eat() {
        return not_eat;
    }

    public List<String> getNot_talk() {
        return not_talk;
    }

    public List<? extends Object> getTaste() {
        return taste;
    }

    public List<String> getEnjoy_eating() {
        return enjoy_eating;
    }

    public List<String> getReport_list() {
        return report_list != null ? report_list : new ArrayList<>();
    }

    public void setReport_list(List<String> report_list) {
        this.report_list = report_list;
    }

    String gender;
    String sePref;
    String aboutMe;
    String profileImage;
    List<String> interest;
    List<String> not_eat;
    List<String> not_talk;
    List<String> taste;
    List<String> enjoy_eating;
    List<String> report_list;
}
