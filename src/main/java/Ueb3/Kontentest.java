package Ueb3;
import java.time.LocalDate;



/**
 * Testprogramm f�r Konten
 * @author Doro
 *
 */
public class Kontentest {

	/**
	 * Testprogramm f�r Konten
	 * @param args wird nicht benutzt
	 */
	public static void main(String[] args) {
		Kunde ich = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));

		Girokonto meinGiro = new Girokonto(ich, 1234, 1000.0);
		meinGiro.einzahlen(50);
		System.out.println(meinGiro);
		
		Sparbuch meinSpar = new Sparbuch(ich, 9876);
		meinSpar.einzahlen(50);
		try
		{
			boolean hatGeklappt = meinSpar.abheben(70);
			System.out.println("Abhebung hat geklappt: " + hatGeklappt);
			System.out.println(meinSpar);
		}
		catch (GesperrtException e)
		{
			System.out.println("Zugriff auf gesperrtes Konto - Polizei rufen!");
		}
		
		//Konto meinKonto = new Konto();
			//abstract == Konstruktoraufruf verboten!
		Konto meinKonto = new Girokonto();   //Upcast
		try {
			boolean hatGeklappt = meinKonto.abheben(10000);
				//Code aus der Klasse Girokonto aufgerufen
			System.out.println(hatGeklappt);
			
			System.out.println(meinKonto.toString());
				//Code in Girokonto aufgerufen
			System.out.println("hier:--------------------------------");
			meinKonto.ausgeben();
				//Auch von Girokonto!!!!
			System.out.println("hier:--------------------------------");

		} catch (GesperrtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Kontoart art;
		art = Kontoart.SPARBUCH;
		System.out.println(art);  //liefert automatisch den Konstantennamen 
		System.out.println(art.name() + " " + art.ordinal());
		//ist verboten, wegen private: art = new Kontoart();
		System.out.println(art.getWerbebotschaft());
		
		System.out.println("Unser Prospekt:");
		Kontoart[] alle = Kontoart.values();
		for(int i=0; i<alle.length; i++)
		{
			System.out.println(alle[i] + ": " + alle[i].getWerbebotschaft());
		}
		
	}

}
