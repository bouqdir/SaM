package com.dm.sam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dm.sam.R;
import com.dm.sam.db.DatabaseHelper;
import com.dm.sam.model.Site;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SiteViewAdapter extends RecyclerView.Adapter<SiteViewAdapter.ViewHolder>{
    Context context;
    List<Site> siteList;
    OnSiteViewListener onSiteViewListener;
    DatabaseHelper dbHelper; // to use in case of update or delete

    public SiteViewAdapter(Context context, List<Site> siteList,OnSiteViewListener onSiteViewListener) {
        this.context = context;
        this.siteList = siteList;
        this.onSiteViewListener=onSiteViewListener;
        dbHelper= new DatabaseHelper(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.site_item_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, onSiteViewListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.nameView.setText(siteList.get(position).getNom());
        holder.resumeView.setText(siteList.get(position).getResume());
        //TODO retrieve avatar from categorie table
        //holder.imageView.setImageResource(GetCategorieAvatar(context,siteList.get(position).getCategorie()));
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView, resumeView;
        ImageView imageView;

        OnSiteViewListener onSiteListener;

        public ViewHolder(@NonNull View itemView, OnSiteViewListener onSiteViewListener) {
            super(itemView);
            nameView = itemView.findViewById(R.id.site_nom);
            resumeView = itemView.findViewById(R.id.site_resume);
            imageView = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onSiteViewListener!=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            onSiteViewListener.OnSiteSelected(pos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(onSiteViewListener!=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            onSiteViewListener.OnSiteLongClick(pos);
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            this.onSiteListener.OnSiteSelected(getLayoutPosition());
        }
    }

    public interface OnSiteViewListener{
        void OnSiteSelected(int position);
        void OnSiteLongClick(int position);
    }
}