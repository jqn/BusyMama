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
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView avatar;
//        public TextView name;
//        public TextView description;
//
//        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
//            super(inflater.inflate(R.layout.transaction_list, parent, false));
//            avatar =  itemView.findViewById(R.id.list_avatar);
//            name = itemView.findViewById(R.id.list_title);
//            description = itemView.findViewById(R.id.list_desc);
//            itemView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, TransactionDetailActivity.class);
//                    intent.putExtra(TransactionDetailActivity.EXTRA_POSITION, getAdapterPosition());
//                    context.startActivity(intent);
//                }
//            });
//        }
//    }

//    /**
//     * Adapter to display recycler view.
//     */
//    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
//        // Ste numbers of List in RecyclerView
//        // TODO make this dynamic
//        private static final int LENGTH = 6;
//
//        private final String[] mPlaces;
//        private final String[] mPlaceDesc;
//        private final Drawable[] mPlaceAvatars;
//
//        public ContentAdapter(Context context) {
//            Resources resources = context.getResources();
//            mPlaces = resources.getStringArray(R.array.expenses);
//            mPlaceDesc = resources.getStringArray(R.array.store_details);
//            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
//            mPlaceAvatars = new Drawable[a.length()];
//            for (int i = 0; i < mPlaceAvatars.length; i++) {
//                mPlaceAvatars[i] = a.getDrawable(i);
//            }
//            a.recycle();
//        }
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            holder.avatar.setImageDrawable(mPlaceAvatars[position % mPlaceAvatars.length]);
//            holder.name.setText(mPlaces[position % mPlaces.length]);
//            holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
//        }
//
//        @Override
//        public int getItemCount() {
//            return LENGTH;
//        }
//    }

}
