package estg.ipp.pt.aroundtmegaesousa.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.MapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PointOfInterestFragment.OnFragmentInteractionListener, OnFragmentsChangeViewsListener {

    private String TAG = "MainActivity";
    public static final int RC_SIGN_IN = 1;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeTheme(this);
        setContentView(R.layout.activity_main);

        mAuthStateListener = new AuthStateListener();
        mFirebaseAuth = FirebaseAuth.getInstance();


        //Connect Views
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout); //main layout
        navigationView = findViewById(R.id.nav_view); //navigation drawer
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPointActivity.class);
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

    //------------------------------ AUTH ------------------------------//


    private void onSignInInitialize(String string) {
      /*  loginName.setText(string);*/
    }

    private void onSignOutCleanup() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) { //se foi do meu pedido de login
            if (resultCode == RESULT_OK) { //se retornou sucesso
     /*           initLayout();*/
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) { // se o utilizador cancelou BACK ou sem ligação á internet
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private class AuthStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //signed in
   /*             initLayout();*/
                onSignInInitialize(user.getDisplayName());
            } else {
                //signed out
                onSignOutCleanup();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                        ))
                                .setLogo(R.drawable.around_logo)
                                .setTheme(R.style.LoginTheme)
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }
}
