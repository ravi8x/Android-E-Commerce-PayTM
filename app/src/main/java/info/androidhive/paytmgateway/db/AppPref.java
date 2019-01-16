package info.androidhive.paytmgateway.db;

import android.content.Context;
import android.content.SharedPreferences;

import info.androidhive.paytmgateway.app.MyApplication;

public class AppPref {

    private static AppPref singleTonInstance = null;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String PREF_NAME = "app_prefs";
    private static final int PRIVATE_MODE = 0;
    private static final String KEY_AUTH_TOKEN = "auth_token";

    public static AppPref getInstance() {
        if (singleTonInstance == null) {
            singleTonInstance = new AppPref(MyApplication.getInstance().getApplicationContext());
        }
        return singleTonInstance;
    }

    private AppPref(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void saveAuthToken(String authToken) {
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.commit();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void clearData() {
        editor.clear().commit();
    }
}
