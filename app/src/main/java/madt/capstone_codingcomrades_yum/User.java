package madt.capstone_codingcomrades_yum;

public class User {
    public User(String firstName, String lastName, String dob, String gender, String sePref) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.sePref = sePref;
    }

    String firstName;
    String lastName;
    String dob;

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

    String gender;
    String sePref;
}
