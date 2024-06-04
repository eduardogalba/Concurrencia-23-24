package E9;

//CC_09_MezclaMon.java
//
//Mezclamos dos streams de enteros crecientes
//para formar un �nico stream creciente

public class CC_09_MezclaMon {

 static final int NUM_OPS = 1000000; // OJO!!
 
 // PRODUCTORES Y CONSUMIDORES
 // Los productores insertan en la cola valores
 // s, s+2, s+4, etc.
 static class Productor extends Thread {
	private int cont;
	private Mezclador mezclador; // make it generic

	// constructor
	public Productor (int start, Mezclador m) {
	    this.cont = start;
	    this.mezclador = m;
	}

	public void run () {
	    for (int i = 0; i < NUM_OPS; i++) {
		mezclador.insertar(cont%2,cont);
		cont += 2;
	    }
	}
 }

 // Solo habr� un hilo consumidor y se dedica a extraer lo que haya
 // en la cola e imprimirlo
 static class Consumidor extends Thread {

	private Mezclador mezclador; 
	
	public Consumidor (Mezclador m) {
	    this.mezclador = m;
	}
	
	public void run () {
	    while (true) { // OJO!!
		int val = mezclador.extraerMenor();
		System.out.println(val);
		//System.out.println(" ");
	    }
	}
 }

 public static void main(String[] args)
 throws InterruptedException {

	// el recurso compartido
	Mezclador mezclador = new MezcladorMon(); // OJO!!
	
	// creaci�n de hilos
	Productor par   = new Productor(0,mezclador);
	Productor impar = new Productor(1,mezclador);
	
	Consumidor consumidor = new Consumidor(mezclador);

	// arrancamos los hilos
	par.start();
	impar.start();
	consumidor.start();

	// esperamos a que terminen los productores
	par.join();
	impar.join();
	// y luego nos cargamos al consumidor
	// TO DO
 }// main

}// CC_09_MezclaMon