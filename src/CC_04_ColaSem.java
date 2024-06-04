// CC_04_ColaSem.java
//
// Queremos provocar una carrera por acceso
// simultaneo a una estructura de datos

// En este caso la implementacion de cola estara fija,
// sera una cola "circular" de tamanho acotado.

// Y solo tendremos _1_ productor y un consumidor.

// Se trata de resolver las situaciones de carrera
// mediante el empleo de semaforos.
// TO DO: importar biblioteca de semaforos 
import es.upm.babel.cclib.Semaphore;


public class CC_04_ColaSem {

    // Cuantas iteraciones necesitaremos para provocar un problema?
    static final int NUM_OPS = 1000;
    // el tamanho de la cola acotada
    static final int TAM = 5;

    // TO DO: declarar semaforo(s) para exclusion mutua
    
    /* Semaforos para garantizar exclusion mutua 
     * Al principio, la cola esta vacia:
     * - No se puede consumir
     * */ 
    static volatile Semaphore produce = new Semaphore (1);
    static volatile Semaphore consume = new Semaphore (0);
    
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

	
	public void run () {
	    // TO DO: usar semaforos para evitar situaciones de carrera
	    for (int i = 0; i < NUM_OPS; i++) {
		// nos protegemos ante el llenado de la cola
	    	
		/* Protocolo de entrada a SC */
		produce.await();
		/* Seccion Critica */
		boolean colaLlena;
		do {
		    colaLlena = cola.esLlena();
		} while (colaLlena);
		cola.encola(this.cont);
		/* Protocolo de salida de SC */
		consume.signal();
		
		/* Resto codigo */
		cont++;
		
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
	
	public void run () {
	    // TO DO: usar semaforos para evitar situaciones de carrera
	    for (int i = 0; i < NUM_OPS; i++) {
		// si hay elementos en la cola extraemos el primero
		// y lo imprimimos
	    	;
			/* Protocolo de entrada a SC */
			consume.await();
			/* Seccion Critica */
			boolean colaVacia;
			do {
			    colaVacia = cola.esVacia();
			} while (colaVacia);
			int val = cola.desencola();
			/* Protocolo de salida de SC */
			produce.signal();
			System.out.printf("%4d",val);
			
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