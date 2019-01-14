package info.androidhive.paytmgateway.networking.model;

import java.util.List;

public class PrepareOrderRequest {
    public String orderId;
    public List<OrderItem> orderItems;
}
