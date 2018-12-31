package info.androidhive.paytmgateway.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.app.GlideApp;
import info.androidhive.paytmgateway.db.model.Cart;
import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.networking.model.Product;
import io.realm.RealmResults;
import timber.log.Timber;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    private Context context;
    private RealmResults<Product> products;
    private ProductsAdapterListener listener;
    private Cart cart;

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

        @BindView(R.id.product_count)
        TextView lblQuantity;

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

    public void setCart(Cart cart) {
        this.cart = cart;
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
            listener.onProductAddedCart(position, product);
        });

        holder.icRemove.setOnClickListener(view -> {
            listener.onProductRemovedFromCart(position, product);
        });

        if (cart != null) {
            CartItem cartItem = cart.cartItems.where().equalTo("product.id", products.get(position).id).findFirst();
            if (cartItem != null) {
                Timber.e("product quantity: %d", cartItem.quantity);
                if (cartItem.quantity > 0) {
                    holder.lblQuantity.setText(String.valueOf(cartItem.quantity));
                    holder.icRemove.setVisibility(View.VISIBLE);
                    holder.lblQuantity.setVisibility(View.VISIBLE);
                } else {
                    holder.icRemove.setVisibility(View.GONE);
                    holder.lblQuantity.setVisibility(View.GONE);
                }
            }else {
                holder.lblQuantity.setText(String.valueOf(0));
                holder.icRemove.setVisibility(View.GONE);
                holder.lblQuantity.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateItem(int position, Cart cart) {
        this.cart = cart;
        notifyItemChanged(position);
    }

    public interface ProductsAdapterListener {
        void onProductAddedCart(int index, Product product);

        void onProductRemovedFromCart(int index, Product product);
    }
}
