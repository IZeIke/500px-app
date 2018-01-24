package com.example.haritmoolphunt.liveat500px.manager.http;

import com.example.haritmoolphunt.liveat500px.dao.PhotoItemCollectionDao;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Harit Moolphunt on 26/10/2560.
 */

public interface ApiService {

    @GET("list")
    Call<PhotoItemCollectionDao> loadPhotoList();

    @GET("list/after/{id}")
    Call<PhotoItemCollectionDao> loadNewPhotoList(@Path("id") int id);

    @GET("list/before/{id}")
    Call<PhotoItemCollectionDao> loadOldPhotoList(@Path("id") int id);


}
