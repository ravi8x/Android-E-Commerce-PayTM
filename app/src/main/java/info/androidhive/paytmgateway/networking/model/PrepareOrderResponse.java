package info.androidhive.paytmgateway.networking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrepareOrderResponse {
    public long id;

    @SerializedName("order_gateway_id")
    public String orderGatewayId;

    @SerializedName("amount")
    public String amount;

    @SerializedName("order_items")
    public List<OrderItem> orderItems;

    public String status;
}
