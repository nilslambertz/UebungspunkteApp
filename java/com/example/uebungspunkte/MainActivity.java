package com.example.uebungspunkte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private LinearLayout linearLayout;
    private static final String filename = "uebungssave.txt";
    private boolean prozentSwitch, emojiSwitch;
    private Datenverwaltung datenverwaltung = new Datenverwaltung(MainActivity.this, filename);
    public static final String myPref = "SwitchSettings";
    private String happyEmoji = new String(Character.toChars(0x1F60A));
    private String normalEmoji = new String(Character.toChars(0x1F610));
    private String sadEmoji = new String(Character.toChars(0x2639));
    private String partyEmoji = new String(Character.toChars(0x1F973));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linearLayout);

        // Bei veralteten/unvollstaendigen Dateien wird die Datei mit neuen Features erweitert
        datenverwaltung.updateDatei();

        // Gespeicherte Einstellungen abrufen
        prozentSwitch = getPreferenceValue("proz");
        emojiSwitch = getPreferenceValue("emoji");

        // FachAdden-Button einrichten
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FachHinzufuegen.class).putExtra("filename", filename));
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Warum tust du das?", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // View updaten
        update();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Switches richtig im Menue setzen
        prozentSwitch = getPreferenceValue("proz");
        emojiSwitch = getPreferenceValue("emoji");
        menu.findItem(R.id.uebungProzentCheck).setChecked(prozentSwitch);
        menu.findItem(R.id.uebungEmojiCheck).setChecked(emojiSwitch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {

        switch(i.getItemId()) {
            case R.id.clearItem: {

                // Clear-Button wurde gedrueckt, Dialog mit Confirm/Break-Button erzeugen
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                // Bestaetigungs-Dialog mit Zahleneingabeaufforderung anzeigen
                                DeleteAllDialog delete = new DeleteAllDialog(MainActivity.this, filename);
                                delete.show(getSupportFragmentManager(), "deleteAllDialog");
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // Dialog anzeigen
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Willst du alle Fächer und Übungen unwiderruflich löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
                return true;
            }
            case R.id.uebungProzentCheck: {
                // prozentSwitch umstellen und speichern
                prozentSwitch = !i.isChecked();
                i.setChecked(prozentSwitch);
                writeToPreference(prozentSwitch, "proz");
                return true;
            }
            case R.id.uebungEmojiCheck: {
                // emojiSwitch umstellen, speichern und den View updaten
                emojiSwitch = !i.isChecked();
                i.setChecked(emojiSwitch);
                writeToPreference(emojiSwitch, "emoji");
                update();
                return true;
            }
        }
        return false;
    }

    // Auf gespeicherte Einstellungen zugreifen
    public boolean getPreferenceValue(String arg)
    {
        SharedPreferences sp = getSharedPreferences(myPref, Context.MODE_PRIVATE);
        Boolean b = sp.getBoolean(arg, false);
        return b;
    }

    // Einstellungen veraendern
    public void writeToPreference(boolean b, String arg)
    {
        SharedPreferences.Editor editor = getSharedPreferences(myPref,Context.MODE_PRIVATE).edit();
        editor.putBoolean(arg, b);
        editor.commit();
    }

    public LinearLayout getElem(final int prozent, final String titel, final int benoetigt) {

        // Neue aeussere Box erstellen und Werte setzen
        final LinearLayout aussen = new LinearLayout(MainActivity.this);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        lp.setMargins(0,0,0,20);
        aussen.setLayoutParams(lp);
        aussen.setBackgroundResource(R.drawable.rundeecken);
        aussen.setFocusableInTouchMode(false);
        aussen.setOrientation(LinearLayout.HORIZONTAL);
        aussen.setGravity(Gravity.CENTER_VERTICAL);
        aussen.setClickable(true);
        aussen.setPadding(5,10,5,10);

        // Box mit erreichten und benoetigten Prozentzahlen erstellen
        LinearLayout beideProzente = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams prozentParams = new LinearLayout.LayoutParams((emojiSwitch ? 290 : 220), ViewGroup.LayoutParams.MATCH_PARENT);
        prozentParams.setMargins(20,0,0,0);
        beideProzente.setLayoutParams(prozentParams);
        beideProzente.setGravity(Gravity.CENTER_VERTICAL);
        beideProzente.setOrientation(LinearLayout.VERTICAL);

        // Erreichte Prozent
        TextView prozentView = new TextView(MainActivity.this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        prozentView.setLayoutParams(params);
        prozentView.setText(((prozent > 100) ? 100 : prozent) +"%");
        prozentView.setTextSize(30);

        // Je nach Prozentzahl wird die Farbe angepasst
        if(prozent < benoetigt && benoetigt != 0) {
            prozentView.setTextColor(Color.rgb(255,0,0));
            try {
                if(emojiSwitch) {
                    prozentView.setText(prozent + "% " + sadEmoji);
                    prozentView.setTextSize(25);
                }
            } catch(Exception e) {
                // :|
            }
        } else {
            if(prozent <= benoetigt + 10 && benoetigt != 0) {
                prozentView.setTextColor(Color.rgb(255,127,0));
                try {
                    if(emojiSwitch) {
                        prozentView.setText(prozent + "% " + normalEmoji);
                        prozentView.setTextSize(25);
                    }
                } catch(Exception e) {
                    // :|
                }
            } else {
                prozentView.setTextColor(Color.rgb(0,139,69));
                try {
                    if(emojiSwitch) {
                        prozentView.setText(((prozent > 100) ? 100 : prozent) + "% " + ((prozent >= 80) ? partyEmoji : happyEmoji));
                        prozentView.setTextSize(25);
                    }
                } catch(Exception e) {
                    // :|
                }
            }
        }

        // Maximale Prozent
        TextView maxProzentView = new TextView(MainActivity.this);
        maxProzentView.setLayoutParams(params);
        maxProzentView.setText("/" + benoetigt +"%");
        maxProzentView.setTextColor(Color.GRAY);
        maxProzentView.setTextSize(15);

        // Views zum den aeusseren View hinzufuegen
        beideProzente.addView(prozentView);
        beideProzente.addView(maxProzentView);
        aussen.addView(beideProzente);

        // Trennstrich erzeugen und zum View adden
        View trenner = new View(MainActivity.this);
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(5, ViewGroup.LayoutParams.MATCH_PARENT);
        mlp.setMargins(30,0,30,0);
        trenner.setLayoutParams(mlp);
        trenner.setBackgroundColor(Color.LTGRAY);
        aussen.addView(trenner);

        // Titel und Anzahl der Uebungen erzeugen
        LinearLayout beideTexte = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams beideTexteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        beideTexte.setLayoutParams(beideTexteParams);
        beideTexte.setGravity(Gravity.CENTER_VERTICAL);
        beideTexte.setOrientation(LinearLayout.VERTICAL);

        // Titel-View
        final TextView titelView = new TextView(MainActivity.this);
        params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        titelView.setLayoutParams(params);
        titelView.setText(titel);
        titelView.setTextSize(emojiSwitch ? 25 : 30);

        // Anzahl der Uebungen
        TextView uebungsanzahlView = new TextView(MainActivity.this);
        uebungsanzahlView.setLayoutParams(params);
        uebungsanzahlView.setTextColor(Color.GRAY);
        uebungsanzahlView.setTextSize(15);
        if(datenverwaltung.getAnzahlUebungen(titel) == 1) {
            uebungsanzahlView.setText("Eine Übung");
        } else {
            uebungsanzahlView.setText(datenverwaltung.getAnzahlUebungen(titel) + " Übungen");
        }

        // Texte zum View adden, diesen widerrum zum aeusseren View adden
        beideTexte.addView(titelView);
        beideTexte.addView(uebungsanzahlView);
        aussen.addView(beideTexte);

        // Aktion beim Klick auf die aeussere Box definieren
        aussen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Werte zum uebergeben speichern
                Intent i = new Intent(MainActivity.this, Punkte.class);
                String[] werte = new String[3];
                werte[0] = titel;
                werte[1] = getPreferenceValue("proz") == true ? "1" : "0";
                werte[2] = filename;
                i.putExtra("args", werte);

                // Neue Activity starten
                startActivity(i);
            }
        });

        // Beim laengeren Klick wird der Dialog zum Loeschen aufgerufen
        aussen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                datenverwaltung.deleteFach(titel);
                                ValueAnimator anim = ValueAnimator.ofInt(aussen.getMeasuredHeight(), 0);
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int val = (Integer) animation.getAnimatedValue();
                                        lp.height = val;
                                        lp.setMargins(0,0,0,val/10);
                                        aussen.setLayoutParams(lp);
                                    }
                                });
                                anim.setDuration(1000);
                                anim.start();

                                aussen.animate().setDuration(1000).alpha(0).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        update();
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Willst du das Fach \"" + titel + "\" unwiderruflich löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
                return true;
            }
        });

        return aussen;
    }

    // View updaten
    public void update() {
        linearLayout.removeAllViews();
        for(int i=0; i < datenverwaltung.getAnzahlFaecher(); i++) {
            linearLayout.addView(getElem(datenverwaltung.getFachProzent(i), datenverwaltung.getFachTitel(i), datenverwaltung.getBenoetigteProzent(i)));
        }
        datenverwaltung.printDatei();
    }
}
