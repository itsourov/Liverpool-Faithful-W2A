package com.liverpoolfaithful.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.adapter.CommentsAdapter;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.model.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;


public class CommentListFragment extends BottomSheetDialogFragment {


    List<Comment> comments;
    CommentsAdapter adapter;
    RecyclerView rvComment;
    String postID;
    Toolbar toolbar;
    MasterSourov sourov;

    RequestQueue queue;

    NestedScrollView nothingFoundView;
    SharedPreferences saved_values;

    public CommentListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postID = getArguments().getString("postID");

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        sourov = new MasterSourov(getActivity());

        saved_values = PreferenceManager.getDefaultSharedPreferences(getContext());

        queue = Volley.newRequestQueue(requireContext());

        comments = new ArrayList<>();


        nothingFoundView = view.findViewById(R.id.nothingFoundView);
        rvComment = view.findViewById(R.id.rvComment);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvComment.setLayoutManager(linearLayoutManager);

        adapter = new CommentsAdapter(comments, getContext());
        rvComment.setAdapter(adapter);


        getCommentsData();


        toolbar = view.findViewById(R.id.toolbarOnCLF);
        toolbar.setTitle("Comments");
        toolbar.setNavigationOnClickListener(v -> dismiss());

        view.findViewById(R.id.refreshButton).setOnClickListener(v -> {
            comments.clear();
            adapter.showShimmer = true;
            rvComment.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            getCommentsData();

            rvComment.setVisibility(View.VISIBLE);
            nothingFoundView.setVisibility(View.GONE);
        });

        view.findViewById(R.id.leaveAComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View commentDialogView = factory.inflate(R.layout.comment_input_box, null);
                final AlertDialog commentDialog = new AlertDialog.Builder(getContext()).create();
                commentDialog.setView(commentDialogView);


                commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                commentDialog.setCancelable(false);
                commentDialog.show();

                EditText nameBoxOnCommentInput, emailBoxOnCommentInput, commentBoxOnCommentInput;

                nameBoxOnCommentInput = commentDialogView.findViewById(R.id.nameBoxOnCommentInput);
                emailBoxOnCommentInput = commentDialogView.findViewById(R.id.emailBoxOnCommentInput);
                commentBoxOnCommentInput = commentDialogView.findViewById(R.id.commentBoxOnCommentInput);

                ProgressBar progressBar = commentDialog.findViewById(R.id.progressBar);

                nameBoxOnCommentInput.setText(saved_values.getString("user_name",""));
                emailBoxOnCommentInput.setText(saved_values.getString("user_email",""));
                commentDialog.findViewById(R.id.cancelButton).setOnClickListener(v1 -> {
                    //your business logic
                    commentDialog.dismiss();
                });
                commentDialog.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);

                        String name = nameBoxOnCommentInput.getText().toString().trim();
                        String email = emailBoxOnCommentInput.getText().toString().trim();
                        String comment = commentBoxOnCommentInput.getText().toString().trim();

                        String commentPostUrl = Constants.baseRestUrl + "comments";

                        StringRequest stringRequesta = new StringRequest(Request.Method.POST, commentPostUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                progressBar.setVisibility(View.GONE);
                                commentDialog.dismiss();

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    sourov.showToast("Comment submitted! Status: " + jsonObject.getString("status"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                commentDialog.dismiss();
                                MaterialDialog mDialog = new MaterialDialog.Builder(requireActivity())
                                        .setTitle(getResources().getString(R.string.got_an_error))
                                        .setMessage(sourov.getVolleyResponse(error))
                                        .setCancelable(true)
                                        .setPositiveButton(getString(R.string.ok), (dialogInterface, which) -> dialogInterface.dismiss())
                                        .build();

                                // Show Dialog
                                mDialog.show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("content", comment);
                                params.put("post", postID);
                                params.put("author_name", name);
                                params.put("author_email", email);
                                params.put("let_me_insert", "please");


                                return params;
                            }
                        };

                        stringRequesta.setShouldCache(false);
                        queue.add(stringRequesta);

                        SharedPreferences.Editor editor=saved_values.edit();
                        editor.putString("user_name",name);
                        editor.putString("user_email",email);
                        editor.apply();
                    }
                });
            }
        });

        return view;
    }


    private void getCommentsData() {

        String loadingUrl = Constants.baseRestUrl + "comments?";

        @SuppressLint("NotifyDataSetChanged")
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loadingUrl + "per_page=1000&post=" + postID,
                data -> {


                    try {
                        JSONArray jsonArr = new JSONArray(data);
                        loadToRev(jsonArr);
                        toolbar.setTitle("Comments (" + jsonArr.length() + ")");
                        if (jsonArr.length() == 0) {
                            gotAnError();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        gotAnError();
                    }

                }, error -> {
            sourov.showToast(sourov.getVolleyResponse(error));
            gotAnError();

        });
        if (!Configs.shouldCacheRequest) {
            stringRequest.setShouldCache(false);
        }
        queue.add(stringRequest);
    }

    private void gotAnError() {
        rvComment.setVisibility(View.GONE);
        nothingFoundView.setVisibility(View.VISIBLE);
        if (adapter.showShimmer) {
            adapter.showShimmer = false;
            adapter.notifyDataSetChanged();
        }
    }


    private void loadToRev(JSONArray response) {
        adapter.showShimmer = false;


        for (int i = 0; i < response.length(); i++) {

            try {
                Comment p = new Comment();


                JSONObject jsonObjectData = response.getJSONObject(i);

                // extract the date
                p.setDate(jsonObjectData.getString("date"));

                //extract Author name
                p.setAuthor_name(jsonObjectData.getString("author_name"));

                //extract the comment content
                JSONObject contentObject = jsonObjectData.getJSONObject("content");
                p.setContent(contentObject.getString("rendered"));

                //extract the author image
                JSONObject authorImageObject = jsonObjectData.getJSONObject("author_avatar_urls");
                p.setAuthor_avatar_urls(authorImageObject.getString("96"));

                if (Configs.replyInCommentEnabled) {
                    if (!jsonObjectData.getJSONObject("_links").has("in-reply-to")) {
                        if (jsonObjectData.getJSONObject("_links").has("children")) {
                            p.setChildNumber(jsonObjectData.getJSONObject("_links").getJSONArray("children").length());
                        }
                        comments.add(p);
                        if (comments.size() > 1) {
                            adapter.notifyItemInserted(comments.size() - 1);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    comments.add(p);
                    if (comments.size() > 1) {
                        adapter.notifyItemInserted(comments.size() - 1);
                        rvComment.scrollToPosition(0);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        rvComment.scrollToPosition(0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }


    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}