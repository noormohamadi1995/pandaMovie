package com.virlabs.demo_flx_application.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
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

public class MoviesFragment extends Fragment {


    private View view;
    private RelativeLayout relative_layout_movies_fragement_filtres_button;
    private RelativeLayout card_view_movies_fragement_filtres_layout;
    private ImageView image_view_movies_fragement_close_filtres;
    private AppCompatSpinner spinner_fragement_movies_orders_list;
    private final List<Genre> genreList =  new ArrayList<>();
    private AppCompatSpinner spinner_fragement_movies_genre_list;
    private RelativeLayout relative_layout_frament_movies_genres;
    private RecyclerView recycler_view_movies_fragment;
    private LinearLayout linear_layout_page_error_movies_fragment;
    private LinearLayout linear_layout_load_movies_fragment;
    private SwipeRefreshLayout swipe_refresh_layout_movies_fragment;
    private RelativeLayout relative_layout_load_more_movies_fragment;
    private ImageView image_view_empty_list;


    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;
    private final List<Poster> movieList =  new ArrayList<>();

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer item = 0 ;
    private Button button_try_again;
    private int genreSelected = 0;
    private String orderSelected = "created";

    private boolean firstLoadGenre = true;
    private boolean firstLoadOrder = true;
    private boolean loaded = false;

    private PrefManager prefManager;


    public MoviesFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded) {
                loaded=true;
                page = 0;
                loading = true;
                getGenreList();
                loadMovies();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_movies, container, false);
        movieList.add(new Poster().setTypeView(2));
        prefManager= new PrefManager(requireContext());

        initView();
        initActon();

        return view;
    }

    private void getGenreList() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Genre>> call = service.getGenreList();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                apiClient.FormatData(getActivity(),response);
                if (response.isSuccessful()){
                    if (response.body().size()>0) {
                        final String[] countryCodes = new String[response.body().size()+1];
                        countryCodes[0] = "All genres";
                        genreList.add(new Genre());

                        for (int i = 0; i < response.body().size(); i++) {
                            countryCodes[i+1] = response.body().get(i).getTitle();
                            genreList.add(response.body().get(i));
                        }
                        ArrayAdapter<String> filtresAdapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_layout,R.id.textView,countryCodes);
                        filtresAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        spinner_fragement_movies_genre_list.setAdapter(filtresAdapter);
                        relative_layout_frament_movies_genres.setVisibility(View.VISIBLE);
                    }else{
                        relative_layout_frament_movies_genres.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
            }
        });
    }

    private void initActon() {



        this.relative_layout_movies_fragement_filtres_button.setOnClickListener(v->{
            card_view_movies_fragement_filtres_layout.setVisibility(View.VISIBLE);
            relative_layout_movies_fragement_filtres_button.setVisibility(View.GONE);
        });
        this.image_view_movies_fragement_close_filtres.setOnClickListener(v->{
            card_view_movies_fragement_filtres_layout.setVisibility(View.GONE);
            relative_layout_movies_fragement_filtres_button.setVisibility(View.VISIBLE);
        });
        spinner_fragement_movies_genre_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstLoadGenre) {
                    if (id == 0) {
                        genreSelected = 0;
                    } else {
                        genreSelected = genreList.get((int) id).getId();
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    movieList.clear();
                    movieList.add(new Poster().setTypeView(2));
                    adapter.notifyDataSetChanged();
                    loadMovies();
                }else{
                    firstLoadGenre = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_fragement_movies_orders_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstLoadOrder) {
                    switch ((int) id){
                        case 0:
                            orderSelected = "created";
                            break;
                        case 1:
                            orderSelected = "rating";
                            break;
                        case 2:
                            orderSelected = "imdb";
                            break;
                        case 3:
                            orderSelected = "title";
                            break;
                        case 4:
                            orderSelected = "year";
                            break;
                        case 5:
                            orderSelected = "views";
                            break;
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    movieList.clear();
                    movieList.add(new Poster().setTypeView(2));
                    adapter.notifyDataSetChanged();
                    loadMovies();
                }else{
                    firstLoadOrder = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipe_refresh_layout_movies_fragment.setOnRefreshListener(() -> {
            item = 0;
            page = 0;
            loading = true;
            movieList.clear();
            movieList.add(new Poster().setTypeView(2));
            adapter.notifyDataSetChanged();
            loadMovies();

        });
        button_try_again.setOnClickListener(view -> {
            item = 0;
            page = 0;
            loading = true;
            movieList.clear();
            movieList.add(new Poster().setTypeView(2));
            adapter.notifyDataSetChanged();
            loadMovies();

        });
        recycler_view_movies_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadMovies();
                        }
                    }
                }else{

                }
            }
        });
    }
    public boolean checkSUBSCRIBED(){
        return prefManager.getString("SUBSCRIBED").equals("TRUE") || prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE");
    }
    private void initView() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        // prod

        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);
        this.relative_layout_load_more_movies_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_movies_fragment);
        this.swipe_refresh_layout_movies_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_movies_fragment);
        this.linear_layout_load_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_movies_fragment);
        this.linear_layout_page_error_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_movies_fragment);
        this.recycler_view_movies_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_movies_fragment);
        this.relative_layout_movies_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_movies_fragement_filtres_button);
        this.card_view_movies_fragement_filtres_layout = (RelativeLayout) view.findViewById(R.id.card_view_movies_fragement_filtres_layout);
        this.image_view_movies_fragement_close_filtres = (ImageView) view.findViewById(R.id.image_view_movies_fragement_close_filtres);
        this.spinner_fragement_movies_orders_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_orders_list);
        this.spinner_fragement_movies_genre_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_genre_list);
        this.relative_layout_frament_movies_genres = (RelativeLayout) view.findViewById(R.id.relative_layout_frament_movies_genres);


        adapter = new PosterAdapter(movieList,getActivity());
        if (tabletSize) {
            this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),6,RecyclerView.VERTICAL,false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ( position == 0) ? 6 : 1;
                }
            });
        } else {
            this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),3,RecyclerView.VERTICAL,false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ( position == 0) ? 3 : 1;
                }
            });
        }

        recycler_view_movies_fragment.setHasFixedSize(true);
        recycler_view_movies_fragment.setAdapter(adapter);
        recycler_view_movies_fragment.setLayoutManager(gridLayoutManager);
        // test


        final String[] countryCodes = getResources().getStringArray(R.array.orders_list);

        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_layout,R.id.textView,countryCodes);
        ordersAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_fragement_movies_orders_list.setAdapter(ordersAdapter);
    }
    private void loadMovies() {
        if (page==0){
            linear_layout_load_movies_fragment.setVisibility(View.VISIBLE);
        }else{
            relative_layout_load_more_movies_fragment.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_movies_fragment.setRefreshing(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Poster>> call = service.getMoviesByFiltres(genreSelected,orderSelected,page);
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, final Response<List<Poster>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0){
                        movieList.addAll(response.body());
                        linear_layout_page_error_movies_fragment.setVisibility(View.GONE);
                        recycler_view_movies_fragment.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                        page++;
                        loading=true;
                    }else{
                        if (page==0) {
                            linear_layout_page_error_movies_fragment.setVisibility(View.GONE);
                            recycler_view_movies_fragment.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                    recycler_view_movies_fragment.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                relative_layout_load_more_movies_fragment.setVisibility(View.GONE);
                swipe_refresh_layout_movies_fragment.setRefreshing(false);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {
                linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                recycler_view_movies_fragment.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                relative_layout_load_more_movies_fragment.setVisibility(View.GONE);
                swipe_refresh_layout_movies_fragment.setVisibility(View.GONE);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);

            }
        });
    }
}
