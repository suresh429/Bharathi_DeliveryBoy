
package com.barathi.captain.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ProfileRoot {

    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private Long status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @SuppressWarnings("unused")
    public static class Data {

        @SerializedName("cash_to_pay")
        private Long cashToPay;
        @Expose
        private String fullname;
        @Expose
        private Long id;
        @SerializedName("is_avialable")
        private Long isAvialable;
        @SerializedName("mobile_no")
        private String mobileNo;
        @SerializedName("profile_image")
        private String profileImage;
        @SerializedName("total_deliveries")
        private Long totalDeliveries;
        @SerializedName("user_id")
        private String userId;
        @Expose
        private String username;

        public Long getCashToPay() {
            return cashToPay;
        }

        public void setCashToPay(Long cashToPay) {
            this.cashToPay = cashToPay;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getIsAvialable() {
            return isAvialable;
        }

        public void setIsAvialable(Long isAvialable) {
            this.isAvialable = isAvialable;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public Long getTotalDeliveries() {
            return totalDeliveries;
        }

        public void setTotalDeliveries(Long totalDeliveries) {
            this.totalDeliveries = totalDeliveries;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }
}
