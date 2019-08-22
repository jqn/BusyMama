package io.jqn.busymama.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.jqn.busymama.AppExecutors;
import io.jqn.busymama.R;
import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.MyPlacesEntry;
import timber.log.Timber;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    // Constants
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    // Used for selecting the current place.
    private static final int MAX_ENTRIES = 20;
    // Used for selecting the current place.
    private String[] mPlaceIds;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    // The entry points to the Places API.
    private PlacesClient mPlacesClient;

    // Member Variables
    private BusyMamaDatabase mDatabase;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the switch state and Handle enable/disable switch change
//        Switch onOffSwitch = getView().findViewById(R.id.enable_switch);

        // Initialize the Places client
        String apiKey = getString(R.string.busymama_api_key);
        Places.initialize(getContext().getApplicationContext(), apiKey);
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(getContext());
        //  Initialize member variable for the database
        mDatabase = BusyMamaDatabase.getInstance(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        Button button = view.findViewById(R.id.add_location_button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    /***
     * Button Click event handler to handle clicking the "Add new location" Button
     *
     * @param view
     */
    @Override
    public void onClick(View v) {
        Timber.d("Button clicked");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            return;
        }
        // Call findCurrentPlace and handle the response
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        // Use the builder to create a FindCurrentPlaceRequest.
        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(getActivity(),
                new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {

                        if (task.isSuccessful() && task.getResult() != null) {

                            Timber.d("places completed");

                            FindCurrentPlaceResponse response = task.getResult();

                            // Set the count, handling cases where less than 20 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = MAX_ENTRIES;
                            }

                            int i = 0;
                            mPlaceIds = new String[count];
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                // Build a list of likely places to show the user.
                                Place currentPlace = placeLikelihood.getPlace();

                                mPlaceIds[i] = currentPlace.getId();
                                mLikelyPlaceNames[i] = currentPlace.getName();
                                mLikelyPlaceAddresses[i] = currentPlace.getAddress();

                                Timber.d("likelyPlaces %s", placeLikelihood.getPlace().getName());

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }
                        }
                        openPlacesDialog();
                    }
                });
    }


    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("likely place name %s", mLikelyPlaceNames[which]);
                String placeID = mPlaceIds[which];
                String placeName = mLikelyPlaceNames[which];
                String placeAddress = mLikelyPlaceAddresses[which];

                // Assign current date
                Date date = new Date();

                final MyPlacesEntry myPlacesEntry = new MyPlacesEntry(placeID, placeName, placeAddress, date );
                AppExecutors.getInstance().diskIO().execute((new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.myPlacesDao().insertMyPlace(myPlacesEntry);
                    }
                }));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Pick a place")
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

}
