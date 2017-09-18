package com.labs.tatu.runclub.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.labs.tatu.runclub.R;
import com.labs.tatu.runclub.model.Award;
import com.labs.tatu.runclub.model.Location;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by amush on 18-Sep-17.
 */

public class AwardsFragment extends Fragment {
    private static final String TAG = "AwardsFragment";


    private CircleImageView btnImage;
    private RecyclerView mAwardsList;

    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Award,AwardsViewHolder> firebaseRecyclerAdapter;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_awards_layout,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Awards");
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

        mAwardsList=(RecyclerView)view.findViewById(R.id.awards_list);
        //mLocationsList.setHasFixedSize(true);
        mAwardsList.setLayoutManager(new LinearLayoutManager(getActivity()));



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Award,AwardsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Award, AwardsViewHolder>(
                Award.class,
                R.layout.award_row,
                AwardsViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(AwardsViewHolder viewHolder, Award model, int position) {
                    viewHolder.setAwardName(model.getAwardName());
                    viewHolder.setAwardDesc(model.getAwardDescription());
                    viewHolder.setAwardImage(getActivity(),model.getAwardImage());

                Log.d(TAG, "AwardImage"+model.getAwardImage());
                Log.d(TAG, "AwardName"+model.getAwardName());
            }
        };

        mAwardsList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class AwardsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;


        public AwardsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setAwardName(String name)
        {
            TextView awardName=(TextView)mView.findViewById(R.id.txtAwardName);
            awardName.setText(name);
        }
        public void setAwardDesc(String desc)
        {
            TextView awardDesc=(TextView)mView.findViewById(R.id.txtAwardDesc);
            awardDesc.setText(desc);

        }
        public void setAwardImage(final Context ctx, final String image)
        {
            final ImageView award_image=(ImageView)mView.findViewById(R.id.awardImage);


            Picasso
                    .with(ctx)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(award_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(image).into(award_image);
                        }
                    });
        }
    }



}
