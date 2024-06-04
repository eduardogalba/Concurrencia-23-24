package E9;

import es.upm.babel.cclib.Monitor;

public class MezcladorMon implements Mezclador {
	public static final int IZDA = 0;
	public static final int DCHA = 1;
	public static final int LADOS = 2;

	// Definicion del estado del recurso
	private boolean[] hayDato;
	private int[] dato;

	private Monitor mutex;
	// 2 Colas por cada posible valor del parametro (IZDA,DCHA)
	private Monitor.Cond esperaInsIzda;
	private Monitor.Cond esperaInsDcha;
// 	Nueva Idea: Mas eficiente
//	private Monitor.Cond [] esperaIns;
	
	// Cola de procesos para los que quieren extraer
	private Monitor.Cond esperaExt;

	public MezcladorMon() {
		hayDato = new boolean[LADOS];
		dato = new int[LADOS];

		mutex = new Monitor();
		esperaInsIzda = mutex.newCond();
		esperaInsDcha = mutex.newCond();
		
//		esperaIns = new Monitor.Cond[LADOS];
		
//		for (int i = 0; i < LADOS; i++)
//			esperaIns[i] = mutex.newCond();
		
		esperaExt = mutex.newCond();
	}

	public void insertar(int l, int d) {
		// Protocolo de entrada de exclusion mutua
		mutex.enter();
		
		// Se cumple la CPRE?
		if (hayDato[l]) {
			if (l == IZDA)
				esperaInsIzda.await();
			else
				esperaInsDcha.await();
		}
		
		// POST
		hayDato[l] = true;
		dato[l] = d;
		
		// Desbloqueo?
		if (hayDato[(l + 1) % LADOS])
			esperaExt.signal();
		
		// Protocolo de salida de exclusion mutua
		mutex.leave();
	}

	public int extraerMenor() {
		// Protocolo de entrada de exclusion mutua
		mutex.enter();
		
		// Se cumple la CPRE?
		if (!hayDato[IZDA] || !hayDato[DCHA]) 
			esperaExt.await();
		
		// POST
		int min = 0;
		
		if (dato[IZDA] <= dato[DCHA]) {
			min = dato[IZDA];
			hayDato[IZDA] = false;
		} else if (dato[DCHA] <= dato[IZDA]) {
			min = dato[DCHA];
			hayDato[DCHA] = false;
		}
		
		// Desbloqueo?
		
//		esperaIns[l].signal();
		
		if (!hayDato[DCHA])
			esperaInsDcha.signal();
		else if (!hayDato[IZDA])
			esperaInsIzda.signal();
		
		//Protocolo de salida de exclusion mutua
		mutex.leave();

		return min;
	}
	
}