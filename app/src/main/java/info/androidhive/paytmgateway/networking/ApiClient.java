package info.androidhive.paytmgateway.networking;

import java.util.List;
import java.util.Map;

import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.PrepareOrderRequest;
import info.androidhive.paytmgateway.networking.model.PrepareOrderResponse;
import info.androidhive.paytmgateway.networking.model.Product;
import info.androidhive.paytmgateway.networking.model.login.LoginRequest;
import info.androidhive.paytmgateway.networking.model.register.UserRegisterRequest;
import info.androidhive.paytmgateway.networking.model.register.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiClient {
    @POST("login")
    Call<User> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<User> register(@Body UserRegisterRequest userRegisterRequest);

    @GET("appConfig")
    Call<AppConfig> getAppConfig();

    @FormUrlEncoded
    @POST("getChecksum")
    Call<PrepareOrderResponse> getCheckSum(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("verifyChecksum")
    Call<Boolean> verifyChecksum(@FieldMap Map<String, String> params);

    @GET("products")
    Call<List<Product>> getProducts();

    @POST("prepareOrder")
    Call<PrepareOrderResponse> prepareOrder(@Body PrepareOrderRequest request);
}