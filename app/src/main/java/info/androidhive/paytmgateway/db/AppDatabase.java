package info.androidhive.paytmgateway.db;

import java.util.List;

import info.androidhive.paytmgateway.networking.model.AppConfig;
import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.Realm;

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
}
