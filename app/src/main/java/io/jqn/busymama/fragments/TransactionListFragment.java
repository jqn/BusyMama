package io.jqn.busymama.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.jqn.busymama.MainViewModel;
import io.jqn.busymama.R;
import io.jqn.busymama.TransactionDetailActivity;
import io.jqn.busymama.adapters.TransactionAdapter;
import io.jqn.busymama.database.TransactionEntry;
import timber.log.Timber;

/**
 * Provides UI for the expenses view.
 */
public class TransactionListFragment extends Fragment implements TransactionAdapter.ItemClickListener {
    private Context mContext;

    // Member variables
    private TransactionAdapter mAdapter;

    public TransactionListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Timber.d("on attach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get a reference to the RecyclerView from xml
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TransactionAdapter(recyclerView.getContext(), this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);
        // Set the LayoutManager for measuring and positioning items views withing the
        // RecyclerView into a linear list
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        retrieveTransactions();
        return recyclerView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("Timbeeeeeer! on resume");
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch TransactionDetail adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
        intent.putExtra(TransactionDetailActivity.EXTRA_TRANSACTION_ID, itemId);
        startActivity(intent);
    }

    public void retrieveTransactions() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(List<TransactionEntry> transactionEntries) {
                mAdapter.setTransactions(transactionEntries);
            }
        });


    }

}
