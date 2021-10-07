package Ueb3;



/**
 * Ein Girokonto
 * @author Doro
 *
 */
public class Girokonto extends Konto implements Ueberweisungsfaehig{
	/**
	 * Wert, bis zu dem das Konto �berzogen werden darf
	 */
	private double dispo;

	/**
	 * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto
	 * von Herrn MUSTERMANN
	 */
	public Girokonto()
	{
		super(Kunde.MUSTERMANN, 99887766);
		this.dispo = 500;
	}

	/**
	 * erzeugt ein Girokonto mit den angegebenen Werten mit default dispo 2000
	 * @param inhaber Kontoinhaber
	 * @param nummer Kontonummer
	 */
	public Girokonto(Kunde inhaber, long nummer) {
		super(inhaber, nummer);
		this.dispo = 2000;
	}

	/*@Override
	public void waehrungswechsel(Waehrung neu) {
		dispo=Waehrung.exchange(dispo,this.getWaehrung(),neu);
		super.waehrungswechsel(neu);

	}*/

	@Override
	protected void spezifischUmrechnen(Waehrung neu) {
		dispo = Waehrung.exchange(dispo, this.getWaehrung(), neu);
	}

	/**
	 * erzeugt ein Girokonto mit den angegebenen Werten
	 * @param inhaber Kontoinhaber
	 * @param nummer Kontonummer
	 * @param dispo Dispo
	 * @throws IllegalArgumentException wenn der inhaber null ist oder der angegebene dispo negativ bzw. NaN ist
	 */
	public Girokonto(Kunde inhaber, long nummer, double dispo)
	{
		//nur Kommentare...
		super(inhaber, nummer);
		if(dispo < 0 || Double.isNaN(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht g�ltig!");
		this.dispo = dispo;
	}
	
	/**
	 * liefert den Dispo
	 * @return Dispo von this
	 */
	public double getDispo() {
		return dispo;
	}

	/**
	 * setzt den Dispo neu
	 * @param dispo muss gr��er sein als 0
	 * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
	 */
	public void setDispo(double dispo) {
		if(dispo < 0 || Double.isNaN(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
		this.dispo = dispo;
	}
	
	@Override
    public boolean ueberweisungAbsenden(double betrag, 
    		String empfaenger, long nachKontonr, 
    		long nachBlz, String verwendungszweck) 
    				throws GesperrtException 
    {
      if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (betrag < 0 || Double.isNaN(betrag) || empfaenger == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        if (getKontostand() - betrag >= - dispo)
        {
            setKontostand(getKontostand() - betrag);
            return true;
        }
        else
        	return false;
    }

    @Override
    public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz, String verwendungszweck)
    {
        if (betrag < 0 || Double.isNaN(betrag) || vonName == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        setKontostand(getKontostand() + betrag);
    }
    
    @Override
    public String toString()
    {
		return "-- GIROKONTO --" + System.lineSeparator() +
		super.toString()
		+ "Dispo: " + this.dispo + " "+ this.getWaehrung()+ System.lineSeparator();
    }

	@Override
	public boolean pruefAbhebeBedingung(double betrag) {
		if (getKontostand() - betrag >= -dispo)
			return true;
		return false;
	}


}
