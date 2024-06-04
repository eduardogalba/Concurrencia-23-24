package E6;

//Productor-Consumidor con un almacen para N datos
//*** NO MODIFICAR ***

class CC_06_PNCSem {

 // Probad distintos valores
 static final int NUM_ITERS = 100;
 static final int CAPAC     = 5;

 // CLASE PRODUCTOR
 static class Productor extends Thread {
	// vamos a escribir numeros en una cola,
	// de dos en dos, a partir de uno inicial
	// constructor: pasamos referencia a objeto compartido
	
	int      cont; // valor a escribir
	AlmacenN alm;  // el sitio donde escribimos
	
	Productor (int ini, AlmacenN alm) {
	    this.cont = ini;
	    this.alm  = alm;
	}

	// escribimos en la cola e incrementamos,
	// y asi hasta que nos aburramos.
	public void run() {
	    for (int i = 0; i < NUM_ITERS; i++) {
		this.alm.almacenar(this.cont);
		this.cont += 2;
	    }
	}
 }// END CLASS PRODUCTOR

 // CLASE CONSUMIDOR
 static class Consumidor extends Thread {

	// tendremos una referencia al objeto compartido
	// del que leemos
	AlmacenN alm;
	int id;

	Consumidor (int id, AlmacenN alm) {
	    this.id = id;
	    this.alm = alm;
	}
	
	public void run() {
	    // leemos sin parar...
	    while (true) {
	    //if (this.alm.esVacio() == 0) {
	    	int valor = this.alm.extraer();
	    	//if (valor < 0)
	    		//break;
	    	System.out.printf("[%d]: %d \n", this.id, valor);
	  //  }
	//    break;
	    // OPCIONAL: retardos
		// try {Thread.sleep(100);} catch (Exception e) {}
	    }            
     }
 }// END CLASS CONSUMIDOR

 public static final void main(final String[] args)
    throws InterruptedException {
     // Numero de productores y consumidores
     // Va a haber dos productores
	// y un numero variable de consumidores
     final int N_CONSS = 2;

     // Almacen compartido
     AlmacenN almac = new AlmacenN(CAPAC);

     // Array de consumidores
     Consumidor[] consumidores = new Consumidor[N_CONSS];

     // Creacion de los productores
	Productor productorPar   = new Productor(0,almac);
	Productor productorImpar = new Productor(1,almac); 

     // Creacion de los consumidores
     for (int i = 0; i < N_CONSS; i++) {
         consumidores[i] = new Consumidor(i, almac);
     }

     // Lanzamiento de los productores
     productorPar.start();
	productorImpar.start();

     // Lanzamiento de los consumidores
     for (int i = 0; i < N_CONSS; i++) {
         consumidores[i].start();
     }

     // Espera hasta la terminacion de los productores
     try {
	    productorPar.join();
	    productorImpar.join();
     } catch (Exception ex) {
         ex.printStackTrace();
         System.exit (-1);
     }
     
     //System.out.printf("%d\n",almac.esVacio());     
     /*
      * Se queda en bucle porque no hay productores que le hagan un signal
     for (int i = 0; i < N_CONSS; i++) {
         consumidores[i].join();
     }*/

	//System.exit(almac.esVacio());
     System.exit(0);
 }
}