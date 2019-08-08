package io.jqn.busymama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.jqn.busymama.adapters.TransactionAdapter;
import io.jqn.busymama.database.BusyMamaDatabase;
import io.jqn.busymama.database.TransactionEntry;
import io.jqn.busymama.fragments.TransactionListFragment;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();

    // Fields for views
    EditText mEditText;

    // Member variables
    private DrawerLayout mDrawerLayout;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private BusyMamaDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        // Add Toolbar to Main screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashbord_screen);
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
                // TODO: handle navigation

                // Close drawer on item click
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //  Initialize member variable for the database
        mDatabase = BusyMamaDatabase.getInstance(getApplicationContext());

        Timber.d("Transactions %s", mDatabase.transactionDao().loadAllTransactions());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("on resume called");
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
//            float amount = Integer.parseInt(mEditText.getText().toString());
            float amount = Float.parseFloat(mEditText.getText().toString());
            // assing current location to place
            String place = "King soopers";
            // Assign current date
            Date date = new Date();
            Timber.d("date %s", date);

            final TransactionEntry transactionEntry = new TransactionEntry(amount, place, date);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDatabase.transactionDao().insertTransaction(transactionEntry);
                }
            });

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







