package com.barathi.captain.models;

import com.google.gson.annotations.SerializedName;

public class RestResponse2 {

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("success_code")
    private int successCode;

    @SerializedName("response_message")
    private String responseMessage;

    public int getResponseCode() {
        return responseCode;
    }

    public int getSuccessCode() {
        return successCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}