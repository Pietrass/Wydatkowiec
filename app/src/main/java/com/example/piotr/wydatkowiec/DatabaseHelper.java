package com.example.piotr.wydatkowiec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.piotr.wydatkowiec.HistoriaWydatkow.suma;
import static com.example.piotr.wydatkowiec.SpinnerListener.currentIndSelected;
import static com.example.piotr.wydatkowiec.SpinnerYearsListener.currentYearSelected;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "Wydatki";
    static final String TABLE_NAME = "TabelaWydatki";

    static final String ID = "id";
    static final String KWOTA = "kwota";
    static final String OPIS = "opis";
    static final String DATA = "data";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KWOTA + " REAL,"
                + OPIS + " TEXT,"
                + DATA + " DATE"
                + ")";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



    public long insertWydatek(float kwota, String opis, Calendar date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KWOTA, kwota);
        contentValues.put(OPIS, opis);
        contentValues.put(DATA, getDate(date));

        long id = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        return id;
    }



    public ArrayList<Wydatek> getWydatki() {
        ArrayList<Wydatek> wydatki = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + DATA + " " + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                float kwota = cursor.getFloat(cursor.getColumnIndex(KWOTA));
                String opis = cursor.getString(cursor.getColumnIndex(OPIS));
                String data = cursor.getString(cursor.getColumnIndex(DATA));
                long idDb = cursor.getLong(cursor.getColumnIndex(ID));
                Wydatek wydatek = new Wydatek(kwota, opis, data, idDb);
                suma += kwota;
                wydatki.add(wydatek);
            } while (cursor.moveToNext());
        }
        db.close();
        return wydatki;
    }

    public ArrayList<Wydatek> getWydatkiFromSelectedMonth() {
        ArrayList<Wydatek> wydatki = new ArrayList<>();
        String monthFormatted = String.format("%02d", currentIndSelected);
        String year = currentYearSelected;
        String selectQuery;
        if (currentIndSelected == 0) {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE strftime('%Y', data) = '" + year +"'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE strftime('%m', data) = '" + monthFormatted + "' AND " +
                    "strftime('%Y', data) = '" + year + "'";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                float kwota = cursor.getFloat(cursor.getColumnIndex(KWOTA));
                String opis = cursor.getString(cursor.getColumnIndex(OPIS));
                String data = cursor.getString(cursor.getColumnIndex(DATA));
                long idDb = cursor.getLong(cursor.getColumnIndex(ID));
                Wydatek wydatek = new Wydatek(kwota, opis, data, idDb);
                suma += kwota;
                wydatki.add(wydatek);
            } while (cursor.moveToNext());
        }
        db.close();
        return wydatki;
    }

    public Wydatek getItem(long ind) {
        Wydatek wydatek;

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "=" + ind;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        float kwota = cursor.getFloat(cursor.getColumnIndex(KWOTA));
        String opis = cursor.getString(cursor.getColumnIndex(OPIS));
        String data = cursor.getString(cursor.getColumnIndex(DATA));
        long idDb = cursor.getLong(cursor.getColumnIndex(ID));

        wydatek = new Wydatek(kwota, opis, data, idDb);
        db.close();

        return wydatek;
    }

    public void updateItem(long ind, float kwota, String opis) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + KWOTA + " = " + kwota + " WHERE " + ID + " = " + ind);
        db.close();
    }



    public String getDate(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTimeInMillis());
    }

    public void removeItem(long ind) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + ind);
        db.close();
    }
}
