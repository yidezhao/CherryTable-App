package edu.upenn.cis350.cherrytable.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class Donation {
    int Donation_id;
    int amount;
    Map<String, Date> statusTrack;
    String title;
    String description;
    public static ArrayList<String> statuses = new ArrayList<String>(
            Arrays.asList("Your payment donated",
                    "Received by Organization",
                    "Items shipped",
                    "Items delivering",
                    "Donation completed")); ;


    public Donation(int donation_id) {
        Donation_id = donation_id;
        this.title="Donation" + donation_id;
        this.statusTrack = new HashMap<String,Date> ();
        statusTrack.put ("Your payment donated",null);
        statusTrack.put ("Received by Organization",null);
        statusTrack.put ("Items shipped",null);
        statusTrack.put ("Items delivering",null);
        statusTrack.put ("Donation completed",null);
    }

    public Donation(int amount, int donation_id, String description, Map<String, Date> statusTrack) {
        this.amount = amount;
        this.title="Donation" + donation_id;
        Donation_id = donation_id;
        this.statusTrack = statusTrack;
        this.description = description;
    }

    public Donation(int donation_id, Map<String, Date> statusTrack) {
        this.title="Donation" + donation_id;
        Donation_id = donation_id;
        this.statusTrack = statusTrack;
    }

    public Donation(int donation_id, String description, Map<String, Date> statusTrack) {
        this.title="Donation" + donation_id;
        Donation_id = donation_id;
        this.statusTrack = statusTrack;
        this.description = description;
    }

    // only for testing
//    public Donation(int donation_id, String status) {
//        Donation_id = donation_id;
//        statusTrack = new HashMap<String,Date> ();
//        statusTrack.put ("Your payment donated",null);
//        statusTrack.put ("Received by Organization",null);
//        statusTrack.put ("Items shipped",null);
//        statusTrack.put ("Items delivering",null);
//        statusTrack.put ("Donation completed",null);
//
//        boolean flag = true;
//        for (Map.Entry<String,Date> entry : statusTrack.entrySet()) {
//            if (flag) {
//                entry.setValue(new Date());
//            }
//            if (entry.getKey().equals(status)) {
//                flag = false;
//            }
//        }
//    }
    public String getTitle(){return this.title;}
    public int getDonation_id() {
        return Donation_id;
    }
    public int getAmount() {return amount;}
    public String getDescription() {return description;}

    public String getStatus() {
        String status = "";
        for (Map.Entry<String, Date> entry : statusTrack.entrySet()) {
            if (entry.getValue() != null) {
                status = entry.getKey();
            }
        }
        return status;
    }

    public Map<String,Date> getStatusTrack() {
        return statusTrack;
    }

    public void updateStatus(String newStatus) {
        statusTrack.put(newStatus, new Date());
    }


}

