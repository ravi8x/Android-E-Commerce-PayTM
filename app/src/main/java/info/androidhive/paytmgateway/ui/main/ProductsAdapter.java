package info.androidhive.paytmgateway.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.app.GlideApp;
import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.RealmResults;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    private Context context;
    private RealmResults<Product> products;
    private ProductsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.thumbnail)
        ImageView thumbnail;

        @BindView(R.id.ic_add)
        ImageView icAdd;

        @BindView(R.id.ic_remove)
        ImageView icRemove;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public ProductsAdapter(Context context, RealmResults<Product> products, ProductsAdapterListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.name);
        holder.price.setText(context.getString(R.string.price_with_currency, product.price));
        GlideApp.with(context).load(product.imageUrl).into(holder.thumbnail);

        holder.icAdd.setOnClickListener(view -> {
            listener.onProductAddedCart(product);
        });

        holder.icRemove.setOnClickListener(view -> {
            listener.onProductRemovedFromCart(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface ProductsAdapterListener {
        void onProductAddedCart(Product product);

        void onProductRemovedFromCart(Product product);
    }
}
