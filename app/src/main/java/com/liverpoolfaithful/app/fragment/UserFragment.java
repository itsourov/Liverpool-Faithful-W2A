package com.liverpoolfaithful.app.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.helper.SaveState;


public class UserFragment extends Fragment {
    MasterSourov sourov;
    SaveState saveState ;
    ImageButton layout_grid,layout_Linear;
    public UserFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);

        sourov= new MasterSourov(getActivity());
        saveState= new SaveState(getActivity());

        layout_grid = view.findViewById(R.id.layout_grid);
        layout_Linear = view.findViewById(R.id.layout_Linear);

        view.findViewById(R.id.itemAboutUs).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.about_us_url)));
        view.findViewById(R.id.itemContactUs).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.contact_us_url)));
        view.findViewById(R.id.itemPrivacyPolicy).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.privacy_policy_url)));

        view.findViewById(R.id.itemRateUs).setOnClickListener(v -> sourov.openLinkInDefault("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
        view.findViewById(R.id.itemMoreApps).setOnClickListener(v -> sourov.openLinkInDefault(getResources().getString(R.string.more_apps_url)));

        if (saveState.getApplyGridLayout()){
            layout_grid.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_200)));
        }else {
            layout_Linear.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_200)));
        }
        layout_grid.setOnClickListener(v -> {
            saveState.setApplyGridLayout(true);
            layout_grid.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_200)));
            layout_Linear.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black_white)));

            sourov.showToast("Refresh the app to see changes");
        });
        layout_Linear.setOnClickListener(v -> {
            saveState.setApplyGridLayout(false);
            layout_Linear.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_200)));
            layout_grid.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black_white)));
            sourov.showToast("Refresh the app to see changes");
        });

        return view;
    }
}