package com.example.uebungspunkte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteAllDialog extends AppCompatDialogFragment {
    private TextView zahlenView;
    private EditText zahlenEditView;
    private MainActivity activity;
    private int random = 1111;
    private Datenverwaltung datenverwaltung;

    public DeleteAllDialog(MainActivity activity, String filename) {
        this.activity = activity;
        datenverwaltung = new Datenverwaltung(activity, filename);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_deletealldialog, null);

        zahlenView = view.findViewById(R.id.zahlenView);
        zahlenEditView = view.findViewById(R.id.zahlenEditView);

        // Zahl zwischen 1000 und 9999 erzeugen
        random = (int) (Math.random() * 8999 + 1000);
        zahlenView.setText("" + random);

        builder.setView(view)
                .setTitle("Bestätigung")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int eingegeben = -1;
                        // Falls nichts eingegeben wurde
                        if(!zahlenEditView.getText().toString().equals("") && zahlenEditView.getText().toString() != null) {
                            eingegeben = Integer.parseInt(zahlenEditView.getText().toString());
                        }

                        // Falls Zahlen uebereinstimmen werden alle Faecher deletet, sonst passiert nichts
                        if(eingegeben == random) {
                            datenverwaltung.clear();
                            activity.update();
                            Toast.makeText(activity, "Alle Fächer wurden erfolgreich gelöscht!", Toast.LENGTH_LONG).show();
                        } else {
                            activity.update();
                            Toast.makeText(activity, "Falsche Zahl eingegeben, Fächer wurden nicht gelöscht", Toast.LENGTH_LONG).show();
                        }

                    }
                });
        return builder.create();
    }
}
