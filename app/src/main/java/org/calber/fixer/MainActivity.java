package org.calber.fixer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(view -> (new SelectProduct()).show(getSupportFragmentManager(), "buy"));
    }

    private List<Product> createProducts() {
        List<Product> products = new ArrayList<>(4);
        products.add(new Product("Peas","bag",0.95));
        products.add(new Product("Eggs","dozen",2.1));
        products.add(new Product("Milk","bottle",1.3));
        products.add(new Product("Beans","can",0.73));

        return products;
    }

    public static class SelectProduct extends DialogFragment {

        public SelectProduct() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.select, null);
            return new AlertDialog.Builder(getActivity()).setTitle("select your product")
                    .setView(view)
                    .setPositiveButton("OK",
                            (dialog, whichButton) -> {
                            }
                    )
                    .create();
        }


        @Override
        public void onStart() {
            super.onStart();

            final AlertDialog d = (AlertDialog) getDialog();
            if (d != null) {
                Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                });
            }

        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }

}
