package madt.capstone_codingcomrades_yum;

public class User {

    String uuid;
    String firstName;
    String lastName;
    String dob;
    String gender;
    String sePref;
    String aboutMe;
    Object[] interest;
    Object[] not_eat;
    Object[] not_talk;
    Object[] taste;
    Object[] enjoy_eating;

    public User(String uuid, String firstName, String lastName, String dob, String gender, String sePref, String aboutMe, Object[] interest, Object[] not_eat, Object[] not_talk, Object[] taste, Object[] enjoy_eating) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.sePref = sePref;
        this.aboutMe = aboutMe;
        this.interest = interest;
        this.not_eat = not_eat;
        this.not_talk = not_talk;
        this.taste = taste;
        this.enjoy_eating = enjoy_eating;
    }

    public String getUuid() {
        return uuid;
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

    public Object[] getInterest() {
        return interest;
    }

    public void setInterest(Object[] interest) {
        this.interest = interest;
    }

    public Object[] getNot_eat() {
        return not_eat;
    }

    public void setNot_eat(Object[] not_eat) {
        this.not_eat = not_eat;
    }

    public Object[] getNot_talk() {
        return not_talk;
    }

    public void setNot_talk(Object[] not_talk) {
        this.not_talk = not_talk;
    }

    public Object[] getTaste() {
        return taste;
    }

    public void setTaste(Object[] taste) {
        this.taste = taste;
    }

    public Object[] getEnjoy_eating() {
        return enjoy_eating;
    }

    public void setEnjoy_eating(Object[] enjoy_eating) {
        this.enjoy_eating = enjoy_eating;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName(){
        return firstName + " " + lastName;
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


}
