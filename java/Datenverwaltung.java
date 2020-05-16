package com.example.uebungspunkte;

import android.content.Context;

import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Datenverwaltung {
    Context context;
    String filename;

    public Datenverwaltung(Context context, String filename) {
        this.context = context;
        this.filename = filename;
    }

    // "Index"/Zeilennummer für einen bestimmten Titel zurückgeben
    public int getIndex(String titel) {
        String[] alleNamen = getAlleNamen();
        for(int i=0; i < getAnzahlFaecher(); i++) {
            if(alleNamen[i].equals(titel)) {
                return i;
            }
        }
        // Falls kein Treffer gefunden wurde (sollte eigentlich nicht passieren)
        return -1;
    }

    // Datei updaten und "neue" Features hinzufuegen
    public void updateDatei() {
        for(int i=0; i < getAnzahlFaecher(); i++) {
            // Falls noch kein "Anzahl der Uebungen"-Eintrag existiert
            if(!getZeile(i).contains("AnzahlUebungen")) {
                String zeile = getZeile(i);
                // 10 als Default-Wert einsetzen
                String neu = zeile.replace("}", ";\"AnzahlUebungen\":\"" + 10 + "\"}");
                String ganz = getGanzeDatei();
                ganz = ganz.replace(zeile, neu);
                try {
                    OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
                    write.write(ganz);
                    write.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Bestimmte Uebung loeschen (durch -1, -1 als Parameter in changeUebung)
    public void deleteUebung(String titel, int nr) {
        changeUebung(titel, nr, -1, -1);
    }

    // Ganze Datei als String zurueckgeben
    public String getGanzeDatei() {
        String ganz = "";
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String x = null;
            while((x = br.readLine()) != null) {
                ganz = ganz + x + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ganz;
    }

    // Ein komplettes Fach deleten
    public void deleteFach(String titel) {
        try {
            String ganz = "";
            for(int i = 0; i < getAnzahlFaecher(); i++) {
                // Alle Zeilen beibehalten, ausser die, die entfernt werden soll
                if(getFachTitel(i).equals(titel)) continue;
                ganz += getZeile(i) + "\n";
            }
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
            printDatei();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Namen eines Fachs aendern
    public void namenAendern(String alterTitel, String neuerTitel) {
        String alteZeile = getZeile(alterTitel);
        String neueZeile = alteZeile.replace(alterTitel, neuerTitel);
        try {
            String ganz = getGanzeDatei();
            ganz = ganz.replace(alteZeile, neueZeile);
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Anzahl der Uebungen eines Fachs aendern
    public void anzahlUebAendern(String titel, int anzahlUeb) {
        String x = getZeile(titel);
        String s = x.substring(x.indexOf("\"AnzahlUebungen\":\"") + "\"AnzahlUebungen\":\"".length());
        s = s.substring(0, s.indexOf("\""));
        String neu = x.replace("\"AnzahlUebungen\":\"" + s, "\"AnzahlUebungen\":\"" + anzahlUeb);
        try {
            String ganz = getGanzeDatei();
            ganz = ganz.replace(x, neu);
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prozentAendern(String titel, int prozentNeu) {
        String x = getZeile(titel);
        String s = x.substring(x.indexOf("\"benoetigteProzent\":\"") + "\"benoetigteProzent\":\"".length());
        s = s.substring(0, s.indexOf("\""));
        String neu = x.replace("\"benoetigteProzent\":\"" + s, "\"benoetigteProzent\":\"" + prozentNeu);
        try {
            String ganz = getGanzeDatei();
            ganz = ganz.replace(x, neu);
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAnzahlUebungen(String titel) {
        int ergebnis = 0;
        String x = getZeile(titel);
        if(x.equals("") || x == null) {
            return 0;
        }
        int s = x.indexOf("\"ErreichtePunkte\":")  + "\"ErreichtePunkte\":".length() + 1;
        String erreicht = x.substring(s);
        erreicht = erreicht.substring(0, erreicht.indexOf("\""));
        StringTokenizer st = new StringTokenizer(erreicht, ",");
        return st.countTokens();
    }

    public String getZeile(String titel) {
        return getZeile(getIndex(titel));
    }

    public int getBenoetigteProzent(String s) {
        return getBenoetigteProzent(getIndex(s));
    }

    public int getBenoetigteProzent(int nr) {
        String zeile = getZeile(nr);
        if(zeile.equals("") || zeile == null) {
            return -1;
        }
        int start = zeile.indexOf("\"benoetigteProzent\":\"") + "\"benoetigteProzent\":\"".length();
        zeile = zeile.substring(start);
        zeile = zeile.substring(0, zeile.indexOf("\""));
        return Integer.parseInt(zeile);
    }

    synchronized public void changeUebung(String titel, int nr, double pkt, double max) {
        String ganz = "";
        String zeile = "";
        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = context.openFileInput(filename);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        String s = "";
        String alt = "";
        nr = nr -1;
        try {
            while((s = br.readLine()) != null) {
                ganz = ganz + s + "\n";
                if (getName(s).equals(titel)) {
                    alt = s;
                    zeile = s;
                    int start = s.indexOf("\"ErreichtePunkte\":\"") + "\"ErreichtePunkte\":\"".length();
                    s = s.substring(start);
                    s = s.substring(0, s.indexOf("\""));
                    StringTokenizer st = new StringTokenizer(s, ",");
                    String change = "";
                    int a = 0;
                    while(a < nr) {
                        change = change + st.nextToken() + ",";
                        a++;
                    }
                    if(pkt == -1 && max == -1) {
                        if(!change.equals("")) {
                            change = change + (a == getAnzahlUebungen(titel) - 1 ? "" : ",");
                        }
                    } else {
                        change = change + pkt + ",";
                    }
                    st.nextToken();
                    a++;
                    while(a < getAnzahlUebungen(titel) -1) {
                        change = change + st.nextToken() + ",";
                        a++;
                    }

                    if(st.hasMoreTokens()) {
                        change = change + st.nextToken();
                    } else {
                        if(!change.equals("")) change = change.substring(0, change.length() - 1);
                    }


                    zeile = zeile.replace("{\"Name\":\"" + titel + "\";\"ErreichtePunkte\":\"" + s, "{\"Name\":\"" + titel + "\";\"ErreichtePunkte\":\"" + change);

                    s = zeile;
                    start = s.indexOf("\"MoeglichePunkte\":\"") + "\"MoeglichePunkte\":\"".length();
                    s = s.substring(start);
                    s = s.substring(0, s.indexOf("\""));
                    st = new StringTokenizer(s, ",");
                    change = "";
                    a = 0;
                    while(a < nr) {
                        change = change + st.nextToken() + ",";
                        a++;
                    }
                    if(pkt == -1 && max == -1) {
                        if(!change.equals("")) {
                            change = change + (a == getAnzahlUebungen(titel) - 1 ? "" : ",");
                        }
                    } else {
                        change = change + max + ",";
                    }
                    st.nextToken();
                    a++;
                    while(a < getAnzahlUebungen(titel) -1) {
                        change = change + st.nextToken() + ",";
                        a++;
                    }

                    if(st.hasMoreTokens()) {
                        change = change + st.nextToken();
                    } else {
                        if(!change.equals("")) change = change.substring(0, change.length() - 1);
                    }

                    zeile = zeile.replace("\";\"MoeglichePunkte\":\"" + s, "\";\"MoeglichePunkte\":\"" + change);

                    ganz = ganz.replace(alt, zeile);
                }
            }
            OutputStreamWriter write = null;
            write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
            br.close();
            fis.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] getUeb(String titel, int nr) {
        double feld[] = new double[2];
        String x = getZeile(titel);
        int s = x.indexOf("\"ErreichtePunkte\":")  + "\"ErreichtePunkte\":".length() + 1;
        String erreicht = x.substring(s);
        erreicht = erreicht.substring(0, erreicht.indexOf("\""));
        StringTokenizer st = new StringTokenizer(erreicht, ",");
        for(int a=0; a < nr; a++) {
            st.nextToken();
        }
        if(st.hasMoreTokens()) {
            feld[0] = Double.parseDouble(st.nextToken());
        } else {
            double[] falsch = {-1.0, -1.0};
            return falsch;
        }
        s = x.indexOf("\"MoeglichePunkte\":")  + "\"MoeglichePunkte\":".length() + 1;
        String max = x.substring(s);
        max = max.substring(0, max.indexOf("\""));
        st = new StringTokenizer(max, ",");
        for(int a=0; a < nr; a++) {
            st.nextToken();
        }
        feld[1] = Double.parseDouble(st.nextToken());
        return feld;
    }

    public String getZeile(int i) {
        String ganz = getGanzeDatei();
        Scanner sc = new Scanner(ganz);
        int x = 0;
        while(x < i) {
            if(sc.hasNextLine()) {
                sc.nextLine();
            }
            x++;
        }
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    public int getFachProzent(int i) {
        if(getAnzahlFaecher() <= i) {
            return -1;
        }

        int ergebnis = -1;

        String x = getZeile(i);
        if(x.equals("") || x == null) {
            return -1;
        }
        int s = x.indexOf("\"ErreichtePunkte\":")  + "\"ErreichtePunkte\":".length() + 1;
        String erreicht = x.substring(s);
        erreicht = erreicht.substring(0, erreicht.indexOf("\""));
        StringTokenizer st = new StringTokenizer(erreicht, ",");

        double err = 0.0;
        while(st.hasMoreTokens()) {
            err = err + Double.parseDouble(st.nextToken());
        }

        s = x.indexOf("\"MoeglichePunkte\":") + "\"MoeglichePunkte\":".length() + 1;
        String moeglich = x.substring(s);
        moeglich = moeglich.substring(0, moeglich.indexOf("\""));
        st = new StringTokenizer(moeglich, ",");
        double moeg = 0.0;
        while(st.hasMoreTokens()) {
            moeg = moeg + Double.parseDouble(st.nextToken());
        }

        if(moeg != 0) {
            ergebnis = (int) ((err / moeg) * 100.0);
        } else {
            ergebnis = 0;
        }

        return ergebnis;
    }

    public String getFachTitel(int i) {
        if(getAnzahlFaecher() <= i) {
            return null;
        }
        String[] alle = getAlleNamen();
        return alle[i];
    }

    public String[] getAlleNamen() {
        String[] arr = new String[getAnzahlFaecher()];
        for(int i=0; i < getAnzahlFaecher(); i++) {
            arr[i] = getName(getZeile(i));
        }
        return arr;
    }

    public String getName(String x) {
        if(x.equals("")) {
            return "LOL";
        }
        int s = x.indexOf("\"Name\"") + 8;
        int e = (x.substring(s)).indexOf("\"");
        return x.substring(s, s+e);
    }

    public int getAnzahlFaecher() {
        int anzahl = 0;
        String x = getGanzeDatei();
        Scanner scanner = new Scanner(x);
        while(scanner.hasNextLine()) {
            anzahl++;
            scanner.nextLine();
        }
        return anzahl;
    }

    public void newFach(String titel, int prozent, int anzahlUebungen) {
        try {
            String str = "{\"Name\":\"" + titel + "\";\"ErreichtePunkte\":\"\";\"MoeglichePunkte\":\"\";\"benoetigteProzent\":\"" + prozent + "\";\"AnzahlUebungen\":\"" + anzahlUebungen + "\"}\n";
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_APPEND));
            write.append(str);
            write.close();
            printDatei();
        } catch (Exception fnfe) {
            fnfe.printStackTrace();
        }
    }

    public boolean isValiderTitel(String titel) {
        if(titel.contains(";") || titel.equals("") || titel.contains("\"") || titel.contains("{") || titel.contains("}")
                || titel.contains("AnzahlUebungen") || titel.contains("ErreichtePunkte") || titel.contains("MoeglichePunkte")
                || titel.contains("Name") || titel.contains("benoetigteProzent")) {
            return false;
        }
        return true;
    }

    public boolean isTitelVergeben(String alt, String neu) {
        String[] alle = getAlleNamen();
            for (String s : alle) {
                if (neu.toLowerCase().equals(s.toLowerCase())) {
                    if(alt == null) {
                        return true;
                    } else {
                        if(!s.toLowerCase().equals(alt.toLowerCase())) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    public double getMostCommonMaxPoints(String titel) {
        double[] allMaxPoints = new double[getAnzahlUebungen(titel)];
        for(int i=0; i < getAnzahlUebungen(titel); i++) {
            allMaxPoints[i] = getUeb(titel, i)[1];
        }
        int count = 1;
        int tempCount = 0;
        double popular = allMaxPoints[0];
        double temp = 0.0;
        for(int i=0; i < allMaxPoints.length - 1; i++) {
            temp = allMaxPoints[i];
            tempCount = 0;
            for(int j=1; j < allMaxPoints.length; j++) {
                if(temp == allMaxPoints[j]) {
                    tempCount++;
                }
            }
            if(tempCount > count) {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    public String fachAddenOderAendern(String titel, String neu, int prozent, int anzahlUebungen) {
        if(prozent < 0 || prozent > 100) {
            return "Prozentangabe muss zwischen 0 und 100 liegen";
        }
        if(anzahlUebungen < 1) {
            return "Mindestens eine Übung erforderlich";
        }
        if(titel == null) {
            return "Es ist ein Fehler aufgetreten";
        }
        String[] alle = getAlleNamen();
        if(titel.equals("")) {
            return "Ungültiger Titel";
        }
        if(neu == null) {
            for(String s : alle) {
                if(titel.toLowerCase().equals(s.toLowerCase())) {
                    return "Titel bereits vergeben!";
                }
            }
            if(!isValiderTitel(titel)) {
                return "Titel enthält ungültige Zeichen/Worte";
            }
            newFach(titel, prozent, anzahlUebungen);
        } else {
            if(neu.equals("")) {
                return "Ungültiger Titel";
            }
            for (String s : alle) {
                if (neu.toLowerCase().equals(s.toLowerCase()) && !neu.toLowerCase().equals(titel.toLowerCase())) {
                    return "Titel bereits vergeben";
                }
            }
            if (!isValiderTitel(neu)) {
                return "Neuer Titel enthält ungültige Zeichen/Worte";
            }
            namenAendern(titel, neu);
            anzahlUebAendern(neu, anzahlUebungen);
            prozentAendern(neu, prozent);
        }
        return null;
    }

    public int getVorrAnzahlUeb(String titel) {
        String zeile = getZeile(titel);
        int start = zeile.indexOf("\"AnzahlUebungen\":\"") + "\"AnzahlUebungen\":\"".length();
        zeile = zeile.substring(start);
        int ende = zeile.indexOf("\"");
        return Integer.parseInt(zeile.substring(0, ende));
    }

    synchronized public void addUebung(String titel, double pkt, double max) {
        String ganz = "";
        String zeile = "";
        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = context.openFileInput(filename);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        String s = "";
        String alt = "";
        try {
            while((s =br.readLine()) != null) {

                ganz = ganz + s + "\n";
                if (getName(s).equals(titel)) {
                    alt = s;
                    zeile = s;
                    int start = s.indexOf("\"ErreichtePunkte\":\"") + "\"ErreichtePunkte\":\"".length();
                    s = s.substring(start);
                    s = s.substring(0, s.indexOf("\""));
                    String change = "";
                    if(getAnzahlUebungen(titel) > 0) {
                        change = s + "," + pkt;
                    } else {
                        change = s + pkt;
                    }

                    zeile = zeile.replace("{\"Name\":\"" + titel + "\";\"ErreichtePunkte\":\"" + s, "{\"Name\":\"" + titel + "\";\"ErreichtePunkte\":\"" + change);

                    s = zeile;
                    start = s.indexOf("\"MoeglichePunkte\":\"") + "\"MoeglichePunkte\":\"".length();
                    s = s.substring(start);
                    s = s.substring(0, s.indexOf("\""));
                    if(getAnzahlUebungen(titel) > 0) {
                        change = s + "," + max;
                    } else {
                        change = s + max;
                    }

                    zeile = zeile.replace("\";\"MoeglichePunkte\":\"" + s, "\";\"MoeglichePunkte\":\"" + change);

                    ganz = ganz.replace(alt, zeile);
                }
            }
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write(ganz);
            write.close();
            br.close();
            fis.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Komplette Datei auf der Konsole ausgeben
    public void printDatei() {
        for(int i = 0; i < getAnzahlFaecher(); i++) {
            System.out.println("Zeile " + i + ": " + getZeile(i));
        }
    }

    // Komplette Datei löschen
    public void clear() {
        try {
            OutputStreamWriter write = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            write.write("");
            write.close();
        } catch (Exception fnfe) {
            fnfe.printStackTrace();
        }
    }
}
