package com.dm.sam.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.*;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.dm.sam.R;
import com.dm.sam.databinding.ActivityClientCarteBinding;
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.db.service.SiteService;
import com.dm.sam.listener.DirectionsListener;
import com.dm.sam.listener.FilterListener;
import com.dm.sam.listener.MapListener;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.dm.sam.utils.GpsTracker;
import com.dm.sam.utils.SitesManager;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class CarteActivity extends FragmentActivity implements  OnMapReadyCallback {
    private static final int HANDLER_DELAY = 1000 * 60 * 5; // delay to get location every 1 min
    private static final int START_HANDLER_DELAY = 0; // delay to get the location the first time
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int PERMISSION_ALL = 1;
    private static final  LatLng METZ_LAT_LNG = new LatLng(49.1196964,6.1763552);
    private static final int DEFAULT_ZOOM=17;
    private static final float DEFAULT_RADIUS=200;
    private ActivityClientCarteBinding binding;
    CategorieService categorieService;
    SiteService siteService;
    private LatLng currentLocation, destination;
    private GoogleMap mMap;
    private MapListener  mapListener;
    private GpsTracker gpsTracker;
    private Marker currentLocationMarker;
    private  View mapView;
    private LocationManager locationManager;
    private DirectionsListener directionsListener;
    private Button stopRoute;
    private FloatingActionButton floatingSearchButton, floatingSitesListButton, floatingInfosButton;
    private List<Marker> markers = new LinkedList<>();
    private List<Circle> circles= new LinkedList<>();
    private SitesManager sitesManager;

    private float LastSelectedRadius;
    private  String LastSelectedCategorie;
    private FilterListener filterListener;
    private TextView categorie_txt_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientCarteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializer();

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        // Initialise the location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getLocation(binding.getRoot());
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);

        floatingSearchButton.setOnClickListener(filterListener);
        floatingSitesListButton.setOnClickListener(view -> startActivity(new Intent(CarteActivity.this,TabbedListsActivity.class)));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView= mapFragment.getView();

    }

    /**
     * This method is used to initialize all the variables before use.
     */
    public void initializer(){
        siteService = SiteService.getInstance(this);
        categorieService = CategorieService.getInstance(this);
        floatingSearchButton = findViewById(R.id.floatingSearchButton);
        floatingSitesListButton=findViewById(R.id.floatingSitesListButton);
        floatingInfosButton= findViewById(R.id.floatingInfoButton);
        stopRoute= findViewById(R.id.stopPath);
        filterListener= new FilterListener(CarteActivity.this,floatingSearchButton);
        categorie_txt_view= findViewById(R.id.categorie_txt_view);
        categorie_txt_view.setText("Toutes les catégories");
        categorieService.setDefaultCategories();
        siteService.createDefaultSitesIfNeed();
        LastSelectedRadius=DEFAULT_RADIUS;
    }
    /**
     * This method is used to get the current location
     * or go to settings if gps is disabled
     */
    public void getLocation(View view){
        gpsTracker = new GpsTracker(this,this);
        //gpsTracker.isOnline();
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            this.currentLocation= new LatLng(latitude,longitude);
        }else{
            gpsTracker.showSettingsAlert();
            this.currentLocation=METZ_LAT_LNG;
        }
    }

    /**
     * This method is triggered to show a selected site from the list on the map
     * @param i is an Intent containing the selected site's data
     */
    public void showSiteFromTheList(Intent i){
        float lat = Float.parseFloat(i.getStringExtra("latitude"));
        float lng = Float.parseFloat(i.getStringExtra("longitude"));
        currentLocation=new LatLng(lat,lng);
    }

    /**
     * This method is triggered when the map is ready for use
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initializing the listeners and updating the location
        directionsListener= new DirectionsListener(this,mMap);
        sitesManager = new SitesManager(CarteActivity.this, mMap,directionsListener);
        mapListener= new MapListener(this,sitesManager, mMap,binding);
        getLocation(this.binding.getRoot());

        //getting extra from the site fragment list to use to show on the map
        Intent i = getIntent();
        if(i.hasExtra("latitude") && i.hasExtra("longitude")) {
            showSiteFromTheList(i);
        }

        //Defining listeners for actions on the map
        mMap.setOnInfoWindowClickListener(mapListener);
        mMap.setOnMapLongClickListener(mapListener);
        mMap.setOnMapClickListener(mapListener);
        mMap.setOnMarkerDragListener(mapListener);
        mMap.setOnMyLocationButtonClickListener(mapListener);
        floatingInfosButton.setOnClickListener(mapListener);
        stopRoute.setOnClickListener(directionsListener);

        /* Apply default filter when no values ae selected : Show places of all
        categories on a 200m away from the current position   */
        getDisplayedSites(DEFAULT_RADIUS,null, currentLocation);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        //Placing the re-centring button in the bottom right of the screen
        setLocationButtonPosition(mapView);
    }

    /**
     * This method is used to change the position of
     * the built-in GoogleMaps MtLocation button
     */
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

    //The following section covers getters and setters for editable attributes

    /**
     * This method is used to get currentPosition
     * */
    public LatLng getcurrentPosition(){
        return currentLocation;
    }
    /**
     * This method is used to set currentPosition
     * */
    public void setcurrentPosition(LatLng position){
         currentLocation=position;
    }
    /**
     * This method is used to get destination
     * */
    public LatLng getDestination(){
        return destination;
    }
    /**
     * This method is used to set destination
     * */
    public void setDestination(LatLng position){
         destination=position;
    }
    /**
     * This method is used to get the LastSelectedCategorie
     * */
    public String getLastSelectedCategorie(){
        return LastSelectedCategorie;
    }
    /**
     * This method is used to get LastSelectedRadius
     * */
    public float getLastSelectedRadius(){
        return LastSelectedRadius;
    }

    /**
     * This method is used to add a market to the Markers list
     * */
    public void setMarkers(Marker m){
        markers.add(m);
    }

    /**
     * This method is used to hide the markers from the map
     * */
    public void hideMarkers(){
        for (Marker m : markers) {
            m.setVisible(false);
        }
    }
    /**
     * This method is used to hide the circles from the map
     * */
    public void hideCircles(){
        for (Circle c : circles) {
            c.remove();
        }
    }

    /**
     * This method is used to filter the displayed sites on the map according to the provided radius, category, and location
     * @param radius is a float representing the provided radius
     * @param categorie is a string representing the provided category
     * @param position is a float representing the current/selected position
     *
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void getDisplayedSites(Float radius, String categorie, LatLng position){

        //Initialisation des champs
        hideMarkers();
        hideCircles();
        List<Site> places;
        Marker marker;

        if(getLastSelectedCategorie()!=null){
            categorie_txt_view.setText(categorie);
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(true)
                .title("Vous êtes ici"));
        markers.add(currentLocationMarker);

        // get the sites to show based on the selected category
        if(categorie!=null){
            Categorie c = categorieService.findByName(categorie);
            places= Objects.requireNonNull(siteService.findByCategory(c.getId_categorie()));
        }else{
            places= Objects.requireNonNull(siteService.findAll());
        }

        //Calculate the distance between the current location and the sites on the list
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

            // filter the list based on the radius and add markers to the map
              if  (distance.length>0 && distance[0] <= radius){
                  marker=mMap.addMarker(new MarkerOptions()
                          .position(coordPlace)
                          .zIndex(1.0f)
                          .snippet("Distance : " + Math.round(distance[0]) + " m")
                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                          //.icon(getMarkerIconFromDrawable(getDrawable(getResources().getIdentifier(icon, "drawable", getPackageName()))))
                          .title(place.getNom()));
                  marker.setVisible(true);
                  markers.add(marker);
              }
        }
        // Move the camera instantly to location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));


        //Drawing the circle with the selected radius : a default radius is set to 200m
        Circle circle=mMap.addCircle(new CircleOptions()
                .center(position).strokeWidth(5f)
                .strokeColor(getResources().getColor(R.color.colorAccent))
                .fillColor(getResources().getColor(R.color.lightBlueFill))
                .radius(radius));
        circles.add(circle);
        LastSelectedCategorie=categorie;
        LastSelectedRadius=radius;
    }

}