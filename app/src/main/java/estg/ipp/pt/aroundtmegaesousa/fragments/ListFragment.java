package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.content.Context;
import android.os.Bundle;
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
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
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
    private OnFragmentsCommunicationListener communicationListener;
    private RecyclerView recyclerView;
    private View filterBar;
    private View filters;
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

        mQuery = this.getLists();


        itemAdapter = new ListItemAdapter(this, mQuery);
        recyclerView.setAdapter(itemAdapter);
        mFilters = Filters.getDefault();
        filters = mContentView.findViewById(R.id.filters);
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
                mFilters = Filters.getDefault();
                onFilter(mFilters);
                mFilterDialog = new FilterDialogFragment();
            }
        });


        mFilterDialog = new FilterDialogFragment();

        changeLayoutByFragmentType();
        if (getArguments() != null && getArguments().getSerializable(FILTER) != null) {
            mFilters = (Filters) getArguments().getSerializable(FILTER);
            mFilterDialog.setFilters(mFilters);
        }
        return mContentView;
    }

    private void changeLayoutByFragmentType() {
        if (mContext != null) {
            String title;
            switch (typeOfFragment) {
                case LIST:
                    communicationListener.showFloatingButton(false);
                    title = getString(R.string.title_fragment_list);
                    break;
                case FAVORITES:
                    filters.setVisibility(View.GONE);
                    communicationListener.showFloatingButton(false);
                    title = getString(R.string.title_fragment_list_favo);
                    break;
                case MY_POINTS:
                    filters.setVisibility(View.GONE);
                    communicationListener.showFloatingButton(true);
                    setOnScrolledRecyclerView(communicationListener);
                    title = getString(R.string.title_fragment_list_my_points);
                    break;
                default:
                    filters.setVisibility(View.GONE);
                    title = getString(R.string.title_fragment_list);
            }
            communicationListener.changeActionBarTitle(title);
        }


    }

    private void setOnScrolledRecyclerView(final OnFragmentsCommunicationListener viewChanger) {
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

        if (context instanceof OnFragmentsCommunicationListener) {
            mContext = context;
            communicationListener = (OnFragmentsCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentsCommunicationListener");
        }
    }


    @Override
    public void onItemSelected(PointOfInterest pointOfInterest) {
        Fragment fragment = PointOfInterestFragment.newInstance(pointOfInterest);
        communicationListener.replaceFragment(fragment);
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilter(Filters filters) {

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
        if (typeOfFragment.equals(LIST)) {//apenas existe barra de filtros na lista geral
            onFilter(mFilters);
        }
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
        switch (typeOfFragment) {
            case LIST:
                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .orderBy(PointOfInterest.FIELD_DATE);
                break;
            case FAVORITES:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -2000);

                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .whereGreaterThan("favorites." + communicationListener.getLoggedUser().getUid(), cal.getTime())
                        .orderBy("favorites." + communicationListener.getLoggedUser().getUid());
                break;
            case MY_POINTS:
                mQuery = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                        .whereEqualTo("user", communicationListener.getLoggedUser().getUid())
                        .orderBy("date", Query.Direction.DESCENDING);
                break;
        }
        return mQuery;
    }

    @Override
    public void onDestroyView() {
        getArguments().putSerializable(FILTER, mFilters);
        super.onDestroyView();


    }
}
