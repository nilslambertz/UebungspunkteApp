package com.example.uebungspunkte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Punkte extends AppCompatActivity {
    private String titel;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private Button addButton;
    private int uebNummer = 0;
    private int savedNr = 0;
    private static boolean prozentSwitch = false;
    private static String filename;
    private Datenverwaltung datenverwaltung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punkte);

        linearLayout = findViewById(R.id.linearLayout);
        addButton = findViewById(R.id.addButton);
        scrollView = findViewById(R.id.scrollView);

        Intent i = getIntent();
        titel = i.getStringArrayExtra("args")[0];
        prozentSwitch = i.getStringArrayExtra("args")[1].equals("1") ? true : false;
        filename = i.getStringArrayExtra("args")[2];
        datenverwaltung = new Datenverwaltung(Punkte.this, filename);

        setTitle(titel);
        update();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uebungAdden();
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    int unicode = 0x1F621;
                    String toxicEmoji = new String(Character.toChars(unicode));
                    String text = toxicEmoji + toxicEmoji + toxicEmoji + toxicEmoji + toxicEmoji + toxicEmoji + toxicEmoji + toxicEmoji;
                    Toast.makeText(Punkte.this, text, Toast.LENGTH_LONG).show();
                } catch(Exception e) {
                    // :|
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.uebungs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        switch(i.getItemId()) {
            case R.id.changeFachItem: {
                AendernDialog aendern = new AendernDialog(titel, datenverwaltung.getBenoetigteProzent(titel), datenverwaltung.getVorrAnzahlUeb(titel), Punkte.this, filename);
                aendern.show(getSupportFragmentManager(), "aenderDialog");
                return true;
            }
            case R.id.uebersichtItem: {
                UebersichtDialog uebersicht = new UebersichtDialog(titel, datenverwaltung);
                uebersicht.show(getSupportFragmentManager(), "uebersichtDialog");
                return true;
            }
        }
        return false;
    }

    private void uebungAdden() {
        addElem(-1, -1, false);
    }

    private void addElem(double x, double y, boolean aendern) {
        if(uebNummer > savedNr) {
            Toast toast = Toast.makeText(Punkte.this, "Bitte zuerst den vorherigen Eintrag bestätigen!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        uebNummer++;
        final int save = uebNummer;
        final LinearLayout l = new LinearLayout(Punkte.this);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        lp.setMargins(0,0,0,20);
        l.setLayoutParams(lp);
        l.setBackgroundResource(R.drawable.rundeecken);
        l.setFocusableInTouchMode(false);
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setPadding(15,15,15,15);

        final TextView uebNr = new TextView(Punkte.this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(275, TableRow.LayoutParams.MATCH_PARENT);
        uebNr.setLayoutParams(params);
        uebNr.setText("Üb. " + uebNummer);
        uebNr.setGravity(Gravity.CENTER_VERTICAL);
        uebNr.setTextSize(30);
        l.addView(uebNr);

        final View trenner = new View(Punkte.this);
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(5, ViewGroup.LayoutParams.MATCH_PARENT);
        mlp.setMargins(0,0,30,0);
        trenner.setLayoutParams(mlp);
        trenner.setBackgroundColor(Color.LTGRAY);
        l.addView(trenner);

        final EditText edit1 = new EditText(Punkte.this);
        edit1.setWidth(150);
        edit1.setTextSize(18);
        edit1.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        edit1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if(x != -1) edit1.setText("" + x);
        l.addView(edit1);

        final TextView tv = new TextView(Punkte.this);
        TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        p.setMargins(20,0,20,0);
        tv.setLayoutParams(p);
        tv.setText("/");
        tv.setTextSize(30);
        l.addView(tv);

        final TextView prozentView = new TextView(Punkte.this);
        final TableRow.LayoutParams prozentParams = new TableRow.LayoutParams(378, TableRow.LayoutParams.MATCH_PARENT);
        prozentView.setLayoutParams(prozentParams);
        prozentView.setGravity(Gravity.CENTER);
        double prozent = (x / y)*100;
        prozentView.setText(String.format("%.2f",(prozent > 100.0 ? 100.0 : prozent)) + "%");
        prozentView.setTextSize(30);
        prozentView.setVisibility(View.INVISIBLE);

        final EditText edit2 = new EditText(Punkte.this);
        edit2.setWidth(150);
        edit2.setTextSize(18);
        double vorherigeMaxPunktzahl = 0.0;
        vorherigeMaxPunktzahl = datenverwaltung.getUeb(titel, uebNummer-2)[1];
        if(y == -1) {
            if(vorherigeMaxPunktzahl != -1.0) {
                edit2.setText("" + vorherigeMaxPunktzahl);
            }
        } else {
            edit2.setText("" + y);
        }
        edit2.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        edit2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        l.addView(edit2);

        final View trenner2 = new View(Punkte.this);
        ViewGroup.MarginLayoutParams mlp2 = new ViewGroup.MarginLayoutParams(5, ViewGroup.LayoutParams.MATCH_PARENT);
        mlp2.setMargins(30,0,15,0);
        trenner2.setLayoutParams(mlp2);
        trenner2.setBackgroundColor(Color.LTGRAY);
        l.addView(trenner2);

        final Button confirmButton = new Button(Punkte.this);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        confirmButton.setLayoutParams(lp2);
        confirmButton.setText(aendern ? "Ändern" : "Okay");
        confirmButton.setGravity(Gravity.CENTER);
        l.addView(confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (this) {
                    String err = edit1.getText().toString();
                    String max = edit2.getText().toString();

                    if (err.equals("") ||max.equals("")) {
                        if(max.equals("")) {
                            Toast toast = Toast.makeText(Punkte.this, "Bitte die mögliche Maximalpunktzahl eintragen!", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        if(err.equals("")) {
                            err = "0.0";
                        }
                    }

                    if(Double.parseDouble(max) < 0.0) {
                        Toast toast = Toast.makeText(Punkte.this, "Mehr als 0 Punkte als mögliche Punktzahl erforderlich!", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    if (confirmButton.getText().equals("Ändern")) {
                        datenverwaltung.changeUebung(titel, save, Double.parseDouble(err), Double.parseDouble(max));
                    } else {
                        datenverwaltung.addUebung(titel, Double.parseDouble(err), Double.parseDouble(max));
                        confirmButton.setText("Ändern");
                        savedNr++;
                    }
                    update();
                }
            }
        });

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uebNummer > savedNr || !prozentSwitch) {
                    return;
                }
                switchProzentView(l, uebNr, trenner, edit1, tv, prozentView, edit2, trenner2, confirmButton);
            }
        });

        l.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(save < savedNr) {
                    Toast.makeText(Punkte.this, "Bitte zuerst die letzte Übung löschen", Toast.LENGTH_SHORT).show();
                    return true;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                datenverwaltung.deleteUebung(titel, save);
                                ValueAnimator anim = ValueAnimator.ofInt(l.getMeasuredHeight(), 0);
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int val = (Integer) animation.getAnimatedValue();
                                        lp.height = val;
                                   //     lp.setMargins(0,0,0,val/8);
                                        l.setLayoutParams(lp);
                                    }
                                });
                                anim.setDuration(1000);
                                anim.start();

                                l.animate().setDuration(1000).alpha(0).setListener(new AnimatorListenerAdapter() {
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

                AlertDialog.Builder builder = new AlertDialog.Builder(Punkte.this);
                builder.setMessage("Diese Übung löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
                return true;
            }
        });

        linearLayout.addView(l);
    }

    private void switchProzentView(LinearLayout l, TextView tv, View v, EditText et, TextView tv2, TextView prozent, EditText et2, View v2, Button b) {
        if(prozent.getVisibility() == View.INVISIBLE) {
            l.removeAllViews();
            l.addView(tv);
            l.addView(v);
            l.addView(prozent);
            l.addView(v2);
            l.addView(b);
            prozent.setVisibility(View.VISIBLE);
        } else {
            prozent.setVisibility(View.INVISIBLE);
            l.removeAllViews();
            l.addView(tv);
            l.addView(v);
            l.addView(et);
            l.addView(tv2);
            l.addView(et2);
            l.addView(v2);
            l.addView(b);
        }
    }

    public void update() {
        uebNummer = 0;
        savedNr = 0;
        linearLayout.removeAllViews();

        for(int x=0; x < datenverwaltung.getAnzahlUebungen(titel); x++) {
            double[] feld = datenverwaltung.getUeb(titel, x);
            addElem(feld[0], feld[1], true);
            savedNr++;
        }

        String alle[] = datenverwaltung.getAlleNamen();
        int prozent = 0;
        int max = 0;
        for(int i=0; i < datenverwaltung.getAnzahlFaecher(); i++) {
            if(datenverwaltung.getName(datenverwaltung.getZeile(i)).equals(titel)) {
                prozent = datenverwaltung.getFachProzent(i);
                max = datenverwaltung.getBenoetigteProzent(i);
            }
        }

        double punkte = 0;
        for(int i=0; i < datenverwaltung.getAnzahlUebungen(titel); i++) {
            punkte = punkte + datenverwaltung.getUeb(titel, i)[0];
        }

        setTitle(titel + ": " + (prozent > 100 ? 100 : prozent) + "%");
    }

    public void changeTitel(String n) {
        titel = n;
        update();
    }

}
