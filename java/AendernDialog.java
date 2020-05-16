package com.example.uebungspunkte;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AendernDialog extends AppCompatDialogFragment {
    private TextView alterNameView, alteProzentView, alteVorrUebungenView;
    private EditText neuerNameEdit, neueProzentEdit, neueVorrUebungenEdit;
    private String titel = "";
    private int prozent, vorrUeb;
    private Activity activity;
    private Datenverwaltung datenverwaltung;
    private Handler handler = new Handler();

    public AendernDialog(String titel, int prozent, int vorrUeb, Activity activity, String filename) {
        this.titel = titel;
        this.prozent = prozent;
        this.vorrUeb = vorrUeb;
        this.activity = activity;
        datenverwaltung = new Datenverwaltung(activity, filename);
    }

    private Runnable titelChecker = new Runnable() {
        public void run() {
            String text = neuerNameEdit.getText().toString().trim();
            if(!datenverwaltung.isValiderTitel(text)) {
                neuerNameEdit.setError("Ungültiger Titel");
            }
            if(datenverwaltung.isTitelVergeben(titel, text)) {
                neuerNameEdit.setError("Titel bereits vergeben");
            }
        }
    };

    private Runnable prozentChecker = new Runnable() {
        public void run() {
            String text = neueProzentEdit.getText().toString().trim();
            if(text != null || !text.equals("")) {
                int i = Integer.parseInt(text);
                if(i < 0 || i > 100) {
                    neueProzentEdit.setError("Prozentzahl muss zwischen 0 und 100 liegen");
                }
            }
        }
    };

    private Runnable anzahlUebungenChecker = new Runnable() {
        public void run() {
            String text = neueVorrUebungenEdit.getText().toString().trim();
            if(text != null || !text.equals("")) {
                int i = Integer.parseInt(text);
                if(i < 1) {
                    neueVorrUebungenEdit.setError("Mindestens eine Übung erforderlich");
                }
            }
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        alterNameView = view.findViewById(R.id.alterNameView);
        alteProzentView = view.findViewById(R.id.alteProzentView);
        alteVorrUebungenView = view.findViewById(R.id.alteVorrUebungenView);

        neuerNameEdit = view.findViewById(R.id.neuerNameEdit);
        neueProzentEdit = view.findViewById(R.id.neueProzentEdit);
        neueVorrUebungenEdit = view.findViewById(R.id.neueVorrUebungenEdit);

        alterNameView.setText("Alter Name: " + titel);
        alteProzentView.setText("Alte Prozent: " + prozent);
        alteVorrUebungenView.setText("Alte vorauss. Übungen: " + vorrUeb);

        neuerNameEdit.setHint(titel);
        neueProzentEdit.setHint("" + prozent);
        neueVorrUebungenEdit.setHint("" + vorrUeb);

        neuerNameEdit.setText(titel);
        neueProzentEdit.setText("" + prozent);
        neueVorrUebungenEdit.setText("" + vorrUeb);

        neuerNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(titelChecker);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    handler.postDelayed(titelChecker, 0);
                }
            }
        });


        neueProzentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(prozentChecker);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    handler.postDelayed(prozentChecker, 0);
                }
            }
        });


        neueVorrUebungenEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(anzahlUebungenChecker);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    handler.postDelayed(anzahlUebungenChecker, 0);
                }
            }
        });





        builder.setView(view)
                .setTitle("Daten ändern")
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String n = neuerNameEdit.getText().toString().trim();
                        String p = neueProzentEdit.getText().toString().trim();
                        String u = neueVorrUebungenEdit.getText().toString().trim();
                        String erfolg = "";


                        if(p != null && !p.equals("")) {
                            int prozent = Integer.parseInt(p);
                            if(u != null && !u.equals("")) {
                                int uebAnzahl = Integer.parseInt(u);
                                erfolg = datenverwaltung.fachAddenOderAendern(titel, n, prozent, uebAnzahl);
                            }
                        }
                        if(erfolg != null) {
                            Toast toast = Toast.makeText(activity, erfolg, Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }

                        if(activity instanceof MainActivity) {
                           ((MainActivity) activity).update();
                        } else {
                            if(activity instanceof Punkte) {
                                ((Punkte) activity).update();
                                ((Punkte) activity).changeTitel(n);
                            }
                        }
                    }
                });
        return builder.create();
    }
}
