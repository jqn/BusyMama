package io.jqn.busymama.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.jqn.busymama.R;
import io.jqn.busymama.database.TransactionEntry;
import io.jqn.busymama.fragments.TransactionListFragment;
import timber.log.Timber;

/**
 * This TransactionAdapter creates and binds ViewHolders, that hold the amount and description of a transaction,
 * to a RecyclerView to efficiently display data.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds transaction data and the Context
    private List<TransactionEntry> mTransactionEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TransactionAdapter that initializes the Context.
     *  @param context  the current Context
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
        String description = transactionEntry.getAmount();
        int priority = transactionEntry.getPriority();
        String updatedAt = dateFormat.format(transactionEntry.getUpdatedAt());

        //Set values
        holder.transactionDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);

        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.priorityView.setText(priorityString);

    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mTransactionEntries == null) {
            return 0;
        }
        Timber.d("entries" + mTransactionEntries.size() );
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
        TextView transactionDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        /**
         * Constructor for the TransactionViewHolder.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TransactionViewHolder(View itemView) {
            super(itemView);

            transactionDescriptionView = itemView.findViewById(R.id.list_title);
            updatedAtView = itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTransactionEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
