package io.jqn.busymama.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.jqn.busymama.R;
import io.jqn.busymama.adapters.TransactionAdapter;

/**
 * Provides UI for the expenses view.
 */
public class TransactionListFragment extends Fragment implements TransactionAdapter.ItemClickListener {
    private Context mContext;

    public TransactionListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get a reference to the RecyclerView from xml
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        // Initialize the adapter and attach it to the RecyclerView
        TransactionAdapter adapter = new TransactionAdapter(recyclerView.getContext(), this);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        // Set the LayoutManager for measuring and positioning items views withing the
        // RecyclerView into a linear list
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;

    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
    }

//

}
