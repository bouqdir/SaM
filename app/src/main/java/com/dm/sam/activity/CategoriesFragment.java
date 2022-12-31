package com.dm.sam.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dm.sam.R;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.dm.sam.adapter.CategorieViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategorieViewAdapter.OnCategorieViewListener{

    RecyclerView recyclerView;
    List<Categorie> categorieList = new ArrayList<Categorie>();
    CategorieViewAdapter categorieViewAdapter;
    DatabaseHelper db;
    int selectedItemPosition;
    Button btnDelete, btnCancel;
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategorie);
        floatingActionButton= view.findViewById(R.id.addCategorieFloatingActionButton);
        floatingActionButton.setOnClickListener(v->{
            Intent i = new Intent(getActivity(), AddCategorieActivity.class);
            startActivity(i);
        });

        db = new DatabaseHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        this.categorieList = db.getAllCategories();

        if (categorieList.size() > 0){
            categorieViewAdapter = new CategorieViewAdapter(view.getContext(), this.categorieList,this);
            recyclerView.setAdapter(categorieViewAdapter);
            registerForContextMenu(recyclerView);
        }else {
            view = inflater.inflate(R.layout.empty_page, container, false);
        }
        return view ;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.cat_option_delete, Menu.NONE, "Supprimer");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cat_option_delete:
                categorieDeleteConfirmationDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void categorieDeleteConfirmationDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cat_delete_confirmation_dialog);

        btnDelete= dialog.findViewById(R.id.confirmDelete);
        btnDelete.setOnClickListener(v->{
            List<Site> sites = db.getSitesByCategorie(categorieList.get(selectedItemPosition).getId_categorie());
            for (Site s : sites){
                db.deleteSite(s.getId_site());
                //notify fragment with changes
            }
            db.deleteCategorie(categorieList.get(selectedItemPosition).getId_categorie());
            categorieList.remove(selectedItemPosition);
            categorieViewAdapter.notifyItemRemoved(selectedItemPosition);
            dialog.dismiss();
        });

        btnCancel= dialog.findViewById(R.id.cancelDelete);
        btnCancel.setOnClickListener(v-> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void OnCategorieSelected(int position) {
        this.selectedItemPosition = position;
    }

    @Override
    public void OnCategorieLongClick(int position) {
        this.selectedItemPosition = position;
    }
}