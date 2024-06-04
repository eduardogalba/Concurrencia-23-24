package segundoParcial;

import es.upm.babel.cclib.Monitor;

public class MateriasPrimas {
    private int cereal;
    private int agua;
    private int madera;

    private Monitor mutex;

    private Monitor.Cond espCereal;
    private Monitor.Cond espAgua;
    private Monitor.Cond espMadera;
    private Monitor.Cond espAvanzar;
    private Monitor.Cond espReparar;

    public MateriasPrimas () {
        cereal = 0;
        agua = 0;
        madera = 0;

        mutex = new Monitor();

        espCereal = mutex.newCond();
        espAgua = mutex.newCond();
        espMadera = mutex.newCond();
        espAvanzar = mutex.newCond();
        espReparar = mutex.newCond();
    }

    
    public void cargarCereal () {
        mutex.enter();
        if (cereal + agua + madera + 1 >= 10)
            espCereal.await();

        cereal++;

        desbloqueo();

        mutex.leave();
    }

    public void cargarAgua () {
        mutex.enter();
        if (cereal + agua + madera + 1 >= 10)
            espAgua.await();

        agua++;

        desbloqueo();

        mutex.leave();
    }

    public void cargarMadera() {
        mutex.enter();
        if (cereal + agua + madera + 1 >= 10)
            espMadera.await();

        madera++;

        desbloqueo();

        mutex.leave();
    }

    public void avanzar () {
        mutex.enter();
        if (! (cereal > 0 && agua > 0))
            espAvanzar.await();

        cereal--;
        agua--;

        desbloqueo();

        mutex.leave();
    }

    public void reparar () {
        mutex.enter();
        if (! (madera > 0 && agua > 0))
            espReparar.await();

        madera--;
        agua--;

        desbloqueo();

        mutex.leave();
    }

    private void desbloqueo () {
        if (cereal + agua + madera + 1 < 10) {
            if (espCereal.waiting() > 0)
                espCereal.signal();
            else if (espAgua.waiting() > 0)
                espAgua.signal();
            else if (espMadera.waiting() > 0)
                espMadera.signal();
        } else if (cereal > 0 && agua > 0 && espAvanzar.waiting() > 0) 
            espAvanzar.signal();
        else if (madera > 0 && agua > 0)
            espReparar.signal();
    }
}
