package info.androidhive.paytmgateway.helper;

import info.androidhive.paytmgateway.db.model.CartItem;
import io.realm.RealmResults;

public class Utils {
    public static float getCartPrice(RealmResults<CartItem> cartItems) {
        float price = 0f;
        for (CartItem item : cartItems) {
            price += item.product.price * item.quantity;
        }
        return price;
    }
}
