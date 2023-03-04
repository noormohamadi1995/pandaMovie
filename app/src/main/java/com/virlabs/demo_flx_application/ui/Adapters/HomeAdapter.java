package com.virlabs.demo_flx_application.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.entity.Data;
import com.virlabs.demo_flx_application.ui.activities.ActorsActivity;
import com.virlabs.demo_flx_application.ui.activities.GenreActivity;
import com.virlabs.demo_flx_application.ui.activities.HomeActivity;
import com.virlabs.demo_flx_application.ui.activities.MyListActivity;
import com.virlabs.demo_flx_application.ui.activities.SearchActivity;
import com.virlabs.demo_flx_application.ui.activities.TopActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity activity;
    private final List<Data> dataList;
    private int slide_count = 0;


    // private Timer mTimer;
    public HomeAdapter(List<Data> dataList,  Activity activity) {
        this.dataList = dataList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v0 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v0);
                break;
            }
            case 1: {
                View v1 = inflater.inflate(R.layout.item_slides, parent, false);
                viewHolder = new SlideHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_channels, parent, false);
                viewHolder = new ChannelHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_actors, parent, false);
                viewHolder = new ActorHolder(v3);
                break;
            }
            case 4: {
                View v4 = inflater.inflate(R.layout.item_genres, parent, false);
                viewHolder = new GenreHolder(v4);
                break;
            }
            case 7: {
                View v7  = inflater.inflate(R.layout.item_search, parent, false);
                viewHolder = new SearchHolder(v7);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        PosterAdapter posterAdapter;
        switch (getItemViewType(position)){
            case 1:

                final SlideHolder holder = (SlideHolder) holder_parent;
                slide_count = dataList.get(position).getSlides().size();
                SlideAdapter slide_adapter = new SlideAdapter(activity, dataList.get(position).getSlides());
                holder.view_pager_slide.setAdapter(slide_adapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);
                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);
                holder.view_pager_slide.setCurrentItem(0);
                slide_adapter.notifyDataSetChanged();
                break;
            case 2:
                final ChannelHolder holder_channel = (ChannelHolder) holder_parent;
                LinearLayoutManager linearLayoutManagerChannelAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                ChannelAdapter channelAdapter = new ChannelAdapter(dataList.get(position).getChannels(), activity);
                holder_channel.recycle_view_channels_item.setHasFixedSize(true);
                holder_channel.recycle_view_channels_item.setAdapter(channelAdapter);
                holder_channel.recycle_view_channels_item.setLayoutManager(linearLayoutManagerChannelAdapter);
                channelAdapter.notifyDataSetChanged();
                holder_channel.image_view_item_channel_more.setOnClickListener(v -> {
                    ((HomeActivity) activity).goToTV();
                });
                break;
            case 3:
                final ActorHolder holder_actor = (ActorHolder) holder_parent;
                LinearLayoutManager linearLayoutManagerActorAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                ActorAdapter actorAdapter = new ActorAdapter(dataList.get(position).getActors(), activity);
                holder_actor.recycle_view_actors_item_actors.setHasFixedSize(true);
                holder_actor.recycle_view_actors_item_actors.setAdapter(actorAdapter);
                holder_actor.recycle_view_actors_item_actors.setLayoutManager(linearLayoutManagerActorAdapter);
                actorAdapter.notifyDataSetChanged();
                holder_actor.image_view_item_actors_more.setOnClickListener(v -> {
                    Intent intent  =  new Intent(activity.getApplicationContext(), ActorsActivity.class);
                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                });
                break;
            case 4:
                final GenreHolder holder_genres = (GenreHolder) holder_parent;
                holder_genres.text_view_item_genre_title.setText(dataList.get(position).getGenre().getTitle());
                holder_genres.image_view_item_genre_more.setOnClickListener(v-> {
                    if (dataList.get(position).getGenre().getId() == -1){
                        Intent intent  =  new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "rating");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else if (dataList.get(position).getGenre().getId() == 0){
                        Intent intent  =  new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "views");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else if (dataList.get(position).getGenre().getId() == -2){
                        Intent intent  =  new Intent(activity.getApplicationContext(), MyListActivity.class);
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else{
                        Intent intent  =  new Intent(activity.getApplicationContext(), GenreActivity.class);
                        intent.putExtra("genre", dataList.get(position).getGenre());
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }

                });
                LinearLayoutManager linearLayoutManagerGenreAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                if (dataList.get(position).getGenre().getId() == -2)
                    posterAdapter =new PosterAdapter(dataList.get(position).getGenre().getPosters(),activity,true);
                else
                    posterAdapter =new PosterAdapter(dataList.get(position).getGenre().getPosters(),activity);

                holder_genres.recycle_view_posters_item_genre.setHasFixedSize(true);
                holder_genres.recycle_view_posters_item_genre.setAdapter(posterAdapter);
                holder_genres.recycle_view_posters_item_genre.setLayoutManager(linearLayoutManagerGenreAdapter);
                posterAdapter.notifyDataSetChanged();

                break;
            case 7:{
                final SearchHolder holder_admob = (SearchHolder) holder_parent;


                holder_admob.edit_text_home_activity_search.setOnEditorActionListener((v,actionId,event) -> {
                    if (holder_admob.edit_text_home_activity_search.getText().length()>0){
                        Intent intent =  new Intent(activity, SearchActivity.class);
                        intent.putExtra("query",holder_admob.edit_text_home_activity_search.getText().toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);

                        holder_admob.edit_text_home_activity_search.setText("");
                    }
                    return false;
                });
                holder_admob.edit_text_home_activity_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (holder_admob.edit_text_home_activity_search.getText().length()>0) {
                            holder_admob.image_view_activity_home_close_search.setVisibility(View.VISIBLE);

                        }else{
                            holder_admob.image_view_activity_home_close_search.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                holder_admob.image_view_activity_home_close_search.setOnClickListener(v->{
                    holder_admob.edit_text_home_activity_search.setText("");
                });
                holder_admob.image_view_activity_home_search.setOnClickListener(v->{
                    if (holder_admob.edit_text_home_activity_search.getText().length()>0) {

                        Intent intent =  new Intent(activity, SearchActivity.class);
                        intent.putExtra("query",holder_admob.edit_text_home_activity_search.getText().toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);

                        holder_admob.edit_text_home_activity_search.setText("");
                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if(dataList.get(position).getSlides() != null){
            type = 1;
        }
        if(dataList.get(position).getChannels() != null){
            type = 2;
        }
        if(dataList.get(position).getActors() != null){
            type = 3;
        }
        if(dataList.get(position).getGenre() != null){
            type = 4;
        }
        if (dataList.get(position).getViewType() == 5){
            type = 5;

        }
        if (dataList.get(position).getViewType() == 6){
            type = 6;

        }
        if (dataList.get(position).getViewType() == 7){
            type = 7;

        }
        return type;
    }
    private class SlideHolder extends RecyclerView.ViewHolder {
        private final ViewPagerIndicator view_pager_indicator;
        private final ViewPager view_pager_slide;
        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator=(ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide=(ViewPager) itemView.findViewById(R.id.view_pager_slide);
            pageSwitcher(5);

        }
        Timer timer;
        int page = 0;

        public void pageSwitcher(int seconds) {
            timer = new Timer(); // At this line a new Thread will be created
            timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000L); // delay
            // in
            // milliseconds
        }

        class RemindTask extends TimerTask {

            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    if (page == slide_count) { // In my case the number of pages are 5
                        page=0;
                    }
                    view_pager_slide.setCurrentItem(page);
                    page++;
                });

            }
        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_channels_item;
        private final ImageView image_view_item_channel_more;
        public ChannelHolder(View itemView) {
            super(itemView);
            this.recycle_view_channels_item=(RecyclerView) itemView.findViewById(R.id.recycle_view_channels_item);
            this.image_view_item_channel_more=  (ImageView) itemView.findViewById(R.id.image_view_item_channel_more);

        }
    }
    private class SearchHolder extends RecyclerView.ViewHolder {
        EditText edit_text_home_activity_search;
        ImageView image_view_activity_home_close_search;
        ImageView image_view_activity_home_search;
        public SearchHolder(View itemView) {
            super(itemView);

            this.edit_text_home_activity_search =  (EditText) itemView.findViewById(R.id.edit_text_home_activity_search);
            this.image_view_activity_home_close_search =  (ImageView) itemView.findViewById(R.id.image_view_activity_home_close_search);
            this.image_view_activity_home_search =  (ImageView) itemView.findViewById(R.id.image_view_activity_home_search);
        }
    }

    private class ActorHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_actors_item_actors;
        private final ImageView image_view_item_actors_more;

        public ActorHolder(View itemView) {
            super(itemView);
            this.recycle_view_actors_item_actors=  (RecyclerView) itemView.findViewById(R.id.recycle_view_actors_item_actors);
            this.image_view_item_actors_more=  (ImageView) itemView.findViewById(R.id.image_view_item_actors_more);
        }
    }
    private class GenreHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_posters_item_genre;
        private final TextView text_view_item_genre_title;
        private final ImageView image_view_item_genre_more;

        public GenreHolder(View itemView) {
            super(itemView);
            this.recycle_view_posters_item_genre=  (RecyclerView) itemView.findViewById(R.id.recycle_view_posters_item_genre);
            this.text_view_item_genre_title=  (TextView) itemView.findViewById(R.id.text_view_item_genre_title);
            this.image_view_item_genre_more=  (ImageView) itemView.findViewById(R.id.image_view_item_genre_more);
        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
