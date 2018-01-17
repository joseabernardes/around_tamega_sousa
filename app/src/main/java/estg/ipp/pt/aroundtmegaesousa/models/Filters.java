package estg.ipp.pt.aroundtmegaesousa.models;

import android.content.Context;
import android.text.TextUtils;


import com.google.firebase.firestore.Query;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;


public class Filters {

    private int typeOfLocation = -1;
    private String city = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {
    }

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(PointOfInterest.FIELD_DATE);
        filters.setSortDirection(Query.Direction.DESCENDING);
        return filters;
    }

    public boolean hasTypeOfLocation() {
        return typeOfLocation != -1;
    }

    public boolean hasCity() {
        return !(TextUtils.isEmpty(city));
    }


    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public int getTypeOfLocation() {
        return typeOfLocation;
    }

    public void setTypeOfLocation(int typeOfLocation) {
        this.typeOfLocation = typeOfLocation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (typeOfLocation == -1 && city == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_points));
            desc.append("</b>");
        }

        if (typeOfLocation != -1) {
            desc.append("<b>");
            desc.append(Enums.getTypeOfLocationByID(typeOfLocation));
            desc.append("</b>");
        }

        if (typeOfLocation != -1 && city != null) {
            desc.append(" " + context.getString(R.string.filter_in) + " ");
        }

        if (city != null) {
            desc.append("<b>");
            desc.append(Enums.getCityByID(city));
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (PointOfInterest.FIELD_AVG_RATING.equals(sortBy)) {
            return context.getString(R.string.sorted_by_rating);
        } else {
            return context.getString(R.string.sorted_by_date);
        }
    }
}
