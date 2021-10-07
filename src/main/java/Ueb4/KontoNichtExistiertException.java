package Ueb4;

/**
 * Exception für nicht existierende Konto
 * @author Nathanael Isaac Hermanto s0570619
 */
public class KontoNichtExistiertException extends Exception {
    public KontoNichtExistiertException(String msg) {
        super(msg);
    }

    public KontoNichtExistiertException() {
        super();
    }
}
