package org.calber.fixer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.calber.fixer.models.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by calber on 27/4/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final Context context;
    private final OnItemSelected listener;
    List<Product> productList = new ArrayList<>();
    private String base = "GBP";


    public ProductAdapter(Context context, OnItemSelected listener) {
        this.context = context;
        this.listener = listener;
    }

    public List<Product> getProductList() {
        return productList;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder h, int position) {
        h.product = productList.get(position);

        h.name.setText(String.format("%d %s of %s", h.product.quantity, h.product.unit, h.product.name));
        h.price.setText(String.format("item price %.2f %s, subtotal %.2f %s",h.product.unitprice, base,h.product.quantity * h.product.unitprice,base));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void add(Product product) {
        if(productList.contains(product)) {
            final int indexOf = productList.indexOf(product);
            Product existing = productList.get(indexOf);
            existing.quantity += product.quantity;
            notifyItemChanged(indexOf);
            return;
        }
        productList.add(new Product(product.name,product.unit,product.unitprice,product.quantity));
        notifyItemInserted(productList.size() - 1);
    }

    public void remove(Product product) {
        int idx = productList.indexOf(product);
        productList.remove(idx);
        notifyItemRemoved(idx);
    }

    public void update(Product product) {
        int idx = productList.indexOf(product);
        productList.set(idx, product);
        notifyItemChanged(idx);
    }

    public void load(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    public void setBase(String base) {
        this.base = base;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.price)
        TextView price;

        Product product;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this,view);
        }

        @Override
        public void onClick(View v) {
            listener.onDataReady(product, productList.indexOf(product));
        }

    }
}

