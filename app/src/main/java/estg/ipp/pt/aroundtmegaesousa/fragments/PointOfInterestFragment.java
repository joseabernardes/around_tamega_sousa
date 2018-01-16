package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.LocalDateTime;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.AddPointActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.adapters.ImageAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;


public class PointOfInterestFragment extends Fragment implements View.OnClickListener {


    private int numImages;
    private ViewPager mPager;
    private ImageAdapter mImageAdapter;
    private Context mContext;
    private ImageView[] dots;
    LinearLayout sliderDotspanel;
    private TextView title;
    private AlertDialog dialog;
    private static final String ARG_POI = "arg_poi";
    private PointOfInterest pointOfInterest;
    private AppCompatRatingBar ratingBar;
    private TextView ratingText;
    private TextView description;
    private TextView location;
    private TextView localType;
    private TextView date;
    private boolean imageOpen;
    private PhotoView expandedImageView;
    public ProgressBar progressBar;


    private OnFragmentInteractionListener mListener;

    public PointOfInterestFragment() {
    }


    public static PointOfInterestFragment newInstance(PointOfInterest pointOfInterest) {
        PointOfInterestFragment fragment = new PointOfInterestFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POI, pointOfInterest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
            pointOfInterest = (PointOfInterest) getArguments().getSerializable(ARG_POI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_point_of_interest, container, false);
        setHasOptionsMenu(true);
        mPager = mContentView.findViewById(R.id.slider);
        sliderDotspanel = mContentView.findViewById(R.id.slider_dots);
        mImageAdapter = new ImageAdapter(mContext, pointOfInterest.getPhotosThumbs(), this);
        mPager.setAdapter(mImageAdapter);
        numImages = mImageAdapter.getCount();
        dots = new ImageView[numImages];
        //find
        progressBar = mContentView.findViewById(R.id.image_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(progressBar.getContext(), R.color.greyDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        expandedImageView = mContentView.findViewById(R.id.expanded_image);
        imageOpen = false;
        title = mContentView.findViewById(R.id.title);
        ratingBar = mContentView.findViewById(R.id.rating_bar);
        ratingText = mContentView.findViewById(R.id.rating_text);
        description = mContentView.findViewById(R.id.description);
        location = mContentView.findViewById(R.id.location);
        localType = mContentView.findViewById(R.id.local_type);
        date = mContentView.findViewById(R.id.date);

        title.setText(pointOfInterest.getName());
        description.setText(pointOfInterest.getDescription());
        ratingBar.setRating(pointOfInterest.getAvgRatting());
        ratingText.setText(String.valueOf(pointOfInterest.getAvgRatting()));
        date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(pointOfInterest.getDate()));
        City city = Enums.getCityByID(pointOfInterest.getCity());
        if (city != null) {
            location.setText(city.getName());
        }
        TypeOfLocation typeOfLocation = Enums.getTypeOfLocationByID(pointOfInterest.getTypeOfLocation());
        if (city != null) {
            localType.setText(typeOfLocation.getType());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        OnFragmentsChangeViewsListener context = (OnFragmentsChangeViewsListener) mContext;

        final String[] options;
        if (context.getLoggedUser().getUid().equals(pointOfInterest.getUser())) {
            options = new String[]{
                    getString(R.string.edit_poi),
                    getString(R.string.delete_poi),
                    getString(R.string.google_maps),
                    getString(R.string.add_favorites),
//                    getString(R.string.remove_favorites)
            };
        } else {
            options = new String[]{
                    getString(R.string.google_maps),
                    getString(R.string.add_favorites),
//                    getString(R.string.remove_favorites)
            };
        }


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.edit_poi))) {
                    Toast.makeText(mContext, "editar", Toast.LENGTH_SHORT).show();

                } else if (options[which].equals(getString(R.string.delete_poi))) {

                    FirebaseHelper fbh = new FirebaseHelper();
                    fbh.deletePOI(pointOfInterest.getId(), PointOfInterestFragment.this);

                } else if (options[which].equals(getString(R.string.google_maps))) {

                    Uri gmmIntentUri = Uri.parse("geo:" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude() + "?q=" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                } else if (options[which].equals(getString(R.string.add_favorites))) {


                } else if (options[which].equals(getString(R.string.remove_favorites))) {

                }
            }
        });
        dialog = builder.create();

        for (int i = 0; i < numImages; i++) {
            dots[i] = new ImageView(mContext);
            dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.indicator_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        if (dots.length > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.indicator_active));
        }


        //deve ser no onCreate provavelmente

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < numImages; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.indicator_inactive));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.indicator_active));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return mContentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    on Click Image
     */
    @Override
    public void onClick(View v) {
        OnFragmentsChangeViewsListener context = (OnFragmentsChangeViewsListener) mContext;
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(pointOfInterest.getPhotos().get(Integer.valueOf(v.getTag().toString()))).fit().centerInside().into(expandedImageView, new Callback() {
            @Override
            public void onSuccess() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {

            }
        });
        expandedImageView.setVisibility(View.VISIBLE);
        context.setOnBackPressedListener(new ImageBack());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_point_of_interest, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_options:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void deleteSuccess(boolean success) {
        if (success) {
            Toast.makeText(mContext, "Removido com sucesso", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(mContext, "Não Removido", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeImage() {
        expandedImageView.setVisibility(View.GONE);
        expandedImageView.setDisplayMatrix(new Matrix());
        expandedImageView.setSuppMatrix(new Matrix());
        progressBar.setVisibility(View.GONE);
        OnFragmentsChangeViewsListener context = (OnFragmentsChangeViewsListener) mContext;
        context.removeOnBackPressedListener();
    }


    public class ImageBack implements MainActivity.OnBackPressedListener {

        @Override
        public void doBack() {
            closeImage();
        }
    }
}
