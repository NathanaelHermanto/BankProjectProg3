package Fabrik;

import Ueb3.Girokonto;
import Ueb3.Konto;
import Ueb3.Kunde;

public class GirokontoFabrik extends Kontofabrik {
    @Override
    public Konto kontoErstellen(Kunde inhaber, long kontonummer) {
        return new Girokonto(inhaber, kontonummer);
    }
}
