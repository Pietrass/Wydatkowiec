package com.example.piotr.wydatkowiec;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.appContext;
import static com.example.piotr.wydatkowiec.SpinnerYearsListener.currentYearSelected;

public class SpinnerListener implements AdapterView.OnItemSelectedListener {

    public static int currentIndSelected;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentIndSelected = parent.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
