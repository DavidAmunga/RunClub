package com.labs.tatu.runclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codemybrainsout.placesearch.PlaceSearchDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private EditText add_event_name, add_event_details, add_event_type, add_event_fee, add_event_attending;
    private Button search_loc, btn_add_event, btn_event_date, btn_event_time;
    private TextView add_event_loc_name, add_event_date, add_event_time;

    private ImageButton event_image;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;

    private static final String TAG = "AddEventActivity";

    private static final int GALLERY_REQUEST = 1;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initViews();

        search_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PlaceSearchDialog placeSearchDialog = new PlaceSearchDialog.Builder(AddEventActivity.this)
                        .setHeaderImage(R.drawable.header)
                        .setHintText("Enter location name")
//                        .setHintTextColor(R.color.light_gray)
                        .setNegativeText("CANCEL")
//                        .setNegativeTextColor(R.color.gray)
                        .setPositiveText("SUBMIT")
                        .setPositiveTextColor(R.color.colorPrimary)
//                        .setLatLngBounds(BOUNDS)
                        .setLocationNameListener(new PlaceSearchDialog.LocationNameListener() {
                            @Override
                            public void locationName(String locationName) {
                                add_event_loc_name.setText(locationName);
                            }
                        })
                        .build();
                placeSearchDialog.show();
            }
        });

        mProgress = new ProgressDialog(this);

        btn_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_event();
            }
        });


        btn_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        AddEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle("Pick a Date");
                datePickerDialog.show(getFragmentManager(), "DatePicker");
            }
        });
        btn_event_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog().newInstance(
                        AddEventActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                ); //True is 24 Hrs , False is 12 hours
                timePickerDialog.setTitle("Pick a time");
                timePickerDialog.show(getFragmentManager(), "Time Picker");
            }
        });
    }

    private void add_event() {
        mProgress.setMessage("Adding Event");
        mProgress.setCancelable(false);
        mProgress.show();
        final String add_name = add_event_name.getText().toString().trim();
        final String add_details = add_event_details.getText().toString().trim();
        final String add_type = add_event_type.getText().toString().trim();
        final String add_fee = add_event_fee.getText().toString().trim();
        final String add_attending = add_event_attending.getText().toString().trim();
        final String add_loc = add_event_loc_name.getText().toString().trim();
        final String add_date = add_event_date.getText().toString().trim();
        final String add_time = add_event_time.getText().toString().trim();

        if (!TextUtils.isEmpty(add_name) && !TextUtils.isEmpty(add_details) && !TextUtils.isEmpty(add_type) && !TextUtils.isEmpty(add_fee) && !TextUtils.isEmpty(add_attending) && !TextUtils.isEmpty(add_loc)) {
            StorageReference filepath = mStorage.child("Event_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTesting") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    DatabaseReference newEvent = mDatabase.push();
                    newEvent.child("eventName").setValue(add_name);
                    newEvent.child("eventFee").setValue(add_fee);
                    newEvent.child("eventDesc").setValue(add_details);
                    newEvent.child("eventAttending").setValue(add_attending);
                    newEvent.child("eventLocation").setValue(add_loc);
                    newEvent.child("eventDate").setValue(add_date);
                    newEvent.child("eventType").setValue(add_type);
                    newEvent.child("eventTime").setValue(add_time);
                    newEvent.child("eventImage").setValue(downloadUrl.toString());

                    mProgress.dismiss();

                    startActivity(new Intent(AddEventActivity.this, EventsActivity.class));
                }
            });
        }
    }

    private void initViews() {


        add_event_name = (EditText) findViewById(R.id.add_event_name);
        add_event_details = (EditText) findViewById(R.id.add_event_details);
        add_event_type = (EditText) findViewById(R.id.add_event_type);
        add_event_fee = (EditText) findViewById(R.id.add_event_fee);
        add_event_attending = (EditText) findViewById(R.id.add_event_attending);

        add_event_loc_name = (TextView) findViewById(R.id.add_event_loc_name);
        add_event_date = (TextView) findViewById(R.id.add_event_date);
        add_event_time = (TextView) findViewById(R.id.add_event_time);

        search_loc = (Button) findViewById(R.id.search_loc);
        btn_add_event = (Button) findViewById(R.id.btn_add_event);
        btn_event_date = (Button) findViewById(R.id.btn_event_date);
        btn_event_time = (Button) findViewById(R.id.btn_event_time);

        event_image = (ImageButton) findViewById(R.id.add_event_image);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        event_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + monthOfYear + "-" + year;
        Log.d(TAG, "onDateSet: " + date);
        add_event_date.setText(date);
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + ":" + minute;
        Log.d(TAG, "onTimeSet: " + time);
        add_event_time.setText(time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            event_image.setImageURI(mImageUri);
        }
    }
}
