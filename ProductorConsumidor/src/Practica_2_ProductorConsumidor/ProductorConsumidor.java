package Practica_2_ProductorConsumidor;



public class ProductorConsumidor implements Runnable{
	
	//Creamos una variable tipo boolean para alternar entre consumidos y cocinero
	private boolean consumidor;
	private static int tarta = 0;
	//Candado
	private static Object lock = new Object();
	
	 
	//Constructor
	public ProductorConsumidor(boolean consumidor) {
		this.consumidor = consumidor;
	}
	
	@Override
	public void run() {
		while(true) {
			if(consumidor) {
				//si es el consumidor
				consumiendo();		
			}else {
				//si es el cocinero
				cocinando();	
			}			
		}		
	}
	//Función para comer tartas
	private void consumiendo() {
		synchronized(lock) {
		//Si tenemos mas de 0 tartas, el consumidor se come una porción
		if(tarta>0) {
			tarta--;
			System.out.println("Soy el consumidor y quiero un trozo de tarta.");
			System.out.println("Quedan " + tarta + " porciones de tarta.");
			System.out.println("----------------------------------------");
			try {
				Thread.sleep(1000);  //OJO OJO OJO  Dormir un poco
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}else {
			//Si no hay tartas, se despiertan a todos los hilos
			//El consumidor no hará nada y llegará aqui de nuevo
			//El cocinero hará su acción y creará 10 tartas
			lock.notifyAll();
			
			
			//Y el consumidor se va a dormir
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		  }
		}
	}
	//Función para cocinar tartas
	private void cocinando() {
		synchronized(lock) {
			//Sólo un hilo puede escribir a la vez en la variable tarta
			//Con el if, si no hay tartas se crean y si hay se va directamente a dormir
			if(tarta==0) {
				tarta=10;
				System.out.println("Soy el cocinero y voy a hacer una tarta de " + tarta + " porciones.");
				System.out.println("----------------------------------------");
				//Tendrá que informar para despertar al consumidor
				lock.notifyAll();
			}
			try {
				lock.wait();
				//Ya ha creado la tarta y se duerme
			}catch(Exception e) {}
		}	
	}
	
	
	
	
	

	public static void main(String[] args) {
		//Tendremos dos hilos, uno el cocinero y otro el cliente
		int numerohilos = 2;
		
		//Creamos el lector de hilos a traves de la implementación de runnable
		Thread[] hilo = new Thread[numerohilos];
		
		for(int i=0;i<hilo.length;i++) {
			Runnable runnable = null;
			
			//Con este iff else distinguimos al cocinero del consumidor.
			//Si es true es consumidor, si es false es cocinero
			if(i!=0) {
				runnable = new ProductorConsumidor(true);
			}else {
				runnable = new ProductorConsumidor(false);
			}
			
			//Empiezan los hilos
			hilo[i] = new Thread(runnable);
			hilo[i].start();
		}
		
		for (int i=0; i<hilo.length;i++) {
			try {
				hilo[i].join();
			}catch(Exception e){}
		}		
	}
}
