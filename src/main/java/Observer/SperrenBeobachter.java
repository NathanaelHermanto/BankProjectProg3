package Observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Beobachter für Kontozustand gesperrt oder nicht
 */
public class SperrenBeobachter implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("Sperren")){
            boolean alt = (boolean) evt.getOldValue();
            boolean neu = (boolean) evt.getNewValue();

            if(alt && !neu) System.out.println("Konto ist entsperrt");

            if(!alt && neu) System.out.println("Konto ist gesperrt");
        }
    }
}
