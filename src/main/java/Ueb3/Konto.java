package Ueb3;

import Observer.Beobachter;
import javafx.beans.property.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.LinkedList;
import java.util.List;


/**
 * stellt ein allgemeines Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable, Cloneable
{
	/**
	 * PropertyChangesupport
	 */
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	/** 
	 * der Kontoinhaber
	 */
	private Kunde inhaber;

	/**
	 * die Kontonummer
	 */
	private final long nummer;

	/**
	 * der aktuelle Kontostand
	 */
	private ReadOnlyDoubleWrapper kontostand;

	/**
	 * die Waehrung
	 */
	private Waehrung waehrung;

	/**
	 * Setter fuer PropertyChangeSupport
	 * @param support PropertyChangeSupport
	 */
	public void setSupport(PropertyChangeSupport support) {
		this.support = support;
	}

	/**
	 * gibt das ReadOnlyDoubleProperty von Kontostand zurueck
	 * @return ReadOnlyDoubleProperty
	 */
	public ReadOnlyDoubleProperty kontostandProperty() {
		return kontostand.getReadOnlyProperty();
	}

	/**
	 * setzt den aktuellen Kontostand
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		support.firePropertyChange("Kontostand", this.getKontostand(), kontostand);
		this.kontostand.set(kontostand);
		if(this.getKontostand()<0) this.kontostandMinus.set(false);
		else this.kontostandMinus.set(true);
	}


	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), k�nnen keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers w�ren (abheben, Inhaberwechsel)
	 */
	private BooleanProperty gesperrt;

	/**
	 * true wenn der Kontostand minus ist
	 */
	private ReadOnlyBooleanWrapper kontostandMinus = new ReadOnlyBooleanWrapper(true);

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anf�ngliche Kontostand wird auf 0 gesetzt.
	 * setzt waehrung EUR
	 * @param inhaber der Inhaber
	 * @param kontonummer die gew�nschte Kontonummer
	 * @throws IllegalArgumentException wenn der Inhaber null
	 */
	public Konto(Kunde inhaber, long kontonummer) {
		if(inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostand = new ReadOnlyDoubleWrapper(0);
		this.gesperrt = new SimpleBooleanProperty(false);
		this.waehrung = Waehrung.EUR;
	}

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anf�ngliche Kontostand wird auf 0 gesetzt.
	 *
	 * @param inhaber der Inhaber
	 * @param kontonummer die gew�nschte Kontonummer
	 * @param w1 die Waehrung
	 * @throws IllegalArgumentException wenn der Inhaber null
	 */
	public Konto(Kunde inhaber, long kontonummer, Waehrung w1) {
		if(inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostand = new ReadOnlyDoubleWrapper(0);
		this.gesperrt = new SimpleBooleanProperty(false);
		this.waehrung = w1;
	}
	
	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567);
	}

	/**
	 * liefert den Kontoinhaber zur�ck
	 * @return   der Inhaber
	 */
	public Kunde getInhaber() {
		return this.inhaber;
	}
	
	/**
	 * setzt den Kontoinhaber
	 * @param kinh   neuer Kontoinhaber
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException{
		if (kinh == null)
			throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
		if(this.isGesperrt())
			throw new GesperrtException(this.nummer);
		support.firePropertyChange("Inhaber", this.getInhaber(), kinh);
		this.inhaber = kinh;

	}
	
	/**
	 * liefert den aktuellen Kontostand
	 * @return   double
	 */
	public double getKontostand() {
		return kontostand.getValue();
	}

	/**
	 * liefert die Kontonummer zur�ck
	 * @return   long
	 */
	public long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zur�ck, ob das Konto gesperrt ist oder nicht
	 * @return true, wenn das Konto gesperrt ist
	 */
	public boolean isGesperrt() {   //Getter, aber eben f�r booleschen Wert
		return gesperrt.getValue();
	}

	/**
	 * liefert die Währung zurück, in der das Konto aktuell geführt wird.
	 * @return this-waehrung
	 */
	public Waehrung getWaehrung() {
		return waehrung;
	}

	/**
	 * setzt neue Waehrung
	 * @param waehrung neue Waehrung
	 */
	public void setWaehrung(Waehrung waehrung) {
		this.waehrung = waehrung;

	}

	/**
	 * wechselt die Währung, in der das Konto aktuell geführt wird.
	 *
	 * @param neu (EUR/BGN/KM/LTL)
	 */
	public void waehrungswechsel(Waehrung neu) {
		kontostand.set(Waehrung.exchange(getKontostand(),waehrung,neu));
		spezifischUmrechnen(neu);
		support.firePropertyChange("Waehrung", this.getWaehrung(), neu);
		this.waehrung=neu;

	}

	/**
	 * Diese Methode rechnet bei einem Währungswechsel alle weiteren Attribute des Kontos um
	 * außer dem Kontostand
	 *
	 * @param neu die neue Währung
	 */
	protected abstract void spezifischUmrechnen(Waehrung neu);

	/**
	 * Erh�ht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist 
	 */
	public void einzahlen(double betrag) {
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		setKontostand(getKontostand() + betrag);

	}

	/**
	 * zahlt den in der Währung w angegebenen Betrag ein.
	 * Erh�ht den Kontostand um den eingezahlten Betrag.
	 * @param betrag double
	 * @param w die Waehrung
	 */
	public void einzahlen(double betrag, Waehrung w) throws IllegalArgumentException{
		betrag = Waehrung.exchange(betrag,w,this.waehrung);
		einzahlen(betrag);
	}
	
	/**
	 * Gibt eine Zeichenkettendarstellung der Kontodaten zur�ck.
	 */
	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
				+ System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + this.kontostand + " " + this.getWaehrung();
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}
	
	/**
	 * dient rein didaktischen Zwecken, geh�rt hier eigentlich nicht her
	 */
	public void ausgeben()
	{
		System.out.println(this.toString());
	}

	/**
	 * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist.
	 *
	 * @param betrag double
	 * @return true, wenn die Abhebung geklappt hat,
	 * false, wenn sie abgelehnt wurde
	 * @throws GesperrtException        wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */
	public boolean abheben(double betrag) throws GesperrtException, IllegalArgumentException {
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Betrag ungültig");
		}
		if(this.isGesperrt())
			throw new GesperrtException(this.getKontonummer());

		if(!this.pruefAbhebeBedingung(betrag)) return false;

		this.setKontostand(this.getKontostand()-betrag);
		nachAbhebung(betrag);
		return true;
	}

	/**
	 * Mit dieser Methode wird der geforderte Betrag in der Waehrung w vom Konto abgehoben, wenn es nicht gesperrt ist.
	 *
	 * @param betrag double
	 * @param w      die Waehrung
	 * @return true, wenn die Abhebung geklappt hat,
	 * false, wenn sie abgelehnt wurde
	 * @throws GesperrtException        wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */
	public boolean abheben(double betrag, Waehrung w)
			throws GesperrtException {
		betrag=Waehrung.exchange(betrag,w,this.waehrung);
		return abheben(betrag);
	}

	/**
	 * Prüft, ob Konditionen zur Abhebung eingehalten werden
	 *
	 * @return true - Abhebung möglich<br>
	 * false - Abhebung nicht möglich
	 */
	public abstract boolean pruefAbhebeBedingung(double betrag);

	/**
	 * Führt Aktionen nach Abhebung durch
	 *
	 * @param betrag double
	 */
	protected void nachAbhebung(double betrag) {}

	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr m�glich.
	 */
	public final void sperren() {
		support.firePropertyChange("Sperren", this.isGesperrt(), true);
		this.gesperrt.set(true);
	}

	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder m�glich.
	 */
	public final void entsperren() {
		support.firePropertyChange("Sperren", this.isGesperrt(), false);
		this.gesperrt.set(false);
	}
	
	
	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public final String getGesperrtText()
	{
		if (this.isGesperrt())
		{
			return "GESPERRT";
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * liefert die ordentlich formatierte Kontonummer
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert()
	{
		return String.format("%d", this.nummer);
	}
	
	/**
	 * liefert den ordentlich formatierten Kontostand
	 * @return formatierter Kontostand mit 2 Nachkommastellen und W�hrungssymbol
	 */
	public String getKontostandFormatiert()
	{
		return String.format("%.2f "+ getWaehrung() , this.getKontostand());
	}
	
	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich,
	 * wen sie die gleiche Kontonummer haben
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other)
	{
		if(this == other)
			return true;
		if(other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		if(this.nummer == ((Konto)other).nummer)
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode()
	{
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
	}

	@Override
	public int compareTo(Konto other)
	{
		if(other.getKontonummer() > this.getKontonummer())
			return -1;
		if(other.getKontonummer() < this.getKontonummer())
			return 1;
		return 0;
	}

	public void addPropertyChangeListener(List<PropertyChangeListener> list){
		list.forEach(p -> {support.addPropertyChangeListener(p);});
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl){
		support.removePropertyChangeListener(pcl);
	}

	/**
	 * return true wenn kontostand minus ist
	 * @return boolean
	 */
	public boolean isKontostandMinus() {
		return kontostandMinus.get();
	}

	/**
	 * return kontostandMinus (Boolean property)
	 * @return BooleanProperty
	 */
	public final ReadOnlyBooleanProperty kontostandMinusProperty() {
		return kontostandMinus.getReadOnlyProperty();
	}

	/**
	 * setzt kontostandMinus true/false
	 * @param kontostandMinus boolean
	 */
	public void setKontostandMinus(boolean kontostandMinus) {
		this.kontostandMinus.set(kontostandMinus);
	}

	/**
	 * return kontonummer
	 * @return long
 	 */
	public long getNummer() {
		return nummer;
	}

	/**
	 * return gesperrtProperty
	 * @return BooleanProperty
	 */
	public BooleanProperty gesperrtProperty() {
		return gesperrt;
	}
}
