package info.androidhive.paytmgateway.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import info.androidhive.paytmgateway.networking.model.register.UserRegisterRequest;
import info.androidhive.paytmgateway.networking.model.register.UserRegisterResponse;
import info.androidhive.paytmgateway.ui.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);
        changeStatusBarColor();

        registerDevice();
        fetchAppConfig();
    }

    private void registerDevice() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UserRegisterRequest request = new UserRegisterRequest();
        request.deviceId = deviceId;

        getApi().registerDevice(request).enqueue(new Callback<UserRegisterResponse>() {
            @Override
            public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                if (!response.isSuccessful()) {
                    // TODO
                    // unable to register device
                    Timber.e("Unable to register device");
                    return;
                }

                Timber.e("User registered! %s", response.body().authToken);

                // TODO - store user auth token
                fetchAppConfig();
            }

            @Override
            public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                Timber.e("Failed to register device");
            }
        });
    }

    private void fetchAppConfig() {
        Call<AppConfig> call = getApi().getAppConfig();
        call.enqueue(new Callback<AppConfig>() {
            @Override
            public void onResponse(Call<AppConfig> call, Response<AppConfig> response) {
                if (!response.isSuccessful()) {
                    handleError(null);
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
                    handleError(null);
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

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
