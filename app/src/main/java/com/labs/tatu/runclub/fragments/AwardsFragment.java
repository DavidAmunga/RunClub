package com.labs.tatu.runclub.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.labs.tatu.runclub.R;

/**
 * Created by amush on 18-Sep-17.
 */

public class AwardsFragment extends Fragment {
    private static final String TAG = "AwardsFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_awards_layout,container,false);

        return view;
    }
}
