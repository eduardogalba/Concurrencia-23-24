package MultiBuffer;

import es.upm.babel.cclib.Producto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.MultiAlmacen;

// importar la librer ´ı a de monitores
class MultiAlmacenMon implements MultiAlmacen {
    private int capacidad = 0;
    private Producto [] almacenado = null;
    private int aExtraer = 0;
    private int aInsertar = 0;
    private int nDatos = 0;

    /* Declaracion de condiciones de sincronizacion */
    private Monitor mutex;
    Collection<Solicitud> pendientes;

    public MultiAlmacenMon(int n) {
        almacenado = new Producto[n];
        aExtraer = 0;
        aInsertar = 0;
        capacidad = n;
        nDatos = 0;

        mutex = new Monitor();
        pendientes = new ArrayList<>();
    }

    @SuppressWarnings("squid:S1104")
    public class Solicitud {
        public int longitud;
        public int n;
        public boolean esAlmacenar;
        public Monitor.Cond cond;

        public Solicitud (int n, boolean fid) {
            if (fid) {
                this.longitud = n;
                this.esAlmacenar = fid;
            } else {
                this.n = n;
                this.esAlmacenar = fid;
            }

            cond = mutex.newCond();
        }
    }

    public void almacenar(Producto[] productos) {

        /* Evaluamos la PRE */
        if (productos.length <= (capacidad / 2)) {
            /* Protocolo de entrada a SC */
            mutex.enter();

            /* Se cumple la CPRE? */
            if (productos.length + nDatos > capacidad) {
                Solicitud nueva = new Solicitud(productos.length, true);
                pendientes.add(nueva);
                nueva.cond.await();
            }

            /* Seccion Critica */
            for (int i = 0; i < productos.length; i++) {
                almacenado[aInsertar] = productos[i];
                nDatos++;
                aInsertar++;
                aInsertar %= capacidad;
            }

            /* Desbloqueo? */
            desbloqueo();
            /* Protocolo de salida de SC */
            mutex.leave();
        }
    }

    public Producto[] extraer(int n) {
        Producto[] result = null;
        if (n <= (capacidad / 2)) {
            result = new Producto[n];
            /* Protocolo de entrada a SC */
            mutex.enter();

            /* Se cumple la CPRE? */
            if (nDatos < n) {
                Solicitud nueva = new Solicitud(n, false);
                pendientes.add(nueva);
                nueva.cond.await();
            }

            /* Seccion Critica */
            for (int i = 0; i < result.length; i++) {
                result[i] = almacenado[aExtraer];
                almacenado[aExtraer] = null;
                nDatos--;
                aExtraer++;
                aExtraer %= capacidad;
            }

            /* Desbloqueo? */
            desbloqueo();
            /* Protocolo de salida de SC */
            mutex.leave();
        }
        return result;
    }

    private void desbloqueo () {
        boolean encontrado = false;
        Iterator<Solicitud> it = pendientes.iterator();
        while (it.hasNext() && !encontrado) {
            Solicitud actual = it.next();
            if (actual.esAlmacenar) 
                encontrado = actual.longitud + nDatos <= capacidad;
            else
                encontrado = nDatos >= actual.n;

            if (encontrado) {
                it.remove();
                actual.cond.signal();
            }
        }
    }

}
