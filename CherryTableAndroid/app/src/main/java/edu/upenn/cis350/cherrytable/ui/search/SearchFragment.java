package edu.upenn.cis350.cherrytable.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Donation;

public class SearchFragment extends Fragment {

    private DonationViewModel dashboardViewModel;

    private RecyclerView mRecyclerView;
    private ArrayList<Donation> mNames = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dashboardViewModel = new ViewModelProvider(this).get(DonationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view1);
        initText();
        adapter = new RecyclerViewAdapter(getContext(), mNames);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), DonationDetailActivity.class);
                intent.putExtra("status", (Serializable)new HashMap<String,Date>());
                intent.putExtra("id", mNames.get(position).getDonation_id());
                getContext().startActivity(intent);
            }
        });
        return root;
    }

    private void initText() {
        Map<String, Date> dummy1 = new HashMap<String,Date>();
        dummy1.put("Your payment donated", new Date());
        dummy1.put("Received by Organization", null);
        dummy1.put("Items shipped", null);
        dummy1.put("Items delivering", null);
        dummy1.put("Donation completed", null);

        Map<String, Date> dummy2 = new HashMap<String,Date>();
        dummy2.put("Your payment donated", new Date());
        dummy2.put("Received by Organization", new Date());
        dummy2.put("Items shipped", null);
        dummy2.put("Items delivering", null);
        dummy2.put("Donation completed", null);

        Map<String, Date> dummy3 = new HashMap<String,Date>();
        dummy3.put("Your payment donated", new Date());
        dummy3.put("Received by Organization", new Date());
        dummy3.put("Items shipped", new Date());
        dummy3.put("Items delivering", null);
        dummy3.put("Donation completed", null);

        Map<String, Date> dummy4 = new HashMap<String,Date>();
        dummy4.put("Your payment donated", new Date());
        dummy4.put("Received by Organization", new Date());
        dummy4.put("Items shipped", new Date());
        dummy4.put("Items delivering", new Date());
        dummy4.put("Donation completed", null);

        Map<String, Date> dummy5 = new HashMap<String,Date>();
        dummy5.put("Your payment donated", new Date());
        dummy5.put("Received by Organization", new Date());
        dummy5.put("Items shipped", new Date());
        dummy5.put("Items delivering", new Date());
        dummy5.put("Donation completed", new Date());

        mNames.add(createDonation( 1, dummy1));
        mNames.add(createDonation( 2, dummy2));
        mNames.add(createDonation( 3, dummy3));
        mNames.add(createDonation( 4, dummy4));
        mNames.add(createDonation( 5, dummy5));
        mNames.add(createDonation( 6, dummy1));
        mNames.add(createDonation( 7, dummy2));
        mNames.add(createDonation( 8, dummy3));
        mNames.add(createDonation( 9, dummy4));
        mNames.add(createDonation( 10, dummy5));
    }

    private Donation createDonation(int i, Map<String, Date> status) {
        Donation d = new Donation(i, status);
        return d;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }

        });
    }
}
