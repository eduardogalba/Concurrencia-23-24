package accesopuente;

import es.upm.babel.cclib.Monitor;

public class AccesoPuenteMon implements AccesoPuente {
    private static final int NORTE = 0;
    private static final int SUR = 1;

    private int self;

    private Monitor mutex;
    private Monitor.Cond [] espEntrada;

    public AccesoPuenteMon () {
        self = 0;

        mutex = new Monitor();

        espEntrada = new Monitor.Cond[2];
        espEntrada[NORTE] = mutex.newCond();
        espEntrada[SUR] = mutex.newCond();
        
    }

    @Override
    public void solicitarEntrada(int l) {
        mutex.enter();
        if (l == NORTE && self < 0)
            espEntrada[NORTE].await();
        else if (l == SUR && self > 0)
            espEntrada[SUR].await();

        if (l == NORTE) 
            self++;
        else if (l == SUR)
            self--;

        desbloqueo();

        mutex.leave();
    }

    @Override
    public void notificarSalida(int l) {
        mutex.enter();

        if ((l == NORTE && self > 0) || (l == SUR && self < 0))
            throw new IllegalArgumentException();
        
        if (l == NORTE)
            self--;
        else if (l == SUR)
            self++;
        
        desbloqueo();

        mutex.leave();
    }

    private void desbloqueo () {
        if (self >= 0 && espEntrada[NORTE].waiting() > 0)
            espEntrada[NORTE].signal();
        else if (self <= 0)
            espEntrada[SUR].signal();
    }

}
