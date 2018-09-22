package info.androidhive.paytmgateway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.paytmgateway.app.PrefManager;
import info.androidhive.paytmgateway.networking.ApiClient;
import info.androidhive.paytmgateway.networking.ApiService;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.PrepareOrderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private PrefManager prefs;
    ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = PrefManager.with(this);
        apiClient = ApiService.getClient().create(ApiClient.class);

        fetchAppConfig();
    }

    private void fetchAppConfig() {
        apiClient.getAppConfig().enqueue(new Callback<AppConfig>() {
            @Override
            public void onResponse(Call<AppConfig> call, Response<AppConfig> response) {
                Timber.e("onResponse %s", response);

                if (!response.isSuccessful()) {
                    // TODO - handle error
                    return;
                }

                prefs.setAppConfig(response.body());
            }

            @Override
            public void onFailure(Call<AppConfig> call, Throwable t) {
                Timber.e("onFailure %s", t.getMessage());
                // TODO - handle error
            }
        });
    }

    @OnClick(R.id.btn_pay)
    void pay() {
        getChecksum();
    }

    void getChecksum() {
        AppConfig config = prefs.getAppConfig();
        if (config == null) {
            // TODO config is null
            // handle this error
            Timber.e("App config is null! Can't place the order");
            return;
        }

        final Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback");
        paramMap.put("CHANNEL_ID", config.getChannel());
        paramMap.put("CUST_ID", "customer123");
        paramMap.put("INDUSTRY_TYPE_ID", config.getIndustryType());
        paramMap.put("MID", config.getMerchantId());
        paramMap.put("TXN_AMOUNT", "50");
        paramMap.put("WEBSITE", config.getWebsite());

        apiClient.placeOrder(paramMap).enqueue(new Callback<PrepareOrderResponse>() {
            @Override
            public void onResponse(Call<PrepareOrderResponse> call, Response<PrepareOrderResponse> response) {
                Timber.e("Checksum: " + response);
                if (!response.isSuccessful()) {
                    // TODO - handle error
                    return;
                }

                Timber.e("Placing order");

                paramMap.put("CHECKSUMHASH", response.body().getCheckSum());
                paramMap.put("ORDER_ID", response.body().getOrderId());
                placeOrder(paramMap);
            }

            @Override
            public void onFailure(Call<PrepareOrderResponse> call, Throwable t) {
                // TODO - handle error
                Timber.e("checksum onFailure %s", t.getMessage());
            }
        });
    }

    public void placeOrder(Map<String, String> params) {
        Timber.d("onStartTransaction");

        /*
        paramMap.put("MID" , "WorldP64425807474247");
        paramMap.put("ORDER_ID" , "210lkldfka2a27");
        paramMap.put("CUST_ID" , "mkjNYC1227");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1");
        paramMap.put("WEBSITE" , "worldpressplg");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/


        // TODO - decide on staging or prod
        // PaytmPGService.getProductionService()
        PaytmPGService Service = PaytmPGService.getStagingService();
        PaytmOrder Order = new PaytmOrder(params);

		/*PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Timber.e("someUIErrorOccurred: %s", inErrorMessage);
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

					/*@Override
					public void onTransactionSuccess(Bundle inResponse) {
						// After successful transaction this method gets called.
						// // Response bundle contains the merchant response
						// parameters.
						Log.d("LOG", "Payment Transaction is successful " + inResponse);
						Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onTransactionFailure(String inErrorMessage,
							Bundle inResponse) {
						// This method gets called if transaction failed. //
						// Here in this case transaction is completed, but with
						// a failure. // Error Message describes the reason for
						// failure. // Response bundle contains the merchant
						// response parameters.
						Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
						Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
					}*/

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Timber.e("onTransactionResponse: %s", inResponse.toString());
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Timber.e("networkNotAvailable");
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Timber.e("clientAuthenticationFailed: %s", inErrorMessage);
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Timber.e("onErrorLoadingWebPage: %d | %s | %s", iniErrorCode, inErrorMessage, inFailingUrl);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(MainActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Timber.e("onTransactionCancel: %s | %s", inErrorMessage, inResponse);
                    }

                });
    }
}
