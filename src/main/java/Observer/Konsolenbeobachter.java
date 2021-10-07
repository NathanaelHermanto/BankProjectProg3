package Observer;

import Ueb3.Konto;
import Ueb4.Bank;

public class Konsolenbeobachter implements Beobachter {
    @Override
    public void aktualisieren(Bank b) {
        System.out.println("Es gibt eine Aenderung in der Kontoliste der Bank" +
                System.lineSeparator() +
                b.getAlleKonten()+ System.lineSeparator());
    }

    @Override
    public void aktualisieren(Konto k) {

    }
}
