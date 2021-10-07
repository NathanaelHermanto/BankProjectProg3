package Fabrik;

import Ueb3.Konto;
import Ueb3.Kunde;
import Ueb3.Sparbuch;

public class SparbuchFabrik extends Kontofabrik{
    @Override
    public Konto kontoErstellen(Kunde inhaber, long kontonummer) {
        return new Sparbuch(inhaber,kontonummer);
    }
}
