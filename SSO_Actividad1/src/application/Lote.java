package application;

import java.util.ArrayDeque;
import java.util.Queue;

public class Lote {
	private Queue<Proceso> procesos;
	
	public Lote(){
		procesos = new ArrayDeque<>();
	}
	
	public void agregar(Proceso p){
		procesos.add(p);
	}
	
	public boolean estaVacio(){
		return procesos.isEmpty();
	}
	
	public Proceso extrae(){
		return procesos.poll();
	}
	
	public Proceso[] dameProcesos(){
		return procesos.toArray(new Proceso[procesos.size()]);
	}
}
