package info.androidhive.paytmgateway.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;

import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.networking.ApiClient;
import info.androidhive.paytmgateway.networking.ApiService;
import info.androidhive.paytmgateway.db.model.User;
import info.androidhive.paytmgateway.networking.model.ErrorResponse;
import info.androidhive.paytmgateway.ui.login.LoginActivity;
import info.androidhive.paytmgateway.ui.main.MainActivity;
import okhttp3.ResponseBody;
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
        showErrorDialog(getString(R.string.msg_unknown));
    }

    public void handleError(ResponseBody responseBody) {
        String message = null;
        if (responseBody != null) {
            ErrorResponse errorResponse = new Gson().fromJson(responseBody.charStream(), ErrorResponse.class);
            message = errorResponse.error;
        }

        message = TextUtils.isEmpty(message) ? getString(R.string.msg_unknown) : message;
        showErrorDialog(message);
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog));
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                })
                .show();
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void launchSplash(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
