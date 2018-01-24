package com.example.haritmoolphunt.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.haritmoolphunt.liveat500px.R;
import com.example.haritmoolphunt.liveat500px.dao.PhotoItemCollectionDao;
import com.example.haritmoolphunt.liveat500px.dao.PhotoListDao;
import com.example.haritmoolphunt.liveat500px.manager.PhotoListManager;
import com.example.haritmoolphunt.liveat500px.view.PhotoListItem;

import datatype.MutableInteger;

/**
 * Created by Harit Moolphunt on 23/10/2560.
 */

public class PhotoListAdapter extends BaseAdapter{

    private PhotoItemCollectionDao dao;

    MutableInteger lastPositionInteger;

    public PhotoListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    @Override
    public int getCount() {
        if(dao == null)
        {
            return 1;
        }
        if(dao.getData() == null)
        {
            return 1;
        }
        return dao.getData().size() + 1;
    }

    @Override
    public Object getItem(int position) {
        try {
            return dao.getData().get(position);
        }catch (NullPointerException e){
            return e;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addLastPosition(int addPos)
    {
        lastPositionInteger.setValue(lastPositionInteger.getValue() + addPos);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if(position == getCount() - 1){
                ProgressBar progressBar;
                if(convertView != null){
                    progressBar = (ProgressBar) convertView;
                }else{
                    progressBar = new ProgressBar(parent.getContext());
                }
                return progressBar;
            }

            PhotoListItem item;

            if (convertView != null) {
                item = (PhotoListItem) convertView;
            } else {
                item = new PhotoListItem(parent.getContext());
            }

            PhotoListDao dao = (PhotoListDao) getItem(position);
            item.setName(dao.getCaption());
            item.setDescription(dao.getUsername()+ "\n" + dao.getCamera());
            item.setImageUrl(dao.getImageUrl());

            if(lastPositionInteger.getValue() < position){
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
                item.startAnimation(animation);
                lastPositionInteger.setValue(position);
            }

            return item;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }
}
