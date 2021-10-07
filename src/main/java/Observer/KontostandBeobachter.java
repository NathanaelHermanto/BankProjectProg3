package Observer;

import Ueb3.Konto;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 * zeigt die aktuellen Aenderungen bei dem Kontostand an
 */
public class KontostandBeobachter implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("Kontostand")){
            double alt = (double) evt.getOldValue();
            double neu = (double) evt.getNewValue();
            System.out.println("Alter Kontostand: "+alt+"|| Neuer Kontostand: "+neu);
        }
    }
}
