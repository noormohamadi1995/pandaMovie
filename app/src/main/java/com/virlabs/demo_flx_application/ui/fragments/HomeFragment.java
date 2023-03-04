package com.virlabs.demo_flx_application.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Data;
import com.virlabs.demo_flx_application.entity.Genre;
import com.virlabs.demo_flx_application.ui.Adapters.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_home_fragment;
    private LinearLayout linear_layout_load_home_fragment;
    private LinearLayout linear_layout_page_error_home_fragment;
    private RecyclerView recycler_view_home_fragment;
    private HomeAdapter homeAdapter;

    private Genre my_genre_list;
    private final List<Data> dataList=new ArrayList<>();
    private Button button_try_again;

    private PrefManager prefManager;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view=  inflater.inflate(R.layout.fragment_home, container, false);
        prefManager= new PrefManager(requireContext());

        initViews();
        initActions();
        loadData();
        return view;
    }

    private void loadData() {

        showLoadingView();
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Data> call = service.homeData();
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                apiClient.FormatData(getActivity(),response);
                if (response.isSuccessful()){
                    dataList.clear();
                    dataList.add(new Data().setViewType(0));
                    dataList.add(new Data().setViewType(7));
                    if (response.body().getSlides().size()>0){
                        Data sliodeData =  new Data();
                        sliodeData.setSlides(response.body().getSlides());
                        dataList.add(sliodeData);
                    }
                    if (response.body().getChannels().size()>0){
                       Data channelData = new Data();
                       channelData.setChannels(response.body().getChannels());
                        dataList.add(channelData);
                    }
                    if (response.body().getActors().size()>0){
                        Data actorsData = new Data();
                        actorsData.setActors(response.body().getActors());
                        dataList.add(actorsData);
                    }
                    if (response.body().getGenres().size()>0){
                        if (my_genre_list!=null){
                            Data genreDataMyList = new Data();
                            genreDataMyList.setGenre(my_genre_list);
                            dataList.add(genreDataMyList);
                        }
                        for (int i = 0; i < response.body().getGenres().size(); i++) {
                            Data genreData = new Data();
                            genreData.setGenre(response.body().getGenres().get(i));
                            dataList.add(genreData);
                        }
                    }
                    showListView();
                    homeAdapter.notifyDataSetChanged();
                }else{
                    showErrorView();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                showErrorView();
            }
        });
    }
   private void showLoadingView(){
       linear_layout_load_home_fragment.setVisibility(View.VISIBLE);
       linear_layout_page_error_home_fragment.setVisibility(View.GONE);
       recycler_view_home_fragment.setVisibility(View.GONE);
   }
    private void showListView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        recycler_view_home_fragment.setVisibility(View.VISIBLE);
    }
    private void showErrorView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.VISIBLE);
        recycler_view_home_fragment.setVisibility(View.GONE);
    }
    private void initActions() {
        swipe_refresh_layout_home_fragment.setOnRefreshListener(() -> {
            loadData();
            swipe_refresh_layout_home_fragment.setRefreshing(false);
        });
        button_try_again.setOnClickListener(v->{
            loadData();
        });
    }
    public boolean checkSUBSCRIBED(){
        return prefManager.getString("SUBSCRIBED").equals("TRUE") || prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE");
    }
    private void initViews() {
        this.swipe_refresh_layout_home_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        this.linear_layout_load_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_home_fragment);
        this.linear_layout_page_error_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_home_fragment);
        this.recycler_view_home_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_home_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false);


        this.homeAdapter =new HomeAdapter(dataList,getActivity());
        recycler_view_home_fragment.setHasFixedSize(true);
        recycler_view_home_fragment.setAdapter(homeAdapter);
        recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
    }

}
