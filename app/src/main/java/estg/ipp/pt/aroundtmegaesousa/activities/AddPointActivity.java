package estg.ipp.pt.aroundtmegaesousa.activities;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;
import estg.ipp.pt.aroundtmegaesousa.utils.AddPointTask;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AddPointActivity extends BaseActivity {

    private static final int REQUEST_PHOTO_PERMISSIONS = 100;
    public static final String EDIT_POI_ACTION = "edit";
    public static final String DELETE_POI_ACTION = "delete";
    public static final String ADD_POI_ACTION = "add";
    private static final int REQUEST_MAP_POINT = 420;
    private static final String PHOTOS_KEY = "photos_list";
    private static final String THUMBS_KEY = "img_";
    private List<ImageView> imageViewList;
    private int imageOpen;
    private PhotoView expandedImageView;
    private Menu menu;
    private Toolbar toolbar;
    private ArrayList<File> photos;
    private Button mapButton;
    private LatLng coordinates;
    private EditText location;
    private AlertDialog dialog;
    private City city;
    private EditText name;
    private EditText description;
    private Spinner typeOfLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeTheme(this);
        setContentView(estg.ipp.pt.aroundtmegaesousa.R.layout.activity_add_point);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        typeOfLocation = findViewById(R.id.typeOfLocation);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_add_point);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPointActivity.this, MapPickerActivity.class);
                if (coordinates != null) {
                    i.putExtra(MapPickerActivity.MAP_PARAM, coordinates);
                }
                startActivityForResult(i, REQUEST_MAP_POINT);
            }
        });
        imageViewList = new ArrayList<>();
        imageViewList.add(0, (ImageView) findViewById(R.id.img_0));
        imageViewList.add(1, (ImageView) findViewById(R.id.img_1));
        imageViewList.add(2, (ImageView) findViewById(R.id.img_2));
        imageViewList.add(3, (ImageView) findViewById(R.id.img_3));
        imageViewList.add(4, (ImageView) findViewById(R.id.img_4));
        location = findViewById(R.id.location);
        if (savedInstanceState != null) { //recuperar estado
            coordinates = savedInstanceState.getParcelable(MapPickerActivity.MAP_PARAM);
            city = (City) savedInstanceState.getSerializable(MapPickerActivity.CITY_PARAM);
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
            for (int i = 0; i < 5; i++) {
                if (photos.get(i) != null) {
                    addPhotoToList(photos.get(i), i);

                }


/*                Object obj = savedInstanceState.getParcelable(THUMBS_KEY + i);
                if (obj != null) {//existe imagem
                    ImageView imageView = imageViewList.get(i);
                    imageView.setImageBitmap((Bitmap) obj);
                    imageView.setPadding(0, 0, 0, 0);
                }*/
            }
        } else {
            photos = new ArrayList<File>();
            for (int i = 0; i < 5; i++) {
                photos.add(i, null);
            }
        }

        //check permissions

        EasyImage.configuration(this)
                .setImagesFolderName("photos")
        ;


        //checkGalleryAppAvailability();
        /**
         * gallery
         */
        expandedImageView = findViewById(R.id.expanded_image);
        imageOpen = -1;


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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.message_submit_point));
        builder.setTitle(getString(R.string.title_activity_add_point));

        builder.setPositiveButton(getString(R.string.message_submit_point_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addPoint();
            }
        });
        builder.setNegativeButton(getString(R.string.message_submit_point_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();


        ArrayAdapter<TypeOfLocation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(Enums.getTypeOfLocations());
        typeOfLocation.setAdapter(adapter);
    }

    /**
     * Metodo responsavel por adicionar um ponto de interesse
     */
    private void addPoint() {
        final String name = this.name.getText().toString();
        final String description = this.description.getText().toString();

        List<File> tempList = new ArrayList<>();
        for (File file : photos) {
            if (file != null) {
                tempList.add(file);
            }
        }
        if (coordinates != null && !name.isEmpty() && !description.isEmpty() && city != null && !tempList.isEmpty()) {


/*            if(getIntent().getAction().equals(AddPointActivity.EDIT_POI_ACTION)){

            }*/

            int typeID = ((TypeOfLocation) typeOfLocation.getSelectedItem()).getId();
            PointOfInterest pointOfInterest = new PointOfInterest(name, description, coordinates, city.getId(), typeID, user.getUid(), 0);
            Intent intent = new Intent(this, UploadFirebaseService.class);
            intent.setAction(UploadFirebaseService.START_UPLOAD_ACTION);
            intent.putExtra(UploadFirebaseService.POI_PARAM, pointOfInterest);
            intent.putExtra(UploadFirebaseService.FILES_PARAM, (Serializable) tempList);
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);


                    /*
                       PointOfInterest pointOfInterest = (PointOfInterest) intent.getSerializableExtra(POI_PARAM);
                List<File> photos = (List<File>) intent.getSerializableExtra(FILES_PARAM);
                     */
            startService(intent);
/*            new AddPointTask(pointOfInterest, tempList, this).execute();*/
            finish();
        } else {
            Toast.makeText(this, getString(R.string.warn_params_not_fulfilled), Toast.LENGTH_SHORT).show();
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
                if (imageOpen != -1) {
                    removeImage();
                } else {
                    dialog.show();
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }


    private void removeImage() {
        if (imageOpen != -1) {
            ImageView imageView = imageViewList.get(imageOpen);
            imageView.setImageResource(R.drawable.ic_add_photo);
            imageView.setPadding(60, 60, 60, 60);
            photos.set(imageOpen, null);
            closeImage();

        }


    }


    private void onClickImage(final ImageView imageView) {
        int tag = (Integer) imageView.getTag();
        File image = photos.get(tag);

        if (image != null) { //se tiver imagem
            if (image.exists()) {
                Picasso.with(AddPointActivity.this).load(image).fit().centerInside().into(expandedImageView);
/*                Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);*/
                expandedImageView.setVisibility(View.VISIBLE);
                updateMenuItem(getString(R.string.delete), R.drawable.ic_close);
                toolbar.setTitle(getString(R.string.text_image) + " " + String.valueOf(tag));
                imageOpen = tag;

            } else {
                Toast.makeText(this, getString(R.string.text_file_not_found), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddPointActivity.this, getString(R.string.error_image), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                addPhotoToList(imageFile, type);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(AddPointActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });

        if (requestCode == REQUEST_MAP_POINT) {
            if (resultCode == Activity.RESULT_OK) {
                coordinates = data.getParcelableExtra(MapPickerActivity.MAP_PARAM);
                city = (City) data.getSerializableExtra(MapPickerActivity.CITY_PARAM);
                location.setText(coordinates.latitude + ", " + coordinates.longitude + "\n" + "(" + city.getName() + ")");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.warn_location_not_defined), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void addPhotoToList(File imageFile, int tag) {
        ImageView imageView = imageViewList.get(tag);
        imageView.setPadding(0, 0, 0, 0);
        if (imageFile != null && imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 400, 400);

            imageView.setImageBitmap(thumb);
/*            Picasso.with(AddPointActivity.this).load(imageFile).resize(400,400).centerCrop().into(imageView);*/
            photos.set(tag, imageFile);
        }
    }

    private void closeImage() {
        toolbar.setTitle(R.string.title_activity_add_point);
        updateMenuItem("Adicionar", R.drawable.ic_check);
        expandedImageView.setVisibility(View.GONE);
        expandedImageView.setDisplayMatrix(new Matrix());
        expandedImageView.setSuppMatrix(new Matrix());
        imageOpen = -1;
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
        if (imageOpen != -1) {
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
        outState.putParcelable(MapPickerActivity.MAP_PARAM, coordinates);
        outState.putSerializable(MapPickerActivity.CITY_PARAM, city);
        outState.putSerializable(PHOTOS_KEY, photos);
/*        for (int i = 0; i < 5; i++) {
            Drawable drawable = imageViewList.get(i).getDrawable();
            if (drawable instanceof BitmapDrawable) { //significa que existe imagem
                outState.putParcelable(THUMBS_KEY + i, ((BitmapDrawable) drawable).getBitmap());
            } else {
                outState.putParcelable(THUMBS_KEY + i, null);
            }

        }*/
        super.onSaveInstanceState(outState);
    }


    private void recordPOI() {
        Intent intent = getIntent();
        if (intent.getAction() == AddPointActivity.EDIT_POI_ACTION) {
            PointOfInterest poi = (PointOfInterest) intent.getExtras().getSerializable("POI");
            this.name.setText(poi.getName());
            this.description.setText(poi.getDescription());
            List<City> cities = Enums.getCities();
            for (City city1 : cities) {
                if (city1.getId().equals(poi.getCity())) {
                    this.city = city1;
                }
            }
            this.location.setText(poi.getLatitude() + "-" + poi.getLongitude() + "\n" + "(" + poi.getCity() + ")");

        } else if (intent.getAction() == AddPointActivity.ADD_POI_ACTION) {

        }
    }
}
