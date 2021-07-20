package madt.capstone_codingcomrades_yum.login;

public class LoginUserDetail {
    String uuid;
    String firstName;
    String lastName;
    String dob;

    public LoginUserDetail(String uuid, String firstName, String lastName, String dob, String gender, String sePref, String aboutMe, String profileImage, Object[] interest, Object[] not_eat, Object[] not_talk, Object[] taste, Object[] enjoy_eating) {
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
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
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

    public Object[] getInterest() {
        return interest;
    }

    public Object[] getNot_eat() {
        return not_eat;
    }

    public Object[] getNot_talk() {
        return not_talk;
    }

    public Object[] getTaste() {
        return taste;
    }

    public Object[] getEnjoy_eating() {
        return enjoy_eating;
    }

    String gender;
    String sePref;
    String aboutMe;
    String profileImage;
    Object[] interest;
    Object[] not_eat;
    Object[] not_talk;
    Object[] taste;
    Object[] enjoy_eating;
}
