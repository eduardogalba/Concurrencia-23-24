package E6;

//TODO: importar la clase de los semaforos.
import es.upm.babel.cclib.Semaphore;

//Almacen FIFO concurrente para N datos

class AlmacenN {
 private int capacidad = 0;
 private int[] almacenado = null;
 @SuppressWarnings("unused")
private int nDatos = 0;
 private int aExtraer = 0;
 private int aInsertar = 0;

 // TODO: declaracion de los semaforos necesarios
 
 /* Un semaforo para garantizar la exclusion mutua */
 private Semaphore mutex;
 
 /* Semaforos para la sincronizacion de procesos */
 private Semaphore puedeAlmacenar;
 private Semaphore puedeExtraer;

 public AlmacenN(int n) {
	capacidad = n;
	almacenado = new int[capacidad];
	nDatos = 0;
	aExtraer = 0;
	aInsertar = 0;

	// TODO: inicializacion de los semaforos
	//       si no se ha hecho mas arriba
	mutex = new Semaphore (1);
	puedeAlmacenar = new Semaphore(n);
	puedeExtraer = new Semaphore (0);
 }

 public void almacenar(int producto) {
	 
	// TODO: protocolo de acceso a la seccion critica y codigo de
	// sincronizacion para poder almacenar.
	 puedeAlmacenar.await();
	 mutex.await();
	// Seccion critica //////////////
	almacenado[aInsertar] = producto;
	nDatos++;
	aInsertar++;
	aInsertar %= capacidad;
	// //////////////////////////////

	// TODO: protocolo de salida de la seccion critica y codigo de
	// sincronizacion para poder extraer.
	mutex.signal();
	puedeExtraer.signal();
 }

 /* 
  * Tal y como esta implementado el programa, el hilo consumidor puede quedarse
  * bloqueado sin haber consumido todos los elementos de la lista porque en las 
  * iteraciones de extraer(), aExtraer nunca llega a la posicion i-esima donde
  * se encuentran los ultimos elementos y antes de eso, el semaforo se queda 
  * bloqueado.
  * 
  * */
 
 public int extraer() {
	int result;

	// TODO: protocolo de acceso a la seccion critica y codigo de
	// sincronizacion para poder extraer.
	puedeExtraer.await();
	mutex.await();
	// Seccion critica ///////////
	result = almacenado[aExtraer];
	nDatos--;
	aExtraer++;
	aExtraer %= capacidad;
	//if (esVacio() == 1)
		//return -1;
	// ///////////////////////////

	// TODO: protocolo de salida de la seccion critica y codigo de
	// sincronizacion para poder almacenar.
	mutex.signal();
	puedeAlmacenar.signal();

	return result;
 }
 
 public int esVacio () {
	 
	 mutex.await();
	 for (int i = 0; i < capacidad; i++) {
		 if (almacenado[i] > 0)
			 return 1;
	 }
	 mutex.signal();
	 
	 return 0;
 }
 
}