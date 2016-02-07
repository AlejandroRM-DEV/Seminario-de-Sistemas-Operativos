package application;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import application.Proceso.Estado;
import application.Proceso.Operacion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

public class ControladorVistaPrincipal{
	@FXML private MenuItem ejecutar;
	@FXML private TextField txtOpA;
	@FXML private TextField txtOpB;
	@FXML private TextField txtReloj;
	@FXML private TextField txtOperacion;
	@FXML private TextField txtPendientes;
	@FXML private TextField txtNumeroPrograma;
	@FXML private TextField txtTiempoRestante;
	@FXML private TextField txtTiempoEstimado;
	@FXML private TextField txtTiempoTranscurrido;
	@FXML private TableView<Proceso> tablaLoteActual;
	@FXML private TableView<ItemTerminado> tablaTerminados;
	@FXML private TableColumn<Proceso, Integer> columnaID;
	@FXML private TableColumn<Proceso, Long> columnaTiempoEstimado;
	@FXML private TableColumn<Proceso, Long> columnaTiempoRestante;
	@FXML private TableColumn<ItemTerminado, String> columnaLote;
	@FXML private TableColumn<ItemTerminado, Integer> columnaNumero;
	@FXML private TableColumn<ItemTerminado, String> columnaOperacion;

	private int numeroLote;
	private boolean pausar;
	private boolean terminado;
	private Tiempo reloj;
	private Queue<Lote> lotes;
	private Lote loteEnEjecucion;
	private Proceso procesoActual;
	private Thread actualizarDatos;
	private ManejadorVentanas manejador;
	private ScheduledExecutorService executor;
	private ObservableList<Proceso> datosTablaLote;
	private ObservableList<ItemTerminado> datosTablaTerminados;

	public void configurar(ManejadorVentanas manejador, Queue<Lote> lotes){
		this.lotes = lotes;
		this.manejador = manejador;
		this.manejador.getScene().addEventFilter(KeyEvent.ANY, eventosTeclado());
		this.datosTablaLote = FXCollections.observableArrayList();
		this.datosTablaTerminados = FXCollections.observableArrayList();
		this.procesoActual = new Proceso(Operacion.SUMA,1,0,0);
		this.procesoActual.run();
		this.terminado = false;
		this.pausar = false;
		this.numeroLote = 0;
		this.reloj = new Tiempo();
		this.tablaLoteActual.setItems(datosTablaLote);
		this.tablaTerminados.setItems(datosTablaTerminados);
		configurarHilos();
		configurarTablaLote();
		configurarTablaTerminados();
	}
	
	private void configurarHilos(){
		actualizarDatos = new Thread( new Task<Void>() {
			@Override
			public Void call() throws Exception {
				while (true) {
					if(!pausar){
						String total = Long.toString(reloj.segundos());
						String transcurrido = Long.toString(procesoActual.getTiempoTranscurido());
						String restante = Long.toString(procesoActual.getTiempoRestante());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								txtReloj.setText(total);
								txtTiempoTranscurrido.setText(transcurrido);
								txtTiempoRestante.setText(restante);
							}
						});
						if(procesoActual.getEstado() == Estado.TERMINADO){
							siguienteProceso();
						}
					}
					Thread.sleep(1000);
				}
			}
		});
		actualizarDatos.setDaemon(true);
		executor = Executors.newScheduledThreadPool(1);
	}

	private void configurarTablaLote(){
		columnaID.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("id"));
		columnaTiempoEstimado.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempo"));
		columnaTiempoRestante.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempoRestante"));
	}

	private void configurarTablaTerminados(){
		columnaLote.setCellValueFactory(new PropertyValueFactory<ItemTerminado, String>("idLote"));
		columnaNumero.setCellValueFactory(new PropertyValueFactory<ItemTerminado, Integer>("idProceso"));
		columnaOperacion.setCellValueFactory(new PropertyValueFactory<ItemTerminado, String>("resultadoCompleto"));
	}
	
	@FXML
	private void ejecutar(){
		ejecutar.setDisable(true);
		actualizarDatos.start();
		reloj.inicio();
		iniciaSiguienteLote();
		iniciaProceso();
	}
	
	private EventHandler<KeyEvent> eventosTeclado(){
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>() {
			private boolean consumir = false;
			private long nanosegundos;
			@Override
			public void handle(KeyEvent event) {
				if (event.getEventType() == KeyEvent.KEY_PRESSED && !consumir) {
					consumir = true;
					if( !terminado && (procesoActual.getEstado() != Estado.TERMINADO) ){
						switch (event.getCode()) {
						case I:
							if(!pausar){
								procesoActual.setEstado(Estado.INTERRUMPIDO);
								loteEnEjecucion.agregar(procesoActual);
								datosTablaLote.setAll(loteEnEjecucion.dameProcesos());
								iniciaProceso();

							}
							break;
						case E:
							if(!pausar){
								procesoActual.setEstado(Estado.ERROR);
								siguienteProceso();								
							}
							break;
						case P:
							if(!pausar){
								procesoActual.setEstado(Estado.INTERRUMPIDO);
								nanosegundos = reloj.tiempo();
								pausar = true;

							}
							break;
						case C:
							if(pausar){
								reloj.inicio(nanosegundos);
								executor.submit(procesoActual);
								pausar = false;
							}
							break;
						default: break;
						}
					}
				} else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
					consumir = false;
				}
			}
		};
		return evento;
	}

	private void finalizar(){
		terminado = true;
		actualizarDatos.interrupt();
		rellenaCampos("","","","","","","");
	}

	private void iniciaProceso(){
		if(!loteEnEjecucion.estaVacio()){
			procesoActual = loteEnEjecucion.extrae();
		} else if(!lotes.isEmpty()){
			iniciaSiguienteLote();
			procesoActual = loteEnEjecucion.extrae();
		} else {
			finalizar();
			return;
		}

		rellenaCampos(Integer.toString(procesoActual.getId()), 
				String.valueOf(procesoActual.getOperacion()), 
				Double.toString(procesoActual.getArgA()), 
				Double.toString(procesoActual.getArgB()), 
				Long.toString(procesoActual.getTiempo()), 
				Long.toString(procesoActual.getTiempoRestante()), 
				Long.toString(procesoActual.getTiempoTranscurido()));

		datosTablaLote.remove(procesoActual);
		executor.submit(procesoActual);
	}

	private void iniciaSiguienteLote(){
		if(!lotes.isEmpty()){
			numeroLote++;
			loteEnEjecucion = lotes.poll();
			datosTablaLote.setAll(loteEnEjecucion.dameProcesos());
			txtPendientes.setText(Integer.toString(lotes.size()));
		}
	}

	@FXML
	private void nuevaCaptura(){
		manejador.nuevaCaptura();
	}

	private void rellenaCampos(String id, String operacion, String opA, String opB, 
			String tiempoEstimado, String tiempoRestante,String tiempoTranscurrido ){
		txtNumeroPrograma.setText(id);
		txtOperacion.setText(operacion);
		txtOpA.setText(opA);
		txtOpB.setText(opB);
		txtTiempoEstimado.setText(tiempoEstimado);
		txtTiempoTranscurrido.setText(tiempoTranscurrido);
		txtTiempoRestante.setText(tiempoRestante);
	}

	private void siguienteProceso(){
		datosTablaLote.remove(procesoActual);
		datosTablaTerminados.add(new ItemTerminado(numeroLote, procesoActual));
		iniciaProceso();
	}
}
