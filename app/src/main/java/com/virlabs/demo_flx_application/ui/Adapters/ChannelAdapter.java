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
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Channel;
import com.virlabs.demo_flx_application.ui.activities.ChannelActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChannelAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private  Boolean deletable = false;
    private final List<Channel> channelList;
    private final Activity activity;


    public ChannelAdapter(List<Channel> channelList, Activity activity) {
        this.channelList = channelList;
        this.activity = activity;
    }
    public ChannelAdapter(List<Channel> channelList, Activity activity,Boolean _deletable) {
        this.channelList = channelList;
        this.activity = activity;
        this.deletable =  _deletable;
    }
    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_channel,null);
                viewHolder = new ChannelHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v2);
                break;
            }
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == 1) {
            final ChannelHolder holder = (ChannelHolder) viewHolder;

            if (deletable)
                holder.relative_layout_item_channel_delete.setVisibility(View.VISIBLE);
            else
                holder.relative_layout_item_channel_delete.setVisibility(View.GONE);

            if (channelList.get(position).getLabel() != null) {
                if (channelList.get(position).getLabel().length() > 0) {
                    holder.text_view_item_channel_label.setText(channelList.get(position).getLabel());
                    holder.text_view_item_channel_label.setVisibility(View.VISIBLE);
                } else {
                    holder.text_view_item_channel_label.setVisibility(View.GONE);
                }
            } else {
                holder.text_view_item_channel_label.setVisibility(View.GONE);
            }


            if (channelList.get(position).getSublabel() != null) {
                if (channelList.get(position).getSublabel().length() > 0) {
                    holder.text_view_item_channel_sub_label.setText(channelList.get(position).getSublabel());
                    holder.text_view_item_channel_sub_label.setVisibility(View.VISIBLE);
                } else {
                    holder.text_view_item_channel_sub_label.setVisibility(View.GONE);
                }
            } else {
                holder.text_view_item_channel_sub_label.setVisibility(View.GONE);
            }
            holder.image_view_item_channel_delete.setOnClickListener(v -> {
                final PrefManager prefManager = new PrefManager(activity);
                Integer id_user = Integer.parseInt(prefManager.getString("ID_USER"));
                String key_user = prefManager.getString("TOKEN_USER");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<Integer> call = service.AddMyList(channelList.get(position).getId(), id_user, key_user, "channel");
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            if (response.body() == 202) {
                                Toasty.warning(activity, "This channel has been removed from your list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                    }
                });
                channelList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();

            });
            Picasso.get().load(channelList.get(position).getImage()).placeholder(R.drawable.place_holder_channel).into(holder.image_view_item_channel);
            holder.image_view_item_channel.setOnClickListener(v -> {
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.image_view_item_channel, "imageMain");
                Intent in = new Intent(activity, ChannelActivity.class);
                in.putExtra("channel", channelList.get(holder.getAdapterPosition()));
                activity.startActivity(in, activityOptionsCompat.toBundle());

            });
        }
    }
    @Override
    public int getItemCount() {
        return channelList.size();
    }
    public class ChannelHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_channel_label;
        private final TextView text_view_item_channel_sub_label;
        private final ImageView image_view_item_channel_delete;
        private final RelativeLayout relative_layout_item_channel_delete;
        public ImageView image_view_item_channel ;
        public ChannelHolder(View itemView) {
            super(itemView);
            this.image_view_item_channel =  (ImageView) itemView.findViewById(R.id.image_view_item_channel);
            this.image_view_item_channel =  (ImageView) itemView.findViewById(R.id.image_view_item_channel);
            this.text_view_item_channel_label =  (TextView) itemView.findViewById(R.id.text_view_item_channel_label);
            this.text_view_item_channel_sub_label =  (TextView) itemView.findViewById(R.id.text_view_item_channel_sub_label);
            this.relative_layout_item_channel_delete =  (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_channel_delete);
            this.image_view_item_channel_delete =  (ImageView) itemView.findViewById(R.id.image_view_item_channel_delete);

        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if ((channelList.get(position).getTypeView())==0){
            return 1;
        }
        return   channelList.get(position).getTypeView();
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(activity);
        return prefManager.getString("SUBSCRIBED").equals("TRUE") || prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE");
    }

}
