package com.virlabs.demo_flx_application.ui.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.entity.Category;

import java.util.List;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{
    private final List<Category> categoriesList;

    public CategoryAdapter(List<Category> categoriesList) {
        this.categoriesList = categoriesList;
    }
    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catergory , null);
        return new CategoryHolder(v);
    }
    @Override
    public void onBindViewHolder(CategoryHolder holder, final int position) {
        holder.text_view_item_category_title.setText(categoriesList.get(position).getTitle());
    }
    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
    public class CategoryHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_category_title;
        public CategoryHolder(View itemView) {
            super(itemView);
            this.text_view_item_category_title =  (TextView) itemView.findViewById(R.id.text_view_item_category_title);
        }
    }
}
