package info.androidhive.paytmgateway.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.db.AppPref;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import info.androidhive.paytmgateway.ui.login.LoginActivity;
import info.androidhive.paytmgateway.ui.main.MainActivity;
import info.androidhive.paytmgateway.ui.register.RegisterActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);

        if (TextUtils.isEmpty(AppPref.getInstance().getAuthToken())) {
            // user token is not present, take him to login screen
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        changeStatusBarColor();
        fetchAppConfig();
    }

    private void fetchAppConfig() {
        Call<AppConfig> call = getApi().getAppConfig();
        call.enqueue(new Callback<AppConfig>() {
            @Override
            public void onResponse(Call<AppConfig> call, Response<AppConfig> response) {
                if (!response.isSuccessful()) {
                    handleError(response.errorBody());
                    return;
                }

                // save app config to db
                AppDatabase.saveAppConfig(response.body());

                // fetch products
                fetchProducts();
            }

            @Override
            public void onFailure(Call<AppConfig> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void fetchProducts() {
        Call<List<Product>> call = getApi().getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    handleError(response.errorBody());
                    return;
                }

                // store products in db
                AppDatabase.saveProducts(response.body());

                // start home screen
                launchHomeScreen();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
