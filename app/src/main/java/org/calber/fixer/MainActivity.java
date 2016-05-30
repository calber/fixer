package org.calber.fixer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements OnItemSelected {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.checkout)
    ImageView checkout;
    @BindView(R.id.root)
    View root;

    private static ProductAdapter adapter;
    private static FixerApi api;
    private static List<String> availableCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        api = FixerApi.builder().withBase("GBP").withNetwork().withStaticProductApi().build();
        api.currencies().subscribeOn(Schedulers.io()).subscribe(results -> {
                    availableCurrencies = new ArrayList<>();
                    availableCurrencies.addAll(results);
                }, throwable -> Snackbar.make(root, "No currenciy available", Snackbar.LENGTH_INDEFINITE).show()
        );

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new

                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        list.setLayoutManager(layoutManager);

        adapter = new

                ProductAdapter(this, this);

        list.setAdapter(adapter);
        list.setHasFixedSize(true);

        fab.setOnClickListener(view -> (new

                AddProduct()

        ).

                show(getSupportFragmentManager(),

                        "buy"));
        checkout.setOnClickListener(v -> CheckOut.newInstance().

                show(getSupportFragmentManager(),

                        "checkout"));
    }

    @Override
    public void onDataReady(Object object, int position) {
        EditProduct.newInstance((Product) object).show(getSupportFragmentManager(), "edit");
    }

    @Override
    public void onDataSelection(Object object, int position) {

    }

    @Override
    public void onDataRemoved(Object object, int position) {

    }


    public static class AddProduct extends DialogFragment implements HorizontalNumberPickerListener, AdapterView.OnItemSelectedListener {
        @BindView(R.id.products)
        Spinner spinner;
        @BindView(R.id.quantity)
        HorizontalNumberPicker quantity;
        @BindView(R.id.subtotal)
        TextView subtotal;

        private int current = 0;
        final List<Product> products = api.getProductsApi().getProducts();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.select, null);

            ButterKnife.bind(this, view);

            quantity.setListener(this);
            quantity.setMinValue(1);

            spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, products));
            spinner.setOnItemSelectedListener(this);
            subtotaltText();

            return new AlertDialog.Builder(getActivity()).setTitle("select your product")
                    .setView(view)
                    .setPositiveButton("OK", (dialog, whichButton) -> adapter.add(products.get(current)))
                    .create();
        }

        private void subtotaltText() {
            products.get(current).quantity = quantity.getValue();
            subtotal.setText(String.format("%.2f", quantity.getValue() * products.get(current).unitprice));
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }

        @Override
        public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
            subtotaltText();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            current = position;
            subtotaltText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    public static class EditProduct extends DialogFragment implements HorizontalNumberPickerListener {
        @BindView(R.id.product)
        TextView producttext;
        @BindView(R.id.quantity)
        HorizontalNumberPicker quantity;
        @BindView(R.id.subtotal)
        TextView subtotal;
        @BindView(R.id.delete)
        ImageView delete;

        Product product;

        public static EditProduct newInstance(Product product) {
            Bundle args = new Bundle();
            args.putParcelable("product", product);
            EditProduct dialog = new EditProduct();
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.edit, null);

            product = getArguments().getParcelable("product");

            ButterKnife.bind(this, view);

            quantity.setMinValue(1);
            quantity.setValue(product.quantity);
            quantity.setListener(this);

            producttext.setText(product.name);

            subtotaltText();

            delete.setOnClickListener(v -> {
                adapter.remove(product);
                dismiss();
            });

            return new AlertDialog.Builder(getActivity()).setTitle("edit product")
                    .setView(view)
                    .setPositiveButton("OK", (dialog, whichButton) -> adapter.update(product))
                    .create();
        }

        private void subtotaltText() {
            product.quantity = quantity.getValue();
            subtotal.setText(String.format("%.2f", quantity.getValue() * product.unitprice));
        }


        @Override
        public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
            subtotaltText();
        }
    }


    public static class CheckOut extends DialogFragment {
        @BindView(R.id.currency)
        Spinner spinner;

        public static CheckOut newInstance() {
            CheckOut dialog = new CheckOut();
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.checkout, null);


            ButterKnife.bind(this, view);

            spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, availableCurrencies));

            return new AlertDialog.Builder(getActivity()).setTitle("edit product")
                    .setView(view)
                    .setPositiveButton("OK", (dialog, whichButton) -> dismiss())
                    .create();
        }


    }

}
