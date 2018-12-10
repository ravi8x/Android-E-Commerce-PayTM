package info.androidhive.paytmgateway.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import info.androidhive.paytmgateway.networking.ApiClient;
import info.androidhive.paytmgateway.networking.ApiService;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {
    private static ApiClient mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ApiClient getApi() {
        if (mApi == null) {
            mApi = ApiService.getClient().create(ApiClient.class);
        }

        return mApi;
    }

    public void handleError(Throwable throwable) {
        Timber.e("Handle error: %s", throwable);
    }
}
