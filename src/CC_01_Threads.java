import java.util.Random;


public class CC_01_Threads {

	public static int N = 40;		// Nº threads
	public static int T = 1500;		// T ms a dormir
	
	public static class Proceso extends Thread {
		
		private int id;
		
		public Proceso (int name) {
			id = name;
		}
		
		public void run() {
			int tiempo = new Random ().nextInt(4)*100;		// Tiempo aleatorio generado 
			System.out.println("Thread " + id + " dormire " + (T-tiempo) + " ms.");		// Se identifica y explica cuanto tiempo duerme
			try {
				Thread.sleep(T-(tiempo));							// Duerme T-tiempo ms 
				System.out.println("Thread " + id + " estoy despierto.");  // Se despierta
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main (String args[])
			throws InterruptedException {
		
		Proceso [] procesos = new Proceso [N];
		
		for (int i = 0; i < N; i++) 
			procesos[i] = new Proceso(i);
		
		for (int i = 0; i < N; i++)
			procesos[i].start();
		
		
		for (int i = 0; i < N; i++) 
			procesos[i].join();
		
		System.out.println("Han terminado todos los hilos.");
		
	}
}