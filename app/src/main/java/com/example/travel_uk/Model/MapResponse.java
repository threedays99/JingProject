package com.example.travel_uk.Model;

import com.example.travel_uk.GoogleSite;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapResponse {

    @SerializedName("results")
    @Expose
    private List<GoogleSite> googleSiteList;

    public List<GoogleSite> getGoogleSiteList() {
        return googleSiteList;
    }

    public void setGoogleSiteList(List<GoogleSite> googleSiteList) {
        this.googleSiteList = googleSiteList;
    }
}
