package estg.ipp.pt.aroundtmegaesousa.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.MapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PointOfInterestFragment.OnFragmentInteractionListener, OnFragmentsChangeViewsListener, FirebaseHelper.FirebaseGetPointOfInterest {

    private String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private ImageView userIcon;
    private TextView userName;
    private OnBackPressedListener onBackPressedListener;
    private FirebaseHelper firebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        ThemeUtils.changeTheme(this);
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
        View headerLayout = navigationView.getHeaderView(0);
        userIcon = headerLayout.findViewById(R.id.user_icon);
        userName = headerLayout.findViewById(R.id.user_name);



        /*
        OPEN POI FRAGMENT
         */
        if (getIntent().hasExtra(PointOfInterestFragment.DOCUMENT_ID)) {
            String documentID = getIntent().getStringExtra(PointOfInterestFragment.DOCUMENT_ID);
            firebaseHelper = new FirebaseHelper();
            firebaseHelper.getPointOfInterestByDocumentID(documentID, this);
        }


    }


    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
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
                return false;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
                return true;
               /* break;*/
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
    public FirebaseUser getLoggedUser() {
        return user;
    }

    @Override
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;

    }

    @Override
    public void removeOnBackPressedListener() {
        this.onBackPressedListener = null;
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

    protected void addUserInfo(FirebaseUser user) {
        userName.setText(user.getDisplayName());
        new LoadImage().execute(user.getUid());
    }

    @Override
    public void getPointOfInterest(PointOfInterest pointOfInterest) {
        Fragment fragment = PointOfInterestFragment.newInstance(pointOfInterest);
        replaceFragment(fragment);
        changeActionBarTitle(pointOfInterest.getName());
        showFloatingButton(false);
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private Bitmap loadImage(String file) {
            Bitmap bitmap = null;
            FileInputStream fiStream;
            boolean run = true;
            int times = 5;
            while (run && times > 0) {
                try {
                    times--;
                    run = false;
                    fiStream = getApplicationContext().openFileInput(file);
                    bitmap = BitmapFactory.decodeStream(fiStream);
                    fiStream.close();
                    if (bitmap == null) {
                        throw new FileNotFoundException("FileNotFound");

                    }

                } catch (IOException e) {
                    run = true;
                    Log.d(TAG, "loadImage: wait 5secs");
                    try {
                        Thread.currentThread();
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return bitmap;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            return loadImage(strings[0]);
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), result);
                imageDrawable.setCircular(true);
                imageDrawable.setCornerRadius(Math.max(result.getWidth(), result.getHeight()) / 2.0f);
                userIcon.setImageDrawable(imageDrawable);
            }
        }


    }

    public interface OnBackPressedListener {
        void doBack();
    }


}
