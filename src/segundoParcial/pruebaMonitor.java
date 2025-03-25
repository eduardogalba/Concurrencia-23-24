package segundoparcial;

import es.upm.babel.cclib.Monitor;

public class pruebaMonitor {
    public static void main(String[] args) {
        Monitor mutex = new Monitor();
        Monitor.Cond cond = mutex.newCond();
        int self = 0;
        mutex.enter();
        if (self > 0) 
            cond.await();
        System.out.println("Hello World!");
        mutex.leave();
        
    }
}
