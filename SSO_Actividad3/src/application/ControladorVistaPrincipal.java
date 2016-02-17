package application;

import java.io.IOException;
import java.util.ArrayDeque;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorVistaPrincipal{
	private static final int INFINITO  = Integer.MAX_VALUE;

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
	@FXML private TableView<Proceso> tablaListos;
	@FXML private TableView<Proceso> tablaBloqueados;
	@FXML private TableView<Proceso> tablaTerminados;
	@FXML private TableColumn<Proceso, Integer> columnaIDListo;
	@FXML private TableColumn<Proceso, Integer> columnaIDTerminado;
	@FXML private TableColumn<Proceso, Integer> columnaIDBloqueado;
	@FXML private TableColumn<Proceso, Long> columnaTiempoTranscurrido;
	@FXML private TableColumn<Proceso, Long> columnaTiempoEstimado;
	@FXML private TableColumn<Proceso, Long> columnaTiempoRestante;
	@FXML private TableColumn<Proceso, String> columnaOperacion;
	private boolean pausar;
	private boolean terminado;
	private Tiempo reloj;
	private Queue<Proceso> procesosListos;
	private Queue<Proceso> procesosBloqueados;
	private Queue<Proceso> procesosNuevos;
	private Proceso procesoActual;
	private Thread actualizarDatos;
	private ManejadorVentanas manejador;
	private ScheduledExecutorService executor;
	private ObservableList<Proceso> datosTablaListos;
	private ObservableList<Proceso> datosTablaBloqueados;
	private ObservableList<Proceso> datosTablaTerminados;
	private boolean cambioContexto;
	private boolean error;

	public void configurar(ManejadorVentanas manejador, Queue<Proceso> procesos){
		this.procesosNuevos = procesos;
		this.procesosBloqueados = new ArrayDeque<>();
		this.procesosListos = new ArrayDeque<>();
		this.manejador = manejador;
		this.manejador.getScene().addEventFilter(KeyEvent.ANY, eventosTeclado());
		this.datosTablaListos = FXCollections.observableArrayList();
		this.datosTablaBloqueados = FXCollections.observableArrayList();
		this.datosTablaTerminados = FXCollections.observableArrayList();
		this.procesoActual = new Proceso(Operacion.SUMA,1,0,0);
		this.procesoActual.run();
		this.cambioContexto = false;
		this.terminado = false;
		this.pausar = false;
		this.error = false;
		this.reloj = new Tiempo();
		this.tablaListos.setItems(datosTablaListos);
		this.tablaBloqueados.setItems(datosTablaBloqueados);
		this.tablaTerminados.setItems(datosTablaTerminados);
		configurarHilos();
		configurarTablaListos();
		configurarTablaBloqueados();
		configurarTablaTerminados();
	}

	private void configurarHilos(){
		actualizarDatos = new Thread( new Task<Void>() {
			@Override
			public Void call() {
				while (!terminado) {
					if(!pausar){
						pausar = true;
						
						if(cambioContexto){
							procesoActual.setEstado(Estado.BLOQUEADO);
							procesosBloqueados.add(procesoActual);
							iniciaProceso();
							cambioContexto = false;
						} else if(error){
							procesoActual.setEstado(Estado.ERROR);
							siguienteProceso();	
							error = false;
						}else if(!procesosListos.isEmpty() && (procesoActual.getOperacion() == Operacion.INDEFINIDA) ){
							procesoActual.setEstado(Estado.BLOQUEADO); //Para el Proceso nulo
							iniciaProceso();
						}else if(procesoActual.getEstado() == Estado.TERMINADO){
							siguienteProceso();
						}

						String total = Long.toString(reloj.segundos());
						String transcurrido = Long.toString(procesoActual.getTiempoTranscurido());
						String restante = Long.toString(procesoActual.getTiempoRestante());
						Proceso[] bloqueados = procesosBloqueados.toArray(new Proceso[procesosBloqueados.size()]);
						Proceso[] listos = procesosListos.toArray(new Proceso[procesosListos.size()]);

						if(!terminado){
							Platform.runLater(()->{
								txtReloj.setText(total);
								txtTiempoTranscurrido.setText(transcurrido);
								txtTiempoRestante.setText(restante);
							});
						}
						if(!terminado){
							Platform.runLater(()->{
								datosTablaBloqueados.setAll(bloqueados);
								datosTablaListos.setAll(listos);
							});
						}
						
						for(Proceso p: bloqueados){
							if(p.getTiempoBloqueado() >= 8 || (p.getEstado() != Estado.BLOQUEADO) ){
								procesosListos.add(p);
								procesosBloqueados.remove(p);
							}
						}

						pausar = false;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
				return null;
			}
		});
		actualizarDatos.setDaemon(true);
		executor = Executors.newScheduledThreadPool(1);
	}

	private void configurarTablaBloqueados(){
		columnaIDBloqueado.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("id"));
		columnaTiempoTranscurrido.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempoBloqueado"));
	}

	private void configurarTablaListos(){
		columnaIDListo.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("id"));
		columnaTiempoEstimado.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempo"));
		columnaTiempoRestante.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempoRestante"));
	}

	private void configurarTablaTerminados(){
		columnaIDTerminado.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("id"));
		columnaOperacion.setCellValueFactory(new PropertyValueFactory<Proceso, String>("resultadoCompleto"));
	}

	@FXML
	private void ejecutar(){
		ejecutar.setDisable(true);
		actualizarDatos.start();
		reloj.inicio();
		
		Proceso p;
		
		for(int i = 0; i < 4 && !procesosNuevos.isEmpty(); i++){
			p = procesosNuevos.poll();
			procesosListos.add(p);
			p.setTiempoLlegada(reloj.segundos());
		}
		datosTablaListos.setAll(procesosListos.toArray(new Proceso[procesosListos.size()]));
		txtPendientes.setText(Integer.toString(procesosNuevos.size()));

		iniciaProceso();
	}

	private EventHandler<KeyEvent> eventosTeclado(){
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>() {
			private boolean consumir = false;
			private long nanosegundos;
			@Override
			public void handle(KeyEvent event) {
				if ( (event.getEventType() == KeyEvent.KEY_PRESSED) && !consumir) {
					consumir = true;

					if(  (!terminado) && (procesoActual.getEstado() != Estado.TERMINADO) ){
						switch (event.getCode()) {
						case I:
							if(!pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA) ){
								cambioContexto = true;
							}
							break;
						case E:
							if(!pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA)){
								error = true;							
							}
							break;
						case P:
							if(!pausar){
								procesoActual.setEstado(Estado.BLOQUEADO);
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
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					} 
				} else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
					consumir = false;
				} 
				event.consume();

			}
		};

		return evento;
	}

	private void finalizar(){
		actualizarDatos.interrupt();
		terminado = true;
		rellenaCampos("","","","","","","");
	}

	private void iniciaProceso(){
		if(!procesosListos.isEmpty()){
			procesoActual = procesosListos.poll();
		} else if(!procesosBloqueados.isEmpty()){
			procesoActual = new Proceso(INFINITO, Operacion.INDEFINIDA, INFINITO, 0, 0);
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

		datosTablaListos.remove(procesoActual);
		executor.submit(procesoActual);
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

	@FXML
	private void estadisticas() throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaTerminados.fxml"));

	    Scene escenaTemporal = new Scene(new AnchorPane());
	    escenaTemporal.setRoot((Parent) loader.load());
	    
		Stage ventanaDialogo = new Stage();
	    ventanaDialogo.initModality(Modality.WINDOW_MODAL);
	    ventanaDialogo.initOwner(manejador.getScene().getWindow());
	    ventanaDialogo.setScene(escenaTemporal);

	    ControladorVistaTerminados cvt = loader.getController();
	    cvt.configurar(ventanaDialogo, datosTablaTerminados);
	    ventanaDialogo.showAndWait();
	}
	
	private void siguienteProceso(){
		procesoActual.setTiempoFinalizacion(reloj.segundos());
		datosTablaListos.remove(procesoActual);
		datosTablaTerminados.add(procesoActual);

		if(!procesosNuevos.isEmpty()){
			Proceso p = procesosNuevos.poll();
			procesosListos.add(p);
			datosTablaListos.add(p);
			p.setTiempoLlegada(reloj.segundos());
			txtPendientes.setText(Integer.toString(procesosNuevos.size()));
		}

		iniciaProceso();
	}
}
