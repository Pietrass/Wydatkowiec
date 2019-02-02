package com.example.piotr.wydatkowiec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.numberFormat;
import static com.example.piotr.wydatkowiec.SpinnerListener.currentIndSelected;
import static com.example.piotr.wydatkowiec.SpinnerYearsListener.currentYearSelected;

public class HistoriaWydatkow extends AppCompatActivity {

    RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Wydatek> wydatki;
    Spinner spinnerMonths;
    Spinner spinnerYears;
    Button buttonFilter;
    TextView sumaTextView;
    public static float suma;

    DatabaseHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_wydatkow);

        this.setTitle("Historia wydatków");
        buttonFilter = findViewById(R.id.button_filter);

        db = new DatabaseHelper(this);
        suma = 0;
        wydatki = db.getWydatki();

        // spinnerMonths
        spinnerMonths = findViewById(R.id.spinner_months);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource
                (this, R.array.months_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonths.setAdapter(spinnerAdapter);
        spinnerMonths.setOnItemSelectedListener(new SpinnerListener());

        // spinnerYears
        spinnerYears = findViewById(R.id.spinner_years);
        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("wszystkie");
        for (int i = thisYear; i >= 2018; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYears.setAdapter(yearsAdapter);
        spinnerYears.setOnItemSelectedListener(new SpinnerYearsListener());

        // recyclerView
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WydatkiAdapter(this, wydatki);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndSelected == 0 && (currentYearSelected == null || currentYearSelected.equals("wszystkie"))) {
                    createListWithAllItems();
                } else {
                    if (currentIndSelected != 0) {
                        if (currentYearSelected != null || currentYearSelected.equals("wszystkie")) {
                            currentYearSelected = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                            spinnerYears.setSelection(1);
                        }
                    }
                    createListForSelectedMonth();
                }
            }
        });

        sumaTextView = findViewById(R.id.suma_wydatkow);
        sumaTextView.setText("Suma: " + numberFormat.format(suma));
    }

    private void createListWithAllItems() {
        suma = 0;
        wydatki.clear();
        wydatki = db.getWydatki();
        adapter = new WydatkiAdapter(this, wydatki);
        recyclerView.setAdapter(adapter);
        Log.d("Wydatkowiec", "" + suma);
        sumaTextView.setText("Suma: " + numberFormat.format(suma));

    }

    private void createListForSelectedMonth() {
        suma = 0;
        wydatki.clear();
        wydatki = db.getWydatkiFromSelectedMonth();
        adapter = new WydatkiAdapter(getBaseContext(), wydatki);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        sumaTextView.setText("Suma: " + numberFormat.format(suma));

    }
}
