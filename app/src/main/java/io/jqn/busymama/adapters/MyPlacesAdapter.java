package io.jqn.busymama.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.jqn.busymama.R;
import io.jqn.busymama.database.MyPlacesEntry;

public class MyPlacesAdapter extends RecyclerView.Adapter<MyPlacesAdapter.MyPlacesViewHolder> {
    // Constants
    private static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss a";
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // Class variables
    private Context mContext;
    private List<MyPlacesEntry> mPlacesEntries;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyPlacesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView subHeadView;
        public TextView supportTextView;

        public MyPlacesViewHolder(View v) {
            super(v);
            titleView = v.findViewById(R.id.card_title);
            subHeadView = v.findViewById(R.id.card_subhead);
            supportTextView = v.findViewById(R.id.card_supporting_text);

        }
    }

    // Provide a suitable constructor
    public MyPlacesAdapter(Context context) {
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyPlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);

        return new MyPlacesViewHolder(view);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyPlacesViewHolder holder, int position) {
        // Determine the values of the wanted data
        MyPlacesEntry myPlacesEntry = mPlacesEntries.get(position);

        String title = myPlacesEntry.getPlaceName();
        String subHead = myPlacesEntry.getPlaceAddress();
        String supportText = dateFormat.format(myPlacesEntry.getUpdatedAt());

        // - get element from the my places dataset at this position
        // - replace the contents of the view with that element
        holder.titleView.setText(title);
        holder.subHeadView.setText(subHead);
        holder.supportTextView.setText(supportText);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mPlacesEntries == null) {
            return 0;
        }
        return mPlacesEntries.size();
    }

    /**
     * When data changes, this method updates the list of transactionEntries
     * and notifies the adapter to use the new values on it
     */
    public void setMyPlaces(List<MyPlacesEntry> myPlacesEntries) {
        mPlacesEntries = myPlacesEntries;
        notifyDataSetChanged();
    }
}
