package com.dm.sam.listener;

import android.graphics.Point;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dm.sam.R;
import com.dm.sam.activity.CarteActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DirectionsListener implements Button.OnClickListener{

    CarteActivity activity;
    GoogleMap mMap;
    Button stopRoute;
    private List<Polyline> polylines= new LinkedList<>();


    public DirectionsListener(CarteActivity activity, GoogleMap mMap) {
        this.activity = activity;
        this.mMap = mMap;
    }

    /**
     * This method is used to decode the provided response to positions to add to the path.
     */
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

    /**
     * This method is used to draw the walking path to the selected direction on the map
     */
    public void direction(){
        stopRoute=activity.findViewById(R.id.stopPath);
        stopRoute.setVisibility(View.VISIBLE);

        // Volley is an HTTP library that makes networking for Android apps easier and, most importantly, faster.
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", activity.getDestination().latitude +", "+ activity.getDestination().longitude)
                .appendQueryParameter("origin",  activity.getcurrentPosition().latitude +", "+ activity.getcurrentPosition().longitude)
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
                            polylineOptions.color(ContextCompat.getColor(activity, R.color.orange));
                            polylineOptions.geodesic(true);
                        }
                        Polyline p=mMap.addPolyline(polylineOptions);
                        Marker depart = mMap.addMarker(new MarkerOptions().position(new LatLng(activity.getDestination().latitude, activity.getDestination().longitude)).title("Destination"));
                        Marker destination = mMap.addMarker(new MarkerOptions().position(new LatLng(activity.getcurrentPosition().latitude, activity.getcurrentPosition().longitude)).title("Départ"));
                        activity.setMarkers(depart);
                        activity.setMarkers(destination);
                        polylines.add(p);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {

        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * This method is triggered when the button to stop the path is clicked
     */
    @Override
    public void onClick(View view) {
        stopRoute.setVisibility(View.INVISIBLE);
        for(Polyline p : polylines) { p.remove();}
        activity.getDisplayedSites(activity.getLastSelectedRadius(),activity.getLastSelectedCategorie(),activity.getcurrentPosition());
    }
}
