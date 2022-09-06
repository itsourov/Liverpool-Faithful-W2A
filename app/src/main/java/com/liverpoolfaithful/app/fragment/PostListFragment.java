package com.liverpoolfaithful.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.adapter.RecentPostsAdapter;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PostListFragment extends Fragment {


    public PostListFragment() {
        // Required empty public constructor
    }

    SwipeRefreshLayout SRLInFPL;
    RecyclerView rvRecent;
    List<Post> posts;
    RecentPostsAdapter adapter;
    ProgressBar spin_kit;
    String loadingUrl;
    String catId;


    boolean loading, hasMorePosts;
    int pageNo = 1;

    MasterSourov sourov;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            catId = getArguments().getString("catId");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        sourov = new MasterSourov(getActivity());

        SRLInFPL = view.findViewById(R.id.SRLInFPL);

        if (catId==null){
            loadingUrl = Constants.baseRestUrl + "posts?";
        }else {
            loadingUrl = Constants.baseRestUrl + "posts?categories="+catId+"&";
        }



        posts = new ArrayList<>();

        spin_kit = view.findViewById(R.id.spin_kit);
        rvRecent = view.findViewById(R.id.rvRecentPostList);
        if (Configs.applyGridLayout){
            rvRecent.setLayoutManager(new GridLayoutManager(getContext(),2));
        }else {
            rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        adapter = new RecentPostsAdapter(posts, requireContext());
        rvRecent.setAdapter(adapter);

        variableReset();
        loadRestApi();
        recyclerViewOnBottom();

        SRLInFPL.setOnRefreshListener(() -> {
            variableReset();
            loadRestApi();
        });
        return view;
    }

    private void recyclerViewOnBottom() {

        rvRecent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (hasMorePosts && !loading) {
                        pageNo++;
                        loadRestApi();
                        loading = true;
                        spin_kit.setVisibility(View.VISIBLE);
                        recyclerView.scrollToPosition(posts.size() - 1);

                    }

                }
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void variableReset() {
        pageNo = 1;
        hasMorePosts = true;
        loading = false;
        posts.clear();
        adapter.showShimmer=true;
        rvRecent.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    private void loadRestApi() {
        loading = true;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, loadingUrl + "per_page=20&page=" + pageNo,
                data -> {
                    loading = false;
                    spin_kit.setVisibility(View.GONE);
                    SRLInFPL.setRefreshing(false);

                    try {
                        JSONArray jsonArr = new JSONArray(data);
                        loadToRev(jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        gotAnError();
                    }

                }, error -> {
          gotAnError();
        });
        if (!Configs.shouldCacheRequest){
            stringRequest.setShouldCache(false);
        }
        queue.add(stringRequest);
    }

    private void gotAnError() {
        loading = false;
        spin_kit.setVisibility(View.GONE);
        SRLInFPL.setRefreshing(false);
        hasMorePosts = false;
        if (adapter.showShimmer) {
            adapter.showShimmer = false;
            adapter.notifyDataSetChanged();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadToRev(JSONArray response) {
        if (response.length() < 2) {
            hasMorePosts = false;
        }

        for (int i = 0; i < response.length(); i++) {

            if (Configs.showNativeAds && i % 10 == 0) {

                Post ads = new Post();
                ads.setTypeCode(Constants.TYPE_CODE_FOR_ADS);
                posts.add(ads);
                if (posts.size() > 1) {
                    adapter.notifyItemInserted(posts.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
            try {
                Post p = new Post();

                JSONObject jsonObjectData = response.getJSONObject(i);


                // extract the date
                p.setDate(jsonObjectData.getString("date_gmt"));

                // extract the title
                JSONObject titleObject = jsonObjectData.getJSONObject("title");
                p.setTitle(titleObject.getString("rendered"));


                //extract the id;
                p.setId(jsonObjectData.getString("id"));

                p.setSelfUrl(jsonObjectData.getString("link"));


                try {
                    p.setCategory_name(jsonObjectData.getJSONObject("w2a_by_sourov").getString("catName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {

                    // extract feature image
                    p.setFeature_image_thumb(jsonObjectData.getJSONObject("w2a_by_sourov").getString("image_full"));  //image_thumbnail & image_full
                    p.setFeature_image_full(jsonObjectData.getJSONObject("w2a_by_sourov").getString("image_full"));

                } catch (JSONException e) {
                    e.printStackTrace();

                }


                posts.add(p);
                adapter.showShimmer = false;

                if (posts.size() > 1) {
                    adapter.notifyItemInserted(posts.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}