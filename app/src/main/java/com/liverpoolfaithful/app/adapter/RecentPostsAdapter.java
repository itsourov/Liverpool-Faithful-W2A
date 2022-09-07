package com.liverpoolfaithful.app.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.liverpoolfaithful.app.PostDetails;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.database.BookmarkSaver;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.helper.SaveState;
import com.liverpoolfaithful.app.model.Post;

import java.util.List;

public class RecentPostsAdapter extends RecyclerView.Adapter<RecentPostsAdapter.ViewHolder> {

    public boolean isHorizontal = false;
    List<Post> allPosts;
    Context context;
    Activity activity;
    public boolean showShimmer = true;
    public boolean bookmark = false;
    SaveState saveState;
    MasterSourov sourov;
    BookmarkSaver bookmarkSaver;

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    boolean isAdShowIng = false;

    public RecentPostsAdapter(List<Post> allPosts, Context context) {
        this.allPosts = allPosts;
        this.context = context;
        activity = (Activity) context;
        saveState = new SaveState(context);
        sourov = new MasterSourov(activity);

        bookmarkSaver = new BookmarkSaver(activity);


        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();


    }

    @Override
    public int getItemViewType(int position) {
        if (!showShimmer && allPosts.get(position).getTypeCode() == Constants.TYPE_CODE_FOR_ADS) {
            return Constants.TYPE_CODE_FOR_ADS;

        } else {
            return Constants.TYPE_CODE_FOR_POST;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == Constants.TYPE_CODE_FOR_ADS) {
            v = layoutInflater.inflate(R.layout.ad_view, parent, false);


            AdLoader adLoader = new AdLoader.Builder(context, context.getResources().getString(R.string.native_ad_id))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // Assumes you have a placeholder FrameLayout in your View layout
                            // (with id fl_adplaceholder) where the ad is to be placed.
                            FrameLayout frameLayout =
                                    v.findViewById(R.id.adLayout);
                            // Assumes that your ad layout is in a file call native_ad_layout.xml
                            // in the res/layout folder
                            NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.ad_layout_native, null);
                            // This method sets the text, images and the native ad, etc into the ad
                            // view.
                            populateNativeAdView(nativeAd, adView);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);

                            Log.d("TAG", "onNativeAdLoaded: ");
                        }
                    }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

        } else {
            if (Configs.applyGridLayout) {

                if (isHorizontal) {
                    v = layoutInflater.inflate(R.layout.single_post_item_grid_half_wide, parent, false);
                } else {
                    v = layoutInflater.inflate(R.layout.single_post_item_grid, parent, false);
                }
            } else {
                v = layoutInflater.inflate(R.layout.single_post_item_linear, parent, false);
            }


        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            if (getItemViewType(position) == Constants.TYPE_CODE_FOR_POST) {
                holder.shimmerFrameLayout.stopShimmer();
                holder.shimmerFrameLayout.setShimmer(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.postTitle.setText(Html.fromHtml(allPosts.get(position).getTitle(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.postTitle.setText(Html.fromHtml(allPosts.get(position).getTitle()));
                }

                Glide.with(context)
                        .load(allPosts.get(position).getFeature_image_thumb())
                        .placeholder(R.drawable.image_placeholder)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher)
                        .into(holder.postImage);

                if (allPosts.get(position).getCategory_name() != null) {
                    holder.category_name.setVisibility(View.VISIBLE);
                    holder.category_name.setText(allPosts.get(position).getCategory_name());

                }

                holder.date_text.setText(sourov.convertToAGo(allPosts.get(position).getDate()));

                holder.itemView.setOnClickListener(v -> {

                    Intent i = new Intent(v.getContext(), PostDetails.class);


                    Bundle bundle = new Bundle();
                    bundle.putString("title", allPosts.get(position).getTitle());
                    bundle.putString("postID", allPosts.get(position).getId());
                    bundle.putString("imageLink", allPosts.get(position).getFeature_image_full());
                    bundle.putString("selfUrl", allPosts.get(position).getSelfUrl());
                    bundle.putString("catName", allPosts.get(position).getCategory_name());

                    i.putExtras(bundle);
                    if (saveState.isAnimationOn() && !Configs.postDetailsNoFM) {
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.postImage, holder.postImage.getTransitionName());

                        context.startActivity(i, optionsCompat.toBundle());
                    } else {
                        context.startActivity(i);
                    }

                    if (Configs.showInterstitialAds){
                        showInterstitialAds();
                    }



                });


                holder.btn_heart.setOnClickListener(v -> {
                    if (bookmarkSaver.IfBookmarkExists(allPosts.get(position).getId())) {

                        sourov.showToast(bookmarkSaver.deleteBookmarkPost(allPosts.get(position).getId()));

                        holder.btn_heart.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_white)));
                        if (bookmark) {
                            removeItem(position);
                        }

                    } else {
                        sourov.showToast(bookmarkSaver.addBookmark(allPosts.get(position).getTitle(), allPosts.get(position).getCategory_name(), allPosts.get(position).getId(), allPosts.get(position).getSelfUrl(), allPosts.get(position).getFeature_image_full(), allPosts.get(position).getDate()));
                        holder.btn_heart.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    }

                });

                if (bookmarkSaver.IfBookmarkExists(allPosts.get(position).getId())) {
                    holder.btn_heart.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));

                } else {
                    holder.btn_heart.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_white)));
                }

            }


        }
    }


    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());


        adView.getMediaView().setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                float scale = context.getResources().getDisplayMetrics().density;

                int maxHeightPixels = 175;
                int maxHeightDp = (int) (maxHeightPixels * scale + 0.5f);

                if (child instanceof ImageView) { //Images
                    ImageView imageView = (ImageView) child;
                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(maxHeightDp);

                } else { //Videos
                    ViewGroup.LayoutParams params = child.getLayoutParams();
                    params.height = maxHeightDp;
                    child.setLayoutParams(params);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {}
        });
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (Configs.applyGridLayout){
            adView.getStoreView().setVisibility(View.GONE);
        }else {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }


        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }


    public void showInterstitialAds() {

        InterstitialAd.load(context, context.getResources().getString(R.string.interstitial_ads_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        if (!isAdShowIng) {
                            mInterstitialAd.show(activity);
                        }

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {

                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {

                                mInterstitialAd = null;
                                isAdShowIng = false;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                                mInterstitialAd = null;
                                sourov.showToast(adError.getMessage());
                            }

                            @Override
                            public void onAdImpression() {

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isAdShowIng = true;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        sourov.showToast(loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }


    @Override
    public int getItemCount() {
        int shimmerItemNumber = 10;

        return showShimmer ? shimmerItemNumber : allPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;
        ImageView postImage;
        TextView postTitle, category_name, date_text;
        ImageButton btn_heart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            btn_heart = itemView.findViewById(R.id.btn_heart);
            postImage = itemView.findViewById(R.id.post_img);
            postTitle = itemView.findViewById(R.id.title_text);
            category_name = itemView.findViewById(R.id.category_name);
            date_text = itemView.findViewById(R.id.date_text);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);


        }
    }


    public void removeItem(int position) {
        this.allPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

}

