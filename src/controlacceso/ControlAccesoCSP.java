package controlacceso;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.ProcessManager;

@SuppressWarnings("squid:S2189")
public class ControlAccesoCSP implements ControlAcceso, CSProcess {
    private static final int SALAS = 4;

    /* Declaracion de estado del recurso */
    private int [] nPersonas;

    private Any2OneChannel [] chEntrar;
    private Any2OneChannel chSalir;

    public ControlAccesoCSP () {
        nPersonas = new int[SALAS];
        chEntrar = new Any2OneChannel[SALAS];
        
        for (int i = 0; i < SALAS; i++) {
            nPersonas[i] = 0;
            chEntrar[i] = Channel.any2one();
        }

        chSalir = Channel.any2one();
        
        new ProcessManager(this).start();
    }


    @Override
    public void entrar(int nSala) throws IllegalArgumentException {
        if (nSala < 0 || nSala >= SALAS)
            throw new IllegalArgumentException();
        
        chEntrar[nSala].out().write(0);
    }

    @Override
    public void salir(int nSala) throws IllegalArgumentException {
        if (nSala < 0 || nSala >= SALAS)
            throw new IllegalArgumentException();

        chSalir.out().write(nSala);
        
    }

    private int totalPersonas () {
        int suma = 0;
        for (int i = 0; i < SALAS; i++)
            suma += nPersonas[i];
        
        return suma;
    }

    @Override
    public void run() {
        
        final int SALIR = 4;
        final int ENTRAR0 = 0;
        final int ENTRAR1 = 1;
        final int ENTRAR2 = 2;
        final int ENTRAR3 = 3;

        Guard [] entradas = new Guard[SALAS+1];
        for (int i = 0; i < SALAS; i++)
            entradas[i] = chEntrar[i].in();
        entradas[SALAS] = chSalir.in();

        Alternative servicios = new Alternative(entradas);

        boolean [] sincCond = new boolean[SALAS + 1];

        while (true) {
            for (int i = 0; i < SALAS; i++) 
                sincCond[i] = nPersonas[i] < 5 && totalPersonas() < 15;
            sincCond[SALIR] = true;

            int choice = servicios.fairSelect(sincCond);
            switch(choice) {
                case ENTRAR0:
                    chEntrar[ENTRAR0].in().read();
                    nPersonas[ENTRAR0]++;
                    break;
                case ENTRAR1:
                    chEntrar[ENTRAR1].in().read();
                    nPersonas[ENTRAR1]++;
                    break;
                case ENTRAR2:
                    chEntrar[ENTRAR2].in().read();
                    nPersonas[ENTRAR2]++;
                    break;
                case ENTRAR3:
                    chEntrar[ENTRAR3].in().read();
                    nPersonas[ENTRAR3]++;
                    break;
                    
                case SALIR:
                    int nSala = (int) chSalir.in().read();
                    nPersonas[nSala]--;
                    break;
                default:
                    break;
                
            }
        }
    }

}
