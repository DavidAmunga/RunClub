package com.labs.tatu.runclub.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.labs.tatu.runclub.R;

/**
 * Created by amush on 18-Sep-17.
 */

public class RunHistoryFragment extends Fragment {
    private static final String TAG = "RunHistoryFragment";

    MaterialSpinner spinner;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_run_history_layout,container,false);

        spinner=(MaterialSpinner)view.findViewById(R.id.spinner);
        spinner.setTextColor(getResources().getColor(android.R.color.black));
        spinner.setText("COMPLETED RUNS");
        spinner.setBackgroundColor(Color.parseColor("#c3c3c3"));
        spinner.setItems("Locations", "Events", "Challenges");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.equals("Locations"))
                {


                }
                else if(item.equals("Challenges"))
                {


                }
                else if(item.equals("Events"))
                {


                }


            }
        });

        return view;
    }
}
