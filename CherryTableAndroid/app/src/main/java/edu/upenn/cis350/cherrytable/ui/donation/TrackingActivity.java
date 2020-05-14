package edu.upenn.cis350.cherrytable.ui.donation;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.Map;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Donation;

public class TrackingActivity extends AppCompatActivity {
    private static final String TAG = "TrackingActivity";

    Map<String, Date> statusTrack;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Log.d(TAG, "onCreate: started.");
        getIncomingIntent();
        initRecyclerView();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("status")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");
            Map<String, Date> statusTrack = (Map<String, Date>)getIntent().getSerializableExtra("status");
            this.statusTrack = statusTrack;
            this.id = getIntent().getStringExtra("id");

        }

    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_track);
        TrackingRecyclerViewAdapter adapter = new TrackingRecyclerViewAdapter(this, Donation.statuses,statusTrack);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

}


