package Ueb3;

/**
 * tritt bei einem sch�digenden Zugriff auf ein gesperrtes Konto auf
 * @author Doro
 *
 */
@SuppressWarnings("serial")
public class GesperrtException extends Exception {
	
	/**
	 * Zugriff auf das Konto mit der angegebenen Kontonummer
	 * @param kontonummer die Nummer des gesperrten Kontos, auf das zugegriffen wurde
	 */
	public GesperrtException(long kontonummer)
	{
		super("Zugriff auf gesperrtes Konto mit Kontonummer " + kontonummer);
	}
	

}
