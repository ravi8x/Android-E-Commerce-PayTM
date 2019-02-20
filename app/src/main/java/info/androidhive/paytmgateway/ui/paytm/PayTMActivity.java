package info.androidhive.paytmgateway.ui.paytm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.paytmgateway.BuildConfig;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.app.Constants;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.db.model.User;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.ChecksumResponse;
import info.androidhive.paytmgateway.networking.model.Order;
import info.androidhive.paytmgateway.networking.model.OrderItem;
import info.androidhive.paytmgateway.networking.model.PrepareOrderRequest;
import info.androidhive.paytmgateway.networking.model.PrepareOrderResponse;
import info.androidhive.paytmgateway.ui.base.BaseActivity;
import info.androidhive.paytmgateway.ui.transactions.TransactionsActivity;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PayTMActivity extends BaseActivity {
    @BindView(R.id.lbl_status)
    TextView lblStatus;

    @BindView(R.id.loader)
    AVLoadingIndicatorView loader;

    @BindView(R.id.icon_status)
    ImageView iconStatus;

    @BindView(R.id.status_message)
    TextView statusMessage;

    @BindView(R.id.title_status)
    TextView responseTitle;

    @BindView(R.id.btn_check_orders)
    TextView btnCheckOrders;

    @BindView(R.id.layout_order_placed)
    LinearLayout layoutOrderPlaced;

    private Realm realm;
    private AppConfig appConfig;
    private User user;

    /**
     * Steps to process order:
     * 1. Make server call to prepare the order. Which will create a new order in the db
     * and returns the unique Order ID
     * <p>
     * 2. Once the order ID is received, send the PayTM params to server to calculate the
     * Checksum Hash
     * <p>
     * 3. Send the PayTM params along with checksum hash to PayTM gateway
     * <p>
     * 4. Once the payment is done, send the Order Id back to server to verify the
     * transaction status
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_tm);
        ButterKnife.bind(this);
        setToolbar();
        enableToolbarUpNavigation();
        getSupportActionBar().setTitle(getString(R.string.title_preparing_order));
        changeStatusBarColor();
        init();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_tm;
    }

    private void init() {
        realm = Realm.getDefaultInstance();
        realm.where(CartItem.class).findAllAsync()
                .addChangeListener(cartItems -> {

                });

        user = AppDatabase.getUser();
        appConfig = realm.where(AppConfig.class).findFirst();

        prepareOrder();
    }

    private void setStatus(int message) {
        lblStatus.setText(message);
    }

    /**
     * STEP 1: Sending all the cart items to server and receiving the
     * unique order id that needs to be sent to PayTM
     */
    private void prepareOrder() {
        setStatus(R.string.msg_preparing_order);

        List<CartItem> cartItems = realm.where(CartItem.class).findAll();
        PrepareOrderRequest request = new PrepareOrderRequest();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.productId = cartItem.product.id;
            orderItem.quantity = cartItem.quantity;
            orderItems.add(orderItem);
        }

        request.orderItems = orderItems;

        getApi().prepareOrder(request).enqueue(new Callback<PrepareOrderResponse>() {
            @Override
            public void onResponse(Call<PrepareOrderResponse> call, Response<PrepareOrderResponse> response) {
                if (!response.isSuccessful()) {
                    handleUnknownError();
                    showOrderStatus(false);
                    return;
                }

                getChecksum(response.body());
            }

            @Override
            public void onFailure(Call<PrepareOrderResponse> call, Throwable t) {
                handleError(t);
                showOrderStatus(false);
            }
        });
    }

    /**
     * STEP 2:
     * Sending the params to server to generate the Checksum
     * that needs to be sent to PayTM
     */
    void getChecksum(PrepareOrderResponse response) {
        setStatus(R.string.msg_fetching_checksum);

        if (appConfig == null) {
            Timber.e("App config is null! Can't place the order. This usually shouldn\'t happen");
            // navigating user to login screen
            launchLogin(PayTMActivity.this);
            finish();
            return;
        }

        Map<String, String> paramMap = preparePayTmParams(response);
        Timber.d("PayTm Params: %s", paramMap);

        getApi().getCheckSum(paramMap).enqueue(new Callback<ChecksumResponse>() {
            @Override
            public void onResponse(Call<ChecksumResponse> call, Response<ChecksumResponse> response) {
                if (!response.isSuccessful()) {
                    Timber.e("Network call failed");
                    handleUnknownError();
                    showOrderStatus(false);
                    return;
                }

                Timber.d("Checksum Received: " + response.body().checksum);

                // Add the checksum to existing params list and send them to PayTM
                paramMap.put("CHECKSUMHASH", response.body().checksum);
                placeOrder(paramMap);
            }

            @Override
            public void onFailure(Call<ChecksumResponse> call, Throwable t) {
                handleError(t);
                showOrderStatus(false);
            }
        });
    }

    public Map<String, String> preparePayTmParams(PrepareOrderResponse response) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("CALLBACK_URL", String.format(BuildConfig.PAYTM_CALLBACK_URL, response.orderGatewayId));
        paramMap.put("CHANNEL_ID", appConfig.getChannel());
        paramMap.put("CUST_ID", "CUSTOMER_" + user.id);
        paramMap.put("INDUSTRY_TYPE_ID", appConfig.getIndustryType());
        paramMap.put("MID", appConfig.getMerchantId());
        paramMap.put("WEBSITE", appConfig.getWebsite());
        paramMap.put("ORDER_ID", response.orderGatewayId);
        paramMap.put("TXN_AMOUNT", response.amount);
        return paramMap;
    }


    /**
     * STEP 3: Redirecting to PayTM gateway with necessary params along with checksum
     * This will redirect to PayTM gateway and gives us the PayTM transaction response
     */
    public void placeOrder(Map<String, String> params) {
        setStatus(R.string.msg_redirecting_to_paytm);

        // choosing between PayTM staging and production
        PaytmPGService pgService = BuildConfig.IS_PATM_STAGIN ? PaytmPGService.getStagingService() : PaytmPGService.getProductionService();

        PaytmOrder Order = new PaytmOrder(params);

        pgService.initialize(Order, null);

        pgService.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Timber.e("someUIErrorOccurred: %s", inErrorMessage);
                        finish();
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Timber.d("PayTM Transaction Response: %s", inResponse.toString());
                        String orderId = inResponse.getString("ORDERID");
                        verifyTransactionStatus(orderId);
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Timber.e("networkNotAvailable");
                        finish();
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Timber.e("clientAuthenticationFailed: %s", inErrorMessage);
                        finish();
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
                        finish();
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(PayTMActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Timber.e("onTransactionCancel: %s | %s", inErrorMessage, inResponse);
                        finish();
                    }
                });
    }

    /**
     * STEP 4: Verifying the transaction status once PayTM transaction is over
     * This makes server(own) -> server(PayTM) call to verify the transaction status
     */
    private void verifyTransactionStatus(String orderId) {
        setStatus(R.string.msg_verifying_status);
        getApi().checkTransactionStatus(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (!response.isSuccessful()) {
                    Timber.e("Network call failed");
                    handleUnknownError();
                    showOrderStatus(false);
                    return;
                }

                showOrderStatus(response.body().status.equalsIgnoreCase(Constants.ORDER_STATUS_COMPLETED));
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                handleError(t);
                showOrderStatus(false);
            }
        });
    }

    /*
     * Displaying Order Status on UI. This toggles UI between success and failed cases
     * */
    private void showOrderStatus(boolean isSuccess) {
        loader.setVisibility(View.GONE);
        lblStatus.setVisibility(View.GONE);
        if (isSuccess) {
            iconStatus.setImageResource(R.drawable.baseline_check_black_48);
            iconStatus.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
            responseTitle.setText(R.string.thank_you);
            statusMessage.setText(R.string.msg_order_placed_successfully);

            // as the order placed successfully, clear the cart
            AppDatabase.clearCart();
        } else {
            iconStatus.setImageResource(R.drawable.baseline_close_black_48);
            iconStatus.setColorFilter(ContextCompat.getColor(this, R.color.btn_remove_item));
            responseTitle.setText(R.string.order_failed);
            statusMessage.setText(R.string.msg_order_placed_failed);
        }

        layoutOrderPlaced.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_check_orders)
    void onOrdersClick() {
        startActivity(new Intent(PayTMActivity.this, TransactionsActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
    }
}
