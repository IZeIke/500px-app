package com.example.haritmoolphunt.liveat500px.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.haritmoolphunt.liveat500px.R;
import com.example.haritmoolphunt.liveat500px.activity.MoreInfoActivity;
import com.example.haritmoolphunt.liveat500px.adapter.PhotoListAdapter;
import com.example.haritmoolphunt.liveat500px.dao.PhotoItemCollectionDao;
import com.example.haritmoolphunt.liveat500px.dao.PhotoListDao;
import com.example.haritmoolphunt.liveat500px.manager.Contextor;
import com.example.haritmoolphunt.liveat500px.manager.HttpManager;
import com.example.haritmoolphunt.liveat500px.manager.PhotoListManager;
import com.example.haritmoolphunt.liveat500px.manager.http.ApiService;
import com.example.haritmoolphunt.liveat500px.view.PhotoListItem;
import com.example.haritmoolphunt.liveat500px.view.state.BundleSavedState;

import java.io.IOException;

import datatype.MutableInteger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    //Variable
    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    MutableInteger lastPositionInteger;
    Button newFeedButton;
    Boolean frag_loading = false;

    public interface FragmentListener{
        void onPhotoItemClicked(PhotoListDao dao);
    }

    //Function

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);

        if(savedInstanceState != null)
        {
            //Restore InstanceState
            onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView,savedInstanceState );
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        photoListManager = new PhotoListManager();
        lastPositionInteger = new MutableInteger(-1);
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        newFeedButton = (Button) rootView.findViewById(R.id.new_feed_button);
        newFeedButton.setOnClickListener(buttonClickListener);
        hideButtonNewPhoto();

        listAdapter = new PhotoListAdapter(lastPositionInteger);
        listAdapter.setDao(photoListManager.getDao());
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);

        listView.setOnScrollListener(scrollListener);

        if(savedInstanceState == null)
            refreshData();

    }


    private void showButtonNewPhoto() {
        Animation animation = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(),R.anim.zoom_fade_in);
        newFeedButton.setVisibility(View.VISIBLE);
        newFeedButton.startAnimation(animation);
    }

    private void hideButtonNewPhoto() {
        Animation animation = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(),R.anim.zoom_fade_out);
        newFeedButton.setVisibility(View.GONE);
        newFeedButton.startAnimation(animation);
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new CallbackList(CallbackList.MODE_RELOAD));
    }

    private void reloadNewData() {
        int maxId = photoListManager.getMaximunId();

        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadNewPhotoList(maxId);
        call.enqueue(new CallbackList(CallbackList.MODE_REFRESH));
    }

    private void loadOldData() {
        int minId = photoListManager.getMinnimumId();

        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadOldPhotoList(minId);
        call.enqueue(new CallbackList(CallbackList.MODE_LOAD_OLDER));
    }

    private void refreshData()
    {
        if(photoListManager.getCount() == 0)
        {
            reloadData();
        }else
        {
            reloadNewData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putBundle("photoListManager",
                photoListManager.onSaveInstanceState());

        outState.putBundle("lastPosition",lastPositionInteger.onSaveInstanceState());

    }
    /*
     * Restore Instance State Here
     */
    private void onRestoreInstanceState(Bundle savedInstanceState){
        //Restore Instance State here
        photoListManager.onRestoreInstanceState(savedInstanceState.getBundle("photoListManager"));
        lastPositionInteger.onRestoreInstanceState(savedInstanceState.getBundle("lastPosition"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //Listener Zone

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {

            swipeRefreshLayout.setEnabled(firstVisibleItem == 0);

            if (firstVisibleItem == 0)
                newFeedButton.setVisibility(View.GONE);

            if (firstVisibleItem + visibleItemCount >= totalItemCount)
                if (photoListManager.getCount() > 0)
                    if (frag_loading == false) {
                        loadOldData();
                        frag_loading = true;
                    }
        }
    };

    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PhotoListDao dao = photoListManager.getDao().getData().get(position);
            FragmentListener listener = (FragmentListener) getActivity();
            listener.onPhotoItemClicked(dao);
        }
    };

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listView.smoothScrollToPosition(0);
            hideButtonNewPhoto();
        }
    };

    final SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    //Inner Class

    class CallbackList implements Callback<PhotoItemCollectionDao>{

        public static final int MODE_RELOAD = 0;
        public static final int MODE_REFRESH = 1;
        public static final int MODE_LOAD_OLDER = 2;

        int mode;

        public CallbackList(int mode)
        {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if(response.isSuccessful())
            {
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePos = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = (c == null) ? 0 : c.getTop();

                if(mode == MODE_RELOAD)
                    photoListManager.setDao(dao);
                else if(mode == MODE_REFRESH)
                    photoListManager.insertOnTopDao(dao);
                else if(mode == MODE_LOAD_OLDER)
                    photoListManager.insertOnTailDao(dao);
                frag_loading = false;

                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();

                if(mode == MODE_REFRESH){
                    int additionalSize = (dao == null && dao.getData() == null) ? 0 : dao.getData().size();
                    listAdapter.addLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePos+additionalSize,top);
                    if(additionalSize > 0)
                        showButtonNewPhoto();
                }

                Toast.makeText(Contextor.getInstance().getContext(),"Load Completed",Toast.LENGTH_SHORT).show();
            }else {
                if(mode == MODE_LOAD_OLDER) {
                    frag_loading = false;
                }
                try {
                    Toast.makeText(Contextor.getInstance().getContext(),response.errorBody().string(),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
            if(mode == MODE_LOAD_OLDER) {
                frag_loading = false;
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}
