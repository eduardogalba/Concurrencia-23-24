package bufferpi;

import java.util.LinkedList;
import java.util.Queue;

import es.upm.babel.cclib.Monitor;

public class BufferPIMon implements BufferPI {
    private static final int MAX = 100;
    private static final int PAR = 0;
    private static final int IMPAR = 1;
    private static final int PARIDAD = 2;

    private Queue<Integer> self;
    private int nDatos;

    private Monitor mutex;

    private Monitor.Cond[] espTomar;
    private Monitor.Cond espPoner;

    public BufferPIMon() {
        self = new LinkedList<>();
        nDatos = 0;

        mutex = new Monitor();

        espTomar = new Monitor.Cond[PARIDAD];
        espTomar[PAR] = mutex.newCond();
        espTomar[IMPAR] = mutex.newCond();
    }

    public void poner(int d) {
        mutex.enter();
        if (nDatos >= MAX)
            espPoner.await();

        self.add(d);
        nDatos++;

        desbloqueo();

        mutex.leave();
    }

    public int tomar(int t) {
        mutex.enter();

        if (nDatos == 0 || !concuerda(self.peek(), t)) 
            espTomar[t].await();
        

        int d = self.poll();
        nDatos--;

        desbloqueo();

        mutex.leave();
        return d;
    }

    private void desbloqueo() {
        if (nDatos < MAX && espPoner.waiting() > 0)
            espPoner.signal();
        else if (nDatos > 0 && concuerda(self.peek(), PAR) && espTomar[PAR].waiting() > 0) 
            espTomar[PAR].signal();
        else if (nDatos > 0 && concuerda(self.peek(), IMPAR))
            espTomar[IMPAR].signal();
  
    }

    private boolean concuerda(int d, int t) {
        if (d % 2 == 0)
            return t == PAR;
        else if (d % 2 != 0)
            return t == IMPAR;

        return false;
    }
}
