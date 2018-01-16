package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.AddPointActivity;

/**
 * Created by PC on 27/12/2017.
 */

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> photoThumb;
    private View.OnClickListener imageClickListener;

    public ImageAdapter(Context context, List<String> photoThumb, View.OnClickListener imageClickListener) {
        this.mContext = context;
        this.photoThumb = photoThumb;
        this.imageClickListener = imageClickListener;
    }

    @Override
    public int getCount() {
        return photoThumb.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setTag(position);
        Picasso.with(mContext).load(photoThumb.get(position)).fit().centerInside().into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(imageView, 0);
        imageView.setOnClickListener(imageClickListener);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
