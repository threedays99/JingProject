package com.example.travel_uk;

import com.example.travel_uk.Model.SiteModel;

import java.util.ArrayList;
import java.util.Arrays;

public interface SiteList {

    int STORAGE_REQUEST_CODE = 1000;
    int LOCATION_REQUEST_CODE = 2000;
    String IMAGE_PATH = "/Profile/image_profile.jpg";

    ArrayList<SiteModel> sitesname = new ArrayList<>(
            Arrays.asList(
                    new SiteModel(1, R.drawable.ic_sites, "Tourist Attractions", "tourist_attraction"),
                    new SiteModel(2, R.drawable.ic_shopping, "Grocery", "supermarket"),
                    new SiteModel(3, R.drawable.ic_hotel, "Hotel", "lodging"),
                    new SiteModel(4, R.drawable.ic_coffee, "Coffee", "cafe"),
                    new SiteModel(5, R.drawable.ic_parking, "Parking Space", "parking"),
                    new SiteModel(6, R.drawable.ic_gas, "Gas Station", "gas_station"),
                    new SiteModel(7, R.drawable.ic_pharmacy, "Pharmacy", "pharmacy")

            )
    );
}
