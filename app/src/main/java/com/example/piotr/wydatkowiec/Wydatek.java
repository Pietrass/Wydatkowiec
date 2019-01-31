package com.example.piotr.wydatkowiec;

import java.util.Calendar;

public class Wydatek {

    public float kwota;
    public String opis;
    public String date;
    public long dbId;

    public Wydatek(float kwota, String opis, String date, long dbId) {
        this.kwota = kwota;
        this.opis = opis;
        this.date = date;
        this.dbId = dbId;
    }
}
