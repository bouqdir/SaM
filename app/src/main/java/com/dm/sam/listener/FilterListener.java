package com.dm.sam.listener;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.dm.sam.R;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.db.service.SiteService;
import com.dm.sam.model.Categorie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterListener  implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    CarteActivity activity;
    Spinner categorie_spinner;
    EditText edt_txt_radius;
    TextView close;
    Button search_btn;
    String radius_entry ="200", selected_category=null;
    FloatingActionButton searchButton;
    CategorieService categorieService;

    public FilterListener(CarteActivity activity, FloatingActionButton searchButton) {
        this.activity = activity;
        this.searchButton= searchButton;
    }


    /**
     * This method is triggered when a category is selected from the spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.selected_category=adapterView.getItemAtPosition(i).toString();
    }

    /**
     * This method is triggered when no category is selected from the spinner
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
         this.selected_category=null;
    }


    /**
     * This method is triggered when a category is selected from the spinner
     */
    @Override
    public void onClick(View view) {
        categorieService = CategorieService.getInstance(activity);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.search_bar);
        categorie_spinner = (Spinner) dialog.findViewById(R.id.spinner_categorie);
        categorie_spinner.setOnItemSelectedListener(this);

        ArrayList<String> categories = new ArrayList<String>();
        List<Categorie> c= Objects.requireNonNull(categorieService.findAll());
        for (Categorie categorie : c) {
            categories.add(categorie.getNom());
        }
        ArrayAdapter aa = new ArrayAdapter(dialog.getContext(),android.R.layout.simple_spinner_item,categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorie_spinner.setAdapter(aa);


        edt_txt_radius = dialog.findViewById(R.id.edt_txt_radius);
        search_btn = dialog.findViewById(R.id.btn_apply_filter);

        close = dialog.findViewById(R.id.txtclose);
        close.setOnClickListener(v-> dialog.dismiss());


        search_btn.setOnClickListener(v -> {
            if(!edt_txt_radius.getText().toString().equals("")){
                radius_entry=edt_txt_radius.getText().toString();
            }
            selected_category=categorie_spinner.getSelectedItem().toString();
            activity.getDisplayedSites(Float.parseFloat(radius_entry),selected_category,activity.getcurrentPosition());
            dialog.dismiss();
        });

        dialog.show();
    }
}
