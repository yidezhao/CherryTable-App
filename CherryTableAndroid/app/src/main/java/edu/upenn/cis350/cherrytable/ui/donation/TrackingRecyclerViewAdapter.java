package edu.upenn.cis350.cherrytable.ui.donation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Donation;

import static androidx.recyclerview.widget.RecyclerView.*;

public class TrackingRecyclerViewAdapter extends RecyclerView.Adapter<TrackingRecyclerViewAdapter.TimeLineViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String>  mNames;
    private Map<String,Date> mstatusTrack;
    private Context mContext;

    public TrackingRecyclerViewAdapter(Context context, ArrayList<String> names, Map<String, Date> statusTrack) {
        mNames = names;
        mContext = context;
        mstatusTrack = statusTrack;
    }


    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        String name = Donation.statuses.get(position);
        holder.status.setText(name);
        Date date = mstatusTrack.get(name);
        if (date != null) {
            holder.date.setText(date.toString());
        } else {
            holder.date.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    /*
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text1);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
    */

    public class TimeLineViewHolder extends ViewHolder {
        public TimelineView mTimelineView;
        RelativeLayout parentLayout;
        TextView status;
        TextView date;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            mTimelineView = itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }
}

