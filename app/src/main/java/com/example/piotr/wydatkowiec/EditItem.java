package com.example.piotr.wydatkowiec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.saveWydane;
import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.wydane;

public class EditItem extends AppCompatActivity {

    EditText kwotaEdit, opisEdit;
    Button zatwierdz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        kwotaEdit = findViewById(R.id.kwota_edit);
        opisEdit = findViewById(R.id.opis_edit);
        zatwierdz = findViewById(R.id.button_zatwierdz);

        kwotaEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (kwotaEdit.getText().toString().length() < 1) {
                    zatwierdz.setEnabled(false);
                } else {
                    zatwierdz.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final DatabaseHelper db = new DatabaseHelper(this);
        long dbId = getIntent().getLongExtra("dbId", 0);

        final Wydatek wydatek = db.getItem(dbId);
        final float poprzedniaKwota = wydatek.kwota;

        final NumberFormat numberFormat = new DecimalFormat("#0.00");

        kwotaEdit.setText(String.valueOf(numberFormat.format(wydatek.kwota)));
        if (wydatek.opis.length() > 0) {
            opisEdit.setText(wydatek.opis);
        }

        zatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float kwota = Float.valueOf(kwotaEdit.getText().toString());
                String opis = opisEdit.getText().toString();
                long ind = wydatek.dbId;
                float roznicaKwot = poprzedniaKwota - kwota;
                wydane -= roznicaKwot;
                saveWydane();
                db.updateItem(ind, kwota, opis);
                Intent zatwierdzItem = new Intent(getBaseContext(), HistoriaWydatkow.class);
                startActivity(zatwierdzItem);
            }
        });
    }
}
