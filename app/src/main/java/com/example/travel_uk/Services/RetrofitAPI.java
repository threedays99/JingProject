package com.example.travel_uk.Services;

import com.example.travel_uk.Model.DirectionModel.DirectionResponseModel;
import com.example.travel_uk.Model.MapResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<MapResponse> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}
