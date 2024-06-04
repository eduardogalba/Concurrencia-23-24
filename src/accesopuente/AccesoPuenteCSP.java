package accesopuente;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.ProcessManager;

import es.upm.aedlib.Pair;

public class AccesoPuenteCSP implements AccesoPuente, CSProcess {
    
    private int coches;

    private static final int NORTE = 0;
    private static final int SUR = 1;

    private static final int PRE_KO = -1;
    private static final int PRE_OK = 0;

    private Any2OneChannel [] chEntrada;
    private Any2OneChannel chSalida;

    public AccesoPuenteCSP () {
        coches = 0;

        chEntrada = new Any2OneChannel[2];
        chEntrada[NORTE] = Channel.any2one();
        chEntrada[SUR] = Channel.any2one();
        chSalida = Channel.any2one();

        new ProcessManager(this).start();
    }

    @Override
    public void solicitarEntrada(int l) {
        One2OneChannel chResp = Channel.one2one();
        if (l == NORTE)
            chEntrada[NORTE].out().write(chResp);
        else 
            chEntrada[SUR].out().write(chResp);
        chResp.in().read();
    }

    private void ejecutarEntrada (int l, One2OneChannel chResp) {
        if (l == NORTE)
            coches++;
        else
            coches--;
            
        chResp.out().write(PRE_OK);
    }

    @Override
    public void notificarSalida(int l) throws IllegalArgumentException {
        One2OneChannel chResp = Channel.one2one();
        Pair<Integer, One2OneChannel> paq = new Pair<>(l, chResp);
        chSalida.out().write(paq);
        if ((int) chResp.in().read() == PRE_KO)
            throw new IllegalArgumentException();
    }

    private void ejecutarSalida (Pair<Integer, One2OneChannel> paq) {
        int l = paq.getLeft();
        One2OneChannel chResp = paq.getRight();

        if ((l == NORTE && coches > 0) || (l == SUR && coches < 0))
            chResp.out().write(PRE_KO);
        else {
            if (l == NORTE) 
                coches++;
            else 
                coches--;
            chResp.out().write(PRE_OK);
        }
    }

    @Override
    @SuppressWarnings("squid:S2189")
    public void run() {
        final int ENTRADA_NORTE = 0;
        final int ENTRADA_SUR = 1;
        final int SALIDA = 2;

        Guard[] entradas = {chEntrada[NORTE].in(), chEntrada[SUR].in(), chSalida.in()};
        Alternative servicios = new Alternative(entradas);

        boolean [] sincCond = new boolean[3];

        while (true) {
            sincCond[ENTRADA_NORTE] = coches >= 0;
            sincCond[ENTRADA_SUR] = coches <= 0;
            sincCond[SALIDA] = true;
            One2OneChannel chResp;
            switch (servicios.fairSelect(sincCond)) {
                case ENTRADA_NORTE:
                    chResp = (One2OneChannel) chEntrada[NORTE].in().read();
                    ejecutarEntrada(NORTE, chResp);
                    break;
                
                case ENTRADA_SUR:
                    chResp = (One2OneChannel) chEntrada[SUR].in().read();
                    ejecutarEntrada(SUR, chResp);
                    break;
            
                case SALIDA:
                    Pair<Integer, One2OneChannel> paq = (Pair<Integer, One2OneChannel>) chSalida.in().read();
                    ejecutarSalida(paq);
                    break;

                default:
                    break;
            }
        }
    }
    
}
