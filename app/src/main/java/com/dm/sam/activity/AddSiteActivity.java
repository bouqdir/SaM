
package com.dm.sam.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.dm.sam.R;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.listener.AddSiteButtonListener;
import com.dm.sam.listener.CancelButtonListener;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddSiteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView txt_lat, txt_lng ;
    EditText add_nom, add_resume, add_code_postal;
    Spinner categorie_spinner;
    Button save_btn, cancel_btn, btn_add_cat;
    float lat, lng;
    String selectedCategorie;
    DatabaseHelper db;
    AddSiteButtonListener addButtonListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.nouveauSite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, CarteActivity.class));
        });

        initializer();

        //if none of the existing categories is suitable, the user can add a new category
        btn_add_cat.setOnClickListener(v-> {
            Intent i=new Intent(this, AddCategorieActivity.class);
            i.putExtra("latitude",String.valueOf(lat));
            i.putExtra("longitude",String.valueOf(lng));
            startActivity(i);
        });
        cancel_btn.setOnClickListener(v-> startActivity(new Intent(this, CarteActivity.class)));
        // Disable save button if the name is not provided
        add_nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void afterTextChanged(Editable s) {
                if(!s.equals("")){
                    save_btn.setEnabled(true);
                }else{
                    save_btn.setEnabled(false);
                }
            }
        });
        save_btn.setOnClickListener(addButtonListener);
    }

    public void initializer(){

        db= new DatabaseHelper(this);
        addButtonListener = new AddSiteButtonListener(this);
        txt_lat= findViewById(R.id.txt_lat);
        txt_lng= findViewById(R.id.txt_lng);
        btn_add_cat=findViewById(R.id.ajouterCategorie);

        add_nom = findViewById(R.id.edit_txt_nom);

        categorie_spinner = findViewById(R.id.cat_spinner);
        categorie_spinner.setOnItemSelectedListener(this);

        ArrayList<String> categories = new ArrayList<String>();
        List<Categorie> c= Objects.requireNonNull(db.getAllCategories());
        for (Categorie categorie : c) {
            categories.add(categorie.getNom());
        }

        Intent i = getIntent();
        float e1 =Float.parseFloat(i.getStringExtra("latitude"));
        float e2 =Float.parseFloat(i.getStringExtra("longitude"));
        //lat=e1;
        //lng=e2;
         lat = (float)round(e1,4);
         lng = (float)round(e2,4);

        txt_lat.setText(String.valueOf(lat));
        txt_lng.setText(String.valueOf(lng));
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorie_spinner.setAdapter(aa);

        save_btn= findViewById(R.id.saveBtn);
        cancel_btn= findViewById(R.id.cancelBtn);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCategorie=adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String getSelectedCategorie(){
        return selectedCategorie;
    }
    public LatLng getLatLng(){
        return new LatLng(lat,lng);
    }
}