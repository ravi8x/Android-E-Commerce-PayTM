package info.androidhive.paytmgateway.ui.cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.MyViewHolder> {

    private Context context;
    private Cart cart;
    private CartProductsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.thumbnail)
        ImageView thumbnail;

        @BindView(R.id.btn_remove)
        Button btnRemove;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public CartProductsAdapter(Context context, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        notifyDataSetChanged();
    }

    @Override
    public CartProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new CartProductsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CartItem cartItem = cart.cartItems.get(position);
        Product product = cartItem.product;
        holder.name.setText(product.name);
        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.price), cartItem.quantity));
        GlideApp.with(context).load(product.imageUrl).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        Timber.e("getItemCount: %d", (cart != null ? cart.cartItems.size() : 0));
        return cart != null ? cart.cartItems.size() : 0;
    }

    public void updateItem(int position, Cart cart) {
        this.cart = cart;
        notifyItemChanged(position);
    }

    public interface CartProductsAdapterListener {
        void onProductRemoved(Product product);
    }
}
