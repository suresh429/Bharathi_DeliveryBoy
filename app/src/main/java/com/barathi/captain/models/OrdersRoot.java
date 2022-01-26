package com.barathi.captain.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrdersRoot {

    @SerializedName("data")
    private List<Datum> data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public List<Datum> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class Datum {


        @SerializedName("home_no")
        private String homeNo;

        @SerializedName("assigned_at")
        private String assignedAt;


        public String getAssignedAt() {
            return assignedAt;
        }

        public void setAssignedAt(String assignedAt) {
            this.assignedAt = assignedAt;
        }

        @SerializedName("area")
        private String area;

        @SerializedName("pincode")
        private String pincode;

        @SerializedName("address")
        private String address;

        @SerializedName("distance")
        private String distance;

        @SerializedName("city")
        private String city;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("society")
        private String society;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("street")
        private String street;

        @SerializedName("mobile_number")
        private String mobileNumber;

        @SerializedName("landmark")
        private String landmark;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("first_name")
        private String firstName;

        public String getHomeNo() {
            return homeNo;
        }

        public String getArea() {
            return area;
        }

        public String getPincode() {
            return pincode;
        }

        public String getAddress() {
            return address;
        }

        public String getDistance() {
            return distance;
        }

        public String getCity() {
            return city;
        }

        public String getLastName() {
            return lastName;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public String getSociety() {
            return society;
        }

        public String getUserId() {
            return userId;
        }

        public String getStreet() {
            return street;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getFirstName() {
            return firstName;
        }
    }
}