package primerparcial;

public class ext_2023 {

	static class A extends Thread {
		public void run() {
			Thread b2 = new B();
			b2.start();
			System.out.print("P");
		}
	}

	static class B extends Thread {
		public void run() {
			System.out.print("S");
		}
	}

	public static void main(String[] args) {
		Thread a1 = new A();
		Thread b1 = new B();
		a1.start();
		b1.start();
		try {
			a1.join();
			b1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("F");
	}
}
