package info.androidhive.paytmgateway.db.model;

import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.RealmObject;

public class CartItem extends RealmObject {
    public Product product;
    public int quantity = 0;
}
