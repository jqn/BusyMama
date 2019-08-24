package io.jqn.busymama.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.jqn.busymama.AppExecutors;
import io.jqn.busymama.Constants;
import io.jqn.busymama.GeofenceBroadcastReceiver;
import io.jqn.busymama.GeofenceErrorMessages;
import io.jqn.busymama.R;
import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.MyPlacesEntry;
import timber.log.Timber;

public class SettingsFragment extends Fragment implements View.OnClickListener, OnCompleteListener<Void> {
    // Constants
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    // Used for selecting the current place.
    private static final int MAX_ENTRIES = 20;
    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * `
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    // Used for selecting the current place.
    private String[] mPlaceIds;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private LatLng[] mLikelyPlaceLatLngs;
    // The entry points to the Places API.
    private PlacesClient mPlacesClient;
    // Member Variables
    private BusyMamaDatabase mDatabase;
    private boolean mIsEnabled;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Places client
        String apiKey = getString(R.string.busymama_api_key);
        Places.initialize(getContext().getApplicationContext(), apiKey);

        // Create a new Places client instance.
        mPlacesClient = Places.createClient(getContext());
        //  Initialize member variable for the database
        mDatabase = BusyMamaDatabase.getInstance(getContext());

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        // Instance of the Geofencing Client
        mGeofencingClient = LocationServices.getGeofencingClient(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        Button button = view.findViewById(R.id.add_location_button);
        button.setOnClickListener(this);

        // Initialize the switch state and Handle enable/disable switch change
        Switch onOffSwitch = view.findViewById(R.id.enable_switch);
        mIsEnabled = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                editor.commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        Timber.i("On start called");
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            Toast.makeText(getContext(), getString(R.string.insufficient_permissions), Toast.LENGTH_LONG).show();
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);

    }


    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            Toast.makeText(getContext(), getString(R.string.insufficient_permissions), Toast.LENGTH_LONG).show();
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     *
     * @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(getActivity(), getString(messageId), Toast.LENGTH_LONG).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(getActivity(), task.getException());
            Timber.w(errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;

    }

    /**
     * Create geofences based on the user's location.
     */
    private void populateGeofenceList(String id, LatLng coords) {

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(id)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        coords.latitude,
                        coords.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());


    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /***
     * Button Click event handler to handle clicking the "Add new location" Button
     *
     * @param v
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
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
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
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                // Build a list of likely places to show the user.
                                Place currentPlace = placeLikelihood.getPlace();

                                mPlaceIds[i] = currentPlace.getId();
                                mLikelyPlaceNames[i] = currentPlace.getName();
                                mLikelyPlaceAddresses[i] = currentPlace.getAddress();
                                mLikelyPlaceLatLngs[i] = currentPlace.getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }
                            openPlacesDialog();
                        } else {
                            Timber.e("Exeption %s", task.getException());
                            Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                        }

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
                Timber.d("openPlacesDialog latlngs %s", mLikelyPlaceLatLngs[which]);
                String placeID = mPlaceIds[which];
                String placeName = mLikelyPlaceNames[which];
                String placeAddress = mLikelyPlaceAddresses[which];
                LatLng placeLatLng = mLikelyPlaceLatLngs[which];

                if (mIsEnabled) {
                    // Assign current date
                    Date date = new Date();
                    // Insert new place into DB
                    final MyPlacesEntry myPlacesEntry = new MyPlacesEntry(placeID, placeName, placeAddress, date);
                    AppExecutors.getInstance().diskIO().execute((new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.myPlacesDao().insertMyPlace(myPlacesEntry);
                        }
                    }));
                    populateGeofenceList(mPlaceIds[which], mLikelyPlaceLatLngs[which]);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.need_enable_geofences), Toast.LENGTH_LONG).show();
                }

            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Pick a place")
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Timber.i("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.i("Permission granted.");
                performPendingGeofenceTask();
            } else {
                Timber.i("Permission denied");
                // Permission denied.

                Toast.makeText(getContext(), getString(R.string.permission_denied_explanation), Toast.LENGTH_LONG).show();

                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

}
