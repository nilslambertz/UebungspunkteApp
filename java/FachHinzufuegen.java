package com.example.uebungspunkte;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class FachHinzufuegen extends AppCompatActivity {
    private EditText addFachTitel;
    private EditText addFachProzent;
    private EditText anzahlUebungen;
    private Button fachHinzufuegenConfirm;
    private static String filename;
    private Datenverwaltung datenverwaltung;
    private String crabEmoji = new String(Character.toChars(0x1F980));
    private Handler handler = new Handler();

    private Runnable titelChecker = new Runnable() {
        public void run() {
                String text = addFachTitel.getText().toString().trim();
                if(!datenverwaltung.isValiderTitel(text)) {
                    addFachTitel.setError("Ungültiger Titel");
                }
                if(datenverwaltung.isTitelVergeben(null, text)) {
                    addFachTitel.setError("Titel bereits vergeben");
                }
            }
    };

    private Runnable prozentChecker = new Runnable() {
        public void run() {
                String text = addFachProzent.getText().toString().trim();
                if(text != null || !text.equals("")) {
                    int i = Integer.parseInt(text);
                    if(i < 0 || i > 100) {
                        addFachProzent.setError("Prozentzahl muss zwischen 0 und 100 liegen");
                    }
                }
            }
    };

    private Runnable anzahlUebungenChecker = new Runnable() {
        public void run() {
            String text = anzahlUebungen.getText().toString().trim();
            if(text != null || !text.equals("")) {
                int i = Integer.parseInt(text);
                if(i < 1) {
                    anzahlUebungen.setError("Mindestens eine Übung erforderlich");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fach_hinzufuegen);

        setTitle("Fach hinzufügen");
        filename = getIntent().getStringExtra("filename");
        datenverwaltung = new Datenverwaltung(FachHinzufuegen.this, filename);;

        addFachTitel = findViewById(R.id.addFachTitel);
        addFachProzent = findViewById(R.id.addFachProzent);
        anzahlUebungen = findViewById(R.id.anzahlUebungen);
        fachHinzufuegenConfirm = findViewById(R.id.fachHinzufuegenConfirm);
        fachHinzufuegenConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFach();
            }
        });

        addFachTitel.addTextChangedListener(new TextWatcher() {
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


        addFachProzent.addTextChangedListener(new TextWatcher() {
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


        anzahlUebungen.addTextChangedListener(new TextWatcher() {
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


    }

    private void addFach() {
        String titel = addFachTitel.getText().toString().trim();
        String p = addFachProzent.getText().toString().trim();
        String u = anzahlUebungen.getText().toString().trim();

        String erfolg = "";

        if(p.equals("1337")) {
            Toast toast = Toast.makeText(FachHinzufuegen.this, crabEmoji + " 1337 " + crabEmoji, Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        if(p != null && !p.equals("")) {
            int prozent = Integer.parseInt(p);
            if(u != null && !u.equals("")) {
                int uebAnzahl = Integer.parseInt(u);
                erfolg = datenverwaltung.fachAddenOderAendern(titel, null, prozent, uebAnzahl);
            }
        }
        if(erfolg != null) {
            Toast toast = Toast.makeText(FachHinzufuegen.this, erfolg, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        super.onBackPressed();
    }
}
