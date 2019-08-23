package io.jqn.busymama;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.NumberFormat;
import java.util.Currency;

import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;
import timber.log.Timber;

public class TransactionDetailActivity extends AppCompatActivity {
    // Extra for the transaction ID to be received in the intent
    public static final String EXTRA_TRANSACTION_ID = "extraTransactionId";
    // Extra for the transaction ID to be received after rotation
    public static final String INSTANCE_TRANSACTION_ID = "instanceTransactionId";
    // Constant for default transaction id to be used when not in update mode
    private static final int DEFAULT_TRANSACTION_ID = -1;

    // Fields for views
    TextView mAmount;
    TextView mTransactionDate;
    TextView mTransactionLocation;

    // Member variables
    private BusyMamaDatabase mDb;

    private int mTransactionId = DEFAULT_TRANSACTION_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        Toolbar toolbar = findViewById(R.id.transaction_detail_toolbar);
        toolbar.setTitle(getString(R.string.transaction_detail_screen_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = BusyMamaDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TRANSACTION_ID)) {
            mTransactionId = savedInstanceState.getInt(INSTANCE_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TRANSACTION_ID)) {
            Timber.d("transaction id %s", intent.hasExtra(EXTRA_TRANSACTION_ID));
            if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                // Populate the UI
                mTransactionId = intent.getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);

                Timber.d("Actively retrieving a specific transaction from the DataBase");

                TransactionDetailViewModelFactory factory = new TransactionDetailViewModelFactory(mDb, mTransactionId);
                final TransactionDetailViewModel viewModel = ViewModelProviders.of(this, factory).get(TransactionDetailViewModel.class);

                viewModel.getTransaction().observe(this, new Observer<TransactionEntry>() {
                    @Override
                    public void onChanged(TransactionEntry transactionEntry) {
                        viewModel.getTransaction().removeObserver(this);
                        Timber.d("Receiving database update from LiveData");
                        populateUI(transactionEntry);
                    }
                });
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TRANSACTION_ID, mTransactionId);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Timber.d("onRestoreInstanceState transaction %s", savedInstanceState.getInt(INSTANCE_TRANSACTION_ID));

            mTransactionId = savedInstanceState.getInt(EXTRA_TRANSACTION_ID);

            Timber.d("Actively retrieving a specific transaction from the DataBase");
            TransactionDetailViewModelFactory factory = new TransactionDetailViewModelFactory(mDb, mTransactionId);
            final TransactionDetailViewModel viewModel = ViewModelProviders.of(this, factory).get(TransactionDetailViewModel.class);

            viewModel.getTransaction().observe(this, new Observer<TransactionEntry>() {
                @Override
                public void onChanged(TransactionEntry transactionEntry) {
                    viewModel.getTransaction().removeObserver(this);
                    Timber.d("Receiving database update from LiveData");
                    populateUI(transactionEntry);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param transaction the transactionEntry to populate the UI
     */
    private void populateUI(TransactionEntry transaction) {
        Timber.d("detailtransaction %s", transaction.getAmount());
        if (transaction == null) {
            return;
        }

        float amount = transaction.getAmount();
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("USD"));
        String formattedAmount = format.format(amount);

        mAmount = findViewById(R.id.amount);
        mAmount.setText(formattedAmount);

        mTransactionDate = findViewById(R.id.date_time);
        mTransactionDate.setText(transaction.getUpdatedAt().toString());

        mTransactionLocation = findViewById(R.id.transaction_location);
        mTransactionLocation.setText(transaction.getPlace());
    }
}
