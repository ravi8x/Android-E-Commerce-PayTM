package info.androidhive.paytmgateway.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Cart extends RealmObject {
    @PrimaryKey
    public int id = 0;
    public RealmList<CartItem> cartItems;
    public float amount = 0;
}
