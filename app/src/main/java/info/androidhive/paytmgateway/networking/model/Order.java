package info.androidhive.paytmgateway.networking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    public long id;
    @SerializedName("order_gateway_id")
    public String orderId;
    public String amount;
    public String status;

    @SerializedName("order_items")
    public List<OrderItem> orderItems;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;
}
