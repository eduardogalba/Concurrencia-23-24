package controlacceso;

import java.util.concurrent.ThreadLocalRandom;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;

@SuppressWarnings("squid:S2189")
public class SimuladorAcceso {

    public static void main(String[] args) {
        //ControlAcceso control = new ControlAccesoCSP ();
        ControlAcceso control = new ControlAccesoMon ();
        CSProcess entradaN = new Entrada (control);
        CSProcess entradaS = new Entrada (control);
        CSProcess salidaN = new Salida (control);
        CSProcess salidaS = new Salida (control);

        new Parallel(new CSProcess[] {entradaN, entradaS, salidaN, salidaS }).run();
    }

    public static class Entrada implements CSProcess {
        private ControlAcceso gp;

        public Entrada(ControlAcceso pt) {
            gp = pt;
        }

        public void run() {
            while (true) {
                try {
                    int dir  = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                    gp.entrar(dir);
                    System.out.println("Entrada en la sala " + dir);
                } catch (IllegalArgumentException e) {
                    System.out.println("OOppps something went wrong");
                }

            }
        }
    }

    public static class Salida implements CSProcess {
        private ControlAcceso gp;

        public Salida(ControlAcceso pt) {
            gp = pt;
        }

        public void run () {
            while(true) {
                try {
                    int dir  = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                    gp.salir(dir);
                    System.out.println("Salida de la sala " + dir);
                } catch (IllegalArgumentException e) {
                    System.out.println("OOppps something went wrong");
                }
            }
        }
    }
}

