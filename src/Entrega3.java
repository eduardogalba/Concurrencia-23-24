

public class Entrega3 {
	public static final int N = 100;
	public volatile static int x = 0;
	public volatile static boolean flag [] = {false, false};
	public volatile static int turn;
	
	public static class Inc extends Thread {
		public void run () {
			/* Protocolo de entrada */
//			flag[0] = true;
//			turn = 1;
//			while (flag[0] && turn == 1); /* Bucle de espera activa */
			/* Dentro de Seccion Critica */
			for (int i = 0; i < 100000000; i++)
				x = x + 1;
			/* Protocolo de salida */
//			flag[0] = false;
		}
	}
	
	public static class Dec extends Thread {
		public void run () {
			/* Protocolo de entrada */
//			flag[1] = true;
//			turn = 0;
			/* Bucle de espera activa */
//			while (flag[1] && turn == 0);
			/* Dentro de Seccion Critica */
			for (int i = 0; i < 100000000; i++)
				x = x - 1;
			/* Protocolo de salida */
//			flag[1] = false;
		}
	}
	
	public static void main (String[] args)
	throws InterruptedException {
		Thread p_inc [] = new Thread [N];
		Thread p_dec [] = new Thread [N];
		
		for (int i = 0; i < N; i++) {
			p_inc[i] = new Thread ();
			p_dec[i] = new Thread ();
		}
		
		for (int i = 0; i < N; i++) {
			p_inc[i].start();
			p_dec[i].start();
		}
		
		for (int i = 0; i < N; i++) {
			p_inc[i].join();
			p_dec[i].join();
		}
		
		System.out.println("x = " + x);
	}
	
}