package com.example.travel_uk.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.travel_uk.Adapter.GoogleSiteAdapter;
import com.example.travel_uk.Adapter.InfoWindowAdapter;
import com.example.travel_uk.Direction;
import com.example.travel_uk.LoadDialog;
import com.example.travel_uk.GoogleSite;
import com.example.travel_uk.Model.CustomPlaceModel;
import com.example.travel_uk.Model.MapResponse;
import com.example.travel_uk.NearSite;
import com.example.travel_uk.Permissions.Permission;
import com.example.travel_uk.Services.RetrofitClient;
import com.example.travel_uk.Services.RetrofitInterface;
import com.example.travel_uk.SiteList;
import com.example.travel_uk.Model.SiteModel;
import com.example.travel_uk.databinding.FragmentMapBinding;


import com.example.travel_uk.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NearSite {

    private FragmentMapBinding fragmentMapBinding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Permission permission;
    private boolean isLocationPermissionOk;
    private SupportMapFragment mapFragment;
    private FirebaseAuth firebaseAuth;
    private GoogleMap Googlemap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private Marker marker;
    private InfoWindowAdapter infoWindowAdapter;
    private final LatLng mDefaultLocation = new LatLng(53.8048841, -1.5449990);
    private static final int DEFAULT_ZOOM = 17;
    private LoadDialog loadingDialog;
    private int radius = 3000;
    private RetrofitInterface retrofitInterface;
    private List<GoogleSite> googleSiteList;
    private SiteModel selectedPlaceModel;
    private GoogleSiteAdapter googleSiteAdapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    List<CustomPlaceModel> customPlaceModelList = new ArrayList<>();
    CustomPlaceModel customPlaceModel;

    public HomeMap() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize view
        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false);
        permission = new Permission();
        firebaseAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadDialog(requireActivity());
        retrofitInterface = RetrofitClient.getRetrofitClient().create(RetrofitInterface.class);
        googleSiteList = new ArrayList<>();


        fragmentMapBinding.currentLocation.setOnClickListener(currentLocation -> getCurrentLocation());



        fragmentMapBinding.groupsites.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    SiteModel siteModel = SiteList.sitesname.get(checkedId - 1);
                    fragmentMapBinding.edtplace.setText(siteModel.getSitename());
                    selectedPlaceModel = siteModel;
                    getPlaces(siteModel.getSitetype());
                }
            }
        });


        return fragmentMapBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        for (SiteModel siteModel : SiteList.sitesname) {
            Chip chip = new Chip(requireContext());
            chip.setId(siteModel.getId());
            chip.setText(siteModel.getSitename());
            chip.setTextColor(getResources().getColor(R.color.white, null));
            chip.setPadding(10, 10, 10, 10);
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.darkblue, null));
            chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), siteModel.getDrawableid(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            fragmentMapBinding.groupsites.addView(chip);

        }
        setUpRecyclerView();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        isLocationPermissionOk = true;
        Googlemap = googleMap;
        getDataFromFirebase(Googlemap);

        if (permission.isLocationOk(requireContext())) {
            isLocationPermissionOk = true;

            setUpGoogleMap();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission")
                    .setMessage("I require location permission to show you near by places")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocation();
                        }
                    })
                    .create().show();
        } else {
            requestLocation();
        }

    }

    private void requestLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_BACKGROUND_LOCATION}, SiteList.LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SiteList.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setUpGoogleMap();

            } else {
                isLocationPermissionOk = false;
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        Googlemap.setMyLocationEnabled(true);
        Googlemap.getUiSettings().setTiltGesturesEnabled(true);
        Googlemap.setOnMarkerClickListener(this::onMarkerClick);

        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Location updated started", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
        getCurrentLocation();

    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                infoWindowAdapter = null;
                infoWindowAdapter = new InfoWindowAdapter(currentLocation,requireContext());
                Googlemap.setInfoWindowAdapter(infoWindowAdapter);
                moveCameraToLocation(location);

            }
        });
    }

    private void moveCameraToLocation(Location location) {

        if (location != null){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("I am here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(firebaseAuth.getCurrentUser().getDisplayName());

        if (marker != null) {
            marker.remove();
        }

        marker = Googlemap.addMarker(markerOptions);
        marker.setTag(703);
        Googlemap.animateCamera(cameraUpdate);

    }

    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: Location Update stop");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null)
            stopLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fusedLocationProviderClient != null) {

            startLocationUpdates();
            if (marker != null) {
                marker.remove();
            }
        }
    }

    private void getPlaces(String placeName) {

        if (isLocationPermissionOk) {

            loadingDialog.startLoading();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                     + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                    + "&radius=" + radius + "&type=" + placeName + "&key=" +
                    getResources().getString(R.string.API_KEY);

            if (currentLocation != null) {
                retrofitInterface.NearPlaces(url).enqueue(new Callback<MapResponse>() {
                    @Override
                    public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("TAG", "onResponse: " + res);
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGoogleSiteList() != null && response.body().getGoogleSiteList().size() > 0) {

                                    googleSiteList.clear();
                                    Googlemap.clear();
                                    for (int i = 0; i < response.body().getGoogleSiteList().size(); i++) {

                                        googleSiteList.add(response.body().getGoogleSiteList().get(i));
                                        addMarker(response.body().getGoogleSiteList().get(i), i);
                                    }
                                    googleSiteAdapter.setGoogleSites(googleSiteList);

                                } else {

                                    Googlemap.clear();
                                    googleSiteList.clear();
                                    googleSiteAdapter.setGoogleSites(googleSiteList);

                                    radius += 1000;
                                    Log.d("TAG", "onResponse: " + radius);
                                    getPlaces(placeName);

                                }
                            }

                        } else {
                            Log.d("TAG", "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), "Error : " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }


                        loadingDialog.stopLoading();

                    }

                    @Override
                    public void onFailure(Call<MapResponse> call, Throwable t) {

                        loadingDialog.stopLoading();
                    }
                });
            }
        }
    }


    private void addMarker(GoogleSite googleSite, int i) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googleSite.getGeometry().getLocation().getLat(),
                        googleSite.getGeometry().getLocation().getLng()))
                .title(googleSite.getName())
                .snippet(googleSite.getVicinity());
        markerOptions.icon(getCustomIcon());
        Googlemap.addMarker(markerOptions).setTag(i);
    }

    private BitmapDescriptor getCustomIcon() {

        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_locationplace);
        background.setTint(getResources().getColor(R.color.Red, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUpRecyclerView(){

        fragmentMapBinding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        fragmentMapBinding.placesRecyclerView.setHasFixedSize(false);
        googleSiteAdapter = new GoogleSiteAdapter(this);
        fragmentMapBinding.placesRecyclerView.setAdapter(googleSiteAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(fragmentMapBinding.placesRecyclerView);

        fragmentMapBinding.placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position > -1) {
                    GoogleSite googlePlaceModel = googleSiteList.get(position);
                    Googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                            googlePlaceModel.getGeometry().getLocation().getLng()), 14));
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        int markertag = (int) marker.getTag();
        fragmentMapBinding.placesRecyclerView.scrollToPosition(markertag);
        return false;
    }

    private void getDataFromFirebase(GoogleMap googleMap){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("PlacesInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        customPlaceModelList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,String> map = (Map<String, String>) snapshot.getValue();
                customPlaceModel = new CustomPlaceModel();
                customPlaceModel.setDate(map.get("date"));
                customPlaceModel.setCity(map.get("city"));
                customPlaceModel.setStreet(map.get("street"));
                customPlaceModel.setImage(map.get("image"));

                customPlaceModelList.add(customPlaceModel);

                LatLng address = null;

                for (int i=0;i<customPlaceModelList.size();i++){
                    try {

                        String addr = customPlaceModelList.get(i).getStreet()+","+customPlaceModelList.get(i).getCity();
                         address = getLatLongFromAddress(getActivity(),addr);

                        googleMap.addMarker(new MarkerOptions().position(address).title(customPlaceModelList.get(i).getStreet())).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    }catch (Exception e){
                        Toast.makeText(requireContext(), "Error : " , Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    LatLng getLatLongFromAddress(Context context,String strAddress){
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        LatLng latLng = null;
        try {
            addresses = geocoder.getFromLocationName(strAddress,2);
            if (addresses == null){
                return null;
            }
            Address loc = addresses.get(0);
            latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
        }catch (Exception e){}
        return latLng;
    }

    @Override
    public void onDirectionClick(GoogleSite googleSite) {

        String placeid = googleSite.getPlaceId();
        Double lat = googleSite.getGeometry().getLocation().getLat();
        Double lng = googleSite.getGeometry().getLocation().getLng();

        Intent intent = new Intent(requireContext(), Direction.class);
        intent.putExtra("placeid",placeid);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);

        startActivity(intent);

    }
}





