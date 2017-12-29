package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import estg.ipp.pt.aroundtmegaesousa.R;

/**
 * Created by PC on 27/12/2017.
 */

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImageIds = new int[]{R.drawable.cinf, R.drawable.around_logo, R.drawable.poi};

    public ImageAdapter(Context cotext) {
        mContext = cotext;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView iv = new ImageView(mContext);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(mImageIds[position]);
        container.addView(iv, 0);
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
