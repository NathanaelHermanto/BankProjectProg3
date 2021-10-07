package MockitoTest;

import Fabrik.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import Ueb3.*;
import Ueb4.Bank;
import Ueb4.KontoNichtExistiertException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test JUNIT Bank mit mockito (aus der Musterlösung)
 *
 */
public class MockBankTest2 {
    Bank bank;
    Kunde kunde;
    Konto k;
    long giro1, giro2, spar1, spar2, nichtVorhanden;
    Girokonto gk1, gk2;
    Sparbuch sb1;
    Kontofabrik mockFabrikS;
    Kontofabrik mockFabrikG;


    @BeforeEach
    public void setUp() throws Exception {
        bank = new Bank(16050000);
        kunde = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));
        gk1 = Mockito.mock(Girokonto.class);

        Bank mockBank = Mockito.mock(Bank.class);

        mockFabrikG = new GirokontoFabrik() {
            public Konto kontoErstellen(Kunde inhaber, long kontoNummer) {
                return gk1;
            }
        };

        Mockito.when(gk1.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);

        Mockito.when(gk1.getInhaber()).thenReturn(kunde);
        Mockito.when(gk1.getKontonummer()).thenReturn(1L);
        Mockito.when(gk1.getKontostand()).thenReturn(1.23);
        Mockito.when(gk1.isGesperrt()).thenReturn(false);
        Mockito.when(gk1.toString()).thenReturn("String-Repräsentation des Girokontos 1");

        Mockito.when(gk1.ueberweisungAbsenden(ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(),
                ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);

        gk2 = Mockito.mock(Girokonto.class);

        Mockito.when(gk2.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
        Mockito.when(gk2.getInhaber()).thenReturn(kunde);
        Mockito.when(gk2.getKontonummer()).thenReturn(3L);
        Mockito.when(gk2.getKontostand()).thenReturn(3.23);
        Mockito.when(gk2.isGesperrt()).thenReturn(false);
        Mockito.when(gk2.toString()).thenReturn("String-Repräsentation des Girokontos 2");
                Mockito.when(gk2.ueberweisungAbsenden(ArgumentMatchers.anyDouble(),
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(),
                        ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);

        sb1 = Mockito.mock(Sparbuch.class);
        mockFabrikS = new SparbuchFabrik() {
            public Konto kontoErstellen(Kunde inhaber, long kontoNummer) {
                return sb1;
            }
        };
        Mockito.when(sb1.abheben(ArgumentMatchers.anyDouble())).thenReturn(true)
        ;
        Mockito.when(sb1.getInhaber()).thenReturn(kunde);
        Mockito.when(sb1.getKontonummerFormatiert()).thenReturn("00 0000 0002");
                Mockito.when(sb1.getKontostand()).thenReturn(2.23);
        Mockito.when(sb1.isGesperrt()).thenReturn(false);
        Mockito.when(sb1.toString()).thenReturn("String-Repräsentation des Sparbuchs 2");

    }
    /**
     * testet, ob der Konstruktor eine leere Bank mit korrekter
     * Bankleitzahl erstellt
     */
    @Test
    public void testKonstruktor()
    {
        List<Long> nummernliste = bank.getAlleKontonummern();
        assertTrue(nummernliste.isEmpty());

        String alleKonten = bank.getAlleKonten();
        assertEquals(alleKonten, "");

        long blz = bank.getBankleitzahl();
        assertEquals(16050000, blz);
        try {
            bank.geldEinzahlen(123, 100);
            fail("Kontonummer existiert in leerer Bank");
        } catch (KontoNichtExistiertException e) {}

    }
    /**
     * Testet die Methode mockEinfuegen() und damit auch
     * (mehr oder weniger) girokontoErstellen() und
     * sparbuchErstellen()
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testSparbuchErstellen() throws KontoNichtExistiertException
    {
        long sparKtnr1 = bank.kontoErstellen(mockFabrikS, kunde);

        Mockito.when(sb1.getKontonummer()).thenReturn(sparKtnr1);

        List<Long> nummernliste = bank.getAlleKontonummern();
        assertTrue(nummernliste.contains(sparKtnr1));
        assertEquals(1, nummernliste.size());
        String alleKonten = bank.getAlleKonten();
        assertTrue(alleKonten.contains(sparKtnr1 + ""));
        assertEquals(2.23, bank.getKontostand(sparKtnr1));

    }

    private void kontenErstellen()
    {
        /*Hilfsmethode, um mehrere Mock-Konten in die Bank einzufügen, mit denen dann die
        weiteren Tests durchgeführt werden können. Für alle Mock-Konten wird die von der
        Bank erzeugte Nummer als Kontonummer (als Rückgabewert von getKontonummer())
        festgelegt.*/
        giro1 = bank.kontoErstellen(mockFabrikG, kunde);
        Mockito.when(gk1.getKontonummer()).thenReturn(giro1);
        giro2 = bank.kontoErstellen(new GirokontoFabrik(){
            public Konto kontoErstellen(Kunde inhaber, long kontoNummer) {
                return gk2;
            }
        }, kunde);
        Mockito.when(gk2.getKontonummer()).thenReturn(giro2);
        spar1 = bank.kontoErstellen(mockFabrikS, kunde);
        Mockito.when(sb1.getKontonummer()).thenReturn(spar1);
        nichtVorhanden = Math.max(Math.max(giro1, giro2), spar1) + 5;

    }
    /**
     * Testet das Erstellen mehrerer Konten
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testKontenErstellen() throws KontoNichtExistiertException {
        kontenErstellen();
        List<Long> nummernliste = bank.getAlleKontonummern();
        assertTrue(nummernliste.contains(giro1));
        assertTrue(nummernliste.contains(giro2));
        assertTrue(nummernliste.contains(spar1));
        assertEquals(3, nummernliste.size());
        String alleKonten = bank.getAlleKonten();
        assertTrue(alleKonten.indexOf(giro1+"") != -1);
        assertTrue(alleKonten.indexOf(giro2+"") != -1);
        assertTrue(alleKonten.indexOf(spar1+"") != -1);
        assertEquals(1.23, bank.getKontostand(giro1));
        assertEquals(3.23, bank.getKontostand(giro2));
        assertEquals(2.23, bank.getKontostand(spar1));

        assertTrue(giro1 != giro2);
        assertTrue(giro1 != spar1);
        assertTrue(giro2 != spar1);
        try {
            bank.geldEinzahlen(nichtVorhanden, 100);
            fail("falsche Kontonummer existiert doch");
        } catch (KontoNichtExistiertException e)
        {}

    }
    /**
     * Testet die Methode kontoLoeschen()
     */
    @Test
    public void testKontoLoeschen() {
        kontenErstellen();
        assertTrue(bank.kontoLoeschen(giro1));
        List<Long> nummernliste = bank.getAlleKontonummern();
        assertFalse(nummernliste.contains(giro1));
        assertEquals(2, nummernliste.size());
        String alleKonten = bank.getAlleKonten();
        assertEquals(-1, alleKonten.indexOf("Kontonummer: " + giro1));

        try {
            bank.geldEinzahlen(giro1, 100);
            fail("falsche Kontonummer existiert doch");
        } catch (KontoNichtExistiertException e)
        {}

        assertEquals(false, bank.kontoLoeschen(giro1));
    }
    /**
     * Testet die Methode kontoLoeschen() mit ungueltiger Kontonummer
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testKontoLoeschenWithIllegalKontonummer() throws
            KontoNichtExistiertException {
        kontenErstellen();
        assertFalse(bank.kontoLoeschen(nichtVorhanden));

        List<Long> nummernliste = bank.getAlleKontonummern();
        assertTrue(nummernliste.contains(giro1));
        assertTrue(nummernliste.contains(giro2));
        assertTrue(nummernliste.contains(spar1));
        assertEquals(3, nummernliste.size());
        String alleKonten = bank.getAlleKonten();
        assertTrue(alleKonten.indexOf(giro1+"") != -1);
        assertTrue(alleKonten.indexOf(giro2+"") != -1);
        assertTrue(alleKonten.indexOf(spar1+"") != -1);
        assertEquals(1.23, bank.getKontostand(giro1));
        assertEquals(3.23, bank.getKontostand(giro2));
        assertEquals(2.23, bank.getKontostand(spar1));
    }
    @Test
    public void testEinzahlenAufAlleKonten() throws
            KontoNichtExistiertException
    {
        kontenErstellen();
        bank.geldEinzahlen(giro1, 100);
        bank.geldEinzahlen(giro2, 200);
        bank.geldEinzahlen(spar1, 300);

        Mockito.verify(gk1).einzahlen(100);
        Mockito.verify(gk2).einzahlen(200);
        Mockito.verify(sb1).einzahlen(300);
    }

    /**
     * Testet das Abheben eines Betrags von einem Konto
     * @throws GesperrtException
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testGeldAbhebenKlappt() throws KontoNichtExistiertException, GesperrtException {
        kontenErstellen();
        boolean hatGeklappt = false;
        hatGeklappt = bank.geldAbheben(giro1, 60);
        assertEquals(true, hatGeklappt);
        Mockito.verify(gk1).abheben(60);
    }
    /**
     * Testet das Abheben eines Betrags von einem Konto mit ungueltiger
     * Kontonummer
     * @throws GesperrtException
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testGeldAbhebenKontoWithIllegalKontonummer() throws
            KontoNichtExistiertException, GesperrtException {
        kontenErstellen();
        try {
            bank.geldAbheben(nichtVorhanden, 1);
            fail("nicht vorhandene Kontonummer wurde gefunden");
        }
        catch (KontoNichtExistiertException e) {}

    }
    /**
     * Testet das Abheben eines zu großen Betrags
     * @throws GesperrtException
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testGeldAbhebenZuViel() throws KontoNichtExistiertException, GesperrtException {
        kontenErstellen();

        Mockito.when(sb1.abheben(ArgumentMatchers.anyDouble())).thenReturn(false
        );
        boolean hatGeklappt = false;
        hatGeklappt = bank.geldAbheben(spar1, 500);
        Mockito.verify(sb1).abheben(500);
        assertEquals(false, hatGeklappt);

    }

    /**
     * Testet das Abheben eines negativen Betrags von einem Konto
     * @throws KontoNichtExistiertException
     */
    @Test
    public void testGeldAbhebenMitNegativemBetrag() throws
            KontoNichtExistiertException, GesperrtException{

        kontenErstellen();
        Mockito.when(gk1.abheben(ArgumentMatchers.anyDouble()))
                .thenThrow(new IllegalArgumentException());
        try {
            bank.geldAbheben(giro1, 100);
            fail("Negativer Betrag wurde abgehoben");
        }
        catch (IllegalArgumentException e) {}
    }

}
