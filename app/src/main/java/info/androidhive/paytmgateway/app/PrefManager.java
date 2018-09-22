package info.androidhive.paytmgateway.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import info.androidhive.paytmgateway.networking.model.AppConfig;

public class PrefManager {
    private static PrefManager instance;
    private final SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("paytmgateway", Context.MODE_PRIVATE);
    }

    public static PrefManager with(Context context) {
        if (instance == null) {
            instance = new PrefManager(context);
        }
        return instance;
    }

    public void setAppConfig(AppConfig config){
        editor = sharedPreferences.edit();
        editor.putString("app_config", new Gson().toJson(config));
        editor.commit();
    }

    public AppConfig getAppConfig(){
        String configJson = sharedPreferences.getString("app_config", null);
        if(!TextUtils.isEmpty(configJson)){
            return new Gson().fromJson(configJson, AppConfig.class);
        }

        return null;
    }
}
