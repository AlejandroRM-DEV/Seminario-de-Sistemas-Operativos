package application;

import java.util.concurrent.TimeUnit;

public class Tiempo{
	private long inicio;
	
	public void inicio(){
		inicio(0);
	}
	
	public void inicio(long acumulado){
		inicio = System.nanoTime() - acumulado;
	}
	
	public long tiempo(){
		return System.nanoTime() - inicio;
	}
	
	public long segundos(){
		return TimeUnit.SECONDS.convert(tiempo(), TimeUnit.NANOSECONDS);
	}
}
