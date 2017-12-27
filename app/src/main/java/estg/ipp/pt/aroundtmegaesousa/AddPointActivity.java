package estg.ipp.pt.aroundtmegaesousa;


import android.content.Intent;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddPointActivity extends AppCompatActivity {

    private ImageView thirdImg;
    private ImageView firstImg;
    private ImageView secondImg;
    private ImageView fourthImg;
    private ImageView fithImg;
    private boolean imageOpen;
    private PhotoView expandedImageView;
    private Menu menu;
    private Toolbar toolbar;
    private int[] images;
    private List<Integer> imagesList;
    private ArrayList<File> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_point);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photos = new ArrayList<File>();


        imagesList = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            imagesList.add(i, R.drawable.ic_add);
        }
        imagesList.add(0, R.drawable.img_1);
        imagesList.add(1, R.drawable.img_2);
        /**
         * gallery
         */
        expandedImageView = findViewById(R.id.expanded_image);
        imageOpen = false;
        firstImg = findViewById(R.id.first_img);
        secondImg = findViewById(R.id.second_img);
        thirdImg = findViewById(R.id.third_img);
        fourthImg = findViewById(R.id.fourth_img);
        fithImg = findViewById(R.id.fith_img);

        firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(firstImg, imagesList.get(0));

            }
        });
        secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(secondImg, imagesList.get(1));
            }
        });
        thirdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(thirdImg, imagesList.get(2));
            }
        });
        fourthImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(fourthImg, imagesList.get(3));
            }
        });
        fithImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(fithImg, imagesList.get(4));
            }
        });
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
            case R.id.settings:
                //TODO


                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }


    private void onClickImage(final View thumbView, int imageResId) {
        if (imageResId != R.drawable.ic_add) { //se já tiver imagem
            expandedImageView.setImageResource(imageResId);
            expandedImageView.setVisibility(View.VISIBLE);
            updateMenuItem("Eliminar", R.drawable.ic_cancel);
            toolbar.setTitle("Imagem " + imageResId);
            imageOpen = true;
        } else {

            //adicionar imagem
        }

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


    @Override
    public void onBackPressed() {
        if (imageOpen) {
            closeImage();
        } else {
            super.onBackPressed();
        }

    }
}
