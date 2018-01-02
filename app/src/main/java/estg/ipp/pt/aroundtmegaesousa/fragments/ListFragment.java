package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.ListItemAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsChangeViewsListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;


public class ListFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_TYPE = "type";


    public static final String FAVORITES = "favorites";
    public static final String MY_POINTS = "my_points";
    public static final String LIST = "list";

    private String typeOfFragment;
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private RecyclerView recyclerView;
    private View filterBar;
    private View clearFilter;
    private FilterDialogFragment mFilterDialog;


    public ListFragment() {

    }


    public static ListFragment newInstance(String typeOfFragment, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, typeOfFragment);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            typeOfFragment = getArguments().getString(ARG_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mContentView = inflater.inflate(R.layout.fragment_list, container, false);


        ArrayList<PointOfInterest> contacts = new ArrayList<PointOfInterest>();
        recyclerView = mContentView.findViewById(R.id.recycler);

        for (int i = 0; i < 10; i++) {
            contacts.add(new PointOfInterest("Parque das Nações do Douro " + i));
        }

        ListItemAdapter lia = new ListItemAdapter(mContext, contacts);
        recyclerView.setAdapter(lia);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        filterBar = mContentView.findViewById(R.id.filter_bar);
        filterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
            }
        });
        clearFilter = mContentView.findViewById(R.id.button_clear_filter);


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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
