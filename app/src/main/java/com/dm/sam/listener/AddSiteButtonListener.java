package com.dm.sam.listener;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import com.dm.sam.R;
import com.dm.sam.activity.AddSiteActivity;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddSiteButtonListener implements View.OnClickListener{
    public static final int DEFAULT_ZIP_CODE=57000;
    AddSiteActivity activity;
    public AddSiteButtonListener(AddSiteActivity addSiteActivity) {
        this.activity = addSiteActivity;
    }

    @Override
    public void onClick(View view) {
        EditText add_nom, add_resume, add_code_postal;
        Site site= new Site();
        int zipCode;

        DatabaseHelper db= new DatabaseHelper(activity);
        add_code_postal= activity.findViewById(R.id.edit_txt_codepostal);
        add_resume= activity.findViewById(R.id.edit_txt_resume);
        add_nom = activity.findViewById(R.id.edit_txt_nom);

        if(add_code_postal.getText().toString().equals("")){
            zipCode=DEFAULT_ZIP_CODE;
        }else{
            zipCode=Integer.parseInt(add_code_postal.getText().toString());
        }
        site.setNom(add_nom.getText().toString());
        site.setResume(add_resume.getText().toString());
        site.setCode_postal(zipCode);
        site.setCategorie(db.getCategorieByName(activity.getSelectedCategorie()).getId_categorie());
        site.setLatitude((float)activity.getLatLng().latitude);
        site.setLongitude((float)activity.getLatLng().longitude);
        db.addSite(site);
        activity.startActivity(new Intent(activity, CarteActivity.class));
        Toast.makeText(activity, "Nouveau site disponible sur votre carte !", Toast.LENGTH_LONG).show();
    }

}
