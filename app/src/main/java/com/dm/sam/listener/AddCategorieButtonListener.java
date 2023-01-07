package com.dm.sam.listener;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.dm.sam.R;
import com.dm.sam.activity.AddCategorieActivity;
import com.dm.sam.activity.AddSiteActivity;
import com.dm.sam.activity.TabbedListsActivity;
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.model.Categorie;

public class AddCategorieButtonListener implements View.OnClickListener{

    AddCategorieActivity activity;
    CategorieService categorieService;

    public AddCategorieButtonListener(AddCategorieActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        EditText edit_txt_nom;
        categorieService = CategorieService.getInstance(activity);

        edit_txt_nom=activity.findViewById(R.id.edit_txt_cat_nom);

        Intent intent1 = activity.getIntent();
        // Update category
        if(intent1.hasExtra("id") && intent1.hasExtra("name") && intent1.hasExtra("avatar")){
            Categorie c = new Categorie();
            c.setId_categorie(Integer.parseInt(intent1.getStringExtra("id")));
            c.setNom(edit_txt_nom.getText().toString());
            c.setAvatar(intent1.getStringExtra("avatar"));

            categorieService.update(c);
            Toast.makeText(activity, "Modification enregistrée!", Toast.LENGTH_SHORT).show();

        }else {

            //Add category
            Categorie categorie = new Categorie();
            categorie.setId_categorie(categorieService.getCategoriesCount() + 1);
            categorie.setNom(edit_txt_nom.getText().toString());
            categorie.setAvatar(String.valueOf(activity.getResources().getIdentifier("ic_baseline_category_24", "drawable", activity.getPackageName())));
            if(categorieService.findByName(categorie.getNom())!=null){
                Toast.makeText(activity, "Cette catégorie fait déjà partie de votre liste !", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, TabbedListsActivity.class));

            }else {
                categorieService.create(categorie);
                Toast.makeText(activity, "Nouvelle catégorie ajoutée à la liste!", Toast.LENGTH_SHORT).show();
            }


        }

        // if the creation activity is launched from the creation site activity get site details
        Intent i = activity.getIntent();
        if(i.hasExtra("latitude") && i.hasExtra("longitude")){
            Intent intent = new Intent(activity, AddSiteActivity.class);
            intent.putExtra("latitude",i.getStringExtra("latitude"));
            intent.putExtra("longitude",i.getStringExtra("longitude"));

            if(i.hasExtra("site_nom"))  intent.putExtra("site_nom",i.getStringExtra("site_nom"));
            if(i.hasExtra("site_code_postal"))  intent.putExtra("site_code_postal",i.getStringExtra("site_code_postal"));
            if(i.hasExtra("site_resume")) intent.putExtra("site_resume",i.getStringExtra("site_resume"));

            activity.startActivity(intent);
        }else{
            activity.startActivity(new Intent(activity, TabbedListsActivity.class));
        }

    }




















}
