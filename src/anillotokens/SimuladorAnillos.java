package anillotokens;

public class SimuladorAnillos {
    public static final int N = 50;

    public static void main(String[] args) {
        Adquisidor [] adquisidors = new Adquisidor[3];
        Soltador [] soltadors = new Soltador[3];
        AnilloTokens tokens = new AnilloTokensMon();

        for (int i = 0; i < 1; i++) {
            adquisidors[i] = new Adquisidor(tokens);
            soltadors[i] = new Soltador(tokens);
            adquisidors[i].start();
            soltadors[i].start();
        }
    }

    public static class Adquisidor extends Thread {
        public static AnilloTokens anillos;

        public Adquisidor (AnilloTokens tokens) {
            anillos = tokens;
        }


        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < N; i+=2) {
                    anillos.adquirir(i);
                    System.out.println("Adquiri anillos " + i + " y " + (i+1));
                }
            }
        }

    }

    public static class Soltador extends Thread {
        public static AnilloTokens anillos;

        public Soltador (AnilloTokens tokens) {
            anillos = tokens;
        }


        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < N; i+=2) {
                    anillos.soltar(i);
                    System.out.println("Solte anillos " + i + " y " + (i+1));
                }
            }
        }
        
    }
}
