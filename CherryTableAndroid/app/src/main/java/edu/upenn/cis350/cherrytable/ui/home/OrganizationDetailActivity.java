package edu.upenn.cis350.cherrytable.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Organization;
import edu.upenn.cis350.cherrytable.data.model.Request;

public class OrganizationDetailActivity extends AppCompatActivity {
    private Organization mOrg;
    private Request[] mRequests;

    private RecyclerView mOrgRequestsView;
    private RequestsRecyclerViewAdapter mAdapter;

    private class getAllRequestsTask extends AsyncTask<String, Void, Request[]> {
        private String host;
        private int port;

        public getAllRequestsTask(String host, int port) {
            this.host = host;
            this.port = port;
        }

        protected Request[] doInBackground(String... names) {
            String name = names[0];
            if (name == null) {
                throw new IllegalArgumentException();
            }

            // generate url
            StringBuilder sb = new StringBuilder();
            sb.append("http://").append(host).append(":").append(port);
            sb.append("/requests?");
            sb.append("organization=").append(name);
            Log.i("url:", sb.toString());

            HttpURLConnection conn = null;
            URL url = null;
            int responseCode = 0;
            try {
                url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // get response code
                responseCode = conn.getResponseCode();
            } catch (Exception e) {
                // cannot connect to server
                Log.e("exception:", e.toString());
                throw new IllegalStateException();
            }
            // define return value
            List<Request> ret = new ArrayList<>();
            // handle response
            if (responseCode != 200) {
                // return empty array if bad response
                conn.disconnect();
                return new Request[0];
            } else {
                try {
                    Scanner in = new Scanner(conn.getInputStream());
                    // read the next line of response body
                    while (in.hasNext()) {
                        String line = in.nextLine();
                        JSONArray reqs = new JSONArray(line);
                        for (int i = 0; i < reqs.length(); i++) {
                            JSONObject req = reqs.getJSONObject(i);
                            String title = req.getString("title");
                            String desc = req.getString("description");
                            String org = req.getString("organization");
                            int target = req.getInt("target");
                            int fulfilled = req.getInt("fulfilled");
                            Request newReq = new Request(title, desc, org, target, fulfilled);
                            ret.add(newReq);
                        }
                    }
                    in.close();
                } catch (Exception e) {
                    // error parsing json
                    Log.e("exception:", e.toString());
                    conn.disconnect();
                    return new Request[0];
                }
            }
            // end http connection
            conn.disconnect();
            return ret.toArray(new Request[ret.size()]);
        }

        protected void onPostExecute(Request[] reqs) {
            mRequests = reqs;
            updateRequests();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_detail);

        // Create a new organization from intent
        mOrg = new Organization();
        mOrg.setName(getIntent().getStringExtra("name"));
        mOrg.setAddress(getIntent().getStringExtra("address"));
        mOrg.setContact(getIntent().getStringExtra("contact"));
        mOrg.setDesc(getIntent().getStringExtra("desc"));
        mOrg.setRequests((String[]) getIntent().getSerializableExtra("requests"));

        // Initialize mRequests to empty array for binding
        mRequests = new Request[0];

        // Bind recycler view
        mOrgRequestsView = findViewById(R.id.orgRequests);
        mOrgRequestsView.setHasFixedSize(true);
        mOrgRequestsView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RequestsRecyclerViewAdapter(mOrg.getRequests(), mRequests, this);
        mOrgRequestsView.setAdapter(mAdapter);

        // Update info
        updateOrgInfo();
        new getAllRequestsTask(this.getResources().getString(R.string.test_ip), 8080).execute(mOrg.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        new getAllRequestsTask(this.getResources().getString(R.string.test_ip), 8080).execute(mOrg.getName());
    }

    private void updateOrgInfo() {
        TextView orgName = findViewById(R.id.orgName);
        orgName.setText(mOrg.getName());
        TextView orgAddress = findViewById(R.id.orgAddress);
        orgAddress.setText(mOrg.getAddress());
        TextView orgContact = findViewById(R.id.orgContact);
        orgContact.setText(mOrg.getContact());
        TextView orgDesc = findViewById(R.id.orgDesc);
        orgDesc.setText(mOrg.getDesc());
        orgDesc.setMovementMethod(new ScrollingMovementMethod());
    }

    private void updateRequests() {
        Toast.makeText(this, "Found " + Integer.toString(mRequests.length) + " requests", Toast.LENGTH_SHORT).show();
        mAdapter.setRequests(mRequests);
    }

}
