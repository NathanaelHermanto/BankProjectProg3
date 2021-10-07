package Ueb3;

/**
 * Waehrungen EUR, BGN (Bulgarische Leva), LTL (Litauische Litas) und KM
 * (Konvertible Mark)
 */
public enum Waehrung {
    EUR(1), BGN(1.95583), LTL(1.95583), KM(3.4528);

    /**
     * Wechselkurse in EUR
     */
    private final double kurs;

    Waehrung(double i) {
        this.kurs = i;
    }

    /**
     * @return kurs (die Wechselkurse)
     */
    public double getKurs() {
        return kurs;
    }

    /**
     * EUR --> Waehrung
     * @param betrag betrag in EUR
     * @return betrag in this Waehrung
     */
    public double euroInWaehrungUmrechnen(double betrag){
        return betrag * this.getKurs();
    }

    /**
     * Waehrung --> EUR
     * @param betrag betrag in this Waehrung
     * @return betrag in EUR
     */
    public double waehrungInEuroUmrechnen(double betrag){
        return betrag / this.getKurs();
    }

    /**
     * Auxiliary Method to exchange kurs w1 ---> w2
     * @param betrag double
     * @param w1 old currency
     * @param w2 new currency
     * @return double value in w2
     */
    public static double exchange(double betrag, Waehrung w1, Waehrung w2){
        double betragInEUR = w1.waehrungInEuroUmrechnen(betrag);
        return w2.euroInWaehrungUmrechnen(betragInEUR);
    }



}
