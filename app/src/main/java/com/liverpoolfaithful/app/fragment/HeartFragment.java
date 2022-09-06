package com.liverpoolfaithful.app.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.adapter.RecentPostsAdapter;
import com.liverpoolfaithful.app.database.BookmarkSaver;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.model.Post;

import java.util.ArrayList;
import java.util.List;

public class HeartFragment extends Fragment {

    RecyclerView rvRecentHeartList;
    List<Post> posts;
    RecentPostsAdapter adapter;
    BookmarkSaver bookmarkSaver;
    SwipeRefreshLayout SRLInFH;
    View resultViewInFH,nothingSearchedView;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heart, container, false);


        bookmarkSaver = new BookmarkSaver(getActivity());

        resultViewInFH = view.findViewById(R.id.resultViewInFH);
        nothingSearchedView = view.findViewById(R.id.nothingSearchedView);

        posts = new ArrayList<>();
        SRLInFH = view.findViewById(R.id.SRLInFH);
        rvRecentHeartList = view.findViewById(R.id.rvRecentHeartList);
        if (Configs.applyGridLayout){
            rvRecentHeartList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }else {
            rvRecentHeartList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        adapter = new RecentPostsAdapter(posts, requireContext());
        adapter.bookmark = true;
        rvRecentHeartList.setAdapter(adapter);

        new doingInBGBookmark().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        SRLInFH.setOnRefreshListener(() -> {
            nothingSearchedView.setVisibility(View.GONE);
            resultViewInFH.setVisibility(View.VISIBLE);
            posts.clear();
            adapter.showShimmer = true;
            rvRecentHeartList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            new doingInBGBookmark().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        });
        return view;
    }


    class doingInBGBookmark extends AsyncTask<Void, Post, Void> {

        public doingInBGBookmark() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... voids) {

            posts.clear();
            Cursor cursor = bookmarkSaver.readAllBookmark();

            if (cursor.getCount() == 0) {
                publishProgress(null);

            }


            while (cursor.moveToNext()) {

                Post p = new Post();
                p.setTitle(cursor.getString(1));
                p.setCategory_name(cursor.getString(2));
                p.setFeature_image_full(cursor.getString(3));
                p.setFeature_image_thumb(cursor.getString(3));
                p.setId(cursor.getString(4));
                p.setSelfUrl(cursor.getString(5));
                p.setDate(cursor.getString(6));


                publishProgress(p);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            return null;


        }

        @Override
        protected void onProgressUpdate(Post... values) {

            if (values == null) {
                nothingSearchedView.setVisibility(View.VISIBLE);
                resultViewInFH.setVisibility(View.GONE);
                adapter.showShimmer = false;
                adapter.notifyDataSetChanged();
            } else {
                posts.add(values[0]);
                adapter.showShimmer = false;
                if (posts.size() > 1) {
                    adapter.notifyItemInserted(posts.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
            SRLInFH.setRefreshing(false);


        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

}