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

import org.calber.fixer.models.Product;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;


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

        api = FixerApi.builder().withBase(FixerApi.GBP).withNetwork().withStaticProductApi().build();

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        list.setLayoutManager(layoutManager);

        adapter = new ProductAdapter(this, this);

        list.setAdapter(adapter);
        list.setHasFixedSize(true);

    }

    private void start() {
        fab.setOnClickListener(view -> (new AddProduct()).show(getSupportFragmentManager(), "buy"));
        checkout.setOnClickListener(v -> CheckOut.newInstance().show(getSupportFragmentManager(), "checkout"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        api.currencies().subscribe(results -> {
                    start();
                    availableCurrencies = results;
                }, throwable -> EventBus.getDefault().post(
                Notify().withMessage("No currency available, check network", Snackbar.LENGTH_INDEFINITE)
                        .withAction(v -> finish(), "Exit")
                        .build())
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static Notify.Builder Notify() {
        return new Notify.Builder();
    }

    private static class Notify {
        public String message;
        public int type;
        public View.OnClickListener action;
        public String actionMessage;

        public static final class Builder {
            Notify notify = new Notify();

            Builder withMessage(String message, int type) {
                notify.message = message;
                notify.type = type;
                return this;
            }

            Builder withAction(View.OnClickListener action, String actionMessage) {
                notify.action = action;
                notify.actionMessage = actionMessage;
                return this;
            }

            public Notify build() {
                return notify;
            }
        }
    }

    @Subscribe
    public void onNotifyEvent(Notify event) {
        if (event.action != null)
            Snackbar.make(root, event.message, event.type).setAction(event.actionMessage, event.action).show();
        else
            Snackbar.make(root, event.message, event.type).show();
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
        private List<Product> productsToBuy;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.select, null);

            ButterKnife.bind(this, view);

            productsToBuy = api.getProductsApi().getProducts();
            quantity.setListener(this);
            quantity.setMinValue(1);

            spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, productsToBuy));
            spinner.setOnItemSelectedListener(this);
            subtotaltText();

            return new AlertDialog.Builder(getActivity()).setTitle("select your product")
                    .setView(view)
                    .setPositiveButton("OK", (dialog, whichButton) -> adapter.add(productsToBuy.get(current)))
                    .create();
        }

        private void subtotaltText() {
            productsToBuy.get(current).quantity = quantity.getValue();
            subtotal.setText(String.format("%.2f %s", quantity.getValue() * productsToBuy.get(current).unitprice, api.base));
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
            subtotal.setText(String.format("%.2f %s", quantity.getValue() * product.unitprice, api.base));
        }


        @Override
        public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
            subtotaltText();
        }
    }


    public static class CheckOut extends DialogFragment implements AdapterView.OnItemSelectedListener {
        @BindView(R.id.currency)
        Spinner spinner;
        @BindView(R.id.total)
        TextView total;
        private String convertTo;
        private boolean firstfire = true;
        private List<Product> productsToBuy;

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

            productsToBuy = api.getProductsApi().getProducts();

            spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, availableCurrencies));
            spinner.setSelection(availableCurrencies.indexOf(api.base));
            spinner.setOnItemSelectedListener(this);

            return new AlertDialog.Builder(getActivity()).setTitle("edit product")
                    .setView(view)
                    .setPositiveButton("OK", (dialog, whichButton) -> {
                        dismiss();
                    })
                    .create();
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (firstfire)
                convertTo = api.base;
            else
                convertTo = availableCurrencies.get(position);
            firstfire = false;

            api.conversion().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(exchange -> {
                        adapter.load(api.convertPrices(adapter.getProductList(), exchange, convertTo));
                        api.convertPrices(productsToBuy, exchange, convertTo);
                        adapter.setBase(api.base);
                        total.setText(String.format("%.2f %s", api.shopTotal(adapter.getProductList()), api.base));
                    }, throwable -> EventBus.getDefault().post(
                            Notify().withMessage("no conversion rate available", Snackbar.LENGTH_INDEFINITE)
                                    .withAction(v -> getActivity().finish(), "Exit")
                                    .build()));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
