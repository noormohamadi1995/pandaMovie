package com.virlabs.demo_flx_application.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Genre;
import com.virlabs.demo_flx_application.entity.Poster;
import com.virlabs.demo_flx_application.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GenreActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe_refresh_layout_list_genre_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_genre;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_load_genre_activity;


    private String SelectedOrder = "created";
    private Genre genre;
    private String from;

    private boolean tabletSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        getGenre();
        initView();
        initAction();
        loadPosters();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }
    @Override
    public void onBackPressed(){
        if (from!=null){
            Intent intent =  new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            case android.R.id.home:
                if (from!=null){
                    Intent intent =  new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }else{
                    super.onBackPressed();
                }
                return true;
            case R.id.nav_created:
                SelectedOrder = "created";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_rating:
                SelectedOrder = "rating";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_views:
                SelectedOrder = "views";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_year:
                SelectedOrder = "year";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_title:
                SelectedOrder = "title";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_imdb:
                SelectedOrder = "imdb";
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();

                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }
    private void getGenre() {
        genre = getIntent().getParcelableExtra("genre");
        from = getIntent().getStringExtra("from");
    }
    private void loadPosters() {
        if (page==0){
            linear_layout_load_genre_activity.setVisibility(View.VISIBLE);
        }else{
            relative_layout_load_more.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_list_genre_search.setRefreshing(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Poster>> call = service.getPostersByFiltres(genre.getId(),SelectedOrder,page);
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(@NonNull Call<List<Poster>> call, @NonNull final Response<List<Poster>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0){
                        posterArrayList.addAll(response.body());
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_genre.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                        page++;
                        loading=true;
                    }else{
                        if (page==0) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                            recycler_view_activity_genre.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_genre.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_genre_search.setRefreshing(false);
                linear_layout_load_genre_activity.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Poster>> call, @NonNull Throwable t) {
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_genre.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_genre_search.setVisibility(View.GONE);
                linear_layout_load_genre_activity.setVisibility(View.GONE);
            }
        });
    }

    private void initAction() {
        swipe_refresh_layout_list_genre_search.setOnRefreshListener(() -> {
            page = 0;
            loading = true;
            posterArrayList.clear();
            adapter.notifyDataSetChanged();
            loadPosters();
        });
        button_try_again.setOnClickListener(view -> {
            page = 0;
            loading = true;
            posterArrayList.clear();
            adapter.notifyDataSetChanged();
            loadPosters();
        });
        recycler_view_activity_genre.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) {
                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadPosters();
                        }
                    }
                }
            }
        });
    }

    private void initView() {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        checkSUBSCRIBED();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(genre.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_genre_activity=findViewById(R.id.linear_layout_load_genre_activity);
        this.relative_layout_load_more=findViewById(R.id.relative_layout_load_more);
        this.swipe_refresh_layout_list_genre_search=findViewById(R.id.swipe_refresh_layout_list_genre_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_genre          = findViewById(R.id.recycler_view_activity_genre);
        adapter = new PosterAdapter(posterArrayList, this);

        if (tabletSize) {
            this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),6,RecyclerView.VERTICAL,false);
        } else {
            this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
        }

        recycler_view_activity_genre.setHasFixedSize(true);
        recycler_view_activity_genre.setAdapter(adapter);
        recycler_view_activity_genre.setLayoutManager(gridLayoutManager);

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
