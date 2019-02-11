package info.androidhive.paytmgateway.networking;

import java.util.List;
import java.util.Map;

import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.ChecksumResponse;
import info.androidhive.paytmgateway.networking.model.Order;
import info.androidhive.paytmgateway.networking.model.PrepareOrderRequest;
import info.androidhive.paytmgateway.networking.model.PrepareOrderResponse;
import info.androidhive.paytmgateway.networking.model.Product;
import info.androidhive.paytmgateway.networking.model.LoginRequest;
import info.androidhive.paytmgateway.networking.model.RegisterRequest;
import info.androidhive.paytmgateway.db.model.User;
import info.androidhive.paytmgateway.networking.model.Transaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiClient {
    @POST("login")
    Call<User> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("appConfig")
    Call<AppConfig> getAppConfig();

    @FormUrlEncoded
    @POST("getChecksum")
    Call<ChecksumResponse> getCheckSum(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("transactionStatus")
    Call<Order> checkTransactionStatus(@Field("order_gateway_id") String orderId);

    @GET("products")
    Call<List<Product>> getProducts();

    @POST("prepareOrder")
    Call<PrepareOrderResponse> prepareOrder(@Body PrepareOrderRequest request);

    @GET("transactions")
    Call<List<Transaction>> getTransactions();
}