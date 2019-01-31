package com.example.piotr.wydatkowiec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class OdliczWydatkiActivity extends AppCompatActivity {

    public static float wydane;

    public static Context appContext;

    TextView wydaneTextView;
    EditText kwota, opisEditText;
    Button odlicz;

    Calendar poprzedniaData;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odlicz_wydatki);

        appContext = this;

        getWydane();
        checkMonth();

        wydaneTextView = findViewById(R.id.wydane);
        final NumberFormat numberFormat = new DecimalFormat("#0.00");
        wydaneTextView.setText("Wydane: " + numberFormat.format(wydane));

        kwota = findViewById(R.id.do_odliczenia);
        opisEditText = findViewById(R.id.opis);
        odlicz = findViewById(R.id.odlicz_button);
        odlicz.setEnabled(false);

        db = new DatabaseHelper(this);

        kwota.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (kwota.getText().toString().length() > 0) {
                    odlicz.setEnabled(true);
                } else {
                    odlicz.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        odlicz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float doOdliczenia = Float.valueOf(kwota.getText().toString());
                String opis = opisEditText.getText().toString();
                wydane += doOdliczenia;
                db.insertWydatek(doOdliczenia, opis, Calendar.getInstance());
                saveWydane();
                wydaneTextView.setText("Wydane: " + numberFormat.format(wydane));
                kwota.setText("");
                opisEditText.setText("");
                opisEditText.clearFocus();
            }
        });
    }

    private void checkMonth() {
        Calendar aktualnaData = Calendar.getInstance();
        if (poprzedniaData != null) {
            if (poprzedniaData.get(Calendar.MONTH) != aktualnaData.get(Calendar.MONTH)) {
                wydane = 0;
                poprzedniaData = aktualnaData;
            }
        } else {
            poprzedniaData = aktualnaData;
        }
    }

    public static void saveWydane() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("wydane", wydane);
        editor.commit();
    }

    public void getWydane() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        wydane = sharedPreferences.getFloat("wydane", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.historia_wydatkow) {
            Intent intent = new Intent(this, HistoriaWydatkow.class);
            startActivity(intent);
        }

        return true;
    }
}
