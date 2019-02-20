package info.androidhive.paytmgateway.ui.transactions;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.paytmgateway.R;
import info.androidhive.paytmgateway.networking.model.Transaction;
import info.androidhive.paytmgateway.ui.base.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.layout_empty_data)
    LinearLayout layoutEmptyData;

    private TransactionsAdapter mAdapter;
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        ButterKnife.bind(this);
        setToolbar();
        changeStatusBarColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        fetchTransactions();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transactions;
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new TransactionsAdapter(this, transactions);
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchTransactions() {
        toggleProgress(true);
        getApi().getTransactions().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                toggleProgress(false);
                if (!response.isSuccessful()) {
                    handleUnknownError();
                    return;
                }

                transactions.clear();
                transactions.addAll(response.body());
                mAdapter.notifyDataSetChanged();
                toggleEmptyData();
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                toggleProgress(false);
                handleError(t);
            }
        });
    }

    private void toggleEmptyData() {
        if (transactions.size() > 0) {
            layoutEmptyData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
