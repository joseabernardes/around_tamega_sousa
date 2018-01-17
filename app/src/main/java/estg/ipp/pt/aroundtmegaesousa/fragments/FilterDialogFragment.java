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

        ArrayAdapter<TypeOfLocation> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner);
        adapter.add(new TypeOfLocation(-1, getString(R.string.value_any_local))); //adicionar á primeira posição
        adapter.addAll(Enums.getTypeOfLocations());
        mTypeOfLocationSpinner.setAdapter(adapter);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner);
        cityAdapter.add(new City(null, getString(R.string.value_any_city))); //adicionar á primeira posição
        cityAdapter.addAll(Enums.getCities());
        mCitySpinner.setAdapter(cityAdapter);

        if (getParentFragment() instanceof FilterListener) {
            mFilterListener = (FilterListener) getParentFragment();
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

    @Nullable
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
