package application;

import application.Proceso.Operacion;

public class ItemTerminado {
	private int idLote;
	private Proceso proceso;
	
	public ItemTerminado(int idLote, Proceso proceso) {
		this.idLote = idLote;
		this.proceso = proceso;
	}
	
	public int getIdLote(){
		return idLote;
	}
	
	public int getIdProceso(){
		return proceso.getId();
	}
	
	public String getResultadoCompleto(){
		double argA = proceso.getArgA();
		double argB = proceso.getArgB();
		double resultado = proceso.getResultado();
		Operacion op = proceso.getOperacion();
		String formato = "";
		
		switch (op) {
		case SUMA:
		case RESTA:
		case MULTIPLICACION:
		case DIVISION:
		case MODULO:
			formato = argA + " " + op.simbolo() + " " + argB + " = " + resultado;
			break;
		case RAIZ_CUADRADA:
			formato = op.simbolo() + "" + argB + " = " + resultado;
			break;
		default: break;
		}
		
		return formato;
	}
}