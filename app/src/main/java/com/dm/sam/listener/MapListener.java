package com.dm.sam.listener;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.dm.sam.R;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.databinding.ActivityClientCarteBinding;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Site;
import com.dm.sam.utils.SitesDetails;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.jetbrains.annotations.NotNull;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class MapListener implements  GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener{
    CarteActivity activity;
    SitesDetails sitesDetails;
    DatabaseHelper db;
    LatLng position;
    private GoogleMap mMap;
    ActivityClientCarteBinding binding;

    public MapListener(CarteActivity activity, DatabaseHelper db, SitesDetails sitesDetails, GoogleMap map, ActivityClientCarteBinding binding) {
        this.activity = activity;
        this.sitesDetails = sitesDetails;
        this.mMap=map;
        this.db=db;
        this.binding=binding;
    }

    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {

        float lat =(float)marker.getPosition().latitude;
        float lng =(float)marker.getPosition().longitude;
        LatLng latLng = new LatLng(lat, lng);

        Site site = db.getSiteByLatLng(latLng);
        if(site!=null) {
            sitesDetails.showDetailsDialog(site);
        }
        else {
            sitesDetails.showNoDetailsDialog(latLng);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {

        position = latLng;

    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
        Site s = db.getSiteByLatLng((latLng));
        if(s!=null) sitesDetails.showDetailsDialog(s);
        else sitesDetails.showNoDetailsDialog(latLng);
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        return true;
    }

    @Override
    public void onMarkerDrag(@NonNull @NotNull Marker marker) {

    }

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

    @Override
    public boolean onMyLocationButtonClick() {
        mMap.clear();
        activity.getLocation(binding.getRoot());
        activity.getDisplayedSites( activity.getLastSelectedRadius(), activity.getLastSelectedCategorie(),activity.getcurrentPosition());
        return false;
    }

    @Override
    public void onClick(View view) {
        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.about_us);

        TextView close = dialog.findViewById(R.id.txtclose);
        close.setOnClickListener(l->dialog.dismiss());
        dialog.show();
    }
}
