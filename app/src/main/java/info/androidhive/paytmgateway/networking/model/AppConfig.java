package info.androidhive.paytmgateway.networking.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppConfig extends RealmObject {
    @PrimaryKey
    int id = 0;

    @SerializedName("merchant_id")
    String merchantId;

    @SerializedName("channel")
    String channel;

    @SerializedName("industry_type")
    String industryType;

    @SerializedName("website")
    String website;

    public AppConfig() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getChannel() {
        return channel;
    }

    public String getIndustryType() {
        return industryType;
    }

    public String getWebsite() {
        return website;
    }
}
