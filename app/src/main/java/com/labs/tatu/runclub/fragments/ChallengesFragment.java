package com.labs.tatu.runclub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.labs.tatu.runclub.R;
import com.labs.tatu.runclub.model.Challenge;
import com.squareup.picasso.Picasso;

/**
 * Created by amush on 10-Sep-17.
 */

public class ChallengesFragment extends Fragment {

    private static final String TAG = "QuickStartFragment";

    private RecyclerView mChallengeList;

    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_challenges_layout,container,false);

        //toolBarTitle.setText("Challenges");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Challenges");


        mChallengeList=(RecyclerView)view.findViewById(R.id.challenge_list);
        //mChallengeList.setHasFixedSize(true);
        mChallengeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Challenge,ChallengeViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder>(
                Challenge.class,
                R.layout.challenge_row,
                ChallengeViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(ChallengeViewHolder viewHolder, Challenge model, int position) {
                viewHolder.setChallengeName(model.getChallengeName());
                viewHolder.setImage(getContext(),model.getChallengeImage());
                viewHolder.setChallenger(model.getChallenger());

            }
        };

        mChallengeList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ChallengeViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

        }
        public void setChallengeName(String title)
        {
            TextView challenge_name=(TextView)mView.findViewById(R.id.ch_title);
            challenge_name.setText(title);
        }
        public void setChallengeLoc(String loc)
        {

        }
        public void setImage(Context ctx, String image)
        {
            ImageView challenge_image=(ImageView)itemView.findViewById(R.id.ch_image);
            Picasso.with(ctx).load(image).into(challenge_image);
        }
        public void setChallenger(String challenger_name)
        {
            TextView challenger=(TextView)mView.findViewById(R.id.challenger_name);
            challenger.setText(challenger_name);
        }


    }
}
