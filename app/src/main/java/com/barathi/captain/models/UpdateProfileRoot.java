package com.barathi.captain.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("mobile_no")
        private String mobileNo;

        @SerializedName("id")
        private int id;

        @SerializedName("fullname")
        private String fullname;

        @SerializedName("username")
        private String username;

        public String getProfileImage() {
            return profileImage;
        }

        public String getUserId() {
            return userId;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public int getId() {
            return id;
        }

        public String getFullname() {
            return fullname;
        }

        public String getUsername() {
            return username;
        }
    }
}