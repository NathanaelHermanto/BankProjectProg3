package MockitoTest;

import Fabrik.GirokontoFabrik;
import Fabrik.Kontofabrik;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import Ueb3.*;
import Ueb4.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

public class MockBankTest {
    long blz = 12345;
    Bank bank = new Bank(blz);

    Kunde kunde1 = new Kunde("Bob", "Marley", "Berlin", LocalDate.now());
    Girokonto mockKontoG = Mockito.mock(Girokonto.class);
    Sparbuch mockKontoS = Mockito.mock(Sparbuch.class);
    Bank mockBank = Mockito.mock(Bank.class);

    Kontofabrik mockFabrikG = new GirokontoFabrik() {
        public Konto erstellen(Kunde inhaber, long kontoNummer) {
            return mockKontoG;
        }
    };

    Kontofabrik mockFabrikS = new GirokontoFabrik() {
        public Konto erstellen(Kunde inhaber, long kontoNummer) {
            return mockKontoS;
        }
    };

    /**
     * Testen Bank erzeugen mit blz negativ
     */
    @Test
    public void bankErzeugenFehlerTest() {
        try {
            Bank bf = new Bank(-1);
            fail();
        } catch (IllegalArgumentException e) {
            // leer
        }
    }

    /**
     * Testen das Erstellen eines Girokontos und die zurückgegebene Kontonummer.
     */
    @Test
    public void girokontoErstellenTest(){
        long nummer = bank.kontoErstellen(mockFabrikG, kunde1);
        List<Long> kontonummer = bank.getAlleKontonummern();

        assertTrue(kontonummer.contains(nummer));
    }

    /**
     * Testen das Erstellen eines Girokontos mit null Kunde
     */
    @Test
    public void girokontoErstellenNullTest() {
        Mockito.when(mockBank.girokontoErstellen(null)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    mockBank.girokontoErstellen(null);
        });
    }

    /**
     * Testen das Erstellen eines Sparkontos und die zurückgegebene Kontonummer.
     */
    @Test
    public void sparkontoErstellenTest() throws KontoNichtExistiertException {

        long nummer = bank.kontoErstellen(mockFabrikS,kunde1);
        List<Long> kontonummer = bank.getAlleKontonummern();

        assertTrue(kontonummer.contains(nummer));
    }

    /**
     * Testen das Erstellen eines Sparkontos mit null Kunde
     */
    @Test
    public void sparkontoErstellenNullTest() throws KontoNichtExistiertException {
        Mockito.when(mockBank.sparbuchErstellen(null)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mockBank.sparbuchErstellen(null);
        });
    }

    /**
     * Testen geldAbheben
     */
    @Test
    public void geldAbhebenTest() throws GesperrtException, KontoNichtExistiertException {
        //setup
        double betrag = 10;
        double betragZuGross = 3000;
        double saldo = 0;
        Konto mockKonto1 = Mockito.mock(Konto.class);
        Mockito.when(mockKontoG.getKontostand()).thenReturn(saldo);

        Mockito
                .doAnswer(invocationOnMock -> {
                    Mockito.when(mockKontoG.getKontostand()).thenReturn(saldo-betrag);
                    return true;
                })
                .when(mockKontoG).abheben(ArgumentMatchers.eq(betrag));
        Mockito.doReturn(false).when(mockKontoG).abheben(ArgumentMatchers.eq(betragZuGross));

        long nummer = bank.mockEinfuegen(mockKontoG);
        long nummer1 = bank.mockEinfuegen(mockKonto1);

        //exercise
        double kontostand = bank.getKontostand(nummer);
        boolean success = bank.geldAbheben(nummer,betrag);
        assertTrue(success);

        double aktuelleKontostand = bank.getKontostand(nummer);
        assertEquals(aktuelleKontostand, kontostand - betrag);

        success = bank.geldAbheben(nummer, betragZuGross);
        assertFalse(success);
        assertEquals(aktuelleKontostand, bank.getKontostand(nummer));

        //verification
        Mockito.verify(mockKontoG).abheben(betrag);
        Mockito.verifyNoInteractions(mockKonto1);
    }

    /**
     * Test Geld abheben, Konto existiert nicht
     */
    @Test
    public void geldAbhebenFehlerTest(){
        try {
            bank.geldAbheben(1, 20); // Konto mit der Nummer 1 existiert nicht.
            fail();
        } catch (KontoNichtExistiertException e) {
            // leer
        }
    }



    /**
     * Test Geld einzahlen, Konto existiert nicht
     */
    @Test
    public void geldEinzahlenFehlerTest(){
        try {
            bank.geldEinzahlen(1, 20); // Konto mit der Nummer 1 existiert nicht.
            fail();
        } catch (KontoNichtExistiertException e) {
            // leer
        }
    }

    /**
     * Test Konto loeschen
     */
    @Test
    public void kontoLoeschenTest(){
        long nummer = bank.kontoErstellen(mockFabrikG, kunde1);
        boolean success = bank.kontoLoeschen(nummer);

        assertTrue(success);
        List<Long> kontonummer = bank.getAlleKontonummern();
        assertFalse(kontonummer.contains(nummer));
    }

    /**
     * Test Konto loeschen, Konto existiert nicht
     */
    @Test
    public void kontoLoeschenFehlerTest(){
        boolean success = bank.kontoLoeschen(1);
        assertFalse(success);
    }

    /**
     * Test Ueberweisen
     */
    @Test
    public void ueberweisenTest() throws Exception {
        //setup
        double betrag = 10;
        double betragZuGross = 3000;
        double saldo = 0;
        Konto mockAbsender = Mockito.mock(Konto.class, Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        Konto mockEmpfaenger = Mockito.mock(Konto.class, Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        Konto mockKonto2 = Mockito.mock(Konto.class);

        Mockito.when(mockAbsender.getInhaber()).thenReturn(new Kunde());
        Mockito.when(mockEmpfaenger.getInhaber()).thenReturn(new Kunde());

        Mockito.when(mockAbsender.isGesperrt()).thenReturn(false);
        Mockito.when(mockEmpfaenger.isGesperrt()).thenReturn(false);

        long absender = bank.mockEinfuegen(mockAbsender);
        long empfaenger = bank.mockEinfuegen(mockEmpfaenger);
        long nummer2 = bank.mockEinfuegen(mockKonto2);

        //Mockito.when(mockAbsender.getKontostand()).thenReturn(saldo);
        //Ueberweisung absenden, Betrag < dispo
        doAnswer(invocationOnMock -> {
                    when(mockAbsender.getKontostand()).thenReturn(saldo-betrag);
                    return true;
                })
                .doReturn(true).when((Ueberweisungsfaehig)mockAbsender).ueberweisungAbsenden(ArgumentMatchers.eq(betrag),Mockito.anyString(),Mockito.anyLong(),Mockito.anyLong(),Mockito.anyString());

        //Betrag zu Gross
        doReturn(false).when((Ueberweisungsfaehig)mockAbsender).ueberweisungAbsenden(ArgumentMatchers.eq(betragZuGross),Mockito.anyString(),Mockito.anyLong(),Mockito.anyLong(),Mockito.anyString());

        //Ueberweisung empfangen
        doAnswer(invocationOnMock -> {
            when(mockEmpfaenger.getKontostand()).thenReturn(betrag);
            return null;
        }).when((Ueberweisungsfaehig)mockEmpfaenger).ueberweisungEmpfangen(ArgumentMatchers.eq(betrag),Mockito.anyString(),Mockito.anyLong(),Mockito.anyLong(),Mockito.anyString());

        //exercise
        double absenderSaldo = bank.getKontostand(absender);
        double empfaengerSaldo = bank.getKontostand(empfaenger);

        boolean success = bank.geldUeberweisen(absender,empfaenger,betrag,"test123");
        assertTrue(success);

        double aktuelleSaldoSender = bank.getKontostand(absender);
        double aktuelleSaldoEmpfaenger = bank.getKontostand(empfaenger);
        assertEquals(aktuelleSaldoSender, absenderSaldo - betrag);
        assertEquals(aktuelleSaldoEmpfaenger, empfaengerSaldo + betrag);

        success = bank.geldUeberweisen(absender,empfaenger,betragZuGross,"test123");
        assertFalse(success);
        assertEquals(aktuelleSaldoSender, bank.getKontostand(absender));
        assertEquals(aktuelleSaldoEmpfaenger, bank.getKontostand(empfaenger));

        //verification
        Mockito.verify((Ueberweisungsfaehig) mockAbsender, Mockito.times(2)).ueberweisungAbsenden(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        Mockito.verify((Ueberweisungsfaehig) mockEmpfaenger).ueberweisungEmpfangen(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        Mockito.verifyNoInteractions(mockKontoG);
        Mockito.verifyNoInteractions(mockKonto2);
    }

    /**
     * Test mock in Bank einfuegen
     */
    @Test
    public void mockEinfuegenTest(){
        long nummer = bank.mockEinfuegen(mockKontoG);
        List<Long> kontonummer = bank.getAlleKontonummern();
        assertTrue(kontonummer.contains(nummer));
    }

    /**
     * Test mock in Bank einfuegen
     */
    @Test
    public void mockEinfuegenFehlerTest(){
        Mockito.when(mockBank.mockEinfuegen(null)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mockBank.mockEinfuegen(null);
        });
    }
}
