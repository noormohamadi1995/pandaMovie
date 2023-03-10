package com.virlabs.demo_flx_application.ui.Adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.entity.Country;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CountryAdapter extends  RecyclerView.Adapter<CountryAdapter.CountryHolder>{
    private final List<Country> countriesList;

    public CountryAdapter(List<Country> countriesList) {
        this.countriesList = countriesList;
    }
    @Override
    public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country , null);
        return new CountryHolder(v);
    }
    @Override
    public void onBindViewHolder(CountryHolder holder, final int position) {
        holder.text_view_item_country_title.setText(countriesList.get(position).getTitle());
    }
    @Override
    public int getItemCount() {
        return countriesList.size();
    }
    public class CountryHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_country_title;
        public CountryHolder(View itemView) {
            super(itemView);
            this.text_view_item_country_title =  (TextView) itemView.findViewById(R.id.text_view_item_country_title);
        }
    }
}
