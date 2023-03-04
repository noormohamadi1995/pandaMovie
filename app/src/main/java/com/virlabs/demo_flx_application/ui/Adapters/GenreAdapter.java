package com.virlabs.demo_flx_application.ui.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.entity.Genre;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GenreAdapter extends  RecyclerView.Adapter<GenreAdapter.GenreHolder>{
    private final List<Genre> genreList;

    public GenreAdapter(List<Genre> genreList) {
        this.genreList = genreList;
    }
    @Override
    public GenreAdapter.GenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre , null);
        return new GenreHolder(v);
    }
    @Override
    public void onBindViewHolder(GenreAdapter.GenreHolder holder, final int position) {
        holder.text_view_item_genre_title.setText(genreList.get(position).getTitle());
    }
    @Override
    public int getItemCount() {
        return genreList.size();
    }
    public class GenreHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_genre_title;
        public GenreHolder(View itemView) {
            super(itemView);
            this.text_view_item_genre_title =  (TextView) itemView.findViewById(R.id.text_view_item_genre_title);
        }
    }
}
