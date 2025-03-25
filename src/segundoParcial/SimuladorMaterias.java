package segundoparcial;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;

@SuppressWarnings("squid:S2189")
public class SimuladorMaterias {
    public static void main(String[] args) {
        JuegoCSP juego = new JuegoCSP();

        Lenhador lenhador = new Lenhador(juego);
        Agricultor agricultor = new Agricultor(juego);
        Pozero pozero = new Pozero(juego);
        Reparador reparador = new Reparador(juego);
        Movimiento movimiento = new Movimiento(juego);

        new Parallel(new CSProcess[] {lenhador, agricultor,  pozero,  reparador, movimiento}).run();
    }



    public static class Lenhador implements CSProcess {
        static JuegoCSP juego;
        static final int MADERA = 2;

        public Lenhador (JuegoCSP juego) {
            this.juego = juego;
        }


        @Override
        public void run() {
            while (true) {
                juego.cargar(MADERA);
                System.out.println("Cargue madera");
            }
            
        }

    }

    public static class Agricultor implements CSProcess {
        static JuegoCSP juego;
        static final int CEREAL = 0;

        public Agricultor (JuegoCSP juego) {
            this.juego = juego;

        }


        @Override
        public void run() {
            while (true) {
                juego.cargar(CEREAL);
                System.out.println("Cargue cereal");
            }
            
        }

    }

    public static class Pozero implements CSProcess {
        static JuegoCSP juego;
        static final int AGUA = 1;

        public Pozero (JuegoCSP juego) {
            this.juego = juego;
        }


        @Override
        public void run() {
            while (true) {
                juego.cargar(AGUA);
                System.out.println("Cargue agua");
            }
        }

    }

    public static class Movimiento implements CSProcess {
        static JuegoCSP juego;

        public Movimiento (JuegoCSP juego) {
            this.juego = juego;
        }


        @Override
        public void run() {
            while (true) {
                juego.avanzar();
                System.out.println("Avance");
            }
        }

    }

    public static class Reparador implements CSProcess {
        static JuegoCSP juego;

        public Reparador (JuegoCSP juego) {
            this.juego = juego;
        }


        @Override
        public void run() {
            while (true) {
                juego.reparar();
                System.out.println("Repare");
            }
        }

    }
}
