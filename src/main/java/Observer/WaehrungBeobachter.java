package Observer;

import Ueb3.Waehrung;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * zeigt die aktuellen Aenderungen bei der Waehrung an
 */
public class WaehrungBeobachter implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("Waehrung")){
            Waehrung alt = (Waehrung) evt.getOldValue();
            Waehrung neu = (Waehrung) evt.getNewValue();
            System.out.println("Alte Waehrung: "+alt+"|| Neue Waehrung: "+neu);
        }
    }
}
