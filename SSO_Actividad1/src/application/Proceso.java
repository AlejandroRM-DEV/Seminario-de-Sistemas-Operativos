package application;

public class Proceso implements Runnable{
	
	public static enum Estado {
		LISTO, EJECUTANDO, TERMINADO
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
	private String programador;
	private Operacion operacion;
	private Estado estado;
	private int id;
	private int tiempo;
	private double argA;
	private double argB;
	private double resultado;

	private int tiempoRestante;
	

	/*
	 * Caso de la Raiz cuadrada
	 */
	public Proceso(String programador, Operacion operacion, int id, int tiempo, double argA) {
		this(programador, operacion, id, tiempo, argA, argA);
	}

	public Proceso(String programador, Operacion operacion, int id, int tiempo, double argA, double argB) {
		this.programador = programador;
		this.operacion = operacion;
		this.id = id;
		this.tiempo = tiempo;
		this.argA = argA;
		this.argB = argB;
		
		tiempoRestante = tiempo;
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

	public String getProgramador() {
		return programador;
	}
	
	public double getResultado() {
		return resultado;
	}

	public int getTiempo() {
		return tiempo;
	}

	public int getTiempoRestante() {
		return tiempoRestante;
	}

	@Override
	public void run() {
		if(estado == Estado.LISTO){
			ejecutar();
			estado = Estado.EJECUTANDO;
		} else if( estado == Estado.EJECUTANDO ){
			tiempoRestante--;
			if( tiempoRestante == 0){
				estado = Estado.TERMINADO;
			}
		}
	}
}
