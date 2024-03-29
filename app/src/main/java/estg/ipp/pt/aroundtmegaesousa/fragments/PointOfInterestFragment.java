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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.adapters.ImageAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseResultListener;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.Rating;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;

public class PointOfInterestFragment extends Fragment implements View.OnClickListener, FirebaseResultListener {

    public static final String DOCUMENT_ID = "document_id";
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
    private PhotoView expandedImageView;
    public ProgressBar progressBar;
    private FirebaseHelper fbh;
    private RatingBar rt;
    private Rating firebaseRating;
    private ArrayAdapter<Option> adapter;
    private Button vote, openMap;
    private OnFragmentsCommunicationListener mListener;

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
        final View mContentView = inflater.inflate(R.layout.fragment_point_of_interest, container, false);
        setHasOptionsMenu(true);
        mPager = mContentView.findViewById(R.id.slider);
        sliderDotspanel = mContentView.findViewById(R.id.slider_dots);
        mImageAdapter = new ImageAdapter(mContext, pointOfInterest.getPhotosThumbs(), this);
        mPager.setAdapter(mImageAdapter);
        numImages = mImageAdapter.getCount();
        dots = new ImageView[numImages];
        //findViewsById
        progressBar = mContentView.findViewById(R.id.image_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(progressBar.getContext(), R.color.greyDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        expandedImageView = mContentView.findViewById(R.id.expanded_image);
        title = mContentView.findViewById(R.id.title);
        ratingBar = mContentView.findViewById(R.id.rating_bar);
        ratingText = mContentView.findViewById(R.id.rating_text);
        description = mContentView.findViewById(R.id.description);
        location = mContentView.findViewById(R.id.location);
        localType = mContentView.findViewById(R.id.local_type);
        date = mContentView.findViewById(R.id.date);
        vote = mContentView.findViewById(R.id.vote);
        openMap = mContentView.findViewById(R.id.openMap);
        fbh = new FirebaseHelper();
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.rating_dialog, null);
                builder.setView(view);
                rt = view.findViewById(R.id.rating_dialog_rating_bar);
                builder.setTitle(getString(R.string.classificate_poi));
                final TextView tv = view.findViewById(R.id.rating_dialog_text_view);
                fbh.checkRating(pointOfInterest.getId(), mListener.getLoggedUser().getUid(), PointOfInterestFragment.this);
                rt.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (rating == 1) {
                            tv.setText(getString(R.string.hate));
                        } else if (rating == 2) {
                            tv.setText(getString(R.string.bad));
                        } else if (rating == 3) {
                            tv.setText(getString(R.string.medium));
                        } else if (rating == 4) {
                            tv.setText(getString(R.string.like));
                        } else if (rating == 5) {
                            tv.setText(getString(R.string.love));
                        }
                    }
                });
                builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (rt.getRating() >= 1) {

                            Rating rating = new Rating(mListener.getLoggedUser().getUid(), rt.getRating());
                            if (firebaseRating != null) {
                                fbh.editRating(pointOfInterest.getId(), firebaseRating.getId(), rating.getRating(), PointOfInterestFragment.this);
                            } else {
                                fbh.addRating(rating, pointOfInterest.getId(), PointOfInterestFragment.this);
                            }
                        } else {
                            Toast.makeText(mContext, getString(R.string.invalid_rating), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ItemMapFragment.newInstance(pointOfInterest);
                if (mContext instanceof OnFragmentsCommunicationListener) {
                    OnFragmentsCommunicationListener mListener = (OnFragmentsCommunicationListener) mContext;
                    mListener.replaceFragment(fragment);
                }
            }
        });


        title.setText(pointOfInterest.getName());
        description.setText(pointOfInterest.getDescription());
        ratingBar.setRating(pointOfInterest.getAvgRating());
        ratingText.setText(String.valueOf(pointOfInterest.getAvgRating()));
        date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(pointOfInterest.getDate()));
        City city = Enums.getCityByID(pointOfInterest.getCity());
        if (city != null) {
            location.setText(city.getName());
        }
        TypeOfLocation typeOfLocation = Enums.getTypeOfLocationByID(pointOfInterest.getTypeOfLocation());
        if (city != null) {
            localType.setText(typeOfLocation.getType());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


        final List<Option> options = new ArrayList<>();

        if (mListener.getLoggedUser().getUid().equals(pointOfInterest.getUser())) {
            options.add(new Option(1, getString(R.string.delete)));
            options.add(new Option(2, getString(R.string.google_maps)));
        } else {
            options.add(new Option(2, getString(R.string.google_maps)));
        }


        adapter = new ArrayAdapter<>(mContext, R.layout.item_dialog_options);
        adapter.addAll(options);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (adapter.getItem(which).id) {
                    case 1:

                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mContext);
                        deleteDialog.setTitle(getString(R.string.delete_poi_title));
                        deleteDialog.setMessage(getString(R.string.delete_poi_text));

                        deleteDialog.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fbh.deletePOI(pointOfInterest.getId(), PointOfInterestFragment.this);
                            }
                        });
                        deleteDialog.setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        deleteDialog.show();
                        break;
                    case 2:
                        Uri gmmIntentUri = Uri.parse("geo:" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude() + "?q=" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        break;
                    case 3:
                        fbh.addFavorite(pointOfInterest, mListener.getLoggedUser().getUid(), PointOfInterestFragment.this);
                        break;
                    case 4:
                        fbh.removeFavorite(mListener.getLoggedUser().getUid(), pointOfInterest, PointOfInterestFragment.this);
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
        existFavorite(fbh.checkFavorites(pointOfInterest, mListener.getLoggedUser().getUid()));


        //toolbar
        mListener.changeActionBarTitle(pointOfInterest.getName(), false);
        mListener.showFloatingButton(false);

        return mContentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentsCommunicationListener) {
            mListener = (OnFragmentsCommunicationListener) context;
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
        OnFragmentsCommunicationListener context = (OnFragmentsCommunicationListener) mContext;
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


    private void closeImage() {
        expandedImageView.setVisibility(View.GONE);
        expandedImageView.setDisplayMatrix(new Matrix());
        expandedImageView.setSuppMatrix(new Matrix());
        progressBar.setVisibility(View.GONE);
        OnFragmentsCommunicationListener context = (OnFragmentsCommunicationListener) mContext;
        context.removeOnBackPressedListener();
    }


    public class ImageBack implements MainActivity.OnBackPressedListener {

        @Override
        public void doBack() {
            closeImage();
        }
    }


    public void existFavorite(boolean favorite) {
        if (favorite) {
            adapter.remove(new Option(3, ""));
            adapter.add(new Option(4, getString(R.string.remove_favorites)));
        } else {
            adapter.remove(new Option(4, ""));
            adapter.add(new Option(3, getString(R.string.add_favorites)));
        }
    }

    @Override
    public void firebaseTaskResult(boolean result, String type) {
        String message;
        if (type.equals(FirebaseResultListener.ADD_FAVORITE)) {
            existFavorite(fbh.checkFavorites(pointOfInterest, mListener.getLoggedUser().getUid()));
            if (result) {
                message = getString(R.string.add_to_favorites);
            } else {
                message = getString(R.string.not_add_to_favorites);
            }
        } else if (type.equals(FirebaseResultListener.REMOVE_FAVORITE)) {
            existFavorite(fbh.checkFavorites(pointOfInterest, mListener.getLoggedUser().getUid()));
            if (result) {
                message = getString(R.string.remove_from_favorites);
            } else {
                message = getString(R.string.not_remove_from_favorites);
            }
        } else if (type.equals(FirebaseResultListener.ADD_RATING)) {
            if (result) {
                message = getString(R.string.classificate_added);
            } else {
                message = getString(R.string.classificate_not_added);
            }

        } else if (type.equals(FirebaseResultListener.EDIT_RATING)) {
            if (result) {
                message = getString(R.string.classficate_changed);
            } else {
                message = getString(R.string.classificate_not_changged);
            }

        } else if (type.equals(FirebaseResultListener.DELETE_POI)) {
            if (result) {
                message = getString(R.string.remove_poi_success);
                getActivity().onBackPressed();
            } else {
                message = getString(R.string.remove_poi_unsuccess);
            }
        } else {
            message = getString(R.string.warn_login_unknown_error);
        }

        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void firebaseRatingTaskResult(Rating rating) {
        if (rating != null) {
            rt.setRating(rating.getRating());
            this.firebaseRating = rating;
        }
    }


    private class Option {

        int id;
        String option;

        public Option(int id, String option) {
            this.id = id;
            this.option = option;
        }

        @Override
        public String toString() {
            return option;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Option option = (Option) o;

            return id == option.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

}
