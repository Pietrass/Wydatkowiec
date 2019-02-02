package com.example.piotr.wydatkowiec;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.appContext;

public class SpinnerYearsListener implements android.widget.AdapterView.OnItemSelectedListener {

    public static String currentYearSelected;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentYearSelected = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
