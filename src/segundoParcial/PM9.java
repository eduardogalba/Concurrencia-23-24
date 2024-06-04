package segundoParcial;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.Parallel;

public class PM9 {
    public static void main (String [] args) {
        new Parallel(new CSProcess[] {new P(), new P1(), new P2(), new P3()}).run();
    }

    public static One2OneChannel petA = Channel.one2one();
    public static One2OneChannel petB = Channel.one2one();
    public static One2OneChannel petC = Channel.one2one();

    public static class P implements CSProcess {

        @Override
        @SuppressWarnings("squid:S2189")
        public void run() {
            boolean q = true;
            boolean r = true;
            boolean s = false;

            final int A = 0;
            final int B = 1;
            final int C = 2;

            final Guard[] entradas =
                    {petA.in(), petB.in(), petC.in()};
            final Alternative serv = new Alternative(entradas);
            final boolean [] sincCond = new boolean[3];

            
            while (true) {
                sincCond[A] = q && r;
                sincCond[B] = r && s;
                sincCond[C] = q && s;

                int sel = serv.fairSelect(sincCond);

                switch(sel) {
                    case A:
                        petA.in().read();
                        q = !q;
                        s = !s;
                        System.out.println("A");
                        break;
                    case B:
                        petB.in().read();
                        r = !r;
                        q = !q;
                        System.out.println("B");
                        break;
                    case C:
                        petC.in().read();
                        s = !s;
                        r = !r;
                        System.out.println("C");
                        break;
                }
            }
        }
        
    }

    public static class P1 implements CSProcess {
        @Override
        @SuppressWarnings("squid:S2189")
        public void run() {
            while (true) {
                petA.out().write(0);
            }
        }
    }

    public static class P2 implements CSProcess {
        @Override
        @SuppressWarnings("squid:S2189")
        public void run() {
            while (true) {
                petB.out().write(0);
            }
        }
    }

    public static class P3 implements CSProcess {
        @Override
        @SuppressWarnings("squid:S2189")
        public void run() {
            while (true) {
                petC.out().write(0);
            }
        }
    }
}
