package com.example.piotr.wydatkowiec;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OdliczWydatkiActivity extends AppCompatActivity {

    public static float wydane;

    public static Context appContext;

    TextView wydaneTextView;
    EditText kwota, opisEditText;
    Button odlicz;

    // zalegly wydatek vars

    LinearLayout linearLayoutZaleglyWydatek;
    static TextView zaleglaData;
    CheckBox zaleglyWydatek;
    Button ustawDate;
    static Calendar cZaleglaData;

    static String poprzedniMiesiac;

    DatabaseHelper db;
    public static NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odlicz_wydatki);

        appContext = this;

        numberFormat = new DecimalFormat("#0.00");
        wydaneTextView = findViewById(R.id.wydane);

        getWydane();
        checkMonth();

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

        // zaległy wydatek

        linearLayoutZaleglyWydatek = findViewById(R.id.linear_layout_zalegly_wydatek);
        zaleglyWydatek = findViewById(R.id.checkbox_zalegly);
        zaleglaData = findViewById(R.id.data_zaleglego_wydatku);
        ustawDate = findViewById(R.id.button_ustaw_date);

        Calendar aktualnaData = Calendar.getInstance();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        zaleglaData.setText(simpleDateFormat.format(aktualnaData.getTime()));

        zaleglyWydatek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (zaleglyWydatek.isChecked()) {
                    linearLayoutZaleglyWydatek.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutZaleglyWydatek.setVisibility(View.GONE);
                }
            }
        });

        zaleglyWydatek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(wydaneTextView.getWindowToken(), 0);

            }
        });

        ustawDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new DateFragment();
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        odlicz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float doOdliczenia = Float.valueOf(kwota.getText().toString());
                String opis = opisEditText.getText().toString();
                wydane += doOdliczenia;
                if (zaleglyWydatek.isChecked()) {
                    db.insertWydatek(doOdliczenia, opis, cZaleglaData);
                } else {
                    db.insertWydatek(doOdliczenia, opis, Calendar.getInstance());
                }
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        String aktualnyMiesiac = simpleDateFormat.format(aktualnaData.getTime());
        if (poprzedniMiesiac != null) {
            if (poprzedniMiesiac != aktualnyMiesiac) {
                wydane = 0;
                wydaneTextView.setText("Wydane: " + numberFormat.format(wydane));
                poprzedniMiesiac = aktualnyMiesiac;
                saveWydane();

            }
        } else {
            poprzedniMiesiac = aktualnyMiesiac;
        }
    }

    public static void saveWydane() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("wydane", wydane);
        editor.putString("poprzedniMiesiac", poprzedniMiesiac);
        editor.commit();
    }

    public void getWydane() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        wydane = sharedPreferences.getFloat("wydane", 0);
        poprzedniMiesiac = sharedPreferences.getString("poprzedniMiesiac", null);
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

    public static class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar aktualnaData = Calendar.getInstance();
            int year = aktualnaData.get(Calendar.YEAR);
            int month = aktualnaData.get(Calendar.MONTH);
            int day = aktualnaData.get(Calendar.DATE);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            cZaleglaData = Calendar.getInstance();
            cZaleglaData.set(year, month, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            zaleglaData.setText(simpleDateFormat.format(cZaleglaData.getTime()));
        }
    }
}


