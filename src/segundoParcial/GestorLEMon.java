package segundoparcial;

import es.upm.babel.cclib.Monitor;

public class GestorLEMon {
    private int ley;
    private int esc;
    private int escEsp;

    private Monitor mutex;

    private Monitor.Cond espEscribir;
    private Monitor.Cond espLeer;

    public GestorLEMon () {
        ley = 0;
        esc = 0;
        escEsp = 0;

        mutex = new Monitor ();

        espEscribir = mutex.newCond();
        espLeer = mutex.newCond();
    }

    public void intencionEscribir () {
        mutex.enter();

        escEsp++;

        mutex.leave();
    }

    public void permisoEscribir () {
        mutex.enter();

        if (ley + esc != 0) 
            espEscribir.await();
        
        esc++;
        escEsp--;

        mutex.leave();
    }

    public void finEscribir () {
        mutex.enter();

        esc--;

        if (espEscribir.waiting() > 0)
            espEscribir.signal();
        else if (escEsp == 0)
            espLeer.signal();

        mutex.leave();
    }

    public void inicioLeer () {
        mutex.enter();

        if (esc + escEsp != 0)
            espLeer.await();

        ley++;

        espLeer.signal();

        mutex.leave();
    }

    public void finLeer () {
        mutex.enter();

        ley--;

        if (ley == 0 && espEscribir.waiting() > 0)
            espEscribir.signal();
        else if (escEsp == 0)
            espLeer.signal();

        mutex.leave();
    }


    public void desbloqueo () {
        if (ley + esc == 0 && espEscribir.waiting() > 0)
            espEscribir.signal();
        else if (esc + escEsp == 0)
            espLeer.signal();
    }
}
