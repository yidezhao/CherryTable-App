package edu.upenn.cis350.cherrytable.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.MapView;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Organization;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.constraintlayout.widget.Constraints.TAG;

// TODO: search and mark global location

public class HomeFragment extends Fragment implements
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback{
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MapView mMapView;
    private GoogleMap mMap;
    // private FragmentContainerView mFragContainer;

    private CameraPosition mCameraPosition;
    private Location mLastKnownLocation;
    private Organization[] mNearbyOrgs;

    // Default location and zoom to use when location permission is not granted
    private final LatLng mDefaultLocation = new LatLng(39.952583, -75.165222); // Philly
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Keys for storing activity state.
    private static final String CAMERA_POSITION_KEY = "camera_position";
    private static final String LOCATION_KEY = "location";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private class getNearbyOrgsTask extends AsyncTask<LatLng, Void, Organization[]> {
        private String host;
        private int port;

        public getNearbyOrgsTask(String host, int port) {
            this.host = host;
            this.port = port;
        }

        protected Organization[] doInBackground(LatLng... locs) {
            LatLng loc = locs[0];
            if (loc == null) {
                throw new IllegalArgumentException();
            }

            // generate url
            StringBuilder sb = new StringBuilder();
            sb.append("http://").append(host).append(":").append(port);
            sb.append("/organizationsNearLocation?");
            sb.append("lat=").append(Double.toString(loc.latitude)).append("&long=").append(Double.toString(loc.longitude));
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
            List<Organization> ret = new ArrayList<>();
            // handle response
            if (responseCode != 200) {
                // return empty array if bad response
                conn.disconnect();
                return new Organization[0];
            } else {
                try {
                    Scanner in = new Scanner(conn.getInputStream());
                    // read the next line of response body
                    while (in.hasNext()) {
                        String line = in.nextLine();
                        JSONArray orgs = new JSONArray(line);
                        for (int i = 0; i < orgs.length(); i++) {
                            JSONObject org = orgs.getJSONObject(i);
                            String name = org.getString("name");
                            String address = org.getString("address");
                            String contact = org.getString("contact");
                            String desc = "";
                            if (org.has("description")) {
                                // Log.e("org", name + "has description");
                                desc = org.getString("description");
                            }
                            // parse coordinates
                            JSONArray coords = org.getJSONObject("coords").getJSONArray("coordinates");
                            LatLng coordsLatLng = new LatLng(coords.getDouble(1), coords.getDouble(0));
                            // parse requests
                            JSONArray requestsJSON = org.getJSONArray("requests");
                            String[] requests = new String[requestsJSON.length()];
                            for (int j = 0; j < requestsJSON.length(); j++) {
                                requests[j] = requestsJSON.getString(j);
                            }
                            Organization newOrg = new Organization(name, address, contact, desc, coordsLatLng, requests);
                            ret.add(newOrg);
                        }
                    }
                    in.close();
                } catch (Exception e) {
                    // error parsing json
                    Log.e("exception:", e.toString());
                    conn.disconnect();
                    return new Organization[0];
                }
            }
            // end http connection
            conn.disconnect();
            return ret.toArray(new Organization[ret.size()]);
        }

        protected void onPostExecute(Organization[] orgs) {
            mNearbyOrgs = orgs;
            showNearbyOrgs();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        homeViewModel = new ViewModelProvider(navController.getViewModelStoreOwner(R.id.mobile_navigation))
                .get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mMapView = root.findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Initialize places
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getResources().getString(R.string.api_key));
        }
        // Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentByTag("autocomplete_fragment_tag");
        // Specify the types of place data to return
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                showOrgsNearLocation(place.getName(), new LatLng(place.getLatLng().latitude, place.getLatLng().longitude));
                Log.i(TAG, "Place: " + place.getName() + ", " );
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "Error selecting place: " + status);
            }
        });

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnInfoWindowClickListener(this);
        if (!hasLocationPermissions()) {
            // requestLocationPermission() calls updateUI and updateMapView
            requestLocationPermission();
        } else {
            // Update UI to display my location
            updateUI();
            // Update map to set markers
            updateMap();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if ((Integer) marker.getTag() == 0) {
            Toast.makeText(getActivity(), "Clicked on search result", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent i = new Intent(getActivity(), OrganizationDetailActivity.class);
            Organization org = null;
            for (Organization o : mNearbyOrgs) {
                if (o.getName().equals(marker.getTitle())) {
                    org = o;
                }
            }
            i.putExtra("name", org.getName());
            i.putExtra("address", org.getAddress());
            i.putExtra("contact", org.getContact());
            i.putExtra("desc", org.getDesc());
            i.putExtra("requests", org.getRequests());
            startActivity(i);
        }
    }

    private void showNearbyOrgs () {
        if (mNearbyOrgs == null) {
            return;
        }
        for (Organization o : mNearbyOrgs) {
            Marker orgMarker = mMap.addMarker(new MarkerOptions().position(o.getCoords()).
                    title(o.getName()).snippet("Click for details"));
            orgMarker.setTag(1);
        }
    }

    private void queryAndShowNearbyOrgs(LatLng loc) {
        if (loc == null) {
            return;
        }
        new getNearbyOrgsTask(getActivity().getResources().getString(R.string.test_ip), 8080).execute(loc);
    }

    private void showOrgsNearLocation(String name, LatLng loc) {
        mMap.clear();
        Marker current = mMap.addMarker(new MarkerOptions().position(loc).title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        current.setTag(0);
        current.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM));
        queryAndShowNearbyOrgs(loc);
    }

    private void showOrgsNearDevice() {
        if (mMap == null) {
            return;
        }
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                    } else {
                        Log.d(TAG, "Task result is null. Using lastKnownLocation");
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.e(TAG, "Exception: %s", task.getException());
                }

                // Set camera position to lastKnownLocation or default location
                if (mLastKnownLocation != null) {
                    LatLng loc = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM));
                    queryAndShowNearbyOrgs(loc);
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    queryAndShowNearbyOrgs(mDefaultLocation);
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateMap() {
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
            mCameraPosition = null;
            if(mNearbyOrgs != null) {
                showNearbyOrgs();
            }
        } else {
            showOrgsNearDevice();
        }
    }

    private void updateUI() {
        if (mMap == null) {
            return;
        }
        try {
            boolean permissionGranted = hasLocationPermissions();
            mMap.setPadding(0, dp2Px(70),0,0);
            mMap.setMyLocationEnabled(permissionGranted);
            mMap.getUiSettings().setMyLocationButtonEnabled(permissionGranted);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Get permission: ACCESS_FINE_LOCATION");
                    updateUI();
                    updateMap();
                }
            }
        }
    }

    // Restore states
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle mapSavedInstanceState = homeViewModel.getMapSavedInstanceState();
        if (mapSavedInstanceState != null) {
            mLastKnownLocation = mapSavedInstanceState.getParcelable(LOCATION_KEY);
            mCameraPosition = mapSavedInstanceState.getParcelable(CAMERA_POSITION_KEY);
        }
        Organization[] nearbyOrgs = homeViewModel.getNearbyOrgs();
        if (nearbyOrgs != null) {
            mNearbyOrgs = nearbyOrgs;
        }
    }

    // Store states
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle outState = new Bundle();
        if (mMap != null) {
            outState.putParcelable(CAMERA_POSITION_KEY, mMap.getCameraPosition());
            outState.putParcelable(LOCATION_KEY, mLastKnownLocation);
        }
        homeViewModel.setMapSavedInstanceState(outState);
        if (mNearbyOrgs != null) {
            homeViewModel.setNearbyOrgs(mNearbyOrgs);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    // Convert dp unit to pixel unit
    private int dp2Px(int dp)
    {
        float scale = getActivity().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
