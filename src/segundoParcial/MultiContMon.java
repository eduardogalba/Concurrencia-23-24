package segundoParcial;

import es.upm.babel.cclib.Monitor;

public class MultiContMon {
    private static final int N = 50;

    private int counter;

    private Monitor mutex;
    private Monitor.Cond [] espInc;
    private Monitor.Cond [] espDec;

    public MultiContMon () {
        counter = 0;

        mutex = new Monitor();

        espInc = new Monitor.Cond[N/2];
        espDec = new Monitor.Cond[N/2];

        for (int i = 1; i < N/2; i++) {
            espInc[i] = mutex.newCond();
            espDec[i] = mutex.newCond();
        }
    }

    public void inc (int n) {
        if (n <= 0 && n >= N/2)
            throw new IllegalArgumentException();

        mutex.enter();

        if (counter + n > N)
            espInc[n].await();

        counter += n;

        desbloqueo();

        mutex.leave();
    }

    public void dec (int n) {
        if (n <= 0 && n >= N/2)
            throw new IllegalArgumentException();
        
        mutex.enter();

        if (n > counter)
            espDec[n].await();

        counter -= n;

        desbloqueo();

        mutex.leave();
    }

    private void desbloqueo () {
        boolean enc1 = false;
        boolean enc2 = false;
        for (int i = 1; i < N/2 && !enc1 && !enc2; i++) {
            enc1 = counter + i <= N && espInc[i].waiting() > 0;
            enc2 = i <= counter && espDec[i].waiting() > 0;
            if (enc1)
                espInc[i].signal();
            else if (enc2)
                espDec[i].signal();
        }
    }
}
