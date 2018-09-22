package info.androidhive.paytmgateway.networking.model;

import com.google.gson.annotations.SerializedName;

public class PrepareOrderResponse {
    @SerializedName("checksum")
    String checkSum;

    @SerializedName("order_id")
    String orderId;

    public String getCheckSum() {
        return checkSum;
    }

    public String getOrderId() {
        return orderId;
    }
}
