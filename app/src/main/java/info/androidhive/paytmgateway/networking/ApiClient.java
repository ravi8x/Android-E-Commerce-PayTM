package info.androidhive.paytmgateway.networking;

import java.util.Map;

import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.PrepareOrderResponse;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiClient {
    @GET("appConfig")
    Call<AppConfig> getAppConfig();

    @FormUrlEncoded
    @POST("prepareOrder")
    Call<PrepareOrderResponse> prepareOrder(@FieldMap Map<String, String> params);

    @POST("verifyChecksum")
    Call<Boolean> verifyChecksum();

}