package com.dm.sam.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.dm.sam.R;
import com.dm.sam.databinding.ActivityClientCarteBinding;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.listener.FilterListener;
import com.dm.sam.listener.MapListener;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.dm.sam.utils.GpsTracker;
import com.dm.sam.utils.SitesDetails;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;


public class CarteActivity extends FragmentActivity implements  OnMapReadyCallback, LocationListener {
    private static final  LatLng METZ_LAT_LNG = new LatLng(49.1196964,6.1763552);
    private static final  LatLng LAT_LNG = new LatLng(49.1148,6.2143);
    private static final int DEFAULT_ZOOM=15;
    private static final float DEFAULT_RADIUS=200;
    LatLng currentLocation;
    private GoogleMap mMap;
    Marker currentLocationMarker;
    FloatingActionButton floatingSearchButton, floatingSitesListButton, floatingInfosButton;
    private List<Marker> markers = new LinkedList<>();
    SitesDetails sitesDetails;
    MapListener  mapListener;
    private ActivityClientCarteBinding binding;
    public DatabaseHelper db;
    GpsTracker gpsTracker;
    float LastSelectedRadius;
    String LastSelectedCategorie;
    FilterListener filterListener;
    TextView categorie_txt_view;
    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientCarteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isOnline();

        this.initializer();

        //Open searchFilters
        floatingSearchButton.setOnClickListener(filterListener);
        floatingSitesListButton.setOnClickListener(view -> startActivity(new Intent(CarteActivity.this,TabbedListsActivity.class)));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView= mapFragment.getView();
    }
    public void initializer(){
        db = new DatabaseHelper(this);
        floatingSearchButton = findViewById(R.id.floatingSearchButton);
        floatingSitesListButton=findViewById(R.id.floatingSitesListButton);
        floatingInfosButton= findViewById(R.id.floatingInfoButton);
        filterListener= new FilterListener(CarteActivity.this,floatingSearchButton);
        categorie_txt_view= findViewById(R.id.categorie_txt_view);
        categorie_txt_view.setText("Toutes les catégories");
        db.setDefaultCategories();
        db.createDefaultSitesIfNeed();
        LastSelectedRadius=DEFAULT_RADIUS;
    }

    //get the current location or go to setting if gps is disabled
    public void getLocation(View view){
        gpsTracker = new GpsTracker(this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            this.currentLocation= new LatLng(latitude,longitude);
        }else{
            gpsTracker.showSettingsAlert();
            this.currentLocation=METZ_LAT_LNG;
        }
    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "Pas de connection Internet!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        sitesDetails = new SitesDetails(CarteActivity.this, mMap);
        mapListener= new MapListener(this,db,sitesDetails, mMap,binding);
        getLocation(this.binding.getRoot());

        //getting extra from the site fragment list to use to show on the map
        Intent i = getIntent();
        if(i.hasExtra("latitude") && i.hasExtra("longitude")){
            float lat = Float.parseFloat(i.getStringExtra("latitude"));
            float lng = Float.parseFloat(i.getStringExtra("longitude"));
            currentLocation=new LatLng(lat,lng);
        }

        mMap.setOnInfoWindowClickListener(mapListener);
        mMap.setOnMapLongClickListener(mapListener);
        mMap.setOnMapClickListener(mapListener);
        mMap.setOnMarkerDragListener(mapListener);
        mMap.setOnMyLocationButtonClickListener(mapListener);
        floatingInfosButton.setOnClickListener(mapListener);


        getDisplayedSites(DEFAULT_RADIUS,null, currentLocation);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //Placing the re-centring button in the bottom right of the screen
        setLocationButtonPosition(mapView);
    }

    @SuppressLint("MissingPermission")
    public void setLocationButtonPosition(View mapView){
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
    }

    public void hideMarkers(){
        for (Marker m : markers) {
            m.setVisible(false);
        }
    }

    public LatLng getcurrentPosition(){
        return currentLocation;
    }
    public void setcurrentPosition(LatLng position){
         currentLocation=position;
    }

    public String getLastSelectedCategorie(){
        return LastSelectedCategorie;
    }
    public float getLastSelectedRadius(){
        return LastSelectedRadius;
    }

    public void getDisplayedSites(Float radius, String categorie, LatLng position){
        mMap.clear();
        List<Site> places;
        Marker marker;

        if(getLastSelectedCategorie()!=null){
            categorie_txt_view.setText(categorie);
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(true)
                .title("Vous êtes ici"));

        if(categorie!=null){
            Categorie c = db.getCategorieByName(categorie);
            places= Objects.requireNonNull(db.getSitesByCategorie(c.getId_categorie()));
        }else{
            places= Objects.requireNonNull(db.getAllSites());
        }
        for (Site place : places) {
            LatLng coordPlace = new LatLng(place.getLatitude(), place.getLongitude());
            float[] distance = new float[1];
            Location.distanceBetween(
                    position.latitude,
                    position.longitude,
                    place.getLatitude(),
                    place.getLongitude(),
                    distance
            );
              if  (distance.length>0 && distance[0] <= radius){
                  marker=mMap.addMarker(new MarkerOptions()
                          .position(coordPlace)
                          .zIndex(1.0f)
                          .snippet("Distance : " + Math.round(distance[0]) + " m")
                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                          .title(place.getNom()));
                  marker.setVisible(true);
                  markers.add(marker);
              }
        }
        // Move the camera instantly to location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

        //Drawing the circle with the selected radius : a default radius is set to 200m
        mMap.addCircle(new CircleOptions()
                .center(position).strokeWidth(5f)
                .strokeColor(getResources().getColor(R.color.colorAccent))
                .fillColor(getResources().getColor(R.color.lightBlueFill))
                .radius(radius));

        LastSelectedCategorie=categorie;
        LastSelectedRadius=radius;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        getDisplayedSites(LastSelectedRadius,LastSelectedCategorie, latlng);
    }

}