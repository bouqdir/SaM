package com.dm.sam.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class CarteActivity extends FragmentActivity implements  OnMapReadyCallback {
    private static final int HANDLER_DELAY = 1000 * 60 * 5; // delay to get location every 1 min
    private static final int START_HANDLER_DELAY = 0; // delay to get the location the first time
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int PERMISSION_ALL = 1;
    private static final  LatLng METZ_LAT_LNG = new LatLng(49.1196964,6.1763552);
    private static final int DEFAULT_ZOOM=17;
    private static final float DEFAULT_RADIUS=200;
    LatLng currentLocation;
    private GoogleMap mMap;
    Marker currentLocationMarker;
    FloatingActionButton floatingSearchButton, floatingSitesListButton, floatingInfosButton;
    private List<Marker> markers = new LinkedList<>();
    private List<Circle> circles= new LinkedList<>();
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
    private Polyline mPolyline;

    LatLng destination;
    /////////
    LocationManager locationManager;

    Button goTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientCarteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.initializer();


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


        //Open searchFilters
        floatingSearchButton.setOnClickListener(filterListener);
        floatingSitesListButton.setOnClickListener(view -> startActivity(new Intent(CarteActivity.this,TabbedListsActivity.class)));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView= mapFragment.getView();

        goTo.setOnClickListener(l->{
            goTo.setVisibility(View.INVISIBLE);
            mPolyline.remove();
            getDisplayedSites(LastSelectedRadius,LastSelectedCategorie, currentLocation);

        });
    }
    public void initializer(){
        db = new DatabaseHelper(this);
        floatingSearchButton = findViewById(R.id.floatingSearchButton);
        floatingSitesListButton=findViewById(R.id.floatingSitesListButton);
        floatingInfosButton= findViewById(R.id.floatingInfoButton);
        goTo= findViewById(R.id.stopPath);
        filterListener= new FilterListener(CarteActivity.this,floatingSearchButton);
        categorie_txt_view= findViewById(R.id.categorie_txt_view);
        categorie_txt_view.setText("Toutes les catégories");
        db.setDefaultCategories();
        db.createDefaultSitesIfNeed();
        LastSelectedRadius=DEFAULT_RADIUS;
    }

    //get the current location or go to setting if gps is disabled
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

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

    public LatLng getcurrentPosition(){
        return currentLocation;
    }

    public void setcurrentPosition(LatLng position){
         currentLocation=position;
    }
    public LatLng getDestination(){
        return destination;
    }

    public void setDestination(LatLng position){
         destination=position;
    }

    public String getLastSelectedCategorie(){
        return LastSelectedCategorie;
    }
    public float getLastSelectedRadius(){
        return LastSelectedRadius;
    }

    public void hideMarkers(){
        for (Marker m : markers) {
            m.setVisible(false);
        }
    }
    public void hideCircles(){
        for (Circle c : circles) {
            c.remove();
        }
    }
    public void getDisplayedSites(Float radius, String categorie, LatLng position){
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
        Circle circle=mMap.addCircle(new CircleOptions()
                .center(position).strokeWidth(5f)
                .strokeColor(getResources().getColor(R.color.colorAccent))
                .fillColor(getResources().getColor(R.color.lightBlueFill))
                .radius(radius));
        circles.add(circle);
        LastSelectedCategorie=categorie;
        LastSelectedRadius=radius;
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void direction(){
       goTo.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destination.latitude +", "+ destination.longitude)
                .appendQueryParameter("origin",  currentLocation.latitude +", "+ currentLocation.longitude)
                .appendQueryParameter("mode", "walking")
                .appendQueryParameter("key", "AIzaSyCxOpTV1fSWHBXDQ34fSB8JNh96xLl2l3M")
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i=0;i<routes.length();i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j=0;j<legs.length();j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k=0;k<steps.length();k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l=0;l<list.size();l++){
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(CarteActivity.this, R.color.orange));
                            polylineOptions.geodesic(true);
                        }
                        mPolyline=mMap.addPolyline(polylineOptions);
                        Marker depart =mMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Destination"));
                        Marker arrivee = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.latitude, currentLocation.longitude)).title("Départ"));
                        markers.add(depart);
                        markers.add(arrivee);
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(destination.latitude, destination.longitude))
                                .include(new LatLng(currentLocation.latitude, currentLocation.longitude)).build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}