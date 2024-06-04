package controlacceso;

import es.upm.babel.cclib.Monitor;

public class ControlAccesoMon implements ControlAcceso {
    private static final int SALAS = 4;

    private int [] nPersonas;

    private Monitor mutex;
    private Monitor.Cond [] entradaSala;
    private Monitor.Cond salidaSala;

    public ControlAccesoMon () {

        nPersonas = new int[SALAS];
        
        mutex = new Monitor();

        entradaSala = new Monitor.Cond [SALAS];

        for (int i = 0; i < SALAS; i++) {
            nPersonas[i] = 0;
            entradaSala[i] = mutex.newCond();
        }

        salidaSala = mutex.newCond();
        
    }


    @Override
    public void entrar(int nSala) throws IllegalArgumentException {
        if (nSala < 0 || nSala >= SALAS)
            throw new IllegalArgumentException();
        
        mutex.enter();

        if (nPersonas[nSala] == 5 || totalPersonas() == 15)
            entradaSala[nSala].await();
        
        nPersonas[nSala] += 1;

        desbloqueo();

        mutex.leave();
    }

    private int totalPersonas () {
        int suma = 0;
        for (int i = 0; i < SALAS; i++)
            suma += nPersonas[i];
        
        return suma;
    }

    @Override
    public void salir(int nSala) throws IllegalArgumentException {
        if (nSala < 0 || nSala >= SALAS)
            throw new IllegalArgumentException();
        
        mutex.enter();

        /* No especifica esta CPRE pero sino nPersonas toma valores negativos */
        if (totalPersonas() <= 0)
            salidaSala.await();

        nPersonas[nSala] -= 1;

        desbloqueo();

        mutex.leave();
    }

    private void desbloqueo () {
        boolean encontrado = false;
        if (totalPersonas() > 0 && salidaSala.waiting() > 0)
            salidaSala.signal();
        else {
            for (int i = 0; i < SALAS && !encontrado; i++) {
                encontrado = nPersonas[i] < 5 && totalPersonas() < 15 && entradaSala[i].waiting() > 0;
                if (encontrado)
                    entradaSala[i].signal();
            }
        }
    }

}
