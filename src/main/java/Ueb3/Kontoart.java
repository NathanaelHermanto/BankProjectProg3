package Ueb3;

/**
 * Liste von angebotenen Kontoarten
 *
 */
public enum Kontoart { //ist genau genommen eine Klasse
	GIROKONTO("Ganz hoher Dispo"), 
	SPARBUCH("ganz viele Zinsen"), 
	FESTGELDKONTO("kommt bald noch...");
		//statische Konstanten
		//public static final Kontoart GIROKONTO 
	//                                    = new Kontoart("Ganz hoher Dispo");

	private final String werbebotschaft;
	
	Kontoart(String werbung) //ist private, auch wenn man's nicht hinschreibt...
	{
		this.werbebotschaft = werbung;
	}

	/**
	 * @return the werbebotschaft
	 */
	public String getWerbebotschaft() {
		return this.werbebotschaft;
	}
	
	
}
