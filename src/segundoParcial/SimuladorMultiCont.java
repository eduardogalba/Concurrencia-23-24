package segundoParcial;


public class SimuladorMultiCont {
    private static final int N = 50;

    public static void main(String[] args) {
        Incrementador [] incrementadors = new Incrementador[2];
        Decrementador [] decrementadors = new Decrementador[2];
        MultiContMon buffer = new MultiContMon();

        for (int i = 0; i < 2; i++) {
            incrementadors[i] = new Incrementador(buffer);
            decrementadors[i] = new Decrementador(buffer);
            incrementadors[i].start();
            decrementadors[i].start();
        }
        
    }

        public static class Incrementador extends Thread {
        public static MultiContMon buffer;

        public Incrementador (MultiContMon counter) {
            buffer = counter;
        }


        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < N/2; i++) {
                    buffer.inc(i);
                    System.out.println("Incremente " + i);
                }
            }
        }

    }

    public static class Decrementador extends Thread {
        public static MultiContMon buffer;

        public Decrementador (MultiContMon counter) {
            buffer = counter;
        }


        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < N/2; i++) {
                    buffer.dec(i);
                    System.out.println("Decremente " + i );
                }
            }
        }
        
    }
}
