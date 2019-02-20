package info.androidhive.paytmgateway.ui.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.networking.model.OrderItem;

public class OrderItemsListView extends LinearLayout {
    @BindView(R.id.container)
    LinearLayout layoutContainer;

    private List<OrderItem> items = new ArrayList<>();
    private LayoutInflater inflater;

    public OrderItemsListView(Context context) {
        super(context);
        init(context, null);
    }

    public OrderItemsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_order_items_list_view, this);
        ButterKnife.bind(this);
        if (inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    public void setOrderItems(List<OrderItem> items) {
        this.items.clear();
        this.items.addAll(items);

        renderItems();
    }

    private void renderItems() {
        layoutContainer.removeAllViews();
        for (OrderItem item : items) {
            View view = inflater.inflate(R.layout.view_order_items_list_row, null);
            if (item.product != null) {
                ((TextView) view.findViewById(R.id.name)).setText(item.product.name);
                ((TextView) view.findViewById(R.id.price)).setText(getContext().getString(R.string.price_with_currency, item.product.price));
            }
            layoutContainer.addView(view);
        }
    }
}
