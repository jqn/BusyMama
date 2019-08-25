package io.jqn.busymama.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.jqn.busymama.AppExecutors;
import io.jqn.busymama.R;
import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;
import io.jqn.busymama.fragments.MyPlacesFragment;
import io.jqn.busymama.fragments.SettingsFragment;
import io.jqn.busymama.fragments.TransactionListFragment;
import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity implements View.OnKeyListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // integer for permissions results request
    private static final String KEY_LOCATION = "location";

    public String current_place = "";
    // Fields for views
    EditText mEditText;

    // Member variables
    private DrawerLayout mDrawerLayout;
    private BusyMamaDatabase mDatabase;

    // The entry points to the Places API.
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        }

        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // this listener will be called when there is change in firebase user session
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // Prompt the user for permission.
        getLocationPermission();


        String apiKey = getString(R.string.busymama_api_key);
        // Initialize Places client.
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Add Toolbar to Main screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard_screen);
        setSupportActionBar(toolbar);
        // Set ViewPager for each Tab
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        // Create Navigation drawer and inflate layout
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer);
        // Initialize transaction entry view
        mEditText = findViewById(R.id.transaction_entry);
        mEditText.setOnKeyListener(this);
        // Add a menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_white_24dp, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Set item checked state
                menuItem.setChecked(true);

                // Handle navigation view item clicks here.
                switch (menuItem.getItemId()) {

                    case R.id.logout_link: {
                        Timber.d("Logout user");
                        // Log out the user
                        mAuth.signOut();
                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                        finish();
                        break;
                    }
                }

                // Close drawer on item click
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //  Initialize member variable for the database
        mDatabase = BusyMamaDatabase.getInstance(getApplicationContext());

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }


    /**
     * Gets the current location of the device.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {

                            mLastKnownLocation = task.getResult();
                            Timber.d("mLastKnownLocation %s", mLastKnownLocation);
                            if (mLastKnownLocation != null) {
                                Timber.d("mLastKnownLocation %s", mLastKnownLocation.getLatitude());
                            }

                        } else {
                            Timber.d("Current location is null. Using defaults.");

                            Timber.e(task.getException(), "Exception:");

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
            Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(this,
                    new OnCompleteListener<FindCurrentPlaceResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                            if (task.isSuccessful()) {

                                FindCurrentPlaceResponse response = task.getResult();
                                double maxLikelihood = 0;

                                Place currentPlace = null;

                                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                    if (maxLikelihood < placeLikelihood.getLikelihood()) {
                                        maxLikelihood = placeLikelihood.getLikelihood();
                                        currentPlace = placeLikelihood.getPlace();
                                    }
                                }


                                if (currentPlace != null) {
                                    Timber.d("currentPlace %s", currentPlace.getName());
                                    // passing current location to place
                                    current_place = currentPlace.getName();

                                } else {
                                    current_place = "no location";
                                }

                                // This crashes the app because mEditText is empty
                                if (!mEditText.getText().toString().isEmpty()) {
                                    float amount = Float.parseFloat(mEditText.getText().toString());
                                    // assing current location to place
                                    String place = current_place;
                                    // Assign current date
                                    Date date = new Date();
                                    Timber.d("date %s", date);
                                    // Clear the text input
                                    mEditText.getText().clear();

                                    final TransactionEntry transactionEntry = new TransactionEntry(amount, place, date);
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDatabase.transactionDao().insertTransaction(transactionEntry);
                                        }
                                    });
                                } else {
                                    Toast.makeText(DashboardActivity.this, getString(R.string.empty_text_error), Toast.LENGTH_LONG).show();
                                }


                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Timber.d("Place not found %s", apiException.getStatusCode());
                                }
                            }
                        }
                    });
        } else {
            getLocationPermission();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TransactionListFragment(), getString(R.string.expenses_tab));
        adapter.addFragment(new MyPlacesFragment(), getString(R.string.my_places_tab));
        adapter.addFragment(new SettingsFragment(), getString(R.string.settings_tab));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; it adds items to the action bar if present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * onKey is called when the "enter" button is clicked.
     * It retrieves user input and inserts the new transaction data into the underlying database.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        Timber.d("event %s", event);

        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press
            Timber.d("amount %s", mEditText.getText());
            // assign edit text value to amount
            if (mEditText.getText().toString().matches("")) {
                Toast toast = Toast.makeText(this, getString(R.string.empty_text_error), Toast.LENGTH_LONG);
                toast.show();
            } else {
                showCurrentPlace();

            }


            return true;
        }
        return false;

    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}







