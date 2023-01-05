package com.dm.sam.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.dm.sam.R;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.listener.AddCategorieButtonListener;
import com.dm.sam.listener.CancelButtonListener;
import com.dm.sam.listener.FormValidationWatcher;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class AddCategorieActivity extends AppCompatActivity {
    EditText edit_txt_nom;
    Button  save_btn, cancel_btn;
    ImageView avatar_cat;
    AddCategorieButtonListener addCategorieButtonListener;
    FormValidationWatcher formValidationWatcher;
    CancelButtonListener cancelButtonListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categorie);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.nouvelleCategorie));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        edit_txt_nom=findViewById(R.id.edit_txt_cat_nom);
        save_btn=findViewById(R.id.saveBtn);
        cancel_btn=findViewById(R.id.cancelBtn);
        avatar_cat=findViewById(R.id.cat_imageView);

        editionMode();

        // Disable save button if the name is not provided
        formValidationWatcher= new FormValidationWatcher(save_btn);
        edit_txt_nom.addTextChangedListener(formValidationWatcher);

        addCategorieButtonListener = new AddCategorieButtonListener(this);
        save_btn.setOnClickListener(addCategorieButtonListener);

        TabbedListsActivity tabbedListsActivity= new TabbedListsActivity();
        cancelButtonListener= new CancelButtonListener(this,tabbedListsActivity);
        cancel_btn.setOnClickListener(cancelButtonListener);

    }

    /**
     * This method is used to set the name edit text value
     * with the category to update if the activity is in edition mode.
     */
    public void editionMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("id") && intent.hasExtra("name") && intent.hasExtra("avatar")) {
            edit_txt_nom.setText(intent.getStringExtra("name"));

            /*
        avatar_cat.setImageResource(
            getResources().getIdentifier(intent.getStringExtra("avatar"), "drawable", getPackageName()));
         */

        }
    }
}