package application;

public class Proceso implements Runnable{
	private static int actualID = 0;

	protected static void reiniciarIDS(){
		actualID = 0;
	}

	private Operacion operacion;
	private Estado estado;
	private int id;
	private long tiempoEstimado;
	private long tiempoLlegada;
	private long tiempoFinalizacion;
	private long tiempoRespuesta;
	private double argA;
	private double argB;
	private double resultado;
	private boolean haIniciado;


	private long tiempoBloqueo;
	private long tiempoEjecucion;
	private long tiempoEspera;
	
	protected Proceso(int id, Operacion operacion, long tiempo, double argA, double argB){
		this.operacion = operacion;
		this.id = id;
		this.tiempoEstimado = tiempo;
		this.argA = argA;
		this.argB = argB;

		this.tiempoBloqueo = 0;
		this.tiempoEjecucion = 0;
		this.tiempoEspera = 0;
		
		this.tiempoLlegada = -1;
		this.tiempoFinalizacion = -1;
		this.tiempoRespuesta = -1;

		this.estado = Estado.NUEVO;
		this.haIniciado = false;
		this.resultado = Double.POSITIVE_INFINITY;
	}

	public Proceso(Operacion operacion, long tiempo, double argA) {
		this(operacion,tiempo, argA, 0);
	}

	public Proceso(Operacion operacion, long tiempo, double argA, double argB) {
		this(actualID++, operacion, tiempo, argA, argB);
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
	public String getResultadoCompleto() {
		return toStringResultado();
	}

	public long getTiempo() {
		return tiempoEstimado;
	}

	public String toStringResultado(){
		StringBuilder sb = new StringBuilder();

		if(estado == Estado.TERMINADO){
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
		} else if(estado == Estado.ERROR ){
			sb.append("Error");
		} else {
			sb.append(resultado);
		}

		return sb.toString();
	}
	
	/*********************  EJECUCION **********************/
	public void setEstado(Estado estado) {
		if(this.estado != Estado.TERMINADO){
			this.estado = estado;
		}
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

	@Override
	public void run(){
		if(!haIniciado){
			ejecutar();
			haIniciado = true;
		}
	}

	public void aumentaTiempoBloqueo(){
		tiempoBloqueo++;
	}
	public void reiniciaTiempoBloqueo(){
		tiempoBloqueo = -1;
	}
	public long getTiempoBloqueado() {
		if(estado == Estado.BLOQUEADO ){
			return tiempoBloqueo;	
		} else {
			return 0;
		}
	}
	public long getTiempoRestanteBloqueo(){
		if(estado == Estado.BLOQUEADO ){
			return 8 - getTiempoBloqueado();	
		} else {
			return -1;
		}
	}
	public void aumentaTiempoEpera(){
		tiempoEspera++;
	}
	public void aumentaTiempoEjecucion(){
		tiempoEjecucion++;
	}
	public long getTiempoTranscurido() {
		return tiempoEjecucion;
	}
	public long getTiempoRestante() {
		if(estado == Estado.TERMINADO){
			return 0;
		} else {
			return tiempoEstimado - getTiempoTranscurido();
		}
	}

	public void setTiempoFinalizacion(long segundos) {
		this.tiempoFinalizacion = segundos;
	}

	public void setTiempoLlegada(long segundos) {
		this.tiempoLlegada = segundos;
	}

	public void setTiempoRespuesta(long segundos){
		if(!haIniciado){
			this.tiempoRespuesta = segundos;
		}
	}

	/************************** BCP ***************************/
	public long getLlegada(){
		return tiempoLlegada;
	}

	public long getFinalizacion(){
		return tiempoFinalizacion;
	}

	public long getRetorno(){
		if( tiempoFinalizacion >= 0 ){
			return tiempoFinalizacion - tiempoLlegada;
		} else {
			return -1;
		}
	}

	public long getEspera(){
		return tiempoEspera;
	}

	public long getServicio(){
		return tiempoEjecucion;
	}

	public long getRespuesta(){
		return tiempoRespuesta;
	}
}
