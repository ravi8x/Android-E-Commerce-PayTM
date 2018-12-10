package info.androidhive.paytmgateway.db;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.paytmgateway.db.model.Cart;
import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppDatabase {
    public AppDatabase() {
    }

    public static void saveAppConfig(final AppConfig appConfig) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(appConfig));
    }

    public static void saveProducts(final List<Product> products) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            for (Product product : products) {
                realm.copyToRealmOrUpdate(product);
            }
        });
    }

    public static RealmResults<Product> getProducts() {
        return Realm.getDefaultInstance().where(Product.class).findAll();
    }

    public static void addItemToCart(Product product) {
        Cart cart = getCart();
        Realm realm = Realm.getDefaultInstance();

        if (cart == null) {
            realm.executeTransactionAsync(realm1 -> {
                List<CartItem> cartItems;
                Cart cart1 = Realm.getDefaultInstance().createObject(Cart.class, 0);
                CartItem cartItem = new CartItem();
                cartItem.product = product;
                cartItem.quantity += 1;
                cartItems = new ArrayList<>();
                cartItems.add(cartItem);

                realm1.copyToRealmOrUpdate(cart1);
            });
        } else {
            // existed cart is present
        }
    }

    public static void removeCartItem(Product product) {

    }

    public static Cart getCart() {
        return Realm.getDefaultInstance().where(Cart.class).findFirst();
    }

    public static void clearCart() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(Cart.class));
    }
}
