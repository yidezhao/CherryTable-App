package edu.upenn.cis350.cherrytable.ui.search;

import android.animation.ArgbEvaluator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Request;

public class DonationDetailActivity extends AppCompatActivity {
    ViewPager viewpager;
    DonationDetailAdapter adapter;

    ArrayList<Model> models;
    Request mRequest;
    String mRequestId;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    TextView text;

    private class makeDonationTask extends AsyncTask<String, Void, String> {
        private String host;
        private int port;

        public makeDonationTask(String host, int port) {
            this.host = host;
            this.port = port;
        }

        protected String doInBackground(String... params) {
            String donor = params[0];
            String donee = params[1];
            String requestId = params[2];
            String amount = params[3];
            if (donor == null || donee == null || requestId == null || amount == null) {
                throw new IllegalArgumentException();
            }

            // generate url
            StringBuilder sb = new StringBuilder();
            sb.append("http://").append(host).append(":").append(port);
            sb.append("/makeDonation?");
            sb.append("donor=").append(donor)
                    .append("&donee=").append(donee)
                    .append("&requestId=").append(requestId)
                    .append("&amount=").append(amount);
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
            String ret = "";
            // handle response
            if (responseCode != 200) {
                // return empty array if bad response
                conn.disconnect();
                return "";
            } else {
                try {
                    Scanner in = new Scanner(conn.getInputStream());
                    // read the next line of response body
                    while (in.hasNext()) {
                        String line = in.nextLine();
                        JSONObject idObj = new JSONObject(line);
                        if (!idObj.has("donationId")) {
                            Log.e("JSON", idObj.toString());
                            throw new IllegalStateException();
                        }
                        ret = idObj.getString("donationId");
                    }
                    in.close();
                } catch (Exception e) {
                    // error parsing json
                    Log.e("exception:", e.toString());
                    conn.disconnect();
                    return "";
                }
            }
            // end http connection
            conn.disconnect();
            return ret;
        }

        protected void onPostExecute(String donationId) {
            notifySuccess(donationId);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donation_details);

        mRequest = (Request) getIntent().getSerializableExtra("request");
        mRequestId = getIntent().getStringExtra("requestId");
        initmodels();

        adapter = new DonationDetailAdapter(models,this);
        viewpager=findViewById(R.id.viewPager);
        viewpager.setAdapter(adapter);
        viewpager.setPadding(10,10,10,0);
        text = findViewById(R.id.paragraph);
        // text.setText(mRequest.getDesc());

        Integer[] colors_temp ={
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
        };
        colors = colors_temp;
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position<adapter.getCount()-1 && position< colors.length-1){
                    int tmp =(Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]);
                    viewpager.setBackgroundColor(tmp);
                    text.setBackgroundColor(tmp);
                }
                else{
                    viewpager.setBackgroundColor(colors[colors.length-1]);
                    text.setBackgroundColor(colors[colors.length-1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initmodels() {
        models = new ArrayList<>();
        StringBuilder info = new StringBuilder();
        info.append("To: ").append(mRequest.getOrg()).append("\n")
                .append("Target: ").append(Integer.toString(mRequest.getTarget())).append("\n")
                .append("Fulfilled: ").append(Integer.toString(mRequest.getFulfilled()));
        models.add(new Model(R.drawable.grey, mRequest.getTitle(), info.toString()));
        models.add(new Model(R.drawable.sticker,"Sticker","dummy text"));
        models.add(new Model(R.drawable.poster,"Poster","dummy text"));
        models.add(new Model(R.drawable.blue,"NameCard","dummy text"));
    }

    public void makeDonation(View view) {
        EditText amountText = findViewById(R.id.donationAmount);
        String amountString = amountText.getText().toString();
        int amount = 0;
        try {
            amount = Integer.parseInt(amountString);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid string", Toast.LENGTH_SHORT).show();
            return;
        }
        int maxAmount = mRequest.getTarget() - mRequest.getFulfilled();
        if (amount > maxAmount) {
            Toast.makeText(this, "Maximum donation amount: " + maxAmount, Toast.LENGTH_SHORT).show();
            return;
        }
        new makeDonationTask(this.getResources().getString(R.string.test_ip), 8080).execute("tony", mRequest.getOrg(), mRequestId, amountString);
    }

    private void notifySuccess(String id) {
        Toast.makeText(this, "Donation made, ID:" + id, Toast.LENGTH_SHORT).show();
        finish();
    }
}
