package com.liverpoolfaithful.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.adapter.RecentPostsAdapter;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.helper.SaveState;
import com.liverpoolfaithful.app.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }


    EditText searchViewInFS;
    View nothingSearchedView;
    TextView nothingSearchedText;
    SwipeRefreshLayout SRLInFS;
    View resultViewInFS;
    RecentPostsAdapter adapter;
    RecyclerView rvFS;
    List<Post> posts;
    ProgressBar spin_kit;
    String loadingUrl;


    boolean loading, hasMorePosts;
    int pageNo = 1;

    MasterSourov sourov;
    SaveState saveState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        sourov = new MasterSourov(getActivity());
        saveState = new SaveState(getActivity());
        loadingUrl = Constants.baseRestUrl + "posts?";

        searchViewInFS = view.findViewById(R.id.searchViewInFS);
        SRLInFS = view.findViewById(R.id.SRLInFS);
        nothingSearchedText = view.findViewById(R.id.nothingFoundText);
        nothingSearchedView = view.findViewById(R.id.nothingSearchedView);
        resultViewInFS = view.findViewById(R.id.resultViewInFS);

        searchViewInFS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    nothingSearchedView.setVisibility(View.GONE);
                    resultViewInFS.setVisibility(View.VISIBLE);
                    loadingUrl = Constants.baseRestUrl + "posts?search=" + s + "&";
                    variableReset();
                    loadRestApi();
                } else {
                    nothingSearchedView.setVisibility(View.VISIBLE);
                    nothingSearchedText.setText(getResources().getString(R.string.nothing_searched));
                    resultViewInFS.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        posts = new ArrayList<>();
        spin_kit = view.findViewById(R.id.spin_kitOnFG);
        rvFS = view.findViewById(R.id.rvFS);



        init();
        variableReset();
        loadRestApi();
        recyclerViewOnBottom();

        SRLInFS.setOnRefreshListener(() -> {

            if (searchViewInFS.getText().length() > 0) {
                nothingSearchedView.setVisibility(View.GONE);
                resultViewInFS.setVisibility(View.VISIBLE);
                init();
                variableReset();
                loadRestApi();
            } else {
                SRLInFS.setRefreshing(false);
            }


        });

        return view;
    }

    private void init() {
        if (saveState.getApplyGridLayout()) {
            rvFS.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            rvFS.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        adapter = new RecentPostsAdapter(posts, requireContext());
        rvFS.setAdapter(adapter);
    }

    private void recyclerViewOnBottom() {

        rvFS.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        adapter.showShimmer = true;
        rvFS.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    private void loadRestApi() {
        Log.d("TAG", "loadRestApi: " + loadingUrl + "page=" + pageNo);
        loading = true;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, loadingUrl + "page=" + pageNo,
                data -> {
                    if (pageNo == 1) {
                        variableReset();
                    }
                    loading = false;
                    spin_kit.setVisibility(View.GONE);
                    SRLInFS.setRefreshing(false);

                    try {
                        JSONArray jsonArr = new JSONArray(data);
                        if (jsonArr.length() == 0 && pageNo == 1) {
                            adapter.showShimmer = false;
                            adapter.notifyDataSetChanged();
                            resultViewInFS.setVisibility(View.GONE);
                            nothingSearchedView.setVisibility(View.VISIBLE);
                            nothingSearchedText.setText(getResources().getString(R.string.no_results_found));
                        }
                        loadToRev(jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }, error -> {
            loading = false;
            spin_kit.setVisibility(View.GONE);
            SRLInFS.setRefreshing(false);
            hasMorePosts = false;
            if (adapter.showShimmer) {
                adapter.showShimmer = false;
                adapter.notifyDataSetChanged();
            }
        });

        if (!Configs.shouldCacheRequest){
            stringRequest.setShouldCache(false);
        }
        queue.add(stringRequest);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadToRev(JSONArray response) {
        if (response.length() < 2) {
            hasMorePosts = false;
        }

        for (int i = 0; i < response.length(); i++) {

            if (Configs.showNativeAds && i % 5 == 0) {

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
                    p.setAgo_time(jsonObjectData.getJSONObject("w2a_by_sourov").getString("ago_time"));
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