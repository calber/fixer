package org.calber.fixer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 27/4/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private final Context context;
    private final OnItemSelected listener;
    List<Product> devicelist = new ArrayList<>();


    public ProductAdapter(Context context, OnItemSelected listener) {
        this.context = context;
        this.listener = listener;
    }

    public List<Product> getDevicelist() {
        return devicelist;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder h, int position) {
    }

    @Override
    public int getItemCount() {
        return devicelist.size();
    }

    public void add(Product device) {
        devicelist.add(device);
        notifyItemInserted(devicelist.size() - 1);
    }

    public void remove(Product device) {
        int idx = devicelist.indexOf(device);
        devicelist.remove(idx);
        notifyItemRemoved(idx);
    }

    public void update(Product device) {
        int idx = devicelist.indexOf(device);
        devicelist.set(idx, device);
        notifyItemChanged(idx);
    }

    public void load(List<Product> devices) {
        devicelist = devices;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        devicelist.remove(position);
        notifyItemRemoved(position);
        listener.onDataRemoved(null, position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Product device;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onDataReady(device, devicelist.indexOf(device));
        }

    }
}

