package Ueb4;

import Fabrik.Kontofabrik;
import Observer.*;
import Ueb3.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Diese Klasse verwaltet Konto, Girokonto, Sparbuch
 * @author Nathanael Isaac Hermanto s0570619
 */
public class Bank {
    /**
     * Eine Liste von Beobachter
     */
    private List<Beobachter> beobachterList = new LinkedList<>();

    /**
     * PropertyChangesupport
     */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Nachricht fuer nicht existierendes Konto
     */
    private final static String message = "Konto nicht existiert";
    /**
     * Map(java.util) myBank zum Speichern Konto
     */
    private Map<Long, Konto> myBank;

    /**
     * die Bankleizahl
     */
    private final long bankleitzahl;
    /**
     * die großteste benutzte nummer
     */
    private long biggestAccountNumber = 0;

    /**
     * erzeugt ein Bank mit bankleitzahl
     * @param bankleitzahl die Bankleitzahl
     */
    public Bank(long bankleitzahl) throws IllegalArgumentException{
        if(bankleitzahl<0) throw new IllegalArgumentException("Die Bankleitzahl muss positiv sein");
        this.bankleitzahl=bankleitzahl;
        this.myBank = new TreeMap<>();
        this.benachrichtigen();
    }

    /**
     * @return bankleitzahl
     */
    public long getBankleitzahl(){
        return this.bankleitzahl;
    }

    /**
     * erstellt ein neues girokonto mit nummer biggestAccountNumber+1 von kunde inhaber
     * @param inhaber der inhaber des kontos
     * @return die nummer von girokonto
     */
    public long girokontoErstellen(Kunde inhaber) throws IllegalArgumentException{
        if(inhaber==null) throw new IllegalArgumentException("Kunde darf nicht null sein");
        long b = biggestAccountNumber+1;
        Girokonto g = new Girokonto(inhaber,b);
        myBank.put(b,g);
        biggestAccountNumber++;
        this.benachrichtigen();
        return b;
    }

    /**
     * erstellt ein neues Sparbuch mit Nummer biggestAccountNumber+1 von Kunde Inhaber
     * @param inhaber der Inhaber des Kontos
     * @return die Nummer von Girokonto
     */
    public long sparbuchErstellen(Kunde inhaber) throws IllegalArgumentException{
        if(inhaber==null) throw new IllegalArgumentException("Kunde darf nicht null sein");
        long b = biggestAccountNumber+1;
        Sparbuch s = new Sparbuch(inhaber,b);
        myBank.put(b,s);
        biggestAccountNumber++;
        this.benachrichtigen();
        return b;
    }

    public long kontoErstellen(Kontofabrik fabrik, Kunde inhaber){
        if((inhaber==null) || (fabrik==null))
            throw new IllegalArgumentException("Kunde oder fabrik darf nicht null sein");
        long b = biggestAccountNumber+1;
        Konto s = fabrik.kontoErstellen(inhaber,b);
        myBank.put(b,s);
        biggestAccountNumber++;
        this.benachrichtigen();

        List<PropertyChangeListener> l = new LinkedList<>();
        l.add(new KontostandBeobachter());
        l.add(new InhaberBeobachter());
        l.add(new WaehrungBeobachter());
        l.add(new SperrenBeobachter());

        s.addPropertyChangeListener(l);

        return b;
    }

    /**
     * liefert eine Liste aller gültigen Kontonummern in der Bank
     * @return Linkedlist<Long>
     */
    public List<Long> getAlleKontonummern(){
       return new LinkedList(myBank.keySet());
    }

    /**
     * liefert alle kontonnummer und kontostand
     *
     * @return String von alle konto informationen
     */
    public String getAlleKonten() {
        StringBuilder sb = new StringBuilder();

        for(Map.Entry<Long,Konto> entry : myBank.entrySet()) {
            Long key = entry.getKey();
            Konto value = entry.getValue();
            sb.append("Kontonummer: ").append(key).append("|| Kontostand: ").append(value.getKontostand()).append("\n");
        }

        return sb.toString();
    }

    /**
     * hebt den Betrag vom Konto mit der Nummer von ab und gibt zurück, ob die Abhebung geklappt hat.
     * @param von die Kontonummer
     * @param betrag zu abhebender Betrag
     * @return true wenn es geklappt hat, sonst false
     */
    public boolean geldAbheben(long von, double betrag) throws KontoNichtExistiertException{
        if(myBank.containsKey(von)){
            Konto k = myBank.get(von);
            try{
                return k.abheben(betrag);
            } catch (GesperrtException e){
                System.out.println(e);
                return false;
            }
        } else
            throw new KontoNichtExistiertException(message);

    }

    /**
     * zahlt den angegebenen Betrag auf das Konto mit der Nummer auf ein
     * @param auf die Kontonummer
     * @param betrag zu einzahlender Betrag
     */
    public void geldEinzahlen(long auf, double betrag)throws KontoNichtExistiertException{
        if(myBank.containsKey(auf)){
            try{
                myBank.get(auf).einzahlen(betrag);
            } catch (IllegalArgumentException e){
                System.out.println(e);
            }
        } else
            throw new KontoNichtExistiertException(message);
    }

    /**
     * loesche ein Konto von dem Bank
     * @param nummer Kontonummer
     * @return true wenn das Konto geloescht ist, sonst false
     */
    public boolean kontoLoeschen(long nummer){
        if(myBank.containsKey(nummer)){
            myBank.remove(nummer);
            this.benachrichtigen();
            return true;
        }else
            return false;
    }

    /**
     * Methode zum Senden und Empfangen eine Ueberweisung
     * @param vonKontonr Long Sender
     * @param nachKontonr Long Empfaenger
     * @param betrag double der Betrag
     * @param verwendungszweck string Verwendungszweck
     * @return true wenn eine ueberweisung erfolgreich ist, sonst false
     * @throws Exception
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) throws Exception {
        //Absender nicht existiert
        if(!(myBank.containsKey(vonKontonr)))
            throw new Exception("Absender nicht existiert");
        //Empfaenger nicht existiert
        if(!(myBank.containsKey(nachKontonr)))
            throw new Exception("Empfaenger nicht existiert");


        Konto sender = myBank.get(vonKontonr);
        Konto receiver = myBank.get(nachKontonr);
        boolean success = false;


        //ueberweisen
        try {
            success = ((Ueberweisungsfaehig) sender).ueberweisungAbsenden(betrag, receiver.getInhaber().getName(), nachKontonr, this.bankleitzahl, verwendungszweck);
        } catch (GesperrtException e){
            System.out.println(e);
            return false;
        }

        //Uerberweisung empfangen wenn ueberweisungAbsenden geklappt hat
        if(success){
            try {
                ((Ueberweisungsfaehig)receiver).ueberweisungEmpfangen(betrag,sender.getInhaber().getName(), vonKontonr,this.bankleitzahl,verwendungszweck);
                return true;
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Ueberweisung ist nicht erfolgreich, Geld ist zurueck zu Absender");
                sender.einzahlen(betrag);
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * fügt das gegebene Konto k (bei dem es sich genaugenommen um ein Mock-Objekt
     * handeln sollte) in die Kontenliste der Bank ein und liefert die dabei von der Bank vergebene
     * Kontonummer zurück
     * @param k Konto
     * @return Kontonummer von k
     * @throws IllegalArgumentException wenn konto = null
     */
    public long mockEinfuegen(Konto k) throws IllegalArgumentException{
        if(k==null) throw new IllegalArgumentException("Konto darf nicht null sein");
        long b = biggestAccountNumber+1;
        myBank.put(b,k);
        biggestAccountNumber++;
        return b;
    }

    /**
     * liefert den Kontostand des Kontos mit der angegebenen nummer zurück.
     *
     * @param nummer Nummer des Kontos, dessen Kontostand zurückgeliefert wird.
     * @return der Kontostand
     * @throws KontoNichtExistiertException wenn die angegebene Kontonummer in der Kontoliste nicht enthalten ist.
     */
    public double getKontostand(long nummer) throws KontoNichtExistiertException {
        if (!myBank.containsKey(nummer)) {
            throw new KontoNichtExistiertException(message);
        }

        Konto k = myBank.get(nummer);
        return k.getKontostand();
    }

    @Override
    public String toString() {
        return "Bank{" +
                "myBank=" + myBank +
                ", bankleitzahl=" + bankleitzahl +
                '}';
    }

    /**
     * die Methode sperrt alle Konten, deren Kontostand im Minus ist.
     */
    public void pleitgeierSperren(){
        Stream<Konto> kontoStream = myBank.values().stream();
        kontoStream.forEach(konto ->{
            if(konto.getKontostand()<0)
                konto.sperren();//sperrt das Konto wenn der Kontostand minus is
        });
    }

    /**
     * Die Methode liefert eine Liste aller Kunden, die auf einem Konto einen Kontostand haben, der mindestens minimum beträgt.
     * @param minimum minimum Betrag
     * @return List von Kunden mit mind. minimum Kontostand
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum){
        Stream<Konto> kontoStream = myBank.values().stream();

        return kontoStream
                .filter(konto -> konto.getKontostand()>=minimum) //filtert den Stream
                .map(Konto::getInhaber)//nimmt nur die Inhaber
                .collect(Collectors.toList());
    }

    /**
     * liefert die Namen und Geburtstage aller Kunden der Bank
     * @return String sortierte Namen und Geburtstage nach Geburtsdatum
     */
    public String getKundengeburtstage(){
        Stream<Kunde> kundeStream = myBank
                .values()
                .stream()
                .map(Konto::getInhaber)
                .distinct()
                .sorted((kunde1,kunde2) -> kunde1.getGeburtstag().compareTo(kunde2.getGeburtstag()));

        StringBuilder sb = new StringBuilder();

        kundeStream.forEach(kunde -> {
            String s = "Name: "+kunde.getName()+ " "+kunde.getNachname()+", Geburtstag: "+kunde.getGeburtstag();
            sb.append(s);
        });

        return sb.toString();
    }

    /**
     * liefert eine Liste aller freien Kontonummern, die im von Ihnen vergebenen Bereich liegen
     * @return List<Long> liste von freien Kontonummern
     */
    public List<Long> getKontonummernLuecken(){
        if(myBank.isEmpty()) return new ArrayList<Long>();

        return LongStream
                .rangeClosed(1,biggestAccountNumber)
                .boxed()
                .filter(num -> !myBank.containsKey(num))
                .collect(Collectors.toList());
    }


    /**
     * liefert eine vollständige Kopie von this zurueck
     * @return Bank clone
     */
    public Bank clone() throws CloneNotSupportedException {
        byte[] array;

        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bout)){
            out.writeObject(this);
            array = bout.toByteArray();
        } catch (NotSerializableException e){
            throw new CloneNotSupportedException();
        }  catch (IOException e) {
            return null;
        }

        try (ByteArrayInputStream bin = new ByteArrayInputStream(array);
             ObjectInputStream in = new ObjectInputStream(bin)){
            return (Bank) in.readObject();
        }  catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }


    /**
     * Fuege einen Beobachter zu der Liste
     * @param b Beobachter
     */
    public void addBeobachter(Beobachter b){
        if(b != null)
            beobachterList.add(b);
    }

    /**
     * Loesche einen Beobachter von der Liste
     * @param b Beobachter
     */
    public void removeBeobachter(Beobachter b){
        beobachterList.remove(b);
    }

    /**
     * benachrichtige alle Beobachter von der Liste
     */
    protected void benachrichtigen(){
        beobachterList.forEach(b -> b.aktualisieren(this));
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl){
        support.removePropertyChangeListener(pcl);
    }
}
