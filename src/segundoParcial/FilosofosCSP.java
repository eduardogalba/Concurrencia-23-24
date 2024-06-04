package segundoParcial;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.ProcessManager;

import es.upm.aedlib.Pair;

public class FilosofosCSP implements CSProcess {
    private static final int TENEDORES = 5;
    private Any2OneChannel[] chCoger;
    private Any2OneChannel chSoltar;

    public FilosofosCSP() {
        chSoltar = Channel.any2one();
        chCoger = new Any2OneChannel[TENEDORES];
        for (int i = 0; i < TENEDORES; i++)
            chCoger[i] = Channel.any2one();

        new ProcessManager(this).start();
    }

    public void cogerTenedores(int i) {
        One2OneChannel chResp = Channel.one2one();
        chCoger[i].out().write(chResp.out());
        chResp.in().read();
    }

    public void soltarTenedores(int i) {
        One2OneChannel chResp = Channel.one2one();
        Pair<One2OneChannel, Integer> paq = new Pair<>(chResp, i);
        chCoger[i].out().write(paq);
        chResp.in().read();
    }

    @Override
    @SuppressWarnings("squid:S2189")
    public void run() {
        boolean[] tenedores = new boolean[TENEDORES];

        final int [] COGER = {0, 1, 2, 3, 4};
        final int SOLTAR = 5;

        Guard[] entradas = new Guard[6];
        for (int i = 0; i < 5; i++) 
            entradas[i] = chCoger[i].in();
        entradas[5] = chSoltar.in();

        Alternative servicios = new Alternative(entradas);

        while (true) {
            switch (servicios.fairSelect()) {
                case COGER[0]:
                    
                    break;
                case COGER[1]:
                    break;
                case COGER[2]:
                    break;
                case COGER[3]:
                    break;
                case COGER[4]:
                    break;
                case SOLTAR:
                    break;
            }
        }
    }

}
