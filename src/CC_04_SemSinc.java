// CC_04_ColaSem.java
//
// Queremos provocar una carrera por acceso
// simultaneo a una estructura de datos

// En este caso la implementacion de cola estara fija,
// sera una cola "circular" de tamaño acotado.

// Y solo tendremos _1_ productor y un consumidor.

// Se trata de resolver las situaciones de carrera
// mediante el empleo de semaforos.
// TO DO: importar biblioteca de semaforos 
import es.upm.babel.cclib.Semaphore;


public class CC_04_SemSinc {

    // Cuantas iteraciones necesitaremos para provocar un problema?
    static final int NUM_OPS = 1000;
    // el tamaño de la cola acotada
    static final int TAM = 5;

    // TO DO: declarar semaforo(s) para exclusion mutua
    /* Semaforo para garantizar exclusion mutua */ 
    static volatile Semaphore mutex = new Semaphore (1);
    /* Semaforos para sincronizar los procesos 
     *  - no_llena = Indica que se puede producir, agregar elementos a la cola
     *  - no_vacia = Indica que se puede consumir, quitar elementos de la cola
     *  Al principio, la cola esta vacia
     *  - No se podra consumir
     *  - Si se podra producir hasta TAM elementos
     * */
    static volatile Semaphore no_llena = new Semaphore(TAM);
    static volatile Semaphore no_vacia = new Semaphore (0);
    
    // proporcionamos una implementacion de cola circular
    public static class ColaCircular {
	private int capacidad = 0;
	private int[] almacen = null;
	private int nDatos = 0;
	private int aExtraer = 0;
	private int aInsertar = 0;

	public ColaCircular (int n) {
	    capacidad = n;
	    almacen = new int[capacidad];
	    nDatos = 0;
	    aExtraer = 0;
	    aInsertar = 0;
	}

	public void encola (int dato) {
	    almacen[aInsertar] = dato;
	    nDatos++;
	    aInsertar++;
	    aInsertar %= capacidad;
	}

	public int desencola () {
	    int result;

	    result = almacen[aExtraer];
	    nDatos--;
	    aExtraer++;
	    aExtraer %= capacidad;
	    return result;
	}

	public boolean esVacia () {
	    return nDatos == 0;
	}

	public boolean esLlena () {
	    return nDatos == capacidad;
	}
    } // class ColaCircular


    // PRODUCTORES Y CONSUMIDORES
    // Solo hay un hilo productor que encola valores 
    // 0, 1, 2, 3, 4, ...
    static class Productor extends Thread {
	private int cont;
	private ColaCircular cola;

	// constructor
	public Productor (ColaCircular cola) {
	    this.cont = 0;
	    this.cola = cola;
	}

	void sc () {
		
	}
	
	public void run () {
	    // TO DO: usar semaforos para evitar situaciones de carrera
	    for (int i = 0; i < NUM_OPS; i++) {
		// nos protegemos ante el llenado de la cola
	    	
	    /* Sincronizacion */
		no_llena.await();
		/* Protocolo de entrada a SC */
		mutex.await();
		/* Seccion Critica */
		boolean colaLlena;
		do {
		    colaLlena = cola.esLlena();
		} while (colaLlena);
		cola.encola(this.cont);
		cont++;
		/* Protocolo de salida de SC */
		mutex.signal();
		/* Sincronizacion */
		no_vacia.signal();
	    }
	} // run
    }

    // Solo habra un hilo consumidor y se dedica a extraer lo que haya
    // en la cola e imprimirlo
    static class Consumidor extends Thread {

	private ColaCircular cola;
	
	// constructor
	public Consumidor (ColaCircular cola) {
	    this.cola = cola;
	}
	
	void sc () {
		
	}
	
	public void run () {
	    // TO DO: usar semaforos para evitar situaciones de carrera
	    for (int i = 0; i < NUM_OPS; i++) {
		// si hay elementos en la cola extraemos el primero
		// y lo imprimimos
	    	/* Sincronizacion */
			no_vacia.await();
			/* Protocolo de entrada a SC */
			mutex.await();
			/* Seccion Critica */
			boolean colaVacia;
			do {
			    colaVacia = cola.esVacia();
			} while (colaVacia);
			int val = cola.desencola();
			/* Protocolo de salida de SC */
			mutex.signal();
			System.out.printf("%4d",val);
			/* Sincronizacion */
			no_llena.signal();
	    }
	} // run
    }

    public static void main(String[] args)
    throws InterruptedException {

	// la cola
	ColaCircular cola = new ColaCircular(TAM);

	// creacion de hilos
	Productor  productor  = new Productor(cola);
	Consumidor consumidor = new Consumidor(cola);

	// arrancamos los hilos
	productor.start();
	consumidor.start();

	// esperamos a que terminen
	productor.join();
	consumidor.join();

    }// main

}// CC_04_ColaSem