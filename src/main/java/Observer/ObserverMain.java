package Observer;

import Fabrik.GirokontoFabrik;
import Ueb4.*;
import Ueb3.*;

public class ObserverMain {
    public static void main(String[] args){
        Bank bank = new Bank(12345);
        bank.addBeobachter(new Konsolenbeobachter());


        long l = bank.kontoErstellen(new GirokontoFabrik(), Kunde.MUSTERMANN);
        try {
            bank.geldEinzahlen(l, 100);
        } catch (KontoNichtExistiertException e) {
            e.printStackTrace();
        }
    }
}
