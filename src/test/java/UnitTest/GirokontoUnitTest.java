package UnitTest;

import org.junit.jupiter.api.Test;
import Ueb3.GesperrtException;
import Ueb3.Girokonto;
import Ueb3.Kunde;
import Ueb3.Waehrung;
import org.mockito.Mockito;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static Ueb3.Waehrung.*;


/**
 * Unit Test für Girokonto + von Musterlösung + mit Mockito fuer Beobachter
 */
public class GirokontoUnitTest {
    private Kunde k= new Kunde("Bob", "Marley", "California", LocalDate.parse("1950-04-03"));
    private Girokonto g1= new Girokonto(k, 1111, 1000.0);

    Girokonto gk;

    long nummer = 17;
    double dispo = 1000;

    PropertyChangeSupport propMock = Mockito.mock(PropertyChangeSupport.class);

    @Test//exchange
    public void testExchangeEurEur(){
        assertEquals(10, Waehrung.exchange(10, EUR, EUR));
    }

    @Test//exchange
    public void testExchangeEURBGN(){
        assertEquals(1955.83, Waehrung.exchange(1000, EUR, BGN));
    }

    @Test//exchange
    public void testExchangeBGNEUR(){
        assertEquals(511.29188119621847, Waehrung.exchange(1000, BGN, EUR));
    }

    @Test//Einzahlen
    public void testEinzahlen(){
        g1.setSupport(propMock);
        g1.einzahlen(50);
        assertEquals(50,g1.getKontostand());
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,50.0d);
    }

    @Test//EinzahlenBGN
    public void testEinzahlenBGN(){
        g1.setSupport(propMock);
        g1.einzahlen(50,BGN);
        assertEquals(25.564594059810926,g1.getKontostand());
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,25.564594059810926d);
    }

    @Test//abhebenBGN
    public void testAbhebenBGN() throws GesperrtException {
        g1.setSupport(propMock);
        g1.einzahlen(50,BGN);
        assertEquals(25.564594059810926,g1.getKontostand());
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,25.564594059810926d);
        assertTrue(g1.abheben(50,BGN));
        assertEquals(0,g1.getKontostand());
        Mockito.verify(propMock).firePropertyChange("Kontostand",25.564594059810926d,0.0d);
    }

    @Test//waehrungswechsel
    public void testWaehrungswechsel(){
        g1.setSupport(propMock);
        g1.einzahlen(50,BGN);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,25.564594059810926d);
        g1.waehrungswechsel(BGN);
        assertEquals(BGN,g1.getWaehrung());
        assertEquals(50,g1.getKontostand());
        assertEquals(1955.83,g1.getDispo());
        Mockito.verify(propMock).firePropertyChange("Waehrung",EUR,BGN);
    }

    @Test
    void konstruktorTest() {
        gk = new Girokonto();
        assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
    }

    @Test
    void konstruktorMitParameternTest() {
        gk = new Girokonto(k, nummer, dispo);
        assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), k);
        assertEquals(gk.getDispo(), dispo);
        assertEquals(gk.getKontonummer(), nummer);

    }


    @Test
    void einzahlenNegativTest()
    {
        gk = new Girokonto();
        gk.setSupport(propMock);
        try {
            gk.einzahlen(-100);
            fail("Keine Exception!");
        } catch (IllegalArgumentException e)
        {
        }

            assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
        Mockito.verifyNoInteractions(propMock);
    }

    @Test
    void einzahlenNaNTest()
    {
        gk = new Girokonto();
        gk.setSupport(propMock);
        try {
            gk.einzahlen(Double.NaN);
            fail("Keine Exception!");
        } catch (IllegalArgumentException e)
        {
        }
        assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
        Mockito.verifyNoInteractions(propMock);
    }

    @Test
    void abhebenimKontostandTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto();
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(50 );
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,50.0d);
        assertEquals(gk.getKontostand(), 50 );
        assertEquals(gk.getKontostandFormatiert(), "50,00 EUR");

        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
    }

    @Test
    void abhebenGenauKontostandTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto();
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(100);
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,0.0d);
        assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
    }

    @Test
    void abhebeninEinzelschrittenBisLeerTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto();
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(50);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,50.0d);
        assertTrue(geklappt);
        geklappt = gk.abheben(50);
        Mockito.verify(propMock).firePropertyChange("Kontostand",50.0d,0.0d);
        assertTrue(geklappt);
        assertEquals(gk.getKontostand(), 0 );
        assertEquals(gk.getKontostandFormatiert(), "0,00 EUR");
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
    }

    @Test
    void abhebenInDenDispoTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto(k, nummer, dispo);
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(50 + dispo);
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,-950.0d);
        assertEquals(gk.getKontostand(), 50 - dispo );
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), k);
        assertEquals(gk.getDispo(), dispo);
    }

    @Test
    void abhebenImDispoTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto(k, nummer, dispo);
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(50 + dispo);
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,-950.0d);
        geklappt = gk.abheben(20);
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",-950.0d,-970.0d);
        assertEquals(gk.getKontostand(), 30 - dispo );
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), k);
        assertEquals(gk.getDispo(), dispo);
    }

    @Test
    void abhebenGenauImDispoTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto(k, nummer, dispo);
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(100 + dispo);
        assertTrue(geklappt);
        Mockito.verify(propMock).firePropertyChange("Kontostand",100.0d,-1000.0d);
        assertEquals(gk.getKontostand(), - dispo );
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), k);
        assertEquals(gk.getDispo(), dispo);
    }

    @Test
    void abhebenUeberDispoTest() throws GesperrtException
    {
        boolean geklappt;
        gk = new Girokonto(k, nummer, dispo);
        gk.setSupport(propMock);
        gk.einzahlen(100);
        Mockito.verify(propMock).firePropertyChange("Kontostand",0.0d,100.0d);
        geklappt = gk.abheben(500 + dispo);
        assertFalse(geklappt);
        Mockito.verifyNoMoreInteractions(propMock);
        assertEquals(gk.getKontostand(), 100 );
        assertFalse(gk.isGesperrt());
        assertEquals(gk.getGesperrtText(), "");
        assertEquals(gk.getInhaber(), k);
        assertEquals(gk.getDispo(), dispo);
    }









}
