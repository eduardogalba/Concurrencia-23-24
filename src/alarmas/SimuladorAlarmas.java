package alarmas;

import java.util.concurrent.ThreadLocalRandom;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;

@SuppressWarnings("squid:S2189")
public class SimuladorAlarmas {
    public static void main(String[] args) {
        AlarmasCSP alarmas = new AlarmasCSP();
        Termostato term = new Termostato(alarmas);
        Actuar act1 = new Actuar(alarmas);
        Actuar act2 = new Actuar(alarmas);

        new Parallel(new CSProcess[] { alarmas, term, act1, act2}).run();
    }

    public static class Termostato implements CSProcess {
        private AlarmasCSP alarma;

        public Termostato(AlarmasCSP a) {
            alarma = a;
        }

        public void run () {
            while(true) {
                for (int tm = -10; tm < 51; tm++) {
                    alarma.notificar (tm);
                    System.out.println(String.format("Se ha notificado %d grados", tm));
                }
            }
        }
    }

    public static class Actuar implements CSProcess {
        private AlarmasCSP alarma;

        public Actuar(AlarmasCSP a) {
            alarma = a;
        }

        public void run () {
            while (true) {
                int min = ThreadLocalRandom.current().nextInt(-10, 50 + 1);
                int max = ThreadLocalRandom.current().nextInt(min, 50 + 1);
                try {
                    int t = alarma.detectar(min, max);
                    System.out.println(String.format("Se ha detectado %d grados dentro de [%d,%d]", t, min, max));
                } catch (IllegalArgumentException e) {
                    System.out.println("Oooops something went wrong");
                }
                
            }
        }
    }
}
