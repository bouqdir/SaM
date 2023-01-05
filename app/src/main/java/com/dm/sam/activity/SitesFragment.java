package com.dm.sam.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dm.sam.R;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Site;
import com.dm.sam.adapter.SiteViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SitesFragment extends Fragment  implements SiteViewAdapter.OnSiteViewListener{

    RecyclerView recyclerView;
    List<Site> siteList = new ArrayList<Site>();
   public  SiteViewAdapter siteViewAdapter;
    DatabaseHelper db;
    int selectedItemPosition;
    Button btnDelete, btnCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSite);

        db = new DatabaseHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        this.siteList = db.getAllSites();

        if (siteList.size() > 0){
            siteViewAdapter = new SiteViewAdapter(view.getContext(), this.siteList,this);
            recyclerView.setAdapter(siteViewAdapter);
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
        menu.add(Menu.NONE, R.id.site_option_delete, Menu.NONE, "Supprimer");
        menu.add(Menu.NONE, R.id.site_option_update, Menu.NONE, "Modifier");
        menu.add(Menu.NONE, R.id.site_option_show_on_map, Menu.NONE, "Voir sur la carte");
    }
    /**
     * This method delegates the roles according to the selected menu option
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.site_option_delete:
                this.showConfirmationDialog();
                return true;
            case R.id.site_option_update:
                updateSite(selectedItemPosition);
                return true;
            case R.id.site_option_show_on_map:
                showSiteOnMap();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /**
     * This method is used to send data to use for the update site activity.
     */
    public void updateSite(int index){
            Intent i = new Intent(getActivity(), AddSiteActivity.class);
            i.putExtra("site_id",String.valueOf(siteList.get(index).getId_site()));
            i.putExtra("site_nom",siteList.get(index).getNom());
            i.putExtra("site_resume",siteList.get(index).getResume());
            i.putExtra("longitude",String.valueOf(siteList.get(index).getLongitude()));
            i.putExtra("latitude",String.valueOf(siteList.get(index).getLatitude()));
            i.putExtra("categorie",siteList.get(index).getCategorie());
            i.putExtra("site_code_postal",String.valueOf(siteList.get(index).getCode_postal()));
            startActivity(i);
    }
    /**
     * This method is used to send data to the Map activity  to show the selected site.
     */
    public  void showSiteOnMap(){
        Intent i = new Intent(getContext(),CarteActivity.class);
        i.putExtra("latitude",String.valueOf(siteList.get(selectedItemPosition).getLatitude()));
        i.putExtra("longitude",String.valueOf(siteList.get(selectedItemPosition).getLongitude()));
        startActivity(i);

    }

    /**
     * This method is used to show the delete confirmation.
     */
    public void showConfirmationDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.site_delete_confirmation_dialog);
        btnDelete= dialog.findViewById(R.id.confirmDelete);
        btnDelete.setOnClickListener(v->{
            db.deleteSite(siteList.get(selectedItemPosition).getId_site());
            siteList.remove(selectedItemPosition);
            siteViewAdapter.notifyItemRemoved(selectedItemPosition);
            dialog.dismiss();
        });

        btnCancel= dialog.findViewById(R.id.cancelDelete);
        btnCancel.setOnClickListener(v-> dialog.dismiss());

        dialog.show();
    }

    /**
     * This method is triggered when a value is selected from the sites list
     */
    @Override
    public void OnSiteSelected(int position) {
        this.selectedItemPosition = position;

    }
    /**
     * This method is triggered by a long click on the sites list
     */
    @Override
    public void OnSiteLongClick(int position) {
        this.selectedItemPosition = position;
    }

}