// TODO: importa aqui las clases que necesites

import java.util.LinkedList;
import java.util.Queue;

public class CC_02_Carrera {

	private static final int NDATOS = 1000;
	public static Queue<Integer> recurso = new LinkedList<Integer>();

	// TODO: declara e inicializa aqui la variable con la cola
	// compartida

	private static class Productor extends Thread {
		private int inicial;

		public Productor(int inicial) {
			this.inicial = inicial;
		}

		public void run() {
			// TODO: generar NDATOS desde inicial, guardarlos en la cola,
			// cada dato es el anterior + 2
			for (int i = 0, prod = inicial; i < NDATOS; i++, prod += 2) {
				recurso.add(prod);
			}
		}
	}

	private static class Consumidor extends Thread {
		public void run() {
			// TODO: extraer e imprimir uno a uno todos los datos de la
			// cola, pensar en que pasa si la cola esta vacia

			while (true) {
				while (recurso.peek() != null) {
					System.out.println("Consume: " + recurso.poll());
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {

		Thread par = new Productor(0);
		Thread impar = new Productor(1);
		Thread c = new Consumidor();

		int tam = 0;

		par.start();
		impar.start();
		c.start();

		par.join();
		impar.join();
		c.join();

		// TODO: si todos los procesos terminan imprimir el numero de
		// elementos en la cola
		while (recurso.peek() != null) {
			recurso.poll();
			tam++;
		}

		System.out.println("Elementos de la cola: " + tam);

	}
}