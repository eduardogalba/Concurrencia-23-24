package segundoparcial;

import es.upm.babel.cclib.Monitor;

public class MisilMon {
    private static final int T_UMBRAL = 101;

    private int misil;

    private Monitor mutex;

    private Monitor.Cond [] espDetectar;

    public MisilMon () {
        misil = 0;

        mutex = new Monitor();

        espDetectar = new Monitor.Cond [T_UMBRAL];

        for (int i = 0; i < T_UMBRAL; i++)
            espDetectar[i] = mutex.newCond();
    }

    public void notificar (int desv) {
        mutex.enter();
        
        misil = desv;

        boolean encontrado = false;
        for (int i = 0; i < T_UMBRAL && !encontrado; i++) {
            encontrado = Math.abs(misil) > i;

            if (encontrado)
                espDetectar[i].signal();
        }

        mutex.leave();
    }

    public int detectarDesviacion (int umbral) {
        if (umbral < 0 || umbral >= T_UMBRAL)
            throw new IllegalArgumentException();
        mutex.enter();

        if (Math.abs(misil) <= umbral)
            espDetectar[umbral].await();

        int d = misil;

        mutex.leave();

        return d;
    }
} 
