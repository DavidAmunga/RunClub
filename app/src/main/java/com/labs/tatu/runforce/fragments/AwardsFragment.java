package com.labs.tatu.runforce.fragments;

import android.content.Context;
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
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.labs.tatu.runforce.R;
import com.labs.tatu.runforce.model.Award;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amush on 18-Sep-17.
 */

public class AwardsFragment extends Fragment {
    private static final String TAG = "AwardsFragment";


    private CircleImageView userBadgeImage;
    private ArcProgress arcUserLevel;
    private RecyclerView mAwardsList;

    TextView txtUserLevel, txtUserPoints;


    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Award, AwardsViewHolder> firebaseRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_awards_layout, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Awards");
        mDatabase.keepSynced(true);
        mDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
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

        arcUserLevel = (ArcProgress) view.findViewById(R.id.user_level);
        txtUserLevel = (TextView) view.findViewById(R.id.txtRunLevel);
        userBadgeImage = (CircleImageView) view.findViewById(R.id.imgUserLevel);

        mAwardsList = (RecyclerView) view.findViewById(R.id.awards_list);
        //mLocationsList.setHasFixedSize(true);
        mAwardsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference refUserLevel = FirebaseDatabase.getInstance().getReference().child("Users");
        refUserLevel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(email)) {


                        final String userLevel = snapshot.child("userLevel").getValue().toString();
                        final String userPoints = snapshot.child("userPoints").getValue().toString();

                        DatabaseReference refMaxLevel = FirebaseDatabase.getInstance().getReference("UserLevel").child(userLevel);
                        refMaxLevel.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String maxPoints = dataSnapshot.getValue().toString();

                                txtUserLevel.setText(userLevel + " Level");
                                arcUserLevel.setBottomText(userPoints + "/" + maxPoints + " Points");
                                arcUserLevel.setMax(Integer.parseInt(maxPoints));
                                arcUserLevel.setProgress(Integer.parseInt(userPoints));

                                setUserBadgeLevel(userLevel);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void setUserBadgeLevel(String userLevel) {
        switch (userLevel) {
            case "Tera":
                userBadgeImage.setImageDrawable(getResources().getDrawable(R.drawable.tera));
                break;


        }

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Award, AwardsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Award, AwardsViewHolder>(
                Award.class,
                R.layout.award_row,
                AwardsViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(AwardsViewHolder viewHolder, Award model, int position) {
                viewHolder.setAwardName(model.getAwardName());
                viewHolder.setAwardDesc(model.getAwardDescription());
                viewHolder.setAwardImage(getActivity(), model.getAwardImage());

                Log.d(TAG, "AwardImage" + model.getAwardImage());
                Log.d(TAG, "AwardName" + model.getAwardName());
            }
        };

        mAwardsList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class AwardsViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public AwardsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setAwardName(String name) {
            TextView awardName = (TextView) mView.findViewById(R.id.txtAwardName);
            awardName.setText(name);
        }

        public void setAwardDesc(String desc) {
            TextView awardDesc = (TextView) mView.findViewById(R.id.txtAwardDesc);
            awardDesc.setText(desc);

        }

        public void setAwardImage(final Context ctx, final String image) {
            final ImageView award_image = (ImageView) mView.findViewById(R.id.awardImage);


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
