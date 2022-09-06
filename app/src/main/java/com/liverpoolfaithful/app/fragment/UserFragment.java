package com.liverpoolfaithful.app.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.helper.MasterSourov;


public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    MasterSourov sourov;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);

        sourov= new MasterSourov(getActivity());

        view.findViewById(R.id.itemAboutUs).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.about_us_url)));
        view.findViewById(R.id.itemContactUs).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.contact_us_url)));
        view.findViewById(R.id.itemPrivacyPolicy).setOnClickListener(v -> sourov.openLinkInChromeView(getResources().getString(R.string.privacy_policy_url)));

        view.findViewById(R.id.itemRateUs).setOnClickListener(v -> sourov.openLinkInDefault("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
        view.findViewById(R.id.itemMoreApps).setOnClickListener(v -> sourov.openLinkInDefault(getResources().getString(R.string.more_apps_url)));

        return view;
    }
}