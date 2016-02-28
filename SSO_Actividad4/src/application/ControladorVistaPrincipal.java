package application;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

	protected long reloj = 0;
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
	private ObservableList<Proceso> datosBCP;
	private boolean cambioContexto;
	private boolean error;
	private boolean nuevoProceso;

	public void configurar(ManejadorVentanas manejador, Queue<Proceso> procesos){
		this.procesosNuevos = procesos;
		this.procesosBloqueados = new ArrayDeque<>();
		this.procesosListos = new ArrayDeque<>();
		this.manejador = manejador;
		this.manejador.getScene().addEventFilter(KeyEvent.ANY, eventosTeclado());
		this.datosTablaListos = FXCollections.observableArrayList();
		this.datosTablaBloqueados = FXCollections.observableArrayList();
		this.datosTablaTerminados = FXCollections.observableArrayList();
		this.datosBCP = FXCollections.observableArrayList();
		this.datosBCP.setAll(procesos);
		this.procesoActual = new Proceso(INFINITO,Operacion.SUMA,0,0,0);
		this.procesoActual.setEstado(Estado.EJECUTANDO);
		this.procesoActual.run();
		this.cambioContexto = false;
		this.terminado = false;
		this.pausar = false;
		this.error = false;
		this.nuevoProceso = false;
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
			
			private void actualizaGUI(){
				String total = Long.toString(reloj);
				String transcurrido = Long.toString(procesoActual.getTiempoTranscurido());
				String restante = Long.toString(procesoActual.getTiempoRestante());

				if(!terminado){
					Platform.runLater(()->{
						txtReloj.setText(total);
						txtTiempoTranscurrido.setText(transcurrido);
						txtTiempoRestante.setText(restante);
						datosTablaBloqueados.setAll(procesosBloqueados);
						datosTablaListos.setAll(procesosListos);
					});
				}
			}
			
			@Override
			public Void call() {
				while (!terminado) {
					if(!pausar){
						pausar = true;
						
						if(nuevoProceso){
							creaNuevoProceso();
						}else if(cambioContexto){
							procesoActual.setEstado(Estado.BLOQUEADO);
							procesoActual.aumentaTiempoEjecucion();
							procesosBloqueados.add(procesoActual);
							iniciaSiguienteProceso();
							cambioContexto = false;
						} else if(error){
							procesoActual.setEstado(Estado.ERROR);
							preparaSiguienteProceso();	
							error = false;
						} 
						if(!procesosListos.isEmpty() && (procesoActual.getOperacion() == Operacion.INDEFINIDA) ){
							iniciaSiguienteProceso();
						}
						
						actualizaGUI();
						
						if(procesoActual.getTiempoRestante() <= 0){
							procesoActual.setEstado(Estado.TERMINADO);
							preparaSiguienteProceso();
						}
						if(!terminado) {
							procesoActual.aumentaTiempoEjecucion();
						}
						for(Proceso p:procesosListos){
							p.aumentaTiempoEpera();
						}
						for(Proceso p: procesosBloqueados){
							if(p.getTiempoBloqueado() >= 8 || (p.getEstado() != Estado.BLOQUEADO) ){
								procesosListos.add(p);
								procesosBloqueados.remove(p);
								p.setEstado(Estado.LISTO);
								p.reiniciaTiempoBloqueo();
							} else{
								p.aumentaTiempoBloqueo();
								p.aumentaTiempoEpera();
							}
						}
						
						reloj++;
						pausar = false;
					}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
				}
				return null;
			}

			private void creaNuevoProceso(){
				Random random = new Random();
				Operacion[] operaciones = Operacion.values();
				Proceso nuevo;
				Operacion op;
				int enMemoria = procesosListos.size() + procesosBloqueados.size() + 1; 
				
				do{
					op = operaciones[random.nextInt(operaciones.length)];
				}while(op==Operacion.INDEFINIDA);
				nuevo = new Proceso(op, random.nextInt(15)+4,random.nextInt(200)+1, random.nextInt(200)+1);
				datosBCP.add(nuevo);

				if( enMemoria < 4){
					procesosListos.add(nuevo);
					datosTablaListos.add(nuevo);
					nuevo.setTiempoLlegada(reloj);
					nuevo.setEstado(Estado.LISTO);
				} else {
					procesosNuevos.add(nuevo);
				}
				
				Platform.runLater(()->txtPendientes.setText(Integer.toString(procesosNuevos.size())));
				nuevoProceso = false;
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
		for(int i = 0; i < 4 && !procesosNuevos.isEmpty(); i++){
			Proceso p = procesosNuevos.poll();
			procesosListos.add(p);
			p.setTiempoLlegada(reloj);
			p.setEstado(Estado.LISTO);
		}
		datosTablaListos.setAll(procesosListos.toArray(new Proceso[procesosListos.size()]));
		txtPendientes.setText(Integer.toString(procesosNuevos.size()));
		actualizarDatos.start();
		iniciaSiguienteProceso();
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
		ventanaDialogo.setResizable(false);

		ControladorVistaTerminados cvt = loader.getController();
		cvt.configurar(ventanaDialogo, datosBCP);
		ventanaDialogo.showAndWait();
	}

	private EventHandler<KeyEvent> eventosTeclado(){
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>() {
			private boolean consumir = false;
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
						case N:
							if(!pausar){ nuevoProceso = true; }
							break;
						case B:
							try {
								pausar();
								estadisticas();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							break;
						case P:
							if(!pausar){ pausar(); }
							break;
						case C:
							if(pausar){	reanudar(); }
							break;
						default: break;
						}
						try { Thread.sleep(1000); } catch (InterruptedException e) {}
					} 
				} else if (event.getEventType() == KeyEvent.KEY_RELEASED) {	consumir = false; } 
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

	private void iniciaSiguienteProceso(){
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
		procesoActual.setTiempoRespuesta(reloj);
		procesoActual.setEstado(Estado.EJECUTANDO);
		executor.submit(procesoActual);
	}

	private void pausar(){
		pausar = true;
	}

	private void preparaSiguienteProceso(){
		procesoActual.setTiempoFinalizacion(reloj);
		datosTablaListos.remove(procesoActual);
		datosTablaTerminados.add(procesoActual);

		if(!procesosNuevos.isEmpty()){
			Proceso p = procesosNuevos.poll();
			txtPendientes.setText(Integer.toString(procesosNuevos.size()));
			procesosListos.add(p);
			datosTablaListos.add(p);
			p.setTiempoLlegada(reloj);	
			p.setEstado(Estado.LISTO);
		}

		iniciaSiguienteProceso();
	}

	private void reanudar(){
		System.out.println("Continua");
		pausar = false;
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
}
