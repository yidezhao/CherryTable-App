package edu.upenn.cis350.cherrytable.ui.donation;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Donation;

import static android.os.Build.*;
import static android.os.Build.VERSION_CODES.*;



public class DonationFragment extends Fragment {

    private DonationViewModel dashboardViewModel;

    private RecyclerView mRecyclerView;
    private ArrayList<Donation> mNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //trade off with responsiveness which definitely works for our app
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dashboardViewModel = new ViewModelProvider(this).get(DonationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_donation, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        try {
            initText();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void initText() throws IOException, ParseException {
        URL url = new URL("http://" + getContext().getResources().getString(R.string.test_ip) + ":8080/getAllDonations");
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        httpConn.setRequestMethod("GET");
        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line1 = bufferedReader.readLine();
        JSONParser parser = new JSONParser();
        JSONArray json = new JSONArray();
        try {
            json = (JSONArray) parser.parse(line1);
        } catch (Exception e) {
            System.out.print("Unreadable parse JSON ");
            System.exit(0);
        }
        Iterator iter = json.iterator();
        while (iter.hasNext()) {
            JSONObject curr = (JSONObject) iter.next();
            //get the amount
            int amount = (int) (long) curr.get("amount");
            //get the id
            String ids = (String) curr.get("id");
            int id = Integer.parseInt(ids);
            //get the description
            String description = (String) curr.get("description");
            //get the statusTrack
            JSONArray status = (JSONArray) curr.get("info");
            Iterator statusiter = status.iterator();
            Map<String,Date> statusTrack = new HashMap<String,Date>();
            while (statusiter.hasNext()) {
                JSONObject statuscurr = (JSONObject) statusiter.next();
                String statusd =  (String) statuscurr.get("date");
                Date statusdate = new SimpleDateFormat("yyyy-MM-dd").parse(statusd);
                String stage = (String) statuscurr.get("stage");
                if (stage.equals("Donor payment confirmed")) {
                    stage = "Your payment donated";
                } else if (stage.equals("Organization received")) {
                    stage = "Received by Organization";
                } else if (stage.equals("Shipped")) {
                    stage = "Items shipped";
                } else if (stage.equals("Delivering")) {
                    stage = "Items delivering";
                } else if (stage.equals("Completed")) {
                    stage = "Donation completed";
                }
                statusTrack.put(stage, statusdate);
            }
            Donation donation = new Donation(amount, id, description, statusTrack);
            mNames.add(donation);
        }


        initRecyclerView();
    }

    private Donation createDonation(int i, Map<String, Date> status) {
        Donation d = new Donation(i, status);
        return d;
    }


    private void initRecyclerView(){
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mNames);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
