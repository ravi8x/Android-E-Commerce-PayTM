package info.androidhive.paytmgateway.db;

import java.util.List;

import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import info.androidhive.paytmgateway.db.model.User;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppDatabase {
    public AppDatabase() {
    }

    public static void saveUser(User user) {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(user));
    }

    public static User getUser() {
        return Realm.getDefaultInstance().where(User.class).findFirst();
    }

    public static void saveAppConfig(final AppConfig appConfig) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(appConfig));
    }

    public static AppConfig getAppConfig() {
        return Realm.getDefaultInstance().where(AppConfig.class).findFirst();
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
        initNewCart(product);
        /*Cart cart = getCart();
        if (cart == null) {
            // no cart existed
            initNewCart(product);
        } else {
            // cart already present
            addProductToCart(cart, product);
        }*/
    }

    private static void initNewCart(Product product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItem cartItem = realm.where(CartItem.class).equalTo("product.id", product.id).findFirst();
            if (cartItem == null) {
                CartItem ci = new CartItem();
                ci.product = product;
                ci.quantity = 1;
                realm.copyToRealmOrUpdate(ci);
            } else {
                cartItem.quantity += 1;
                realm.copyToRealmOrUpdate(cartItem);
            }
            /*
            List<CartItem> cartItems;
            Cart cart1 = realm.createObject(Cart.class, 0);
            CartItem cartItem = new CartItem();
            cartItem.product = product;
            cartItem.quantity += 1;
            cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            cart1.cartItems.addAll(cartItems);
            realm.copyToRealmOrUpdate(cart1);*/
        });
    }

    private static void addProductToCart(Product product) {
        initNewCart(product);
        /*CartItem item = cart.cartItems.where().equalTo("product.id", product.id).findFirst();
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
        }*/
    }

    public static void removeCartItem(Product product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItem cartItem = realm.where(CartItem.class).equalTo("product.id", product.id).findFirst();
            if (cartItem != null) {
                if (cartItem.quantity == 1) {
                    cartItem.deleteFromRealm();
                } else {
                    cartItem.quantity -= 1;
                    realm.copyToRealmOrUpdate(cartItem);
                }
            }
            /*
            List<CartItem> cartItems;
            Cart cart1 = realm.createObject(Cart.class, 0);
            CartItem cartItem = new CartItem();
            cartItem.product = product;
            cartItem.quantity += 1;
            cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            cart1.cartItems.addAll(cartItems);
            realm.copyToRealmOrUpdate(cart1);*/
        });
        /*Cart cart = getCart();
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
        }*/
    }

    public static void removeCartItem(CartItem cartItem) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CartItem.class).equalTo("product.id", cartItem.product.id).findAll().deleteAllFromRealm();
            }
        });
    }

    public static void clearCart() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(CartItem.class));
    }

    public static void clearData() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.deleteAll());
    }
}
