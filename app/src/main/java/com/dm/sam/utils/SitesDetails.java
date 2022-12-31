package com.dm.sam.utils;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.dm.sam.R;
import com.dm.sam.activity.AddSiteActivity;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.activity.TabbedListsActivity;
import com.dm.sam.listener.DrawPathListener;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SitesDetails{
    CarteActivity activity;
    GoogleMap map;
    DrawPathListener goToSelection;
    public SitesDetails(CarteActivity activity, GoogleMap map) {

        this.activity = activity;
        this.map=map;
    }
    public void showDetailsDialog(Site site) {


        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.site_details_modal);
        TextView close, text_nom, text_resume, txt_categorie;
        ImageView avatar_categorie;
        Button btn_go = dialog.findViewById(R.id.btn_go);

        LatLng sitesPosition = new LatLng(site.getLatitude(), site.getLongitude());

        goToSelection = new DrawPathListener(sitesPosition, map,activity,dialog);
        btn_go.setOnClickListener(goToSelection);

        text_nom = dialog.findViewById(R.id.site_nom);
        txt_categorie = dialog.findViewById(R.id.categorie_txt);
        text_resume =dialog.findViewById(R.id.site_resume);
        avatar_categorie =dialog.findViewById(R.id.categorie_avatar);
        close = dialog.findViewById(R.id.txtclose);

        Categorie c = activity.db.getCategorie(site.getCategorie());
        //Set site details
        String nom= site.getNom() +", " + site.getCode_postal();
        text_nom.setText(nom);
        text_resume.setText(site.getResume());
        txt_categorie.setText(c.getNom());
        avatar_categorie.setImageResource(activity.getResources().getIdentifier(c.getAvatar(), "drawable", activity.getPackageName()));
        dialog.show();

        // the X label to dissmiss the dialog
        close.setOnClickListener(v-> dialog.dismiss());
    }
    public void showNoDetailsDialog(LatLng latLng){
        Button addSite_btn;
        TextView close;

        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.empty_page);

        Button btn_go = dialog.findViewById(R.id.btn_go);

        goToSelection = new DrawPathListener(latLng, map,activity,dialog);
        btn_go.setOnClickListener(goToSelection);


        close = dialog.findViewById(R.id.txtclose);
        close.setOnClickListener(v-> dialog.dismiss());

        addSite_btn= dialog.findViewById(R.id.button_add_site);
        addSite_btn.setOnClickListener(view->{
            Intent i = new Intent(activity, AddSiteActivity.class);
            i.putExtra("latitude",latLng.latitude+"");
            i.putExtra("longitude",latLng.longitude+"");
            activity.startActivity(i);
            dialog.dismiss();
        });

        dialog.show();
    }

}
