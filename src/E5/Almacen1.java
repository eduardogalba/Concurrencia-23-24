package E5;

//TODO: importar la clase de los semaforos.
//
import es.upm.babel.cclib.Semaphore;

//Almacen concurrente para un dato
class Almacen1 {
 // Producto a almacenar
 private int almacenado;
 
 // TODO: declaracion e inicializacion de los semaforos
 // necesarios
 //
 //
 /* Un semaforo para garantizar la exclusion mutua */
 private Semaphore mutex = new Semaphore (1);
 
 /* 
  * Semaforos para sincronizar los procesos 
  * Como el proceso que almacena tiene que esperar a que el otro proceso extraiga
  * un dato. Dos semaforos, para establecer un turno y unas colas de procesos que 
  * almacenan y extraen.
  */
 private Semaphore puedeExtraer = new Semaphore (0);
 private Semaphore puedeAlmacenar = new Semaphore (1);
 
 
 public Almacen1() {
 }
 
 public void almacenar(int producto) {
	// TODO: protocolo de acceso a la seccion critica y codigo de
	// sincronizacion para poder almacenar.
	// 
	puedeAlmacenar.await();
	mutex.await();
	
	// Seccion critica
	almacenado = producto;

	// TODO: protocolo de salida de la seccion critica y codigo de
	// sincronizacion para poder extraer.
	//
	mutex.signal();
	puedeExtraer.signal();
 }

 public int extraer() {
	int result;

	// TODO: protocolo de acceso a la seccion critica y codigo de
	// sincronizacion para poder extraer.
	// 
	puedeExtraer.await();
	mutex.await();
	// Seccion critica
	result = almacenado;

	// TODO: protocolo de salida de la seccion critica y codigo de
	// sincronizacion para poder almacenar.
	// 
	mutex.signal();
	puedeAlmacenar.signal();
	return result;
 }
}
