package estg.ipp.pt.aroundtmegaesousa.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.ListMapFragment;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentsCommunicationListener, FirebaseHelper.FirebaseGetPointOfInterest {

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
        navigationView.setCheckedItem(R.id.interest_points);
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

        onRestoreState(savedInstanceState);
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
            if (findViewById(R.id.container) != null) { //phone
                Log.d(TAG, "onCreate: Phone Layout");
                Fragment fragment = ListFragment.newInstance(ListFragment.LIST);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            } else {
                Log.d(TAG, "onCreate: TABLET Layout");
            }
        } else {
//algo
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.mypoints:
                fragment = ListFragment.newInstance(ListFragment.MY_POINTS);
                break;
            case R.id.favorites:
                fragment = ListFragment.newInstance(ListFragment.FAVORITES);
                break;
            case R.id.interest_points:
                fragment = ListFragment.newInstance(ListFragment.LIST);
                break;
            case R.id.map:
                fragment = ListMapFragment.newInstance();
                fab.hide();
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

    @Override
    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
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
