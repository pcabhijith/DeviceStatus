package com.example.hp.devicestatus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    ArrayList<String> network_status = new ArrayList<String>();
    ArrayList<String> battery_status = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();


    ArrayList<String> temper = new ArrayList<String>();
    ArrayList<String> details = new ArrayList<String>();


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView network_txt, battery_txt, time_txt;
        TextView temp_txt, detail_text;

        public ViewHolder(View view) {

            super(view);
            network_txt = (TextView) view.findViewById(R.id.network);
            battery_txt = (TextView) view.findViewById(R.id.baterry);
            time_txt = (TextView) view.findViewById(R.id.time);
            temp_txt = (TextView) view.findViewById(R.id.current_temperature_field);
            detail_text = (TextView) view.findViewById(R.id.weathr_detail);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> list, ArrayList<String> list2, ArrayList<String> list3, ArrayList<String> list5, ArrayList<String> list6) {
        network_status = list;
        battery_status = list2;
        time = list3;
        temper = list5;
        details = list6;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.network_txt.setText(network_status.get(position));

        try {
            holder.battery_txt.setText(battery_status.get(position));

        } catch (Exception e) {

        }

        holder.time_txt.setText(time.get(position));

        try {
            holder.temp_txt.setText(temper.get(position));

        } catch (Exception e) {

        }

        try {
            holder.detail_text.setText(details.get(position));

        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return network_status.size();
    }
}