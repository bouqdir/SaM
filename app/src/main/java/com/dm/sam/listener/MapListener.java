package com.dm.sam.listener;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.dm.sam.R;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.databinding.ActivityClientCarteBinding;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Site;
import com.dm.sam.utils.SitesManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.jetbrains.annotations.NotNull;

public class MapListener implements  GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener{
    CarteActivity activity;
    SitesManager sitesManager;
    DatabaseHelper db;
    LatLng position;
    private GoogleMap mMap;
    ActivityClientCarteBinding binding;

    public MapListener(CarteActivity activity, DatabaseHelper db, SitesManager sitesManager, GoogleMap map, ActivityClientCarteBinding binding) {
        this.activity = activity;
        this.sitesManager = sitesManager;
        this.mMap=map;
        this.db=db;
        this.binding=binding;
    }


    /**
     * This method is triggered when the info window on the marker is clicked
     * it shows a dialog message depending on the clicked site situation
     */
    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {

        float lat =(float)marker.getPosition().latitude;
        float lng =(float)marker.getPosition().longitude;
        LatLng latLng = new LatLng(lat, lng);

        Site site = db.getSiteByLatLng(latLng);
        if(site!=null) {
            sitesManager.showDetailsDialog(site);
        }
        else {
            sitesManager.showNoDetailsDialog(latLng);
        }
    }

    /**
     * This method is triggered when a position on the map is clicked
     * it saves the clicked position in a global variable to use later
     */
    @Override
    public void onMapClick(LatLng latLng) {
        position = latLng;
    }

    /**
     * This method is triggered by a long press for on the map
     *
     */
    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
       // Site s = db.getSiteByLatLng((latLng));
        //if(s!=null) sitesManager.showDetailsDialog(s);
        //else
        sitesManager.showNoDetailsDialog(latLng);
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        return true;
    }

    @Override
    public void onMarkerDrag(@NonNull @NotNull Marker marker) {

    }

    /**
     * This method is triggered by the end of the drag action on a marker
     * it applies the current filter on the selected position
     *
     */
    @Override
    public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
        LatLng position = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        activity.setcurrentPosition(position);
        float radius= activity.getLastSelectedRadius();
        String categorie= activity.getLastSelectedCategorie();
        activity.getDisplayedSites(radius,categorie,position);
    }

    @Override
    public void onMarkerDragStart(@NonNull @NotNull Marker marker) {
    }

    /**
     * This method is triggered when myLocationButton is clicked
     * it repositions the camera
     *
     */
    @Override
    public boolean onMyLocationButtonClick() {
        //activity.getLocation(binding.getRoot());
        activity.getDisplayedSites( activity.getLastSelectedRadius(), activity.getLastSelectedCategorie(),activity.getcurrentPosition());
        return false;
    }

    /**
     * This method is triggered by a click on the info floating button
     * it shows a modal with a description of the app
     */
    @Override
    public void onClick(View view) {
        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.about_us);

        TextView close;
        close= dialog.findViewById(R.id.txtclose);
        close.setOnClickListener(l->dialog.dismiss());
        dialog.show();
    }
}
