package edu.upenn.cis350.cherrytable.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.ui.search.Model;

public class DonationDetailAdapter extends PagerAdapter {

    private ArrayList<Model> models;
    private LayoutInflater inflater;
    private Context context;

    public DonationDetailAdapter(ArrayList<Model> model, Context context){
        this.models=model;
        this.context=context;
    }
    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.donation_details_item, container,false);

        ImageView img = view.findViewById(R.id.DonationItemImage);
        TextView title = view.findViewById(R.id.donation_title);
        TextView desc = view.findViewById(R.id.donation_desc);
        img.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDescription());
        container.addView(view,0);
        container.addView(view, 1);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
