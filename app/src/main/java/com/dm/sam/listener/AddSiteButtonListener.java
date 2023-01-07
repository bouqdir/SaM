package com.dm.sam.listener;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import com.dm.sam.R;
import com.dm.sam.activity.AddSiteActivity;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.activity.SitesFragment;
import com.dm.sam.activity.TabbedListsActivity;
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.db.service.SiteService;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;

public class AddSiteButtonListener implements View.OnClickListener{
    public static final int DEFAULT_ZIP_CODE=57000;
    AddSiteActivity activity;
    SiteService siteService;
    CategorieService categorieService;
    public AddSiteButtonListener(AddSiteActivity addSiteActivity) {
        this.activity = addSiteActivity;
    }

    @Override
    public void onClick(View view) {
        EditText add_nom, add_resume, add_code_postal;
        Site site= new Site();
        int zipCode;
        Categorie cat= new Categorie();
        siteService=SiteService.getInstance(activity);
        categorieService = CategorieService.getInstance(activity);

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

        cat = categorieService.findByName(activity.getSelectedCategorie());
        Long idCat = categorieService.findByName(activity.getSelectedCategorie()).getId_categorie();
        site.setCategorie(idCat);
        site.setLatitude((float)activity.getLatLng().latitude);
        site.setLongitude((float)activity.getLatLng().longitude);

        Intent intent1 = activity.getIntent();
        //If the id already exists update the site
        if(intent1.hasExtra("site_id")){
            site.setId_site(Integer.parseInt(intent1.getStringExtra("site_id")));

            siteService.update(site);
            activity.startActivity(new Intent(activity, TabbedListsActivity.class));
            Toast.makeText(activity, "Modification enregistrée!", Toast.LENGTH_SHORT).show();

        }else {
            // if not add a new site
            siteService.create(site);
            activity.startActivity(new Intent(activity, CarteActivity.class));
            Toast.makeText(activity, "Nouveau site disponible sur votre carte !", Toast.LENGTH_LONG).show();

        }
    }

}
