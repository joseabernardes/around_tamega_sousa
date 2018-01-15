package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Type;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.AddPointActivity;
import estg.ipp.pt.aroundtmegaesousa.adapters.ImageAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;


public class PointOfInterestFragment extends Fragment {


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
        mImageAdapter = new ImageAdapter(mContext);
        mPager.setAdapter(mImageAdapter);
        numImages = mImageAdapter.getCount();
        dots = new ImageView[numImages];
        //find
        title = mContentView.findViewById(R.id.title);
        ratingBar = mContentView.findViewById(R.id.rating_bar);
        ratingText = mContentView.findViewById(R.id.rating_text);
        description = mContentView.findViewById(R.id.description);
        location = mContentView.findViewById(R.id.location);
        localType = mContentView.findViewById(R.id.local_type);


        title.setText(pointOfInterest.getName());
        description.setText(pointOfInterest.getDescription());
        ratingBar.setRating(pointOfInterest.getAvgRatting());
        ratingText.setText(String.valueOf(pointOfInterest.getAvgRatting()));
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
                    Intent intent = new Intent(mContext, AddPointActivity.class);
                    intent.setAction(AddPointActivity.EDIT_POI_ACTION);
                    intent.putExtra("POI", pointOfInterest);
                    Toast.makeText(mContext, "editar", Toast.LENGTH_SHORT).show();
                } else if (options[which].equals(getString(R.string.delete_poi))) {
                    FirebaseHelper fbh = new FirebaseHelper();
                    fbh.deletePOI(pointOfInterest.getId(), PointOfInterestFragment.this);
                } else if (options[which].equals(getString(R.string.google_maps))) {

//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("https://www.google.com/maps/dir/?api=1&query=41.145042, -8.611419"));
//                    startActivity(intent);
                    Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                } else if (options[which].equals(getString(R.string.add_favorites))) {
                    Toast.makeText(mContext, "ADD Favoritos", Toast.LENGTH_SHORT).show();
                } else if (options[which].equals(getString(R.string.remove_favorites))) {
                    Toast.makeText(mContext, "REMOVE Favoritos", Toast.LENGTH_SHORT).show();
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
        dots[0].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.indicator_active));

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
//            Toast.makeText(mContext, "Removido com sucesso", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(mContext, "Não Removido", Toast.LENGTH_SHORT).show();
        }
    }
}
