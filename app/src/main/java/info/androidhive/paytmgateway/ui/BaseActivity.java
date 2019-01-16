package info.androidhive.paytmgateway.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.networking.ApiClient;
import info.androidhive.paytmgateway.networking.ApiService;
import info.androidhive.paytmgateway.networking.model.register.User;
import info.androidhive.paytmgateway.ui.login.LoginActivity;
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

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void checkSession(Activity activity) {
        User user = AppDatabase.getUser();
        if (user == null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
