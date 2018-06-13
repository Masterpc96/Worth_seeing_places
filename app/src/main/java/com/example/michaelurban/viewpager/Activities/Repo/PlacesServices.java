package com.example.michaelurban.viewpager.Activities.Repo;


import com.example.michaelurban.viewpager.Activities.OwnClasses.Place;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlacesServices {
    @GET("places")
    Call<ArrayList<Place>> getPlaces();

    @GET("places/{id}")
    Call<ArrayList<Place>> getPlace(@Path("id") int id);
}
