package com.barathi.captain.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailRoot {

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

        @SerializedName("home_no")
        private String homeNo;

        @SerializedName("area")
        private String area;

        @SerializedName("pincode")
        private String pincode;

        @SerializedName("address")
        private String address;

        @SerializedName("city")
        private String city;

        @SerializedName("latitude")
        private String latitude;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("delivery_address_id")
        private String deliveryAddressId;

        @SerializedName("payment_type")
        private String paymentType;

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("society")
        private String society;

        @SerializedName("total_amount")
        private int totalAmount;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("item_details")
        private List<ItemDetailsItem> itemDetails;

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

        @SerializedName("status")
        private String status;

        @SerializedName("longitude")
        private String longitude;

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

        public String getCity() {
            return city;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLastName() {
            return lastName;
        }

        public String getDeliveryAddressId() {
            return deliveryAddressId;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public String getSociety() {
            return society;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public String getUserId() {
            return userId;
        }

        public List<ItemDetailsItem> getItemDetails() {
            return itemDetails;
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

        public String getStatus() {
            return status;
        }

        public String getLongitude() {
            return longitude;
        }
    }

    public static class ItemDetailsItem {

        @SerializedName("unit")
        private String unit;

        @SerializedName("quantity")
        private String quantity;

        @SerializedName("item_id")
        private String itemId;

        @SerializedName("product_image")
        private List<String> productImage;

        @SerializedName("price")
        private String price;

        @SerializedName("product_id")
        private String productId;

        @SerializedName("name")
        private String name;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("price_unit_id")
        private String priceUnitId;

        public String getUnit() {
            return unit;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getItemId() {
            return itemId;
        }

        public List<String> getProductImage() {
            return productImage;
        }

        public String getPrice() {
            return price;
        }

        public String getProductId() {
            return productId;
        }

        public String getName() {
            return name;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getPriceUnitId() {
            return priceUnitId;
        }
    }
}