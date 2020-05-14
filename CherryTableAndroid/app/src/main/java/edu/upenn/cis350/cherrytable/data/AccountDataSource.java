package edu.upenn.cis350.cherrytable.data;


import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import edu.upenn.cis350.cherrytable.data.model.UserAccount;

public class AccountDataSource {
    private String host;
    private int port;

    public AccountDataSource() {
        // use Node Express defaults
        host = "10.0.2.2"; // IP of my computer
        port = 3000;
    }

    public AccountDataSource(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Result<UserAccount> getCurrentProfile(String userId, String username) throws IOException, ParseException {

        URL url = new URL("http://10.0.2.2:8080/getCurrentProfile?name=Matthew");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line1 = bufferedReader.readLine();
        JSONParser parser = new JSONParser();

        org.json.simple.JSONArray json = new org.json.simple.JSONArray();
        try {
            json = (org.json.simple.JSONArray) parser.parse(line1);
        } catch (Exception e) {
            System.out.print("Unreadable parse JSON ");
            System.exit(0);
        }

        Iterator iter = json.iterator();
        while (iter.hasNext()) {
            org.json.simple.JSONObject curr = (org.json.simple.JSONObject) iter.next();
        }

        return null;
    }
}
