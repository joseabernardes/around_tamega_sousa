package estg.ipp.pt.aroundtmegaesousa.activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AndroidException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.MapPickerActivity;
import estg.ipp.pt.aroundtmegaesousa.R;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AddPointActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO_PERMISSIONS = 100;
    private static final int REQUEST_MAP_POINT = 420;
    private static final String PHOTOS_KEY = "photos_list";
    private static final String THUMBS_KEY = "img_";
    private List<ImageView> imageViewList;
    private boolean imageOpen;
    private PhotoView expandedImageView;
    private Menu menu;
    private Toolbar toolbar;
    private ArrayList<File> photos;
    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = m.getInt("AppliedTheme", SettingsActivity.LIGHT_GREEN);
        if (theme == SettingsActivity.LIGHT_GREEN) {

            setTheme(R.style.AppTheme);
        } else if (theme == SettingsActivity.DARK_GREEN) {
            setTheme(R.style.AppTheme_Secondary);
        } else if (theme == SettingsActivity.BROWN) {
            setTheme(R.style.AppTheme_Brown);
        }

        setContentView(estg.ipp.pt.aroundtmegaesousa.R.layout.activity_add_point);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_point);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPointActivity.this, MapPickerActivity.class);
                startActivityForResult(i, REQUEST_MAP_POINT);
            }
        });
        imageViewList = new ArrayList<>();
        imageViewList.add(0, (ImageView) findViewById(R.id.img_0));
        imageViewList.add(1, (ImageView) findViewById(R.id.img_1));
        imageViewList.add(2, (ImageView) findViewById(R.id.img_2));
        imageViewList.add(3, (ImageView) findViewById(R.id.img_3));
        imageViewList.add(4, (ImageView) findViewById(R.id.img_4));

        if (savedInstanceState != null) { //recuperar estado
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
            for (int i = 0; i < 5; i++) {
                Object obj = savedInstanceState.getParcelable(THUMBS_KEY + i);
                if (obj != null) {//existe imagem
                    ImageView imageView = imageViewList.get(i);
                    imageView.setImageBitmap((Bitmap) obj);
                    imageView.setPadding(0, 0, 0, 0);
                }
            }
        } else {
            photos = new ArrayList<File>();
            for (int i = 0; i < 5; i++) {
                photos.add(i, null);
            }
        }

        //check permissions

        EasyImage.configuration(this)
                .setImagesFolderName("Fotografias")
        ;


        //checkGalleryAppAvailability();


        /**
         * gallery
         */
        expandedImageView = findViewById(R.id.expanded_image);
        imageOpen = false;


        for (int i = 0; i < 5; i++) {
            final ImageView tempImage = imageViewList.get(i);
            tempImage.setTag(i); //adicionar o id da imagem
            tempImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImage(tempImage);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check:
                //TODO


                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }


    private void onClickImage(final ImageView imageView) {
        int tag = (Integer) imageView.getTag();
        File image = photos.get(tag);
        if (image != null) { //se já tiver imagem
            if (image.exists()) {
                Picasso.with(AddPointActivity.this).load(image).fit().centerInside().into(expandedImageView);
/*                Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);*/
                expandedImageView.setVisibility(View.VISIBLE);
                updateMenuItem("Eliminar", R.drawable.ic_cancel);
                toolbar.setTitle("Imagem " + image.getName());
                imageOpen = true;
            } else {
                Toast.makeText(this, "O ficheiro não existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            List<String> permissions = checkPermissions(REQUEST_PHOTO_PERMISSIONS);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions, REQUEST_PHOTO_PERMISSIONS);
                return;
            }
            //adicionar imagem
            EasyImage.openChooserWithDocuments(this, "Escolha a fonte", tag);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
                Toast.makeText(AddPointActivity.this, "Erro a carregar a imagem", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                addPhotoToList(imageFile, type);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(AddPointActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void addPhotoToList(File imageFile, int tag) {
        ImageView imageView = imageViewList.get(tag);
        imageView.setPadding(0, 0, 0, 0);
        Picasso.with(AddPointActivity.this).load(imageFile).fit().centerCrop().into(imageView);
        photos.add(tag, imageFile);
    }

    private void closeImage() {
        toolbar.setTitle(R.string.add_point);
        updateMenuItem("Adicionar", R.drawable.ic_check);
        expandedImageView.setVisibility(View.GONE);
        expandedImageView.setDisplayMatrix(new Matrix());
        expandedImageView.setSuppMatrix(new Matrix());
        imageOpen = false;
    }

    private void updateMenuItem(String title, int icon) {
        MenuItem item = menu.findItem(R.id.action_check);
        if (item != null) {
            item.setTitle(title);
            item.setIcon(icon);
        }
    }

    private void requestPermissions(List<String> permissions, int requestCode) {
        if (!permissions.isEmpty()) { //se não existe alguma permissão
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), requestCode);
        }
    }

    private List<String> checkPermissions(int requestCode) {
        List<String> permissions = new ArrayList<>();
        if (requestCode == REQUEST_PHOTO_PERMISSIONS) {
            int storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (storage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (camera != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
        }
        return permissions;

    }


    @Override
    public void onBackPressed() {
        if (imageOpen) {
            closeImage();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PHOTOS_KEY, photos);
        for (int i = 0; i < 5; i++) {
            Drawable drawable = imageViewList.get(i).getDrawable();
            if (drawable instanceof BitmapDrawable) { //significa que existe imagem
                outState.putParcelable(THUMBS_KEY + i, ((BitmapDrawable) drawable).getBitmap());
            } else {
                outState.putParcelable(THUMBS_KEY + i, null);
            }

        }
        super.onSaveInstanceState(outState);
    }
}
