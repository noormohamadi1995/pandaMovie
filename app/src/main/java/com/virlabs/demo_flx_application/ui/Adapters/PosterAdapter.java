package com.virlabs.demo_flx_application.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Channel;
import com.virlabs.demo_flx_application.entity.Poster;
import com.virlabs.demo_flx_application.ui.activities.MovieActivity;
import com.virlabs.demo_flx_application.ui.activities.SerieActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class PosterAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Poster> posterList;
    private List<Channel> channelList;
    private final Activity activity;
    private Boolean deletable = false;

    public PosterAdapter(List<Poster> posterList,List<Channel> channelList, Activity activity) {
        this.posterList = posterList;
        this.channelList = channelList;
        this.activity = activity;
    }
    public PosterAdapter(List<Poster> posterList, Activity activity) {
        this.posterList = posterList;
        this.activity = activity;
    }
    public PosterAdapter(List<Poster> posterList, Activity activity,boolean deletable) {
        this.posterList = posterList;
        this.activity = activity;
        this.deletable = deletable;
    }
    public PosterAdapter(List<Poster> posterList,List<Channel> channelList_, Activity activity,boolean deletable) {
        this.channelList = channelList_;
        this.posterList = posterList;
        this.activity = activity;
        this.deletable = deletable;
    }
    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_poster,null);
                viewHolder = new PosterHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_channels_search, parent, false);
                viewHolder = new ChannelsHolder(v3);
                break;
            }
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        switch (getItemViewType(position)) {
            case 1:

                final PosterHolder holder = (PosterHolder) viewHolder;
                Picasso.get().load(posterList.get(position).getImage()).placeholder(R.drawable.poster_placeholder).into(holder.image_view_item_poster_image);
                if (deletable)
                    holder.relative_layout_item_poster_delete.setVisibility(View.VISIBLE);
                else
                    holder.relative_layout_item_poster_delete.setVisibility(View.GONE);


                if (posterList.get(position).getLabel() != null){
                    if (posterList.get(position).getLabel().length()>0) {
                        holder.text_view_item_poster_label.setText(posterList.get(position).getLabel());
                        holder.text_view_item_poster_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_poster_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_poster_label.setVisibility(View.GONE);
                }


                if (posterList.get(position).getSublabel() != null){
                    if (posterList.get(position).getSublabel().length()>0) {
                        holder.text_view_item_poster_sub_label.setText(posterList.get(position).getSublabel());
                        holder.text_view_item_poster_sub_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_poster_sub_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_poster_sub_label.setVisibility(View.GONE);
                }

                holder.image_view_item_poster_image.setOnClickListener(v -> {
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.image_view_item_poster_image, "imageMain");
                    Intent intent = new Intent(activity, MovieActivity.class);
                    if (posterList.get(position).getType().equals("movie")) {
                        intent = new Intent(activity, MovieActivity.class);
                    } else if (posterList.get(position).getType().equals("serie")) {
                        intent = new Intent(activity, SerieActivity.class);
                    }
                    intent.putExtra("poster", posterList.get(holder.getAdapterPosition()));
                    final Intent intent1 = intent;

                    activity.startActivity(intent1, activityOptionsCompat.toBundle());
                });
                holder.image_view_item_poster_delete.setOnClickListener(v->{
                    final PrefManager prefManager = new PrefManager(activity);
                    Integer id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
                    String   key_user=  prefManager.getString("TOKEN_USER");
                    Retrofit retrofit = apiClient.getClient();
                    apiRest service = retrofit.create(apiRest.class);
                    Call<Integer> call = service.AddMyList(posterList.get(position).getId(),id_user,key_user,"poster");
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                            if (response.isSuccessful()){
                                if (response.body() == 202){
                                    Toasty.warning(activity, "This movie has been removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                        }
                    });
                    posterList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                });
                break;
            case 2:

                break;
            case 3:
                final ChannelsHolder holder_channel = (ChannelsHolder) viewHolder;
                LinearLayoutManager linearLayoutManagerChannelAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                ChannelAdapter channelAdapter = new ChannelAdapter(channelList, activity, deletable);
                holder_channel.recycle_view_channels_item.setHasFixedSize(true);
                holder_channel.recycle_view_channels_item.setAdapter(channelAdapter);
                holder_channel.recycle_view_channels_item.setLayoutManager(linearLayoutManagerChannelAdapter);
                channelAdapter.notifyDataSetChanged();
                break;
        }

    }
    @Override
    public int getItemCount() {
        return posterList.size();
    }
    public class PosterHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_poster_label;
        private final TextView text_view_item_poster_sub_label;
        private final ImageView image_view_item_poster_delete;
        public ImageView image_view_item_poster_image ;
        public RelativeLayout relative_layout_item_poster_delete ;
        public PosterHolder(View itemView) {
            super(itemView);
            this.image_view_item_poster_image =  (ImageView) itemView.findViewById(R.id.image_view_item_poster_image);
            this.relative_layout_item_poster_delete =  (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_poster_delete);
            this.image_view_item_poster_delete =  (ImageView) itemView.findViewById(R.id.image_view_item_poster_delete);
            this.text_view_item_poster_sub_label =  (TextView) itemView.findViewById(R.id.text_view_item_poster_sub_label);
            this.text_view_item_poster_label =  (TextView) itemView.findViewById(R.id.text_view_item_poster_label);
        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if ((posterList.get(position).getTypeView())==0){
            return 1;
        }
        return   posterList.get(position).getTypeView();
    }

    private class ChannelsHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_channels_item;

        public ChannelsHolder(View v3) {
            super(v3);
            this.recycle_view_channels_item=(RecyclerView) itemView.findViewById(R.id.recycle_view_channels_item);
        }
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(activity);
        return prefManager.getString("SUBSCRIBED").equals("TRUE") || prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE");
    }
}
