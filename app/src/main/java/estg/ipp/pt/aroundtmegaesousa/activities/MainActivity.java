package estg.ipp.pt.aroundtmegaesousa.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.ImageAdapter;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.MapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PointOfInterestFragment.OnFragmentInteractionListener, OnFragmentsChangeViewsListener {

    private String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = m.getInt("AppliedTheme", SettingsActivity.LIGHT_GREEN);
        if (theme == SettingsActivity.LIGHT_GREEN) {

            setTheme(R.style.AppTheme);
        } else if (theme == SettingsActivity.DARK_GREEN) {
            setTheme(R.style.AppTheme_Secondary);
        }else if (theme == SettingsActivity.BROWN){
            setTheme(R.style.AppTheme_Brown);
        }
        setContentView(R.layout.activity_main);

        //Connect Views
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout); //main layout
        navigationView = findViewById(R.id.nav_view); //navigation drawer
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPointActivity.class);
              /*  intent.putExtra("edittext", text.getText().toString());*/
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);
        if (findViewById(R.id.container) != null) { //phone
            Log.d(TAG, "onCreate: Phone Layout");

            Fragment fragment = ListFragment.newInstance(ListFragment.LIST, "bb");

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        } else {
            Log.d(TAG, "onCreate: TABLET Layout");
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.interest_points);





    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.mypoints:
                fragment = ListFragment.newInstance(ListFragment.MY_POINTS, "bb");

                break;
            case R.id.favorites:
                fragment = ListFragment.newInstance(ListFragment.FAVORITES, "bb");

                break;
            case R.id.interest_points:
                fragment = ListFragment.newInstance(ListFragment.LIST, "bb");

                break;
            case R.id.map:
                fragment = MapFragment.newInstance("aa", "bb");
                fab.hide();
                break;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                fragment = PointOfInterestFragment.newInstance("S", "SS");
                fab.hide();
                break;
        }

        replaceFragment(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void changeActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showFloatingButton(boolean state) {
        if (state) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override
    public boolean isShownFloatingButton() {
        return fab.isShown();
    }


}
