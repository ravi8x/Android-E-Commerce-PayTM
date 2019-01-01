package info.androidhive.paytmgateway.helper;

import info.androidhive.paytmgateway.db.model.CartItem;
import io.realm.RealmList;

public class Utils {
    public static float getCartPrice(RealmList<CartItem> cartItems) {
        float price = 0f;
        for (CartItem item : cartItems) {
            price += item.product.price * item.quantity;
        }
        return price;
    }
}
