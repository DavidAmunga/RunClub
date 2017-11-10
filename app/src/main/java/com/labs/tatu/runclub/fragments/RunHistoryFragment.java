package com.labs.tatu.runclub.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.labs.tatu.runclub.LocationRunActivity;
import com.labs.tatu.runclub.R;
import com.labs.tatu.runclub.SingleEventActivity;
import com.labs.tatu.runclub.WorkoutActivity;
import com.labs.tatu.runclub.model.Award;
import com.labs.tatu.runclub.model.Challenge;
import com.labs.tatu.runclub.model.Event;
import com.labs.tatu.runclub.model.Location;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amush on 18-Sep-17.
 */

public class RunHistoryFragment extends Fragment {
    private static final String TAG = "RunHistoryFragment";

    private TextView txtAwards, txtLocations, txtEvents, txtChallenges, txtPoints;

    private RecyclerView mList;
    private DatabaseReference mDatabase;
    MaterialSpinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_history_layout, container, false);

        txtAwards = (TextView) view.findViewById(R.id.txt_awards);
        txtLocations = (TextView) view.findViewById(R.id.txt_locations);
        txtEvents = (TextView) view.findViewById(R.id.txt_events);
        txtChallenges = (TextView) view.findViewById(R.id.txt_challenges);
        txtPoints = (TextView) view.findViewById(R.id.txt_points);


        mList = (RecyclerView) view.findViewById(R.id.run_list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));

        spinner = (MaterialSpinner) view.findViewById(R.id.spinner);
        spinner.setTextColor(getResources().getColor(android.R.color.black));
        spinner.setText("COMPLETED RUNS");
        spinner.setBackgroundColor(Color.parseColor("#c3c3c3"));
        spinner.setItems("My Locations", "My Events", "My Challenges", "My Awards");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (item.equals("My Challenges")) {
                    setChallengeList();
                } else if (item.equals("My Events")) {
                    setEventsList();

                } else if (item.equals("My Locations")) {
                    setLocationsList();

                } else {
                    setAwardsList();
                }


            }
        });

        count_no();


        return view;
    }

    private void setAwardsList() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Awards");

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

        mList.setAdapter(firebaseRecyclerAdapter);

    }


    private void setLocationsList() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userLocations");

        FirebaseRecyclerAdapter<Location, LocationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Location, LocationViewHolder>(
                Location.class,
                R.layout.location_row,
                LocationViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(LocationViewHolder viewHolder, final Location model, final int position) {

                viewHolder.setLocationName(model.getLocationName());
                viewHolder.setLocationImage(getContext(), model.getLocationPhotoUrl());
                viewHolder.setLocationDistance(model.getLocationDistance());
                Log.d(TAG, "Photo Url: " + model.getLocationPhotoUrl());
                Log.d(TAG, "Location Name: " + model.getLocationName());
                Log.d(TAG, "Location Distance: " + model.getLocationDistance());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "You clicked on " + position);
                        Intent intent = new Intent(getContext(), LocationRunActivity.class);
                        intent.putExtra("locName", model.getLocationName());
                        intent.putExtra("locDistance", model.getLocationDistance());
                        intent.putExtra("fromLat", model.getFromLat());
                        intent.putExtra("fromLong", model.getFromLong());
                        intent.putExtra("locGoal", model.getLocationGoal());
                        intent.putExtra("toLat", model.getToLat());
                        intent.putExtra("toLong", model.getToLong());
                        Log.d(TAG, "fromLat: " + model.getFromLat());
                        startActivity(intent);

                    }
                });

            }
        };
        mList.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void setEventsList() {


        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userEvents");

        FirebaseRecyclerAdapter<Event, RunHistoryFragment.EventsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, RunHistoryFragment.EventsViewHolder>(
                Event.class,
                R.layout.event_row,
                RunHistoryFragment.EventsViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(RunHistoryFragment.EventsViewHolder viewHolder, final Event model, final int position) {
                viewHolder.setEventsName(model.getEventName());
                Log.d(TAG, "populateViewHolder: " + model.getEventName());
                viewHolder.setEventsDate(model.getEventDate());
                viewHolder.setEventsTime(model.getEventTime());
                viewHolder.setEventsAttending(model.getEventAttending());


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: You clicked on " + position);
                        Intent intent = new Intent(getContext(), SingleEventActivity.class);
                        intent.putExtra("eventName", model.getEventName());
                        startActivity(intent);
                    }
                });

            }
        };
        mList.setAdapter(firebaseRecyclerAdapter);


    }

    public void setChallengeList() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userChallenges");

        FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder>(
                Challenge.class,
                R.layout.challenge_row,
                ChallengeViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(ChallengeViewHolder viewHolder, final Challenge model, int position) {
                viewHolder.setChallengeName(model.getChallengeName());
                viewHolder.setImage(getContext(), model.getChallengeImage());
                viewHolder.setChallenger(model.getChallenger());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LovelyStandardDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColorRes(R.color.colorRed)
                                .setIcon(R.drawable.ic_directions_run)
                                .setTitle("Perform Challenge")
                                .setMessage("Accept " + model.getChallenger() + "'s Challenge?")
                                .setPositiveButton("Perform", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), WorkoutActivity.class);
                                        intent.putExtra("challengeName", model.getChallengeName());
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Dismiss", null)
                                .show();
                    }
                });

            }
        };

        mList.setAdapter(firebaseRecyclerAdapter);
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


    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChallengeViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setChallengeName(String title) {
            TextView challenge_name = (TextView) mView.findViewById(R.id.ch_title);
            challenge_name.setText(title);
        }

        public void setImage(Context ctx, String image) {
            ImageView challenge_image = (ImageView) itemView.findViewById(R.id.ch_image);

            challenge_image.setImageResource(R.drawable.header);
        }

        public void setChallenger(String challenger_name) {
            TextView challenger = (TextView) mView.findViewById(R.id.challenger_name);
            challenger.setText(challenger_name);
        }


    }


    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public EventsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setEventsName(String name) {
            TextView eventsName = (TextView) mView.findViewById(R.id.event_name);
            eventsName.setText(name);
        }

        public void setEventsDate(String date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date firstDate = sdf.parse(date);
                SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy");
                String eventDate = parseFormat.format(firstDate);

                TextView eventsDate = (TextView) mView.findViewById(R.id.event_date);
                eventsDate.setText(eventDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        public void setEventsTime(String time) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

            try {
                Date firstTime = sdf.parse(time);
                String eventTime = sdf.format(firstTime);

                TextView eventsTime = (TextView) mView.findViewById(R.id.event_time);
                eventsTime.setText(eventTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void setEventsAttending(String att) {
            TextView eventAtt = (TextView) mView.findViewById(R.id.event_attending);
            eventAtt.setText("GOING");
        }

//        public void setEventsImage(final Context ctx, final String image)
//        {
//            final ImageView events_image=(ImageView)itemView.findViewById(R.id.event_image);
//            Picasso
//                    .with(ctx)
//                    .load(image)
//                    .networkPolicy(NetworkPolicy.OFFLINE)
//                    .into(events_image, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError() {
//                            Picasso.with(ctx).load(image).into(events_image);
//                        }
//                    });
//        }
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public LocationViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setLocationName(String title) {
            TextView location_name = (TextView) mView.findViewById(R.id.loc_title);
            location_name.setText(title);

        }

        public void setLocationDistance(int distance) {
            TextView locationDistance = (TextView) mView.findViewById(R.id.loc_distance);
            if (distance > 1000) {
                locationDistance.setText(String.valueOf(distance / 1000) + " km");
            } else {
                locationDistance.setText(String.valueOf(distance) + " m");

            }


        }

        public void setLocationImage(final Context ctx, final String image) {
            final ImageView loc_image = (ImageView) mView.findViewById(R.id.loc_image);


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

    private void count_no() {
        //        Count No of Children in Nodes
        //        Points

        DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        pointsRef.keepSynced(true);
        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                        txtPoints.setText(snapshot.child("userPoints").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        Awards

        DatabaseReference awardsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        awardsRef.keepSynced(true);
        awardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                        String userID = snapshot.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("userAwards");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int size = (int) dataSnapshot.getChildrenCount();
                                Log.d(TAG, "Child Size " + size);
                                txtAwards.setText(String.valueOf(size));
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

//        Locations
        DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("Users");
        locRef.keepSynced(true);
        locRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                        String userID = snapshot.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("userLocations");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int size = (int) dataSnapshot.getChildrenCount();
                                Log.d(TAG, "Child Size " + size);
                                txtLocations.setText(String.valueOf(size));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        txtPoints.setText(snapshot.child("userPoints").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //        Events


        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        eventsRef.keepSynced(true);
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                        String userID = snapshot.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("userEvents");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int size = (int) dataSnapshot.getChildrenCount();
                                Log.d(TAG, "Child Size " + size);
                                txtEvents.setText(String.valueOf(size));
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


        //        Challenges
        DatabaseReference challengeRef = FirebaseDatabase.getInstance().getReference().child("Users");
        challengeRef.keepSynced(true);
        challengeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userEmail").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                        String userID = snapshot.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("userChallenges");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int size = (int) dataSnapshot.getChildrenCount();
                                Log.d(TAG, "Child Size " + size);
                                txtChallenges.setText(String.valueOf(size));
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


    }


}
