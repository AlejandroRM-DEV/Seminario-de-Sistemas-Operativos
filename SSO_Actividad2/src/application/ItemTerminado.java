package application;

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

	public String getResultadoCompleto() {
		return proceso.toStringResultado();
	}
}