package info.androidhive.paytmgateway.db;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import info.androidhive.paytmgateway.db.model.Cart;
import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import timber.log.Timber;

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
        Timber.e("addItemToCart: %d", product.id);
        Cart cart = getCart();

        if (cart == null) {
            Timber.e("cart is null. Creating new one");
            Realm.getDefaultInstance().executeTransaction(realm -> {
                List<CartItem> cartItems;
                Cart cart1 = realm.createObject(Cart.class, 0);
                CartItem cartItem = new CartItem();
                cartItem.product = product;
                cartItem.quantity += 1;
                cartItems = new ArrayList<>();
                cartItems.add(cartItem);
                cart1.cartItems.addAll(cartItems);

                Timber.e("Cart1: %s", cart1);

                realm.copyToRealmOrUpdate(cart1);
            });
        } else {
            // existed cart is present
            Timber.e("cart items size: %d", cart.cartItems.size());
            CartItem item = cart.cartItems.where().equalTo("product.id", product.id).findFirst();
            if (item != null) {
                Timber.e("cart item found: %s", item);
                Realm.getDefaultInstance().executeTransaction(realm -> {
                    item.quantity += 1;
                    cart.cartItems.set(cart.cartItems.indexOf(item), item);
                    realm.copyToRealmOrUpdate(cart);
                });

            } else {
                Timber.e("cart item null. Creating new one");
                CartItem cartItem = new CartItem();
                cartItem.product = product;
                cartItem.quantity += 1;
                Realm.getDefaultInstance().executeTransaction(realm -> {
                    cart.cartItems.add(cartItem);
                    realm.copyToRealmOrUpdate(cart);
                });
            }
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
