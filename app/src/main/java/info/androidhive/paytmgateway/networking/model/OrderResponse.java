package info.androidhive.paytmgateway.networking.model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    public long id;
    @SerializedName("order_gateway_id")
    public String orderId;
    public String amount;
    public String status;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;
}
