package com.example.piotr.wydatkowiec;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static com.example.piotr.wydatkowiec.HistoriaWydatkow.adapter;
import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.saveWydane;
import static com.example.piotr.wydatkowiec.OdliczWydatkiActivity.wydane;

public class WydatkiAdapter extends RecyclerView.Adapter<WydatkiAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Wydatek> wydatki;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView kwota;
        public TextView opis;
        public TextView data;

        public MyViewHolder(final View view) {
            super(view);
            linearLayout = view.findViewById(R.id.item_linear_layout);
            kwota = view.findViewById(R.id.item_kwota);
            opis = view.findViewById(R.id.item_opis);
            data = view.findViewById(R.id.item_data);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.item_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_edit:
                                    editItem(getAdapterPosition());
                                    return true;
                                case R.id.action_remove:
                                    removeItem(getAdapterPosition());
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return false;
                }
            });
        }


    }

    private void removeItem(int ind) {
        Wydatek wydatek = wydatki.get(ind);
        long dbId = wydatek.dbId;
        DatabaseHelper db = new DatabaseHelper(context);
        db.removeItem(dbId);
        wydane -= wydatek.kwota;
        saveWydane();
        wydatki.remove(ind);
        adapter.notifyItemRemoved(ind);
    }

    private void editItem(int ind) {
        Wydatek wydatek = wydatki.get(ind);
        long dbId = wydatek.dbId;
        Intent editIntent = new Intent(context, EditItem.class);
        editIntent.putExtra("dbId", dbId);
        context.startActivity(editIntent);
    }


    public WydatkiAdapter(Context context, ArrayList<Wydatek> wydatki) {
        this.wydatki = wydatki;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Wydatek wydatek = wydatki.get(i);

        final NumberFormat numberFormat = new DecimalFormat("#0.00");

        myViewHolder.kwota.setText(String.valueOf(numberFormat.format(wydatek.kwota)));
        myViewHolder.opis.setText(wydatek.opis);
        myViewHolder.data.setText(wydatek.date);
    }


    @Override
    public int getItemCount() {
        return wydatki.size();
    }
}
