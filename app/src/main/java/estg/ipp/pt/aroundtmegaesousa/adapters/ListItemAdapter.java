package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by PC on 29/12/2017.
 */

public class ListItemAdapter extends PointOfInterestAdapter {

    private OnItemSelectedListener mListener;


    public ListItemAdapter(OnItemSelectedListener mListener, Query query) {
        super(query);
        this.mListener = mListener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_poi, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getPointOfInterest(position), mListener);
    }

    @Override
    protected void onError(FirebaseFirestoreException e) {
        mListener.onError(e);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public RatingBar ratingBar;
        public TextView ratingText;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_desc_name);
            image = itemView.findViewById(R.id.list_poi_img);
            ratingBar = itemView.findViewById(R.id.item_desc_rating_bar);
            ratingText = itemView.findViewById(R.id.item_rating_text);
            progressBar = itemView.findViewById(R.id.image_loading_progress);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(progressBar.getContext(), R.color.greyDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
            progressBar.setVisibility(View.VISIBLE);
        }

        public void bind(final PointOfInterest pointOfInterest, final OnItemSelectedListener mListener) {
            name.setText(pointOfInterest.getName());
            String imageURL = null;
            if (!pointOfInterest.getPhotosThumbs().isEmpty()) {
                imageURL = pointOfInterest.getPhotosThumbs().get(0);
            }
            if (imageURL != null) {
                Picasso.with(image.getContext()).load(imageURL).fit().placeholder(R.drawable.default_point).centerInside().into(image, new Callback() {
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
            }
            ratingBar.setRating(pointOfInterest.getAvgRatting());
            ratingText.setText(String.valueOf(pointOfInterest.getAvgRatting()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemSelected(pointOfInterest);
                    }
                }
            });


        }
    }


    public interface OnItemSelectedListener {
        void onItemSelected(PointOfInterest pointOfInterest);

        void onError(FirebaseFirestoreException e);
    }
}
