package application;

import java.util.concurrent.TimeUnit;

public class Proceso implements Runnable{

	public static enum Estado {
		LISTO, EJECUTANDO, TERMINADO, BLOQUEADO, ERROR
	}

	public static enum Operacion {
		SUMA,
		RESTA,
		MULTIPLICACION,
		DIVISION,
		MODULO,
		RAIZ_CUADRADA,
		INDEFINIDA;

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
			default: return "?";
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
	private long tiempoEstimado;
	private long nanosegTranscurridos;
	private long tiempoLlegada;
	private long tiempoFinalizacion;
	private long tiempoRespuesta;
	Tiempo bloqueado;
	Tiempo respuesta;
	private double argA;
	private double argB;
	private double resultado;
	
	protected Proceso(int id, Operacion operacion, long tiempo, double argA, double argB){
		this.operacion = operacion;
		this.id = id;
		this.tiempoEstimado = tiempo;
		this.argA = argA;
		this.argB = argB;
		this.bloqueado = new Tiempo();
		this.respuesta = new Tiempo();
		this.tiempoRespuesta = 0;
		this.nanosegTranscurridos = 0;
		this.tiempoLlegada = 0;
		this.tiempoFinalizacion = 0;
		this.estado = Estado.LISTO;
	}
	public Proceso(Operacion operacion, long tiempo, double argA) {
		this(operacion,tiempo, argA, 0);
	}

	public Proceso(Operacion operacion, long tiempo, double argA, double argB) {
		this(actualID++, operacion, tiempo, argA, argB);
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
		default: 
			resultado = 0;
			break;
		}
	}

	public double getArgA() {
		return argA;
	}

	public double getArgB() {
		return argB;
	}

	public long getEspera(){
		return getRetorno() - getServicio();
	}

	public Estado getEstado() {
		return estado;
	}

	public long getFinalizacion(){
		return tiempoFinalizacion;
	}

	public int getId() {
		return id;
	}
	public long getLlegada(){
		return tiempoLlegada;
	}
	public Operacion getOperacion() {
		return operacion;
	}
	public long getRespuesta(){
		return tiempoRespuesta;
	}
	public double getResultado() {
		return resultado;
	}
	public String getResultadoCompleto() {
		return toStringResultado();
	}
	public long getRetorno(){
		return tiempoFinalizacion - tiempoLlegada;
	}
	public long getServicio(){
		return TimeUnit.SECONDS.convert(nanosegTranscurridos, TimeUnit.NANOSECONDS);
	}

	public long getTiempo() {
		return tiempoEstimado;
	}

	public long getTiempoBloqueado() {
		if(estado == Estado.BLOQUEADO ){
			return bloqueado.segundos();	
		} else {
			return 0;
		}
	}

	public long getTiempoRestante() {
		return tiempoEstimado - getTiempoTranscurido();
	}

	public long getTiempoTranscurido() {
		return TimeUnit.SECONDS.convert(nanosegTranscurridos, TimeUnit.NANOSECONDS);
	}

	@Override
	public void run(){
		Tiempo reloj = new Tiempo();

		if(estado == Estado.LISTO){
			reloj.inicio();
			tiempoRespuesta = respuesta.segundos();
			ejecutar();
			estado = Estado.EJECUTANDO;
		} else if(estado == Estado.BLOQUEADO){
			reloj.inicio(nanosegTranscurridos);
			estado = Estado.EJECUTANDO;
		}

		while(estado != Estado.TERMINADO ){
			nanosegTranscurridos = reloj.tiempo();
			if(reloj.segundos() >= tiempoEstimado){
				estado = Estado.TERMINADO;
			}else if(estado == Estado.BLOQUEADO ){
				bloqueado.inicio();
				break;
			} else if( estado == Estado.ERROR){
				break;
			}
		}
	}

	public void setEstado(Estado estado) {
		if(this.estado != Estado.TERMINADO){
			this.estado = estado;
		}
	}

	public void setTiempoFinalizacion(long tiempoFinalizacion) {
		this.tiempoFinalizacion = tiempoFinalizacion;
	}

	public void setTiempoLlegada(long tiempoLlegada) {
		this.tiempoLlegada = tiempoLlegada;
		respuesta.inicio();
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
			default: 
				sb.append(argA + " " + operacion.simbolo() + " " + argB);
				break;
			}
			sb.append(" = " + resultado);
		}

		return sb.toString();
	}
}
