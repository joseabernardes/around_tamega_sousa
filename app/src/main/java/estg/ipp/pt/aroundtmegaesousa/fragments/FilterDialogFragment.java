package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.Filters;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";
    private static final String ARG_MAP = "is_map";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;
    private Spinner mTypeOfLocationSpinner;
    private Spinner mCitySpinner;
    private Spinner mSortSpinner;
    private Button applyButton;
    private Button cancelButton;
    private FilterListener mFilterListener;
    private Filters filters;
    private boolean isListMap;
    private View spinnerParent;


    public static FilterDialogFragment newInstance(boolean isListMap) {
        FilterDialogFragment f = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MAP, isListMap);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isListMap = getArguments().getBoolean(ARG_MAP, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);
        mTypeOfLocationSpinner = mRootView.findViewById(R.id.spinner_tof);
        mCitySpinner = mRootView.findViewById(R.id.spinner_city);
        mSortSpinner = mRootView.findViewById(R.id.spinner_sort);
        applyButton = mRootView.findViewById(R.id.button_search);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked();
            }
        });
        cancelButton = mRootView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
        spinnerParent = mRootView.findViewById(R.id.spinner_parent);
        if(!isListMap){
            spinnerParent.setVisibility(View.VISIBLE);
        }
        ArrayAdapter<TypeOfLocation> adapter = new ArrayAdapter<>(getContext(), R.layout.item_dialog_options);
        adapter.add(new TypeOfLocation(-1, getString(R.string.value_any_local))); //adicionar á primeira posição
        adapter.addAll(Enums.getTypeOfLocations());
        mTypeOfLocationSpinner.setAdapter(adapter);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dialog_options);
        cityAdapter.add(new City(null, getString(R.string.value_any_city))); //adicionar á primeira posição
        cityAdapter.addAll(Enums.getCities());
        mCitySpinner.setAdapter(cityAdapter);
        ArrayAdapter<String> orderedAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_dialog_options);
        orderedAdapter.add(getString(R.string.sort_by_rating));
        orderedAdapter.add(getString(R.string.sort_by_date));
        mSortSpinner.setAdapter(orderedAdapter);

        if (getParentFragment() instanceof FilterListener) {
            mFilterListener = (FilterListener) getParentFragment();
        }

        if (filters != null) {
            setSelections();
        }


        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }
        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }


    private int getSelectedTypeOfLocation() {
        TypeOfLocation selected = (TypeOfLocation) mTypeOfLocationSpinner.getSelectedItem();
        if (selected != null) {
            return selected.getId();
        } else {
            return -1;
        }
    }

    @Nullable
    private String getSelectedCity() {

        City selected = (City) mCitySpinner.getSelectedItem();
        if (selected != null && selected.getId() != null) {
            return selected.getId();
        }
        return null;  //todos os concelhos
    }


    @Nullable
    private String getSelectedSortBy() {

        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return PointOfInterest.FIELD_AVG_RATING;
        }
        if (getString(R.string.sort_by_date).equals(selected)) {
            return PointOfInterest.FIELD_DATE;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mTypeOfLocationSpinner.setSelection(0);
            mCitySpinner.setSelection(0);
            mSortSpinner.setSelection(0);
            filters = Filters.getDefault();
        }

    }


    public void setFilters(Filters filters) {
        this.filters = filters;

    }

    public void setSelections() {

        if (filters.hasTypeOfLocation()) {
            setSelectedTypeOfLocation(filters.getTypeOfLocation());
        }
        if (filters.hasCity()) {
            setSelectedCity(filters.getCity());
        }
        if (filters.hasSortBy()) {
            setSelectedSortBy(filters.getSortBy());
        }
    }


    private void setSelectedSortBy(String sortBy) {
        if (sortBy.equals(PointOfInterest.FIELD_AVG_RATING)) {
            mSortSpinner.setSelection(1);
        } else {
            mSortSpinner.setSelection(0);
        }
    }


    private void setSelectedTypeOfLocation(int position) {
        mTypeOfLocationSpinner.setSelection(position);
    }

    @Nullable
    private void setSelectedCity(String cityID) {
        Adapter adapter = mCitySpinner.getAdapter();
        for (int i = 1; i < adapter.getCount(); i++) { //skip TODOS OS CONCELHOS
            City city = (City) adapter.getItem(i);
            if (city.getId().equals(cityID)) {
                mCitySpinner.setSelection(i);
                return;
            }
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setTypeOfLocation(getSelectedTypeOfLocation());
            filters.setCity(getSelectedCity());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(Query.Direction.DESCENDING);
        }

        return filters;
    }

}
