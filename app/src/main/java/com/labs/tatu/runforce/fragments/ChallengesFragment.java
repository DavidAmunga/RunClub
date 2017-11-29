package com.labs.tatu.runforce.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.labs.tatu.runforce.R;
import com.labs.tatu.runforce.WorkoutActivity;
import com.labs.tatu.runforce.model.Challenge;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

/**
 * Created by amush on 10-Sep-17.
 */

public class ChallengesFragment extends Fragment {

    private static final String TAG = "Challenges Fragment";

    private RecyclerView mChallengeList;

    private DatabaseReference mDatabase;

    FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_challenges_layout,container,false);

        //toolBarTitle.setText("Challenges");


        String id= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference("Users").child(id).child("userChallenges");



        mChallengeList=(RecyclerView)view.findViewById(R.id.challenge_list);
        mChallengeList.setHasFixedSize(true);
        mChallengeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder>(
                Challenge.class,
                R.layout.challenge_row,
                ChallengeViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(ChallengeViewHolder viewHolder, final Challenge model, int position) {
                viewHolder.setChallengeName(model.getChallengeName());
                viewHolder.setImage(getContext(),model.getChallengeImage());
                viewHolder.setChallenger(model.getChallenger());

                final String key = firebaseRecyclerAdapter.getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LovelyStandardDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColorRes(R.color.colorRed)
                                .setIcon(R.drawable.ic_directions_run)
                                .setTitle("Perform Challenge")
                                .setMessage("Accept "+model.getChallenger()+"'s Challenge?")
                                .setPositiveButton("Perform", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       Intent intent=new Intent(getContext(), WorkoutActivity.class);
                                       intent.putExtra("challengeName",model.getChallengeName());
                                       startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("userChallenges").child(key);
                                        ref.removeValue();

                                        Toast.makeText(getContext(), "Challenged dismissed!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                });

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
        public void setImage(Context ctx, String image)
        {
             ImageView challenge_image=(ImageView)itemView.findViewById(R.id.ch_image);

             challenge_image.setImageResource(R.drawable.header);
        }
        public void setChallenger(String challenger_name)
        {
            TextView challenger=(TextView)mView.findViewById(R.id.challenger_name);
            challenger.setText(challenger_name);
        }


    }
}
