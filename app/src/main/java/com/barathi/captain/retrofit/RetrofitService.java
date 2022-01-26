package com.barathi.captain.retrofit;

import com.barathi.captain.models.CompleteOrderRoot;
import com.barathi.captain.models.DeliveryBoyRoot;
import com.barathi.captain.models.OrderDetailRoot;
import com.barathi.captain.models.OrdersRoot;
import com.barathi.captain.models.ProfileRoot;
import com.barathi.captain.models.RestResponse;
import com.barathi.captain.models.RestResponse2;
import com.barathi.captain.models.UpdateProfileRoot;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("DeliveryBoy/userLogin")
    Call<DeliveryBoyRoot> login(@Header("unique-key") String key,
                                @Field("username") String username,
                                @Field("password") String pwd,
                                @Field("device_type") String devicetype,
                                @Field("device_token") String devicetoken);

    @FormUrlEncoded
    @POST("DeliveryBoy/getPendingOrders")
    Call<OrdersRoot> getPendingOrders(@Header("unique-key") String key, @Header("Authorization") String token,
                                      @Field("latitude") String username,
                                      @Field("longitude") String pwd,
                                      @Field("start") int start,
                                      @Field("limit") int limit);


    @FormUrlEncoded
    @POST("DeliveryBoy/getCompletedOrders")
    Call<CompleteOrderRoot> getCompleteOrders(@Header("unique-key") String key, @Header("Authorization") String token,
                                              @Field("start") int start,
                                              @Field("limit") int limit);

    @FormUrlEncoded
    @POST("DeliveryBoy/startDelivery")
    Call<RestResponse> startDelivery(@Header("unique-key") String key, @Header("Authorization") String token,
                                     @Field("order_id") String orderId);


    @FormUrlEncoded
    @POST("DeliveryBoy/completeDelivery")
    Call<RestResponse> completeDelivery(@Header("unique-key") String key, @Header("Authorization") String token,
                                        @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("DeliveryBoy/onHoldDelivery")
    Call<RestResponse> onHoldDelivery(@Header("unique-key") String key, @Header("Authorization") String token,
                                      @Field("order_id") String orderId,
                                      @Field("reason_type") String reason);

    @FormUrlEncoded
    @POST("DeliveryBoy/getOrderDetails")
    Call<OrderDetailRoot> getOrdersDetail(@Header("unique-key") String key, @Header("Authorization") String token,
                                          @Field("user_id") String username,
                                          @Field("order_id") String pwd);

    @GET("DeliveryBoy/getProfile")
    Call<ProfileRoot> getProfile(@Header("unique-key") String key, @Header("Authorization") String token);


    @POST("DeliveryBoy/Logout")
    Call<RestResponse2> logOut(@Header("unique-key") String key, @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("DeliveryBoy/changeAvialableStatus")
    Call<RestResponse> changeStatus(@Header("unique-key") String key, @Header("Authorization") String token, @Field("is_avialable") Long status);

    @Multipart
    @POST("DeliveryBoy/updateProfile")
    Call<UpdateProfileRoot> updateUser(@Header("unique-key") String key,
                                       @Header("Authorization") String token,
                                       @PartMap Map<String, RequestBody> partMap,
                                       @Part MultipartBody.Part requestBody);

}
