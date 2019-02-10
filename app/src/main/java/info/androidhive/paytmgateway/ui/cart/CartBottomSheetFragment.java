package info.androidhive.paytmgateway.ui.cart;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.db.AppDatabase;
import info.androidhive.paytmgateway.db.model.CartItem;
import info.androidhive.paytmgateway.helper.Utils;
import info.androidhive.paytmgateway.ui.paytm.PayTMActivity;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CartBottomSheetFragment extends BottomSheetDialogFragment implements CartProductsAdapter.CartProductsAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.btn_checkout)
    Button btnCheckout;

    private Realm realm;
    private CartProductsAdapter mAdapter;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartItemRealmChangeListener;

    public CartBottomSheetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Making bottom sheet expanding to full height by default
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItem.class).findAllAsync();

        cartItemRealmChangeListener = cartItems -> {
            mAdapter.setData(cartItems);
            setTotalPrice();
        };

        cartItems.addChangeListener(cartItemRealmChangeListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mAdapter = new CartProductsAdapter(getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setTotalPrice();
    }

    private void setTotalPrice() {
        if (cartItems != null) {
            float price = Utils.getCartPrice(cartItems);
            if (price > 0) {
                btnCheckout.setText(getString(R.string.btn_checkout, getString(R.string.price_with_currency, price)));
            } else {
                // if the price is zero, dismiss the dialog
                dismiss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartItemRealmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }
    }

    @OnClick(R.id.ic_close)
    void onCloseClick() {
        dismiss();
    }

    @OnClick(R.id.btn_checkout)
    void onCheckoutClick() {
        startActivity(new Intent(getActivity(), PayTMActivity.class));
        dismiss();
    }

    @Override
    public void onCartItemRemoved(int index, CartItem cartItem) {
        AppDatabase.removeCartItem(cartItem);
    }
}
