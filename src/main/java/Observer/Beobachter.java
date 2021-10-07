package Observer;

import Ueb3.Konto;
import Ueb4.Bank;

/**
 * interface observer
 */
public interface Beobachter {
    /**
     * aktualisiere neue Aenderungen bei dem Bank
     * @param b Bank
     */
    public void aktualisieren(Bank b);

    /**
     * aktualisiere neue Aenderungen bei einem Konto
     * @param k
     */
    public void aktualisieren(Konto k);

}
