package com.example.haritmoolphunt.liveat500px.fragment;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.haritmoolphunt.liveat500px.R;
import com.example.haritmoolphunt.liveat500px.dao.PhotoListDao;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PhotoSummaryFragment extends Fragment {

    PhotoListDao dao;
    private TextView tvName;
    private TextView tvDes;
    private ImageView ivImg;

    public PhotoSummaryFragment() {
        super();
    }

    public static PhotoSummaryFragment newInstance(PhotoListDao dao) {
        PhotoSummaryFragment fragment = new PhotoSummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable("dao",dao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        dao = getArguments().getParcelable("dao");

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_summary, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        ivImg = (ImageView) rootView.findViewById(R.id.ivImg);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvDes = (TextView) rootView.findViewById(R.id.tvDescription);

        tvName.setText(dao.getCaption());
        tvDes.setText(dao.getUsername()+"\n"+dao.getCamera());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.loading);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(PhotoSummaryFragment.this)
                .setDefaultRequestOptions(requestOptions)
                .load(dao.getImageUrl())
                .into(ivImg);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

}
