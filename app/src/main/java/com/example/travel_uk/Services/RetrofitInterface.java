package com.example.travel_uk.Services;

import com.example.travel_uk.Model.MapResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitInterface {
    @GET
    Call<MapResponse> NearPlaces(@Url String url);

}
