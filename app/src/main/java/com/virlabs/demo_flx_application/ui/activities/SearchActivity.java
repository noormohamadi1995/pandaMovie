package com.virlabs.demo_flx_application.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Channel;
import com.virlabs.demo_flx_application.entity.Data;
import com.virlabs.demo_flx_application.entity.Poster;
import com.virlabs.demo_flx_application.ui.Adapters.PosterAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private SwipeRefreshLayout swipe_refresh_layout_list_search_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_search;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0 ;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    ArrayList<Channel> channelArrayList = new ArrayList<>();
    private LinearLayout linear_layout_load_search_activity;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        prefManager= new PrefManager(getApplicationContext());

        initView();
        initAction();
        loadPosters();
    }

    private void initView() {
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }

        Bundle bundle = getIntent().getExtras() ;
        this.query =  bundle.getString("query");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(query);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_search_activity=findViewById(R.id.linear_layout_load_search_activity);
        this.swipe_refresh_layout_list_search_search=findViewById(R.id.swipe_refresh_layout_list_search_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_search          = findViewById(R.id.recycler_view_activity_search);
        adapter = new PosterAdapter(posterArrayList,channelArrayList, this);

        recycler_view_activity_search.setHasFixedSize(true);
        recycler_view_activity_search.setAdapter(adapter);
    }

    private void loadPosters() {
        swipe_refresh_layout_list_search_search.setRefreshing(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Data> call = service.searchData(query);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, final Response<Data> response) {
                if (response.isSuccessful()){

                    if (response.body().getChannels() !=null){
                        channelArrayList.addAll(response.body().getChannels());
                    }

                    if (channelArrayList.size()>0){
                        posterArrayList.add(new Poster().setTypeView(3));
                        if (tabletSize) {
                            gridLayoutManager=  new GridLayoutManager(getApplicationContext(),6,RecyclerView.VERTICAL,false);
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    return ( position == 0) ? 6 : 1;
                                }
                            });
                        } else {
                            gridLayoutManager=  new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    return ( position == 0) ? 3 : 1;
                                }
                            });
                        }
                    }else{
                        if (tabletSize) {
                            gridLayoutManager=  new GridLayoutManager(getApplicationContext(),6,RecyclerView.VERTICAL,false);
                        } else {
                            gridLayoutManager=  new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
                        }
                    }

                    if (response.body().getPosters() !=null){
                        for (int i = 0; i < response.body().getPosters().size(); i++) {
                            posterArrayList.add(response.body().getPosters().get(i).setTypeView(1));
                        }
                    }
                    if (channelArrayList.size() == 0 && posterArrayList.size() == 0){
                            linear_layout_layout_error.setVisibility(View.GONE);
                            recycler_view_activity_search.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                    }else{
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_search.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);
                    }
                }else{
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_search.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                swipe_refresh_layout_list_search_search.setRefreshing(false);
                linear_layout_load_search_activity.setVisibility(View.GONE);
                recycler_view_activity_search.setLayoutManager(gridLayoutManager);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_search.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                swipe_refresh_layout_list_search_search.setVisibility(View.GONE);
                linear_layout_load_search_activity.setVisibility(View.GONE);

            }
        });
    }

    private void initAction() {
        swipe_refresh_layout_list_search_search.setOnRefreshListener(() -> {
            item = 0;
            page = 0;
            posterArrayList.clear();
            channelArrayList.clear();
            adapter.notifyDataSetChanged();
            loadPosters();
        });
        button_try_again.setOnClickListener(view -> {
            item = 0;
            page = 0;
            posterArrayList.clear();
            channelArrayList.clear();
            adapter.notifyDataSetChanged();
            loadPosters();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        // Respond to the action bar's Up/Home button
        if (itemMenu.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        return prefManager.getString("SUBSCRIBED").equals("TRUE") || prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE");
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}
