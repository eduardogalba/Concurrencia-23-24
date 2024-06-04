package accesopuente;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;

@SuppressWarnings("squid:S2189")
public class SimuladorAccesoPuente {
    private static final int NORTE = 0;
    private static final int SUR = 1;

    public static void main(String[] args) {
        AccesoPuenteCSP control = new AccesoPuenteCSP ();
        CSProcess entradaN = new Entrada (NORTE, control);
        CSProcess entradaS = new Entrada (SUR, control);
        CSProcess salidaN = new Salida (NORTE, control);
        CSProcess salidaS = new Salida (SUR, control);

        new Parallel(new CSProcess[] {control, entradaN, entradaS, salidaN, salidaS, }).run();
    }

    public static class Entrada implements CSProcess {
        private AccesoPuente gp;
        private int dir;

        public Entrada(int d, AccesoPuente pt) {
            dir = d;
            gp = pt;
        }

        public void run() {
            while (true) {
                gp.solicitarEntrada(dir);
                if (dir == NORTE)
                    System.out.println("Entrada por el lado NORTE");
                else
                    System.out.println("Entrada por el lado SUR");
            }
        }
    }

    public static class Salida implements CSProcess {
        private AccesoPuente gp;
        private int dir;

        public Salida(int d, AccesoPuente pt) {
            dir = d;
            gp = pt;
        }

        public void run () {
            while(true) {
                try {
                    gp.notificarSalida(dir);
                    if (dir == NORTE)
                        System.out.println("Salida por el lado NORTE");
                    else
                        System.out.println("Salida por el lado SUR");
                } catch (IllegalArgumentException e) {
                    System.out.println("OOppps something went wrong");
                }
            }
        }
    }
}
