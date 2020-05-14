package edu.upenn.cis350.cherrytable.ui.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Donation;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Donation>  donations;
    private ArrayList<Donation>  donationsAll;
    private Context context;
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
    public RecyclerViewAdapter(Context context , ArrayList<Donation> donations ) {
        this.donations = donations;
        this.context=context;
        this.donationsAll = new ArrayList<>(donations);
        Log.d(TAG, "recyclerview donations"+donationsAll.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ViewHolder holder = new ViewHolder(view,listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        final Donation d = donations.get(position);
        final Map<String, Date> s = d.getStatusTrack();
        final int id = d.getDonation_id();
        holder.name.setText(d.getTitle());
        holder.details.setText("details");


    }

    @Override
    public int getItemCount() {
        return donations.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
     Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Donation> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(donationsAll);
            } else {
                for (Donation d : donationsAll) {
                    if (d.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(d);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();

            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            donations.clear();
            if (results.values!=null) {

                donations.addAll((Collection<? extends Donation>) results.values);
            }
            notifyDataSetChanged();
        }

    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView details;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.textView2);
            details = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

