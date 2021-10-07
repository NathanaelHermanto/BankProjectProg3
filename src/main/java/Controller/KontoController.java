package Controller;

import Oberflaeche.KontoOberflaeche;
import Ueb3.GesperrtException;
import Ueb3.Girokonto;
import Ueb3.Konto;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KontoController extends Application {
    private Konto gk = new Girokonto();
    private Stage stage;
    private KontoOberflaeche view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent lc = new KontoOberflaeche(gk, this);
        Scene scene = new Scene(lc, 320, 343);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Konto Info");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Schlie�en des Fensters
     */
    public void schliessen()
    {
        stage.close();
    }

    /**
     * Ruft abheben methode des Kontos auf
     * @param betrag
     * @return true falls erfolgreich, false falls nicht
     * @throws GesperrtException
     */
     public void abheben (double betrag) {
        try {
            if(gk.abheben(betrag))
                view.meldungZeigen("Abhebung erfolgreich");
            else
                view.meldungZeigen("Kontostand zu gering");
        } catch (IllegalArgumentException | GesperrtException e) {
            view.meldungZeigen("Exception beim Abheben");
        }
    }

    /**
     * Ruft einzahlen methode des Kontos auf
     * @param betrag
     * @throws IllegalArgumentException
     */
    public void einzahlen(double betrag) throws IllegalArgumentException
    {
        try {
            gk.einzahlen(betrag);

        } catch (IllegalArgumentException e)
        {

            view.meldungZeigen("Betrag war negativ");
            return;
        }
        view.meldungZeigen("Einzahlung erfolgreich");
    }
}
