package application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tiempo{
	private long segundos;
	private boolean pausado;
	private ScheduledExecutorService executor;

	public Tiempo() {
		pausado = true;
		segundos = 0;
		executor = Executors.newScheduledThreadPool(1);
	}

	public void inicio(){
		segundos = 0;
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if(!pausado){
					segundos++;
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
		reanudar();
	}

	public void pausar(){
		pausado = true;
	}

	public void reanudar(){
		pausado = false;
	}

	public void parar(){
		executor.shutdownNow();
	}

	public long segundos(){
		return segundos;
	}
}
