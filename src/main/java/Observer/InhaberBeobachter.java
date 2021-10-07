package Observer;

import Ueb3.Kunde;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * zeigt die aktuellen Aenderungen bei dem Inhaber an
 */
public class InhaberBeobachter implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("Inhaber")){
            Kunde alt = (Kunde) evt.getOldValue();
            Kunde neu = (Kunde) evt.getNewValue();
            System.out.println("Alter Inhaber: "+alt+"|| Neuer Inhaber: "+neu);
        }
    }
}
