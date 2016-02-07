package application;

import java.util.concurrent.TimeUnit;

public class Proceso implements Runnable{

	public static enum Estado {
		LISTO, EJECUTANDO, TERMINADO, INTERRUMPIDO, ERROR
	}

	public static enum Operacion {
		SUMA,
		RESTA,
		MULTIPLICACION,
		DIVISION,
		MODULO,
		RAIZ_CUADRADA;

		public String simbolo(){
			switch (this) {
			case SUMA:
				return "+";
			case RESTA:
				return "-";
			case MULTIPLICACION:
				return "*";
			case DIVISION:
				return "/";
			case MODULO:
				return "+";
			case RAIZ_CUADRADA:
				return "\u221A";
			default: return "";
			}
		}
	}

	private static int actualID = 0;

	protected static void reiniciarIDS(){
		actualID = 0;
	}
	
	private Operacion operacion;
	private Estado estado;
	private int id;
	private long tiempo;
	private long tiempoTranscurido;
	
	private double argA;
	private double argB;
	private double resultado;

	public Proceso(Operacion operacion, int tiempo, double argA) {
		this(operacion,tiempo, argA, 0);
	}

	public Proceso(Operacion operacion, int tiempo, double argA, double argB) {
		this.operacion = operacion;
		this.id = actualID++;
		this.tiempo = tiempo;
		this.argA = argA;
		this.argB = argB;

		tiempoTranscurido = 0;
		estado = Estado.LISTO;
	}

	private void ejecutar(){
		switch (operacion) {
		case SUMA:
			resultado = argA + argB;
			break;
		case RESTA:
			resultado = argA - argB;
			break;
		case MULTIPLICACION:
			resultado = argA * argB;
			break;
		case DIVISION:
			resultado = argA / argB;
			break;
		case MODULO:
			resultado = argA % argB;
			break;
		case RAIZ_CUADRADA:
			resultado = Math.sqrt(argA);
			break;
		default: break;
		}
	}

	public double getArgA() {
		return argA;
	}

	public double getArgB() {
		return argB;
	}

	public Estado getEstado() {
		return estado;
	}

	public int getId() {
		return id;
	}

	public Operacion getOperacion() {
		return operacion;
	}

	public double getResultado() {
		return resultado;
	}

	public long getTiempo() {
		return tiempo;
	}

	public long getTiempoRestante() {
		return tiempo - getTiempoTranscurido();
	}
	
	public long getTiempoTranscurido() {
		return TimeUnit.SECONDS.convert(tiempoTranscurido, TimeUnit.NANOSECONDS);
	}
	
	@Override
	public void run(){
		Tiempo reloj = new Tiempo();
		if(estado == Estado.LISTO){
			ejecutar();
			reloj.inicio();
			estado = Estado.EJECUTANDO;
		} else if(estado == Estado.INTERRUMPIDO){
			reloj.inicio(tiempoTranscurido);
			estado = Estado.EJECUTANDO;
		}
		
		while(estado != Estado.TERMINADO ){
			tiempoTranscurido = reloj.tiempo();
			if(reloj.segundos() >= tiempo){
				estado = Estado.TERMINADO;
			}
			if(estado == Estado.INTERRUMPIDO || estado == Estado.ERROR){
				break;
			}
		}
	}

	public void setEstado(Estado estado) {
		if(this.estado != Estado.TERMINADO){
			this.estado = estado;
		}
	}

	public String toStringResultado(){
		StringBuilder sb = new StringBuilder();

		if(estado == Estado.ERROR ){
			sb.append("ERROR");
		} else {
			switch (operacion) {
			case SUMA:
			case RESTA:
			case MULTIPLICACION:
			case DIVISION:
			case MODULO:
				sb.append(argA + " " + operacion.simbolo() + " " + argB);
				break;
			case RAIZ_CUADRADA:
				sb.append(operacion.simbolo() + argA);
				break;
			default: break;
			}
			sb.append(" = " + resultado);
		}

		return sb.toString();
	}
}
