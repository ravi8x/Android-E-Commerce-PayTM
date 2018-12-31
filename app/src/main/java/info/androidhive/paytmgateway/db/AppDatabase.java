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

    /**
     * Adding product to cart
     * Will create a new cart entry if there is no cart created yet
     * Will increase the product quantity count if the item exists already
     */
    public static void addItemToCart(Product product) {
        Cart cart = getCart();
        if (cart == null) {
            // no cart existed
            initNewCart(product);
        } else {
            // cart already present
            addProductToCart(cart, product);
        }
    }

    private static void initNewCart(Product product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            List<CartItem> cartItems;
            Cart cart1 = realm.createObject(Cart.class, 0);
            CartItem cartItem = new CartItem();
            cartItem.product = product;
            cartItem.quantity += 1;
            cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            cart1.cartItems.addAll(cartItems);
            realm.copyToRealmOrUpdate(cart1);
        });
    }

    private static void addProductToCart(Cart cart, Product product) {
        CartItem item = cart.cartItems.where().equalTo("product.id", product.id).findFirst();
        if (item != null) {
            Realm.getDefaultInstance().executeTransaction(realm -> {
                item.quantity += 1;
                cart.cartItems.set(cart.cartItems.indexOf(item), item);
                realm.copyToRealmOrUpdate(cart);
            });
        } else {
            CartItem cartItem = new CartItem();
            cartItem.product = product;
            cartItem.quantity += 1;
            Realm.getDefaultInstance().executeTransaction(realm -> {
                cart.cartItems.add(cartItem);
                realm.copyToRealmOrUpdate(cart);
            });
        }
    }

    public static void removeCartItem(Product product) {
        Cart cart = getCart();
        CartItem item = cart.cartItems.where().equalTo("product.id", product.id).findFirst();
        if (item != null) {
            Realm.getDefaultInstance().executeTransaction(realm -> {
                // if the quantity is 1, remove the item
                if (item.quantity == 1) {
                    cart.cartItems.remove(cart.cartItems.indexOf(item));
                } else {
                    item.quantity -= 1;
                    cart.cartItems.set(cart.cartItems.indexOf(item), item);
                }
                realm.copyToRealmOrUpdate(cart);
            });
        }
    }

    public static Cart getCart() {
        return Realm.getDefaultInstance().where(Cart.class).findFirst();
    }

    public static void clearCart() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(Cart.class));
    }
}
