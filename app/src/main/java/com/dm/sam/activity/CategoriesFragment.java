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
import com.dm.sam.db.service.CategorieService;
import com.dm.sam.db.service.SiteService;
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
    CategorieService categorieService;
    SiteService siteService;
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

        categorieService = CategorieService.getInstance(getActivity());
        siteService= SiteService.getInstance(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        this.categorieList = categorieService.findAll();

        if (categorieList.size() > 0){
            categorieViewAdapter = new CategorieViewAdapter(view.getContext(), this.categorieList,this);
            recyclerView.setAdapter(categorieViewAdapter);
            registerForContextMenu(recyclerView);
        }else {
            view = inflater.inflate(R.layout.empty_page, container, false);
        }
        return view ;
    }

    /**
     * This method creates the context menu related to the view.
     *
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.cat_option_delete, Menu.NONE, "Supprimer");
        menu.add(Menu.NONE, R.id.cat_option_update, Menu.NONE, "Modifier");
    }

    /**
     * This method delegates the roles according to the selected menu option
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cat_option_delete:
                categorieDeleteConfirmationDialog();
                return true;
            case R.id.cat_option_update:
                updateSelection(selectedItemPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * This method is used to show the delete confirmation.
     */
    public void categorieDeleteConfirmationDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cat_delete_confirmation_dialog);

        btnDelete= dialog.findViewById(R.id.confirmDelete);
        btnDelete.setOnClickListener(v->{
            List<Site> sites = siteService.findByCategory(categorieList.get(selectedItemPosition).getId_categorie());
            for (Site s : sites){
                siteService.delete(s.getId_site());
                //notify fragment with changes
            }
            categorieService.delete(categorieList.get(selectedItemPosition).getId_categorie());
            categorieList.remove(selectedItemPosition);
            categorieViewAdapter.notifyItemRemoved(selectedItemPosition);
            dialog.dismiss();
        });

        btnCancel= dialog.findViewById(R.id.cancelDelete);
        btnCancel.setOnClickListener(v-> dialog.dismiss());

        dialog.show();
    }

    /**
     * This method is used to send data to use for the update activity.
     */
    public void updateSelection(int index){
        Intent i = new Intent(getActivity(), AddCategorieActivity.class);
        i.putExtra("id",String.valueOf(categorieList.get(index).getId_categorie()));
        i.putExtra("name",categorieList.get(index).getNom());
        i.putExtra("avatar",categorieList.get(index).getAvatar());
        startActivity(i);
    }

    /**
     * This method is triggered when a value is selected from the categories list
     */
    @Override
    public void OnCategorieSelected(int position) {
        this.selectedItemPosition = position;
    }


    /**
     * This method is triggered by a long click on the categories list
     */
    @Override
    public void OnCategorieLongClick(int position) {
        this.selectedItemPosition = position;
    }
}