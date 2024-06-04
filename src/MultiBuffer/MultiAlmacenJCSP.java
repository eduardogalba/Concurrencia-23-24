package MultiBuffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

// importamos la librer ´ı a JCSP
import org.jcsp.lang.*;

import es.upm.babel.cclib.MultiAlmacen;
import es.upm.babel.cclib.Producto;

class MultiAlmacenJCSP implements MultiAlmacen, CSProcess {
    // Canales para enviar y recibir peticiones al / del servidor
    private final Any2OneChannel chAlmacenar = Channel.any2one();
    private final Any2OneChannel chExtraer = Channel.any2one();

    /* Estado del recurso */
    private int TAM = 0;

    // Para evitar la construcci Â´o n de almacenes sin inicializar la
    // capacidad

    public MultiAlmacenJCSP(int n) {
        this.TAM = n;
        // COMPLETAR : inicializaci Â´o n de otros atributos
    }

    public class PetAlmacenar {
        Producto[] productos;
        One2OneChannel resp;

        public PetAlmacenar(Producto[] prod) {
            this.productos = prod.clone();
            this.resp = Channel.one2one();
        }

    }

    public class PetExtraer {
        int n;
        One2OneChannel resp;
        public PetExtraer(int n) {
            this.n = n;
            this.resp = Channel.one2one();
        }
    }

    @Override
    public void almacenar(Producto[] productos) {
        // COMPLETAR : comunicaci Â´o n con el servidor
        if (productos.length <= (TAM / 2)) {
            PetAlmacenar nueva = new PetAlmacenar(productos);
            chAlmacenar.out().write(nueva);
            //nueva.resp.in().read();
        }
    }

    public Producto[] extraer(int n) {
        Producto[] result = new Producto[n];
        // COMPLETAR : comunicaci Â´o n con el servidor
        if (n <= (TAM / 2)) {
            PetExtraer nueva = new PetExtraer(n);
            chExtraer.out().write(nueva);
            result = (Producto[]) nueva.resp.in().read();
        }
        return result;
    }

    // codigo del servidor
    private static final int ALMACENAR = 0;
    private static final int EXTRAER = 1;

    @SuppressWarnings("squid:S2189")
    public void run() {
        // Estado inicial del recurso
        Producto[] almacenado = new Producto[TAM];
        int aExtraer = 0;
        int aInsertar = 0;
        int nDatos = 0;
        // COMPLETAR : declaraci Â´o n de canales y estructuras auxiliares
        Guard[] entradas = {
                chAlmacenar.in(),
                chExtraer.in()
        };
        Alternative servicios = new Alternative(entradas);
        int choice = 0;

        /* Almacenes */
        Collection<PetAlmacenar> petAlmacenars = new LinkedList<>();
        Collection<PetExtraer> petExtraers = new LinkedList<>();

        while (true) {
            choice = servicios.fairSelect();
            switch (choice) {
                case ALMACENAR:
                    PetAlmacenar p = (PetAlmacenar) chAlmacenar.in().read();
                    petAlmacenars.add(p);
                    break;
                case EXTRAER:
                    PetExtraer p2 = (PetExtraer) chExtraer.in().read();
                    petExtraers.add(p2);
                    break;
                default:
                    break;
            }
            // COMPLETAR : atenci ´o n de peticiones pendientes
            Iterator<PetAlmacenar> it = petAlmacenars.iterator();
            while (it.hasNext()) {
                PetAlmacenar actual = it.next();
                
                if (actual.productos.length + nDatos <= TAM) {
                    for (int i = 0; i < actual.productos.length; i++) {
                        almacenado[aInsertar] = actual.productos[i];
                        nDatos++;
                        aInsertar++;
                        aInsertar %= TAM;
                    }
                    //actual.resp.out().write(1);
                    petAlmacenars.remove(actual);
                }
            }

            Iterator<PetExtraer> it2 = petExtraers.iterator();
            while (it.hasNext()) {
                PetExtraer actual = it2.next();
                
                if (nDatos < actual.n) {
                    Producto [] result = new Producto[actual.n];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = almacenado[aExtraer];
                        almacenado[aExtraer] = null;
                        nDatos--;
                        aExtraer++;
                        aExtraer %= TAM;
                    }
                    actual.resp.out().write(result);
                    petExtraers.remove(actual);
                }
            }
        }
    }

}
