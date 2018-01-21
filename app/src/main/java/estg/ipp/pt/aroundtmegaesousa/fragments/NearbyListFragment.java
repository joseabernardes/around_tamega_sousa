package estg.ipp.pt.aroundtmegaesousa.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.ListItemAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.Filters;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;

/**
 * Created by José Bernardes on 21/01/2018.
 */

public class NearbyListFragment extends Fragment implements ListItemAdapter.OnItemSelectedListener {


    private static final String ARG_POI_LIST = "reco_poi_list";
    private static final String ARG_TYPE = "type";
    public static final String ARG_FRAG_ID = "fragment_id";
    private ArrayList<PointOfInterest> pointsOfInterest;
    private int fragmentID;
    private View filters;
    private Context mContext;
    private OnFragmentsCommunicationListener communicationListener;
    private RecyclerView recyclerView;
    private ListItemAdapter itemAdapter;
    private FirebaseFirestore mFirestore;
    private List<Query> queries;

    public NearbyListFragment() {
        super();
    }


    public static NearbyListFragment newInstance(int fragmentID, ArrayList<PointOfInterest> pointsOfInterest) {
        NearbyListFragment fragment = new NearbyListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAG_ID, fragmentID);
        args.putSerializable(ARG_POI_LIST, pointsOfInterest);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentID = getArguments().getInt(ARG_FRAG_ID);
            pointsOfInterest = (ArrayList<PointOfInterest>) getArguments().getSerializable(ARG_POI_LIST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = mContentView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mFirestore = FirebaseFirestore.getInstance();
        filters = mContentView.findViewById(R.id.filters);
        filters.setVisibility(View.GONE);
        itemAdapter = new ListItemAdapter(this, null);
        recyclerView.setAdapter(itemAdapter);
        communicationListener.changeSelectedNavigationItem(fragmentID);
        communicationListener.showFloatingButton(false);
        communicationListener.changeActionBarTitle(getString(R.string.recommendations), true);
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (itemAdapter != null) {
            for (int i = 0; i < pointsOfInterest.size(); i++) {
                itemAdapter.onDocumentChange(pointsOfInterest.get(i), DocumentChange.Type.ADDED, i, 0);
            }
        }
    }

    @Override
    public void onDestroyView() {
        getArguments().putSerializable(ARG_POI_LIST, pointsOfInterest);
        super.onDestroyView();
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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentsCommunicationListener) {
            mContext = context;
            communicationListener = (OnFragmentsCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentsCommunicationListener");
        }
    }


}
