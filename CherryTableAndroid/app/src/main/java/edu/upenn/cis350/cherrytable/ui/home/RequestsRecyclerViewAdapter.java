package edu.upenn.cis350.cherrytable.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.model.Request;
import edu.upenn.cis350.cherrytable.ui.search.DonationDetailActivity;

public class RequestsRecyclerViewAdapter extends RecyclerView.Adapter<RequestsRecyclerViewAdapter.RequestsViewHolder> {
    private String[] mRequestIds;
    private Request[] mRequests;
    private Context mContext;
    private int mMaxWidth;

    public static final int DONATE_ACTIVITY_CODE = 1;

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView title;
        public TextView fulfilled;
        public TextView target;
        public View progress;
        public RequestsViewHolder(CardView c) {
            super(c);
            cardView = c;
            title = cardView.findViewById(R.id.card_title);
            fulfilled = cardView.findViewById(R.id.card_fulfilled);
            target = cardView.findViewById(R.id.card_target);
            progress = cardView.findViewById(R.id.card_progress);
        }
    }

    public RequestsRecyclerViewAdapter(String[] requestIds, Request[] requests, Context context) {
        mRequestIds = requestIds;
        mRequests = requests;
        mContext = context;
        mMaxWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public RequestsRecyclerViewAdapter.RequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView c = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.req_card_view, parent, false);
        RequestsViewHolder vh = new RequestsViewHolder(c);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RequestsViewHolder holder, int position) {
        Request req = mRequests[position];
        String id = mRequestIds[position];
        holder.title.setText(req.getTitle());

        int ful = req.getFulfilled();
        int tar = req.getTarget();
        holder.fulfilled.setText(Integer.toString(ful));
        holder.target.setText(Integer.toString(tar));

        float progress = Math.max(0, Math.min(1, (float) ful / (float) tar));
        int newWidth = (int) (progress * mMaxWidth);
        newWidth = newWidth == 0 ? 1 : newWidth;
        holder.progress.getLayoutParams().width = newWidth;

        holder.cardView.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("msg", "onClick: clicked on: " + req.getTitle());
                Intent intent = new Intent(mContext, DonationDetailActivity.class);
                intent.putExtra("requestId", id);
                intent.putExtra("request", req);
                ((Activity) mContext).startActivityForResult(intent, DONATE_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequests.length;
    }

    public void setRequests(Request[] newRequests) {
        this.mRequests = newRequests;
        this.notifyItemRangeInserted(0, mRequests.length - 1);
    }
}
