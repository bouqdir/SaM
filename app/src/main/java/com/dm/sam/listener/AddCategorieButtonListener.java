package com.dm.sam.listener;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.dm.sam.R;
import com.dm.sam.activity.AddCategorieActivity;
import com.dm.sam.activity.AddSiteActivity;
import com.dm.sam.activity.TabbedListsActivity;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Categorie;

public class AddCategorieButtonListener implements View.OnClickListener{

    AddCategorieActivity activity;


    public AddCategorieButtonListener(AddCategorieActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        EditText edit_txt_nom;
        DatabaseHelper db;

        db= new DatabaseHelper(activity);

        edit_txt_nom=activity.findViewById(R.id.edit_txt_cat_nom);

        Intent intent1 = activity.getIntent();
        if(intent1.hasExtra("id") && intent1.hasExtra("name") && intent1.hasExtra("avatar")){
            Categorie c = new Categorie();
            c.setId_categorie(Integer.parseInt(intent1.getStringExtra("id")));
            c.setNom(edit_txt_nom.getText().toString());
            c.setAvatar(intent1.getStringExtra("avatar"));

            db.updateCategorie(c);
            Toast.makeText(activity, "Modification enregistrée!", Toast.LENGTH_SHORT).show();

        }else {

            Categorie categorie = new Categorie();
            categorie.setId_categorie(db.getCategoriesCount() + 1);
            categorie.setNom(edit_txt_nom.getText().toString());
            categorie.setAvatar(String.valueOf(activity.getResources().getIdentifier("ic_baseline_category_24", "drawable", activity.getPackageName())));

            db.addCategorie(categorie);
            Toast.makeText(activity, "Nouvelle catégorie ajoutée à la liste!", Toast.LENGTH_SHORT).show();

        }

        Intent i = activity.getIntent();
        if(i.hasExtra("latitude")){
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
