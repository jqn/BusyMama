package io.jqn.busymama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.jqn.busymama.R;
import io.jqn.busymama.database.TransactionEntry;
import timber.log.Timber;

/**
 * This TransactionAdapter creates and binds ViewHolders, that hold the amount and description of a transaction,
 * to a RecyclerView to efficiently display data.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss a";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds transaction data and the Context
    private List<TransactionEntry> mTransactionEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TransactionAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public TransactionAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TransactionViewHolder that holds the view for each transaction
     */
    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the transaction_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.transaction_list, parent, false);

        return new TransactionViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        // Determine the values of the wanted data
        TransactionEntry transactionEntry = mTransactionEntries.get(position);

        float amount = transactionEntry.getAmount();

        // Format the transaction amounts
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("USD"));
        String formattedAmount = format.format(amount);

        String transaction_amount = formattedAmount;
        String updatedAt = dateFormat.format(transactionEntry.getUpdatedAt());
        String place = transactionEntry.getPlace();

        //Set values
        holder.amountView.setText(transaction_amount);
        holder.transactionDescriptionView.setText(updatedAt);
        holder.updatedAtView.setText(place);

    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mTransactionEntries == null) {
            return 0;
        }
        Timber.d("entries %s", mTransactionEntries.size());
        return mTransactionEntries.size();
    }

    /**
     * When data changes, this method updates the list of transactionEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTransactions(List<TransactionEntry> transactionEntries) {
        mTransactionEntries = transactionEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the transaction description and priority TextViews
        TextView amountView;
        TextView transactionDescriptionView;
        TextView updatedAtView;

        /**
         * Constructor for the TransactionViewHolder.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TransactionViewHolder(View itemView) {
            super(itemView);

            amountView = itemView.findViewById(R.id.transaction_amount);
            transactionDescriptionView = itemView.findViewById(R.id.transaction_detail);
            updatedAtView = itemView.findViewById(R.id.transaction_place);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTransactionEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
