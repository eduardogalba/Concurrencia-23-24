package segundoparcial;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.ProcessManager;

public class JuegoCSP implements CSProcess {
    private Any2OneChannel chCargar;
    private Any2OneChannel chAvanzar;
    private Any2OneChannel chReparar;

    private final int CEREAL = 0;
    private final int AGUA = 1;
    private final int MADERA = 2;

    public JuegoCSP () {
        chCargar = Channel.any2one();
        chAvanzar = Channel.any2one();
        chReparar = Channel.any2one();

        new ProcessManager(this).start();
    }
    
    public void cargar (int m) {
        if (m != CEREAL && m != AGUA && m != MADERA)
            throw new IllegalArgumentException();

        chCargar.out().write(m);
    }

    public void avanzar () {
        chAvanzar.out().write(null);
    }

    public void reparar () {
        chReparar.out().write(null);
    }

    @Override
    @SuppressWarnings("squid:S2189")
    public void run() {
        int [] self = new int [3];
        self[CEREAL] = 0;
        self[AGUA] = 0;
        self[MADERA] = 0;

        final int CARGAR = 0;
        final int AVANZAR = 1;
        final int REPARAR = 2;
        Guard [] entradas = {chCargar.in(), chAvanzar.in(), chReparar.in()};
        Alternative servicios = new Alternative(entradas);
        boolean [] sincCond = new boolean [3];
        while (true) {
            sincCond[CARGAR] = self[CEREAL] + self[AGUA] + self[MADERA] + 1 < 10;
            sincCond[AVANZAR] = self[CEREAL] > 0 && self[AGUA] > 0;
            sincCond[REPARAR] = self[MADERA] > 0 && self[AGUA] > 0;
            
            switch (servicios.fairSelect(sincCond)) {
                case CARGAR:
                    int m = (int) chCargar.in().read();
                    switch (m) {
                        case CEREAL:
                            self[CEREAL]++;
                            break;
                        case AGUA:
                            self[AGUA]++;
                            break;
                        case MADERA:
                            self[MADERA]++;
                            break;
                        default:
                            break;
                    }

                    break;
                case AVANZAR:
                    chAvanzar.in().read();
                    self[CEREAL]--;
                    self[AGUA]--;
                    break;
                case REPARAR:
                    chReparar.in().read();
                    self[MADERA]--;
                    self[AGUA]--;
                    break;
                default:
                    break;
            }
            
        }
    }

}
