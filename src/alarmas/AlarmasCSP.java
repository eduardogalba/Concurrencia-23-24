package alarmas;

import java.util.Collection;
import java.util.LinkedList;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.ProcessManager;

public class AlarmasCSP implements CSProcess {
    private int temp;

    private Any2OneChannel chNotificar;
    private Any2OneChannel chDetectar;

    public AlarmasCSP () {
        chDetectar = Channel.any2one();
        chNotificar = Channel.any2one();

        new ProcessManager(this).start();
    }

    public void notificar (int t) {
        if (t < -10 || t > 50)
            throw new IllegalArgumentException();

        chNotificar.out().write(t);
    }

    public int detectar (int min, int max) {
        if ((min < -10 || min > 50) && (max < -10 || max > 50) && min < max)
            throw new IllegalArgumentException();
        One2OneChannel resp = Channel.one2one();
        PetTemp pet = new PetTemp(min, max, resp);
        chDetectar.out().write(pet);

        return (int) resp.in().read();
    }

    private boolean ejecutarDetectar (PetTemp pet) {
        boolean cpre = pet.min <= temp && temp <= pet.max;
        if (cpre)
            pet.chResp.out().write(temp);
        return cpre;
    }

    public class PetTemp  {
        int min;
        int max;
        One2OneChannel chResp;

        public PetTemp (int mn, int mx, One2OneChannel resp) {
            min = mn;
            max = mx;
            chResp = resp;
        }
    }

    @Override
    @SuppressWarnings("squid:S2189")
    public void run() {
        final int DETECTAR = 0;
        final int NOTIFICAR = 1;

        Collection<PetTemp> aplazadas = new LinkedList<>();

        temp = 23;

        Guard [] entradas = {chDetectar.in(), chNotificar.in()};
        Alternative servicios = new Alternative(entradas);

        while (true) {
            switch(servicios.fairSelect()) {
                case DETECTAR:
                    PetTemp pet = (PetTemp) chDetectar.in().read();
                    if (!ejecutarDetectar(pet))
                        aplazadas.add(pet);
                    break;
                case NOTIFICAR:
                    temp = (int) chNotificar.in().read();
                    break;
                default:
                    break;
            }

            boolean cambio;
            do {
                cambio = false;
                for (PetTemp pet : aplazadas) {
                    if (ejecutarDetectar(pet)) {
                        cambio = true;
                        aplazadas.remove(pet);
                    }
                }

            } while (cambio);
        }
    }

}
