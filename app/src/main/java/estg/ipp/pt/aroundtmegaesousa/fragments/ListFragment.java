package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import java.util.Calendar;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.ListItemAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.Filters;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;


public class ListFragment extends Fragment implements ListItemAdapter.OnItemSelectedListener, FilterDialogFragment.FilterListener {


    private static final String ARG_TYPE = "type";
    public static final String FAVORITES = "favorites";
    public static final String MY_POINTS = "my_points";
    public static final String LIST = "list";
    public static final String FILTER = "filter";

    private String typeOfFragment;
    private Context mContext;
    private RecyclerView recyclerView;
    private View filterBar;
    private FilterDialogFragment mFilterDialog;
    private Filters mFilters;
    private ListItemAdapter itemAdapter;

    private TextView currentSearch;
    private TextView currentSortBy;
    private ImageView buttonCancel;


    private FirebaseFirestore mFirestore;
    private Query mQuery;


    public ListFragment() {

    }


    public static ListFragment newInstance(String typeOfFragment) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, typeOfFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            typeOfFragment = getArguments().getString(ARG_TYPE);
        }

        if (savedInstanceState != null) {
            mFilters = (Filters) savedInstanceState.getSerializable(FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = mContentView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        currentSearch = mContentView.findViewById(R.id.text_current_search);
        currentSortBy = mContentView.findViewById(R.id.text_current_sort_by);
        buttonCancel = mContentView.findViewById(R.id.button_clear_filter);
        mFirestore = FirebaseFirestore.getInstance();


//        mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
//                .orderBy("date", Query.Direction.DESCENDING);
        mQuery = this.getLists();

        /*ArrayList<PointOfInterest> contacts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            contacts.add(new PointOfInterest("Parque das Nações do Douro " + i));
        }*/


        itemAdapter = new ListItemAdapter(this, mQuery);
        recyclerView.setAdapter(itemAdapter);
        mFilters = Filters.getDefault();
        filterBar = mContentView.findViewById(R.id.filter_bar);
        filterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.resetFilters();
                onFilter(Filters.getDefault());
            }
        });


        mFilterDialog = new FilterDialogFragment();

        changeActionBarTitle();
        return mContentView;
    }

    private void changeActionBarTitle() {
        if (mContext != null) {
            OnFragmentsChangeViewsListener viewChanger = (OnFragmentsChangeViewsListener) mContext;
            String title;
            switch (typeOfFragment) {
                case LIST:
                    viewChanger.showFloatingButton(false);
                    title = getString(R.string.title_fragment_list);
                    break;
                case FAVORITES:
                    viewChanger.showFloatingButton(false);
                    title = getString(R.string.title_fragment_list_favo);
                    break;
                case MY_POINTS:
                    viewChanger.showFloatingButton(true);
                    setOnScrolledRecyclerView(viewChanger);
                    title = getString(R.string.title_fragment_list_my_points);
                    break;
                default:
                    title = getString(R.string.title_fragment_list);
            }
            viewChanger.changeActionBarTitle(title);
        }


    }

    private void setOnScrolledRecyclerView(final OnFragmentsChangeViewsListener viewChanger) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && viewChanger.isShownFloatingButton()) {
                    viewChanger.showFloatingButton(false);
                } else if (dy < 0 && !viewChanger.isShownFloatingButton()) {
                    viewChanger.showFloatingButton(true);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentsChangeViewsListener) {
            mContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onItemSelected(PointOfInterest pointOfInterest) {

        Fragment fragment = PointOfInterestFragment.newInstance(pointOfInterest);
        if (mContext instanceof OnFragmentsChangeViewsListener) {
            OnFragmentsChangeViewsListener mListener = (OnFragmentsChangeViewsListener) mContext;
            mListener.replaceFragment(fragment);
            mListener.changeActionBarTitle(pointOfInterest.getName());
            mListener.showFloatingButton(false);
        }

    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Raia", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION);

        if (filters.hasTypeOfLocation()) {
            query = query.whereEqualTo(PointOfInterest.FIELD_TYPE_OF_LOCATION, filters.getTypeOfLocation());
        }
        if (filters.hasCity()) {
            query = query.whereEqualTo(PointOfInterest.FIELD_CITY, filters.getCity());
        }
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }
        itemAdapter.setQuery(query);
        currentSearch.setText(Html.fromHtml(filters.getSearchDescription(mContext)));
        currentSortBy.setText(filters.getOrderDescription(mContext));
        this.mFilters = filters;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        //onFilter(mFilters);

        if (itemAdapter != null) {
            itemAdapter.startListening();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (itemAdapter != null) {
            itemAdapter.stopListening();
        }
    }

    private Query getLists() {
        Query mQuery = null;
        OnFragmentsChangeViewsListener viewChanger = (OnFragmentsChangeViewsListener) mContext;
        switch (typeOfFragment) {
            case LIST:
                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .orderBy("date");
                break;
            case FAVORITES:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -2000);

                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .whereGreaterThan("favorites." + viewChanger.getLoggedUser().getUid(),  cal.getTime())
                        .orderBy("favorites."+ viewChanger.getLoggedUser().getUid());
                break;
            case MY_POINTS:
                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .whereEqualTo("user", viewChanger.getLoggedUser().getUid())
                        .orderBy("date", Query.Direction.DESCENDING);
                break;
        }
        return mQuery;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(FILTER, mFilters);
        super.onSaveInstanceState(outState);
    }
}
