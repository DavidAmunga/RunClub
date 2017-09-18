package com.labs.tatu.runclub.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.labs.tatu.runclub.LocationRunActivity;
import com.labs.tatu.runclub.MainActivity;
import com.labs.tatu.runclub.R;
import com.labs.tatu.runclub.model.Location;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by amush on 10-Sep-17.
 */

public class LocationsFragment extends Fragment {

    private static final String TAG = "QuickStartFragment";


    private RecyclerView mLocationsList;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Location,LocationViewHolder> firebaseRecyclerAdapter;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_locations_layout,container,false);

        //toolBarTitle.setText("Locations");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Locations");
        mDatabase.keepSynced(true);
        mDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }
                return Transaction.success(mutableData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mLocationsList=(RecyclerView)view.findViewById(R.id.location_list);
        //mLocationsList.setHasFixedSize(true);
        mLocationsList.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Location, LocationViewHolder>(
                Location.class,
                R.layout.location_row,
                LocationViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(LocationViewHolder viewHolder, final Location model, final int position) {

                viewHolder.setLocationName(model.getLocationName());
                viewHolder.setLocationImage(getContext(),model.getLocationPhotoUrl());
                viewHolder.setLocationDistance(model.getLocationDistance());
                Log.d(TAG, "Photo Url: "+model.getLocationPhotoUrl());
                Log.d(TAG, "Location Name: "+model.getLocationName());
                Log.d(TAG, "Location Distance: "+model.getLocationDistance());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "You clicked on "+position);
                        Intent intent=new Intent(getContext(),LocationRunActivity.class);
                        intent.putExtra("locName",model.getLocationName());
                        intent.putExtra("locDistance",model.getLocationDistance());
                        intent.putExtra("fromLat",model.getFromLat());
                        intent.putExtra("fromLong",model.getFromLong());
                        intent.putExtra("locGoal",model.getLocationGoal());
                        intent.putExtra("toLat",model.getToLat());
                        intent.putExtra("toLong",model.getToLong());
                        Log.d(TAG, "fromLat: "+model.getFromLat());
                        startActivity(intent);

                    }
                });

            }
        };
        mLocationsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public LocationViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setLocationName(String title)
        {
            TextView location_name=(TextView)mView.findViewById(R.id.loc_title);
            location_name.setText(title);

        }
        public void setLocationDistance(long distance)
        {
            TextView locationDistance=(TextView)mView.findViewById(R.id.loc_distance);
            if(distance>1000)
            {
                locationDistance.setText(String.valueOf(distance/1000)+" km");
            }
            else
            {
                locationDistance.setText(String.valueOf(distance)+" m");

            }


        }
        public void setLocationImage(final Context ctx,final String image)
        {
            final ImageView loc_image=(ImageView)mView.findViewById(R.id.loc_image);


            Picasso
                    .with(ctx)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(loc_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(image).into(loc_image);
                        }
                    });


        }
    }



}
