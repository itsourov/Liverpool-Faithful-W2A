package com.liverpoolfaithful.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.liverpoolfaithful.app.adapter.CategoryListAdapter;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoryListFragment extends Fragment {
    SwipeRefreshLayout SRLInFCL;
    RecyclerView rvRecent;
    List<Category> categories;
    CategoryListAdapter adapter;
    ProgressBar spin_kit;
    String loadingUrl;

    boolean loading, hasMorePosts;
    int pageNo = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        SRLInFCL = view.findViewById(R.id.SRLInFCL);
        loadingUrl = Constants.baseRestUrl + "categories?";

        categories = new ArrayList<>();

        spin_kit = view.findViewById(R.id.spin_kit);
        rvRecent = view.findViewById(R.id.rvRecentCatList);
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryListAdapter(categories, requireContext());
        rvRecent.setAdapter(adapter);

        variableReset();
        loadRestApi();
        recyclerViewOnBottom();

        SRLInFCL.setOnRefreshListener(() -> {
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
                        recyclerView.scrollToPosition(categories.size() - 1);

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
        categories.clear();
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
                    SRLInFCL.setRefreshing(false);

                    try {
                        JSONArray jsonArr = new JSONArray(data);
                        loadToRev(jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
            loading = false;
            spin_kit.setVisibility(View.GONE);
            SRLInFCL.setRefreshing(false);
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

            try {
                Category p = new Category();


                JSONObject jsonObjectData = response.getJSONObject(i);

                // extract the date
                p.setId(jsonObjectData.getString("id"));
                p.setCount(jsonObjectData.getString("count"));
                p.setName(jsonObjectData.getString("name"));
                try {
                    p.setImageLink(jsonObjectData.getString("featured_image_url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                categories.add(p);
                adapter.showShimmer = false;

                if (categories.size() > 1) {
                    adapter.notifyItemInserted(categories.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
}
