package Controller;

import Ueb3.GesperrtException;
import Ueb3.Girokonto;
import Ueb3.Konto;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class KontoController2 extends Application {
    /**
     * Das Hauptfenster der Anwendung
     */
    private Stage stage;
    /**
     * Das Model mit den aktuellen Daten des Kontos
     */
    @FXML private Konto konto = new Girokonto();

    @FXML private Text ueberschrift;
    @FXML private GridPane anzeige;
    @FXML private Text txtNummer;
    /**
     * Anzeige der Kontonummer
     */
    @FXML private Text nummer;
    @FXML private Text txtStand;
    /**
     * Anzeige des Kontostandes
     */
    @FXML private Text stand;
    @FXML private Text txtGesperrt;
    /**
     * Anzeige und Änderung des Gesperrt-Zustandes
     */
    @FXML private CheckBox gesperrt;
    @FXML private Text txtAdresse;
    /**
     * Anzeige und Änderung der Adresse des Kontoinhabers
     */
    @FXML private TextArea adresse;
    /**
     * Anzeige von Meldungen über Kontoaktionen
     */
    @FXML private Text meldung;
    @FXML private HBox aktionen;
    /**
     * Auswahl des Betrags für eine Kontoaktion
     */
    @FXML private TextField betrag;
    /**
     * löst eine Einzahlung aus
     */
    @FXML private Button einzahlen;
    /**
     * löst eine Abhebung aus
     */
    @FXML private Button abheben;

    @FXML public void initialize()
    //Diese Methode kann vom FXMLLoader automatisch aufgerufen werden
    //initialize wird direkt nach dem Erzeugen der Objekte aufgerufen
    {
        nummer.setText(konto.getKontonummerFormatiert());
        stand.textProperty().bind
                (konto.kontostandProperty().asString());
        gesperrt.setOnAction(e -> {
            if (konto.isGesperrt()) konto.entsperren();
            else konto.sperren();
        });
        gesperrt.textProperty().bind
                (konto.gesperrtProperty().asString());
        adresse.textProperty().bindBidirectional
                (konto.getInhaber().adresseProperty());
        einzahlen.setOnAction(e -> {
            try {
                einzahlen(Double.parseDouble(betrag.getText()));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        });
        abheben.setOnAction(e -> {
            try {
                abheben(Double.parseDouble(betrag.getText()));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        Parent lc = FXMLLoader.load(KontoController2.class.getResource("/KontoOberflaeche.fxml"));

        Scene scene = new Scene(lc, 400, 350);

        stage.setScene(scene);
        stage.setTitle("Kontooberflaeche");
        stage.show();
    }

    /**
     * Ruft abheben methode des Kontos auf
     * @param betrag
     * @return true falls erfolgreich, false falls nicht
     * @throws GesperrtException
     */
    @FXML public void abheben (double betrag) {
        try {
            konto.abheben(betrag);
        } catch (GesperrtException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ruft einzahlen methode des Kontos auf
     * (wrapper method)
     */
    @FXML public void abheben()
    {
        try{
            abheben(Double.parseDouble(betrag.getText()));
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    /**
     * Ruft einzahlen methode des Kontos auf
     * @param betrag
     */
    @FXML public void einzahlen(double betrag)
    {
        konto.einzahlen(betrag);
    }

    /**
     * Ruft einzahlen methode des Kontos auf
     * (wrapper method)
     */
    @FXML public void einzahlen()
    {
        try{
            einzahlen(Double.parseDouble(betrag.getText()));
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
}
