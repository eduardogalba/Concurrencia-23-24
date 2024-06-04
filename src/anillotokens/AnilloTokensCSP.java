package anillotokens;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.ProcessManager;


public class AnilloTokensCSP implements AnilloTokens, CSProcess {
    private Any2OneChannel chAdquirir;
    private Any2OneChannel chSoltar;

    Map<Integer, Boolean> self;

    private static final int N = 50;

    public AnilloTokensCSP() {
        chAdquirir = Channel.any2one();
        chSoltar = Channel.any2one();

        new ProcessManager(this).start();
    }

    @Override
    public void adquirir(int i) {
        if (i < 0 || i >= N)
            throw new IllegalArgumentException();
        One2OneChannel resp = Channel.one2one();
        Peticion paquete = new Peticion(i, resp, true);
        chAdquirir.out().write(paquete);
        resp.in().read();
    }

    @Override
    public void soltar(int i) {
        if (i < 0 || i >= N)
            throw new IllegalArgumentException();

        One2OneChannel resp = Channel.one2one();
        Peticion paquete = new Peticion(i, resp, false);
        chSoltar.out().write(paquete);
        resp.in().read();
    }

    public class Peticion {
        int index;
        One2OneChannel chResp;
        boolean esAdquirir;

        public Peticion(int i, One2OneChannel resp, boolean met) {
            index = i;
            chResp = resp;
            esAdquirir = met;
        }
    }

    private void ejecutarAdquirir (int i, One2OneChannel channel) {
        self.put(i, true);
        self.put((i + 1) % N, true);
        channel.out().write(0);
    }

    private void ejecutarSoltar (int i, One2OneChannel channel) {
        self.put(i, false);
        self.put((i + 1) % N, false);
        channel.out().write(0);
    }

    @Override
    @SuppressWarnings("squid:S2189")
    public void run() {
        final int ADQUIRIR = 0;
        final int SOLTAR = 1;

        self = new HashMap<>(N);

        for (int i = 0; i < N; i++)
            self.put(i, false);

        Guard[] entradas = { chAdquirir.in(), chSoltar.in() };
        Alternative servicios = new Alternative(entradas);

        Collection<Peticion> aplazadas = new LinkedList<>();

        while (true) {
            Peticion paq;
            int i;
            One2OneChannel channel;
            switch (servicios.fairSelect()) {
                case ADQUIRIR:
                    paq = (Peticion) chAdquirir.in().read();
                    i = paq.index;
                    channel = paq.chResp;
                    if (!self.get(i) && !self.get((i + 1) % N)) 
                        ejecutarAdquirir(i, channel);
                    else
                        aplazadas.add(paq);

                    break;
                case SOLTAR:
                    paq = (Peticion) chSoltar.in().read();
                    i = paq.index;
                    channel = paq.chResp;
                    if (self.get(i) && self.get((i + 1) % N)) 
                        ejecutarSoltar(i, channel);
                    else
                        aplazadas.add(paq);
                    break;
                default:
                    break;
            }
            boolean cambio = false;
            do {
                cambio = false;
                Iterator<Peticion> it = aplazadas.iterator();
                while (it.hasNext()) {
                    Peticion actual = it.next();
                    if (actual.esAdquirir && !self.get(actual.index) && !self.get((actual.index + 1) % N)) {
                        ejecutarAdquirir(actual.index, actual.chResp);
                        it.remove();
                        cambio = true;
                    } else if (self.get(actual.index) && self.get((actual.index + 1) % N)) {
                        ejecutarSoltar(actual.index, actual.chResp);
                        it.remove();
                        cambio = true;
                    }
                }

            } while (cambio);

        }
    }
}
