package estg.ipp.pt.aroundtmegaesousa.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.fragments.ItemMapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListMapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.NearbyListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.services.NearByLocationService;
import estg.ipp.pt.aroundtmegaesousa.services.PushNotificationService;
import estg.ipp.pt.aroundtmegaesousa.services.StartNearbyServiceReceiver;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.LocationUtils;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentsCommunicationListener, FirebaseHelper.FirebaseGetPointOfInterest, ActivityCompat.OnRequestPermissionsResultCallback {

    private String TAG = "MainActivity";
    private static final String IS_MAP = "is_map";
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private ImageView userIcon;
    private TextView userName;
    private OnBackPressedListener onBackPressedListener;
    private FirebaseHelper firebaseHelper;
    private FrameLayout leftContainer;
    private FrameLayout rightContainer;
    private boolean isListMap;
    private int[] layoutParams;
    private boolean isTabletPortrait;
    private boolean isPhoneLayout;
    private boolean locationEnable, permissionLocation, alarmActivated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //findViewsById
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout); //main layout
        navigationView = findViewById(R.id.nav_view); //navigation drawer
        fab = findViewById(R.id.fab);

        //toolbar
        setSupportActionBar(toolbar);

        //navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        userIcon = headerLayout.findViewById(R.id.user_icon);
        userName = headerLayout.findViewById(R.id.user_name);


        //onClicks
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPointActivity.class);
                startActivity(intent);
            }
        });


        //Open PointOfInterest by ID
        if (getIntent().hasExtra(PointOfInterestFragment.DOCUMENT_ID)) {
            Toast.makeText(this, getString(R.string.message_toast_open_poi), Toast.LENGTH_SHORT).show();
            String documentID = getIntent().getStringExtra(PointOfInterestFragment.DOCUMENT_ID);
            firebaseHelper = new FirebaseHelper();
            firebaseHelper.getPointOfInterestByDocumentID(documentID, this);
        }


        if (findViewById(R.id.phone_container) == null) { //tablet
            isPhoneLayout = false;
            leftContainer = findViewById(R.id.left_container);
            rightContainer = findViewById(R.id.right_container);

            if (leftContainer.getTag().equals("large")) {
                layoutParams = new int[]{0, ViewGroup.LayoutParams.MATCH_PARENT, 2};
                isTabletPortrait = false;
            } else {
                layoutParams = new int[]{0, ViewGroup.LayoutParams.MATCH_PARENT, 1};
                isTabletPortrait = true;
            }
        } else {
            isPhoneLayout = true;
        }


        onRestoreState(savedInstanceState);
        //open Lista de poi's
        if (getIntent().hasExtra(NearByLocationService.LIST_POI)) {
            ArrayList<PointOfInterest> pointOfInterestArrayList = (ArrayList<PointOfInterest>) getIntent().getSerializableExtra(NearByLocationService.LIST_POI);
            Fragment fragment = NearbyListFragment.newInstance(R.id.interest_points, pointOfInterestArrayList);
            replaceFragment(fragment);
        }
        checkInternet();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkInternet() {
        if (!isNetworkAvailable() && !isOnline()) {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.offline), Snackbar.LENGTH_LONG).show();

        }

    }


    private void fistTime() {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fist = mSettings.getBoolean("first_time", true);

        if (fist) {
            SharedPreferences.Editor mEditor = mSettings.edit();
            mEditor.putBoolean("first_time", false);
            mEditor.apply();
            FirebaseMessaging.getInstance().subscribeToTopic(PushNotificationService.TOPIC);
            permissionLocation = LocationUtils.checkAndRequestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            Task task = LocationUtils.enableLocationSettings(this, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    locationEnable = true;
                    if (permissionLocation && !alarmActivated) { //só se tiver permissões e a localização ativa
                        alarmActivated = true;
                        setAlarmNearby();
                        Toast.makeText(MainActivity.this, getString(R.string.recom_on), Toast.LENGTH_SHORT).show();
                        setNearbyPreference(true);
                    } else { //se ja tiver locatizaçao ON e nao tiver permicoes
                        setNearbyPreference(false);
                    }
                }
            });
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionLocation = LocationUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionLocation) {
            if (locationEnable && !alarmActivated) { //tem permissões e tem a localização ativa
                alarmActivated = true;
                setAlarmNearby();
                Toast.makeText(MainActivity.this, getString(R.string.recom_on), Toast.LENGTH_SHORT).show();
                setNearbyPreference(true);
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.recom_off), Toast.LENGTH_SHORT).show();
            setNearbyPreference(false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationEnable = LocationUtils.checkLocationSettings(requestCode, resultCode);
        if (locationEnable) {
            if (permissionLocation && !alarmActivated) {//tem permissões e tem a localização ativa
                alarmActivated = true;
                setAlarmNearby();
                Toast.makeText(MainActivity.this, getString(R.string.recom_on), Toast.LENGTH_SHORT).show();
                setNearbyPreference(true);
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.recom_off), Toast.LENGTH_SHORT).show();
            setNearbyPreference(false);
        }
    }

    private void setNearbyPreference(boolean pref) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor mEditor = mSettings.edit();
        mEditor.putBoolean("nearby", pref);
        mEditor.apply();

    }

    private void setAlarmNearby() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartNearbyServiceReceiver.class);
        intent.setAction(StartNearbyServiceReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (36000), 3600000, pendingIntent);

    }

    /**
     * Metodo responsavel por recuperar o estado da activity
     * É utilizado em detrimento do onRestoreInstanceState devido a este ultimo só ser chamado caso
     * exista estado,e neste caso é necessario um metodo que seja sempre chamado na criação da activity
     *
     * @param savedInstanceState
     */

    private void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) { //se não existe estado
            //apenas quando a activity está a ser criada, e não RE-CRIADA
            if (findViewById(R.id.phone_container) != null) { //phone
                Fragment fragment = ListFragment.newInstance(ListFragment.LIST, R.id.interest_points);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.phone_container, fragment)
                        .commit();
            } else {
                Fragment fragment = ListFragment.newInstance(ListFragment.LIST, R.id.interest_points);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.left_container, fragment)
                        .commit();
            }
        } else {
            isListMap = savedInstanceState.getBoolean(IS_MAP, false);
         /*  ;
          *//*  *//*
            navigationView.getMenu().getItem(selectedMenuItem).setChecked(true);*/
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        int id = item.getItemId();
        switch (id) {
            case R.id.mypoints:
                fragment = ListFragment.newInstance(ListFragment.MY_POINTS, R.id.mypoints);
                break;
            case R.id.favorites:
                fragment = ListFragment.newInstance(ListFragment.FAVORITES, R.id.favorites);
                break;
            case R.id.interest_points:
                fragment = ListFragment.newInstance(ListFragment.LIST, R.id.interest_points);
                break;
            case R.id.map:
                fragment = ListMapFragment.newInstance(ListMapFragment.LIST_MAP, R.id.map, null);
                break;
            case R.id.settings:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return false;
            case R.id.logout:
                drawer.closeDrawer(GravityCompat.START);
                AuthUI.getInstance().signOut(this);
                return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        replaceFragment(fragment);
        return true;
    }

    /**
     * Se o fragment a fazer replace for a lista principal de pontos (R.id.interest_points),
     * toda a back stack é consumida, e no proximo BACKPRESSED o utilizador sai da aplicação
     *
     * @param fragment
     */
    @Override
    public void replaceFragment(Fragment fragment) {
        int replaceID;
        if (fragment != null) {
            if (!isPhoneLayout) {//tablet
                if (fragment instanceof ListFragment || fragment instanceof NearbyListFragment) {

                    rightContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], layoutParams[2]));
                    isListMap = false;
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.left_container, fragment)
                            .commit();
                    return;
               /*     }*/
            /*        replaceID = R.id.left_container;*/
                } else if (fragment instanceof ListMapFragment) {
                    isListMap = true;
                    rightContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], 0));
                    replaceID = R.id.left_container;
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.left_container, fragment)
                            .commit();
                    return;
                } else if (fragment instanceof PointOfInterestFragment) {
                    rightContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], layoutParams[2]));
                    replaceID = R.id.right_container;
                } else if (fragment instanceof ItemMapFragment) {
                    rightContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], layoutParams[2]));
                    if (isListMap) {
                        leftContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], 0));
                    }
                    replaceID = R.id.right_container;
                } else {
                    replaceID = R.id.left_container;
                }

            } else {//phote
                if (fragment instanceof ListFragment) {
                    int fragmentID = fragment.getArguments().getInt(ListFragment.ARG_FRAG_ID, 0);
                    if (fragmentID == R.id.interest_points) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.phone_container, fragment)
                                .commit();
                        return;
                    }
                }
                replaceID = R.id.phone_container;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(replaceID, fragment)
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
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isPhoneLayout) {//tablet
                leftContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], 1));
                rightContainer.setLayoutParams(new LinearLayout.LayoutParams(layoutParams[0], layoutParams[1], layoutParams[2]));
            }


            super.onBackPressed();
        }
    }

    @Override
    public void changeActionBarTitle(String title, boolean isList) {
        if (isList || isPhoneLayout) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void changeSelectedNavigationItem(int id) {
        if (id != -1) {
            navigationView.setCheckedItem(id);
        }
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
        fistTime();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_MAP, isListMap);
        super.onSaveInstanceState(outState);
    }

    /**
     * Metodo chamado quando é retornado um ponto de interesse por id á bd
     *
     * @param pointOfInterest
     */
    @Override
    public void getPointOfInterest(PointOfInterest pointOfInterest) {
        Fragment fragment = PointOfInterestFragment.newInstance(pointOfInterest);
        replaceFragment(fragment);
    }


    /**
     * Tarefa responsavel por carregar o avatar do user da memoria
     * <p>
     * Tenta carregas o avatar do user que foi gravado na internalstorage pela LoginActivity
     * tenta o fazer 5vezes com intervalos de 5seg, se não conseguir(caso o imagem ainda nao
     * tenha sido descarregada e guardada), mantem o avatar default
     */
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

    /**
     * Interface reponsavel por permitir fazer override do onBackPressed da activity, num fragment
     */
    public interface OnBackPressedListener {
        void doBack();
    }
}
