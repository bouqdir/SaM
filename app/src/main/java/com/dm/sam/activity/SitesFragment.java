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
        //db.createDefaultSitesIfNeed();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.site_option_delete, Menu.NONE, "Supprimer");
        menu.add(Menu.NONE, R.id.site_option_show_on_map, Menu.NONE, "Voir sur la carte");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent(getContext(),CarteActivity.class);
        i.putExtra("latitude",String.valueOf(siteList.get(selectedItemPosition).getLatitude()));
        i.putExtra("longitude",String.valueOf(siteList.get(selectedItemPosition).getLongitude()));
        switch(item.getItemId()) {
            case R.id.site_option_delete:
                this.showConfirmationDialog();
                return true;
            case R.id.site_option_show_on_map:
                startActivity(i);
            default:
                return super.onContextItemSelected(item);
        }
    }
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

    @Override
    public void OnSiteSelected(int position) {
        this.selectedItemPosition = position;

    }
    @Override
    public void OnSiteLongClick(int position) {
        this.selectedItemPosition = position;
    }

}