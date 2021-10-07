package Ueb3;

import java.time.LocalDate;

public class TestMain {
    public static void main(String[] args){
        Kunde k = new Kunde("Bob", "Marley", "California", LocalDate.parse("1950-04-03"));

        Girokonto kgiro = new Girokonto(k, 1111, 1000.0);
        kgiro.einzahlen(50);
        System.out.println(kgiro);

        System.out.println("Einzahlen");
        kgiro.einzahlen(50,Waehrung.BGN);
        System.out.println(kgiro);

        System.out.println("Waehrungswechsel");
        kgiro.waehrungswechsel(Waehrung.BGN);
        System.out.println(kgiro);

        System.out.println("Abheben");
        try {
            System.out.println(kgiro.abheben(50,Waehrung.EUR));
        } catch (GesperrtException e) {
            e.printStackTrace();
        }
        System.out.println(kgiro);

        System.out.println("Einzahlen");
        Sparbuch kSpar = new Sparbuch(k, 2222);
        kSpar.einzahlen(50,Waehrung.KM);
        System.out.println(kSpar);

        System.out.println("Waehrungswechsel");
        kSpar.waehrungswechsel(Waehrung.BGN);
        System.out.println(kSpar);

        System.out.println("Abheben");
        try {
            System.out.println(kSpar.abheben(50,Waehrung.EUR));
        } catch (GesperrtException e) {
            e.printStackTrace();
        }
        System.out.println(kSpar);

        try {
            System.out.println(kSpar.abheben(5,Waehrung.EUR));
        } catch (GesperrtException e) {
            e.printStackTrace();
        }
        System.out.println(kSpar);
    }
}
