package segundoParcial;

import es.upm.babel.cclib.Monitor;

public class PeligroMon {
    private boolean p;
    private int o;

    private Monitor mutex;

    private Monitor.Cond espEntrar;
    private Monitor.Cond espSalir;

    public PeligroMon () {
        p = false;
        o = 0;

        mutex = new Monitor();
        
        espEntrar = mutex.newCond();
        espSalir = mutex.newCond();
    }

    public void avisarPeligro(boolean x) {
        mutex.enter();

        p = x;

        if (!p && o < 5 && espEntrar.waiting() > 0)
            espEntrar.signal();
        else if (o > 0)
            espSalir.signal();

        mutex.leave();
    }

    public void entrar () {
        mutex.enter();

        if (p || o >= 5)
            espEntrar.await();
        
        o++;

        if (o < 5 && espEntrar.waiting() > 0)
            espEntrar.signal();
        else 
            espSalir.signal();

        mutex.leave();
    }

    public void salir () {
        mutex.enter();

        if (o == 0)
            espSalir.await();
            
        o--;

        if (o > 0 && espSalir.waiting() > 0)
            espSalir.signal();
        else 
            espEntrar.signal();

        mutex.leave();
    }

}
