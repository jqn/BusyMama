package io.jqn.busymama.fragments;

import android.content.Context;
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

import io.jqn.busymama.MyPlacesViewModel;
import io.jqn.busymama.R;
import io.jqn.busymama.adapters.MyPlacesAdapter;
import io.jqn.busymama.database.MyPlacesEntry;


public class MyPlacesFragment extends Fragment {
    private Context mContext;
    // Member variables
    private MyPlacesAdapter mAdapter;

    public MyPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        mAdapter = new MyPlacesAdapter(recyclerView.getContext());

        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        retrieveMyPlaces();
        return recyclerView;
    }

    public void retrieveMyPlaces() {
        // Associate UI controller and ViewModel
        MyPlacesViewModel viewModel = ViewModelProviders.of(this).get(MyPlacesViewModel.class);

        viewModel.getMyPlaces().observe(this, new Observer<List<MyPlacesEntry>>() {
            @Override
            public void onChanged(List<MyPlacesEntry> myPlacesEntries) {
             mAdapter.setMyPlaces(myPlacesEntries);
            }
        });
    }


}
