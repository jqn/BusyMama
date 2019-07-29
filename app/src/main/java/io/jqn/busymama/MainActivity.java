package io.jqn.busymama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity  {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();

    // Member variables
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add Toolbar to Main screen
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    }
    // Add fragments to Tabs
    private  void setupViewPager(ViewPager viewPager) {

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
        return  super.onOptionsItemSelected(item);
    }


}



