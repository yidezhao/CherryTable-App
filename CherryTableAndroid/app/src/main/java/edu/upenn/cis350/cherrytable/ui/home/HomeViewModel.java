package edu.upenn.cis350.cherrytable.ui.home;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import edu.upenn.cis350.cherrytable.data.model.Organization;

public class HomeViewModel extends ViewModel {

    private Bundle mapSavedInstanceState;
    private Organization[] nearbyOrgs;

    public HomeViewModel() {
        mapSavedInstanceState = null;
        nearbyOrgs = null;
    }

    void setMapSavedInstanceState(Bundle b) { mapSavedInstanceState = b; }

    void setNearbyOrgs(Organization[] orgs) { nearbyOrgs = orgs; }

    Bundle getMapSavedInstanceState() { return mapSavedInstanceState; }

    Organization[] getNearbyOrgs() { return nearbyOrgs; }
}