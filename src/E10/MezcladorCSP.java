package E10;

import org.jcsp.lang.*;

import es.upm.aedlib.Pair;

// CTAD Mezclador
public class MezcladorCSP implements CSProcess, Mezclador {
    // tipo Lado = Izda | Dcha
    public static final int IZDA = 0;
    public static final int DCHA = 1;
    public static final int LADOS = 2;

    /* Canales de los clientes */
    private final Any2OneChannel[] chInsertar;
    private final Any2OneChannel chExtraer;

    public MezcladorCSP() {
        chInsertar = new Any2OneChannel[LADOS];
        chInsertar[IZDA] = Channel.any2one();
        chInsertar[DCHA] = Channel.any2one();
        chExtraer = Channel.any2one();
        new ProcessManager(this).start();
    }

    public void insertar(int l, int d) {
        One2OneChannel chAck = Channel.one2one();
        Pair<Integer, One2OneChannel> p = new Pair<>(d, chAck);
        chInsertar[l].out().write(p);
        chAck.in().read();
    }

    public int extraerMenor() {
        One2OneChannel chResp = Channel.one2one();
        chExtraer.out().write(chResp);
        return (int) chResp.in().read();
    }

    @SuppressWarnings({ "squid:S2189", "unchecked"})
    public void run() {
        // Nombres simbólicos para los índices
        final int INS_IZDA = 0;
        final int INS_DCHA = 1;
        final int EXT_MENOR = 2;

        // Estado del recurso: declaración
        boolean[] hayDato = new boolean[LADOS];
        int[] dato = new int[LADOS];

        Guard[] entradas = {
                chInsertar[INS_IZDA].in(),
                chInsertar[INS_DCHA].in(),
                chExtraer.in()
        };

        boolean[] sincCond = new boolean[3];

        // Recepción alternativa
        Alternative servicios = new Alternative(entradas);

        while (true) {
            sincCond[EXT_MENOR] = hayDato[IZDA] && hayDato[DCHA];
            sincCond[INS_IZDA] = !hayDato[IZDA];
            sincCond[INS_DCHA] = !hayDato[DCHA];
            Pair<Integer, One2OneChannel> llamada;
            One2OneChannel chAck;
            switch(servicios.fairSelect(sincCond)) {
                case INS_IZDA:
                    llamada = (Pair<Integer, One2OneChannel>) chInsertar[INS_IZDA].in().read();
                    dato[IZDA] = llamada.getLeft();
                    chAck = llamada.getRight();
                    chAck.out().write("OK");
                    hayDato[IZDA] = true;

                    break;
                case INS_DCHA:
                    llamada = (Pair<Integer, One2OneChannel>) chInsertar[INS_DCHA].in().read();
                    dato[DCHA] = llamada.getLeft();
                    chAck = llamada.getRight();
                    chAck.out().write("OK");
                    hayDato[DCHA] = true;
                    break;
                case EXT_MENOR:
                    One2OneChannel chResp;
                    if (dato[IZDA] < dato[DCHA]) {
                        chResp = (One2OneChannel) chExtraer.in().read();
                        chResp.out().write(dato[IZDA]);
                        hayDato[IZDA] = false;
                    } else {
                        chResp = (One2OneChannel) chExtraer.in().read();
                        chResp.out().write(dato[DCHA]);
                        hayDato[DCHA] = false;
                    }
                    break;
                default:
                    break;
            }

        }
    }
}