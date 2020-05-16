package com.example.uebungspunkte;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.text.HtmlCompat;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class UebersichtDialog extends AppCompatDialogFragment {

    private RelativeLayout relativeLayout;
    private TextView anzahlUebungen, anzahlPunkte, moeglichePunkte, anzahlProzent, benoetigteProzent,
            benoetigtePunkteMinus2, benoetigtePunkteMinus1, benoetigtePunkteMinus0, benoetigtePunktePlus1,
            benoetigtePunktePlus2, disclaimer;
    private LinearLayout linearLayoutZulassung;
    private String titel;
    private double allePunkte;
    private BigDecimal alleMoeglichenPunkte = new BigDecimal(0.0);
    private Datenverwaltung datenverwaltung;
    private int vorrAnzahlUebungen = 10;
    private double vorrMaxPunktzahl = 0;
    private int zahlDerUebungen = 0;
    private int benoetigteProzentZahl = 0;
    private boolean viewSwitched = false;
    private boolean nichtWechseln = false;
    private boolean zulassungErhalten = false;
    private double[][] feld = new double[5][2];
    private String partyEmoji = new String(Character.toChars(0x1F973));
    private String confettiEmoji = new String(Character.toChars(0x1F389));

    public UebersichtDialog(String titel, Datenverwaltung datenverwaltung) {
        this.titel = titel;
        this.datenverwaltung = datenverwaltung;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_uebersichtdialog, null);

        for(int i=0; i < datenverwaltung.getAnzahlUebungen(titel); i++) {
            double[] arr = datenverwaltung.getUeb(titel, i);
            allePunkte += arr[0];
            alleMoeglichenPunkte = alleMoeglichenPunkte.add(BigDecimal.valueOf(arr[1]));
        }

        zahlDerUebungen = datenverwaltung.getAnzahlUebungen(titel);
        benoetigteProzentZahl = datenverwaltung.getBenoetigteProzent(titel);
        vorrAnzahlUebungen = datenverwaltung.getVorrAnzahlUeb(titel);
        vorrMaxPunktzahl = datenverwaltung.getMostCommonMaxPoints(titel);

        relativeLayout = view.findViewById(R.id.relativeLayout);
        anzahlUebungen = view.findViewById(R.id.anzahlUebungen);
        anzahlPunkte = view.findViewById(R.id.anzahlPunkte);
        moeglichePunkte = view.findViewById(R.id.moeglichePunkte);
        anzahlProzent = view.findViewById(R.id.anzahlProzent);
        benoetigteProzent = view.findViewById(R.id.benoetigteProzent);
        linearLayoutZulassung = view.findViewById(R.id.linearLayoutZulassung);
        benoetigtePunkteMinus2 = view.findViewById(R.id.benoetigtePunkteMinus2);
        benoetigtePunkteMinus1 = view.findViewById(R.id.benoetigtePunkteMinus1);
        benoetigtePunkteMinus0 = view.findViewById(R.id.benoetigtePunkteMinus0);
        benoetigtePunktePlus1 = view.findViewById(R.id.benoetigtePunktePlus1);
        benoetigtePunktePlus2 = view.findViewById(R.id.benoetigtePunktePlus2);
        disclaimer = view.findViewById(R.id.disclaimer);

        linearLayoutZulassung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView();
            }
        });

        update();

        builder.setView(view)
                .setTitle("Übersicht - " + titel)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
        return builder.create();
    }

    private Spanned getHtml(String x) {
        return HtmlCompat.fromHtml(x, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    private void update() {
        anzahlUebungen.setText("" + zahlDerUebungen);
        anzahlPunkte.setText("" + Math.round(allePunkte));
        moeglichePunkte.setText("" + alleMoeglichenPunkte.setScale(2, RoundingMode.CEILING));
        anzahlProzent.setText("" + datenverwaltung.getFachProzent(datenverwaltung.getIndex(titel)));
        benoetigteProzent.setText("" + benoetigteProzentZahl);


        disclaimer.setText(getHtml("(mit <b>" + vorrMaxPunktzahl + "</b> erreichbaren Punkten in jeder Übung)"));

        if(benoetigteProzentZahl == 0) {
            benoetigtePunkteMinus2.setText("Keine Zulassung benötigt!");
            nichtWechseln = true;
            linearLayoutZulassung.removeView(benoetigtePunkteMinus1);
            linearLayoutZulassung.removeView(benoetigtePunkteMinus0);
            linearLayoutZulassung.removeView(benoetigtePunktePlus1);
            linearLayoutZulassung.removeView(benoetigtePunktePlus2);
            disclaimer.setHeight(0);
            disclaimer.setVisibility(View.INVISIBLE);
        } else {
            if (zahlDerUebungen == 0) {
                benoetigtePunkteMinus2.setText("Bitte mindestens eine Übung eintragen!");
                nichtWechseln = true;
                linearLayoutZulassung.removeView(benoetigtePunkteMinus1);
                linearLayoutZulassung.removeView(benoetigtePunkteMinus0);
                linearLayoutZulassung.removeView(benoetigtePunktePlus1);
                linearLayoutZulassung.removeView(benoetigtePunktePlus2);
                linearLayoutZulassung.removeView(disclaimer);
            } else {
                int tempUebungsdifferenz = vorrAnzahlUebungen - zahlDerUebungen;
                BigDecimal tempMaxPktZahl = alleMoeglichenPunkte;
                BigDecimal benoetigtePktZahl;
                if (tempUebungsdifferenz <= 0) {
                    int proz = datenverwaltung.getFachProzent(datenverwaltung.getIndex(titel));
                    int benProz = benoetigteProzentZahl;
                    String erreicht = "<b>" + (proz >= benProz ? "erreicht" : "nicht erreicht") + "</b>";
                    String str = "Du hast die Zulassung " + erreicht + "!";
                    nichtWechseln = true;
                    if(proz >= benProz) {
                        zulassungErhalten = true;
                    }
                    benoetigtePunkteMinus2.setText(getHtml(str));
                    benoetigtePunkteMinus2.setAlpha(1.0f);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0,0,0,13);
                    benoetigtePunkteMinus2.setLayoutParams(lp);
                    benoetigtePunkteMinus2.setGravity(Gravity.CENTER_HORIZONTAL);

                    linearLayoutZulassung.removeAllViews();
                    linearLayoutZulassung.addView(benoetigtePunkteMinus2);
                } else {
                    tempUebungsdifferenz -= 2;
                    BigDecimal pktDifferenz;
                    if (vorrAnzahlUebungen > 1 && (vorrAnzahlUebungen - 2) > zahlDerUebungen) {
                        tempMaxPktZahl = tempMaxPktZahl.add(BigDecimal.valueOf((double) tempUebungsdifferenz * vorrMaxPunktzahl));
                        benoetigtePktZahl = tempMaxPktZahl.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(benoetigteProzentZahl));
                        pktDifferenz = BigDecimal.valueOf(allePunkte).subtract(benoetigtePktZahl);
                        pktDifferenz = (pktDifferenz.compareTo(BigDecimal.valueOf(0.0)) > 0 ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(0.0).subtract(pktDifferenz));
                        benoetigtePunkteMinus2.setText(getHtml("Noch <b>" + pktDifferenz.setScale(2, RoundingMode.CEILING) + "</b> " + (pktDifferenz.doubleValue() == 1.0 ? "Punkt" : "Punkte") + " bei " + (vorrAnzahlUebungen - 2) + " Übungen"));
                        benoetigtePunkteMinus2.setAlpha(0.2f);
                        feld[0][0] = pktDifferenz.doubleValue();
                        feld[0][1] = ((vorrAnzahlUebungen - 2) - zahlDerUebungen);
                    } else {
                        feld[0][0] = -1;
                        feld[0][1] = -1;
                        linearLayoutZulassung.removeView(benoetigtePunkteMinus2);
                    }
                    tempUebungsdifferenz++;
                    tempMaxPktZahl = alleMoeglichenPunkte;
                    if (vorrAnzahlUebungen > 0 && (vorrAnzahlUebungen - 1) > zahlDerUebungen) {
                        tempMaxPktZahl = tempMaxPktZahl.add(BigDecimal.valueOf((double) tempUebungsdifferenz * vorrMaxPunktzahl));
                        benoetigtePktZahl = tempMaxPktZahl.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(benoetigteProzentZahl));
                        pktDifferenz = BigDecimal.valueOf(allePunkte).subtract(benoetigtePktZahl);
                        pktDifferenz = (pktDifferenz.compareTo(BigDecimal.valueOf(0.0)) > 0 ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(0.0).subtract(pktDifferenz));
                        benoetigtePunkteMinus1.setText(getHtml("Noch <b>" + pktDifferenz.setScale(2, RoundingMode.CEILING) + "</b> " + (pktDifferenz.doubleValue() == 1.0 ? "Punkt" : "Punkte") + " bei " + (vorrAnzahlUebungen - 1) + " Übungen"));
                        benoetigtePunkteMinus1.setAlpha(0.5f);
                        feld[1][0] = pktDifferenz.doubleValue();
                        feld[1][1] = ((vorrAnzahlUebungen - 1) - zahlDerUebungen);
                    } else {
                        feld[1][0] = -1;
                        feld[1][1] = -1;
                        linearLayoutZulassung.removeView(benoetigtePunkteMinus1);
                        benoetigtePunkteMinus1 = null;
                    }
                    tempUebungsdifferenz++;
                    tempMaxPktZahl = tempMaxPktZahl = alleMoeglichenPunkte;
                    tempMaxPktZahl = tempMaxPktZahl.add(BigDecimal.valueOf((double) tempUebungsdifferenz * vorrMaxPunktzahl));
                    benoetigtePktZahl = tempMaxPktZahl.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(benoetigteProzentZahl));
                    pktDifferenz = BigDecimal.valueOf(allePunkte).subtract(benoetigtePktZahl);
                    pktDifferenz = (pktDifferenz.compareTo(BigDecimal.valueOf(0.0)) > 0 ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(0.0).subtract(pktDifferenz));
                    benoetigtePunkteMinus0.setText(getHtml("<u><b>Noch " + pktDifferenz.setScale(2, RoundingMode.CEILING) + " " + (pktDifferenz.doubleValue() == 1.0 ? "Punkt" : "Punkte") +  " bei " + (vorrAnzahlUebungen) + " Übungen</b></u>"));
                    benoetigtePunkteMinus0.setAlpha(1.0f);
                    feld[2][0] = pktDifferenz.doubleValue();
                    feld[2][1] = ((vorrAnzahlUebungen) - zahlDerUebungen);

                    if(pktDifferenz.doubleValue() == 0.0) {
                        linearLayoutZulassung.removeAllViews();
                        String str = "Du hast die Zulassung <b>erreicht</b>!";
                        nichtWechseln = true;
                        zulassungErhalten = true;
                        linearLayoutZulassung.addView(benoetigtePunkteMinus2);
                        benoetigtePunkteMinus2.setAlpha(1.0f);
                        benoetigtePunkteMinus2.setText(getHtml(str));
                        return;
                    }

                    tempUebungsdifferenz++;
                    tempMaxPktZahl = alleMoeglichenPunkte;
                    tempMaxPktZahl = tempMaxPktZahl.add(BigDecimal.valueOf((double) tempUebungsdifferenz * vorrMaxPunktzahl));
                    benoetigtePktZahl = tempMaxPktZahl.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(benoetigteProzentZahl));
                    pktDifferenz = BigDecimal.valueOf(allePunkte).subtract(benoetigtePktZahl);
                    pktDifferenz = (pktDifferenz.compareTo(BigDecimal.valueOf(0.0)) > 0 ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(0.0).subtract(pktDifferenz));
                    benoetigtePunktePlus1.setText(getHtml("Noch <b>" + pktDifferenz.setScale(2, RoundingMode.CEILING) + "</b> " + (pktDifferenz.doubleValue() == 1.0 ? "Punkt" : "Punkte") + " bei " + (vorrAnzahlUebungen + 1) + " Übungen"));
                    benoetigtePunktePlus1.setAlpha(0.5f);
                    feld[3][0] = pktDifferenz.doubleValue();
                    feld[3][1] = ((vorrAnzahlUebungen + 1) - zahlDerUebungen);


                    tempUebungsdifferenz++;
                    tempMaxPktZahl = alleMoeglichenPunkte;
                    tempMaxPktZahl = tempMaxPktZahl.add(BigDecimal.valueOf((double) tempUebungsdifferenz * vorrMaxPunktzahl));
                    benoetigtePktZahl = tempMaxPktZahl.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(benoetigteProzentZahl));
                    pktDifferenz = BigDecimal.valueOf(allePunkte).subtract(benoetigtePktZahl);
                    pktDifferenz = (pktDifferenz.compareTo(BigDecimal.valueOf(0.0)) > 0 ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(0.0).subtract(pktDifferenz));
                    benoetigtePunktePlus2.setText(getHtml("Noch <b>" + pktDifferenz.setScale(2, RoundingMode.CEILING) + "</b> " + (pktDifferenz.doubleValue() == 1.0 ? "Punkt" : "Punkte") + " bei " + (vorrAnzahlUebungen + 2) + " Übungen"));
                    benoetigtePunktePlus2.setAlpha(0.2f);
                    feld[4][0] = pktDifferenz.doubleValue();
                    feld[4][1] = ((vorrAnzahlUebungen + 2) - zahlDerUebungen);
                }
            }
        }
    }

    private void switchView() {
        if(viewSwitched || nichtWechseln) {
            if(zulassungErhalten && !viewSwitched) {
                linearLayoutZulassung.removeAllViews();

                benoetigtePunkteMinus2.setAlpha(1.0f);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,13);
                benoetigtePunkteMinus2.setLayoutParams(lp);
                benoetigtePunkteMinus2.setGravity(Gravity.CENTER_HORIZONTAL);
                benoetigtePunkteMinus2.setText(confettiEmoji + partyEmoji + " yay " + partyEmoji + confettiEmoji);
                viewSwitched = true;
                linearLayoutZulassung.addView(benoetigtePunkteMinus2);
                return;
            }
            update();
            viewSwitched = false;
            return;
        } else {
            BigDecimal avgNeeded = new BigDecimal(0.0);
            if(feld[0][0] == -1 && feld[0][1] == -1) {
            } else {
                avgNeeded = BigDecimal.valueOf(feld[0][0]).divide(BigDecimal.valueOf(feld[0][1]), 2, RoundingMode.HALF_UP);
                benoetigtePunkteMinus2.setText(getHtml("<b>" + avgNeeded.setScale(2, RoundingMode.CEILING) + "</b> Punkte pro Blatt in den nächsten " + (int) feld[0][1]  + " Übungen benötigt"));
                benoetigtePunkteMinus2.setAlpha(0.2f);
            }
            if(feld[1][0] == -1 && feld[1][1] == -1) {
            } else {
                avgNeeded = BigDecimal.valueOf(feld[1][0]).divide(BigDecimal.valueOf(feld[1][1]), 2, RoundingMode.HALF_UP);
                benoetigtePunkteMinus1.setText(getHtml("<b>" + avgNeeded.setScale(2, RoundingMode.CEILING) + "</b> Punkte pro Blatt in den nächsten " + (int) feld[1][1]  + " Übungen benötigt"));
                benoetigtePunkteMinus1.setAlpha(0.5f);
            }
            avgNeeded = BigDecimal.valueOf(feld[2][0]).divide(BigDecimal.valueOf(feld[2][1]), 2, RoundingMode.HALF_UP);
            benoetigtePunkteMinus0.setText(getHtml("<b><u>" + avgNeeded.setScale(2, RoundingMode.CEILING) + " Punkte pro Blatt in den nächsten " + (int) feld[2][1]  + " Übungen benötigt</u></b>"));
            benoetigtePunkteMinus0.setAlpha(1.0f);

            avgNeeded = BigDecimal.valueOf(feld[3][0]).divide(BigDecimal.valueOf(feld[3][1]), 2, RoundingMode.HALF_UP);
            benoetigtePunktePlus1.setText(getHtml("<b>" + avgNeeded.setScale(2, RoundingMode.CEILING) + "</b> Punkte pro Blatt in den nächsten " + (int) feld[3][1]  + " Übungen benötigt"));
            benoetigtePunktePlus1.setAlpha(0.5f);

            avgNeeded = BigDecimal.valueOf(feld[4][0]).divide(BigDecimal.valueOf((int) feld[4][1]), 2, RoundingMode.HALF_UP);
            benoetigtePunktePlus2.setText(getHtml("<b>" + avgNeeded.setScale(2, RoundingMode.CEILING) + "</b> Punkte pro Blatt in den nächsten " + (int) feld[4][1]  + " Übungen benötigt"));
            benoetigtePunktePlus2.setAlpha(0.2f);


            viewSwitched = true;
        }
    }
}
