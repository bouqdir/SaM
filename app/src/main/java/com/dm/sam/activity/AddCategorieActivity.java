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
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class AddCategorieActivity extends AppCompatActivity {
    EditText edit_txt_nom;
    Button  save_btn, cancel_btn;
    AddCategorieButtonListener addCategorieButtonListener;

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

        addCategorieButtonListener = new AddCategorieButtonListener(this);

        edit_txt_nom=findViewById(R.id.edit_txt_cat_nom);
        save_btn=findViewById(R.id.saveBtn);
        cancel_btn=findViewById(R.id.cancelBtn);

        // Disable save button if the name is not provided
        edit_txt_nom.addTextChangedListener(new TextWatcher() {
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

        save_btn.setOnClickListener(addCategorieButtonListener);

        cancel_btn.setOnClickListener(v->{
            startActivity(new Intent(this, TabbedListsActivity.class));
        });

    }
}