package com.example.test.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoResponse implements Serializable {

    @SerializedName("userId")
    private String userId;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;

    public UserInfoResponse(String userId,String fullname,String phone, String email){
        this.userId = userId;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
