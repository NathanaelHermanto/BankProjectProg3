package Fabrik;

import Ueb3.Konto;
import Ueb3.Kunde;

public abstract class Kontofabrik {

    /**
     * erstellt ein neues Konto
     * @param inhaber der Inhaber
     * @return konto
     */
    public abstract Konto kontoErstellen(Kunde inhaber, long kontonummer);
}
