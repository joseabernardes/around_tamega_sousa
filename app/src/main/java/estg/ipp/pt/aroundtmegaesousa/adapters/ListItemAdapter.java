package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by PC on 29/12/2017.
 */

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

    private Context mContext;
    private List<PointOfInterest> mPOI;

    public ListItemAdapter(Context mContext, List<PointOfInterest> poi_list) {
        this.mContext = mContext;
        this.mPOI = poi_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_poi, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PointOfInterest poi = mPOI.get(position);
        ImageView iv = holder.img;
        iv.setImageResource(R.drawable.cinf);

        TextView tv = holder.nome;
        tv.setText(poi.getName());

        RatingBar ratingBar = holder.rb;
        ratingBar.setRating(3);

        TextView rat = holder.rating_text;
        rat.setText("3");
    }




    @Override
    public int getItemCount() {
        return mPOI.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nome;
        public ImageView img;
        public RatingBar rb;
        public TextView rating_text;

        public ViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.item_desc_name);
            img = itemView.findViewById(R.id.list_poi_img);
            rb = itemView.findViewById(R.id.item_desc_rating_bar);
            rating_text = itemView.findViewById(R.id.item_rating_text);

        }
    }
}
