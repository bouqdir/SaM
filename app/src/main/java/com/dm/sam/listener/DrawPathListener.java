package com.dm.sam.listener;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import com.dm.sam.R;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawPathListener implements View.OnClickListener {
    LatLng sitesPosition;
    GoogleMap map;
    CarteActivity activity;
    Dialog dialog;

    public DrawPathListener(LatLng sitesPosition, GoogleMap map, CarteActivity activity, Dialog dialog) {
        this.sitesPosition = sitesPosition;
        this.map = map;
        this.activity = activity;
        this.dialog = dialog;
    }

    @Override
    public void onClick(View view) {
        Polyline p;
        PolylineOptions polylineOptions= new PolylineOptions().add(sitesPosition, activity.getcurrentPosition()) ;
        dialog.dismiss();

        p=map.addPolyline(polylineOptions);
        p.setColor(activity.getResources().getColor(R.color.orange));
        map.addMarker(new MarkerOptions().position(sitesPosition));

    }
}
