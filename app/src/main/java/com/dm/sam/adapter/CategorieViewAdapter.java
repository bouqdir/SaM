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
import com.dm.sam.model.Categorie;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategorieViewAdapter extends RecyclerView.Adapter<CategorieViewAdapter.ViewHolder>{

    Context context;
    List<Categorie> categorieList;
    OnCategorieViewListener onCategorieViewListener;
    DatabaseHelper dbHelper; // to use in case of update or delete

    public CategorieViewAdapter(Context context, List<Categorie> categorieList, OnCategorieViewListener onCategorieViewListener) {
        this.context = context;
        this.categorieList = categorieList;
        this.onCategorieViewListener = onCategorieViewListener;
        dbHelper= new DatabaseHelper(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cat_item_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, onCategorieViewListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.nameView.setText(categorieList.get(position).getNom());
        if(categorieList.get(position).getAvatar()==null)
            holder.imageView.setImageResource(R.drawable.ic_baseline_category_24);
        else
        holder.imageView.setImageResource(context.getResources().getIdentifier(categorieList.get(position).getAvatar(), "drawable", context.getPackageName()));
    }


    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        ImageView imageView;

        OnCategorieViewListener onCategorieViewListener;


        public ViewHolder(@NonNull View itemView, OnCategorieViewListener onCategorieViewListener) {
            super(itemView);
            nameView = itemView.findViewById(R.id.cat_nom);
            imageView = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(view -> {
                if(onCategorieViewListener!=null){
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        onCategorieViewListener.OnCategorieSelected(pos);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                if(onCategorieViewListener!=null){
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        onCategorieViewListener.OnCategorieLongClick(pos);
                    }
                }
                return false;
            });
        }

        @Override
        public void onClick(View view) {
            this.onCategorieViewListener.OnCategorieSelected(getLayoutPosition());
        }
    }

    public interface OnCategorieViewListener{
        void OnCategorieSelected(int position);
        void OnCategorieLongClick(int position);
    }
}
