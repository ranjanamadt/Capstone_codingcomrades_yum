package madt.capstone_codingcomrades_yum.chat;

import java.util.HashMap;

import madt.capstone_codingcomrades_yum.utils.FSConstants;

public class UserDetail {

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
    String lastName;

    public UserDetail(HashMap<String, Object> userDetail) {
        this.firstName = userDetail.get(FSConstants.USER.FIRST_NAME).toString();
        this.lastName = userDetail.get(FSConstants.USER.FIRST_NAME).toString();

    }


}
