package application;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	@FXML private TextField txtQuantum;
	@FXML private TextField txtQuantumContador;
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
	private Queue<Proceso> procesosListos;
	private Queue<Proceso> procesosBloqueados;
	private Queue<Proceso> procesosNuevos;
	private Proceso procesoActual;
	private Thread actualizadorGUI;
	private ManejadorVentanas manejador;
	private ObservableList<Proceso> observableListListos;
	private ObservableList<Proceso> observableListBloqueados;
	private ObservableList<Proceso> observableListTerminados;
	private ObservableList<Proceso> observableListBCP;
	private boolean pausar;
	private boolean finalizado;
	private boolean procesoBloqueo;
	private boolean procesoError;
	private boolean procesoNuevo;
	private long reloj = 0;
	private int quantum;
	private int quantumContador;

	private Logger logger;
	private ConsoleHandler handler;

	private void configurarLogger(){
		logger = Logger.getGlobal();
		logger.setLevel(Level.INFO);
		logger.setUseParentHandlers( false );

		handler = new ConsoleHandler();
		handler.setLevel(Level.INFO);
		logger.addHandler(handler);
	}

	public void configurar(ManejadorVentanas manejador, Queue<Proceso> procesos, int quantum){
		this.manejador = manejador;
		this.manejador.getScene().addEventFilter(KeyEvent.ANY, eventosTeclado());
		this.procesosNuevos = procesos;
		this.quantum = quantum;
		this.txtQuantum.setText(Integer.toString(quantum));
		this.quantumContador = 0;
		this.procesosBloqueados = new ArrayDeque<>();
		this.procesosListos = new ArrayDeque<>();
		this.observableListListos = FXCollections.observableArrayList();
		this.observableListBloqueados = FXCollections.observableArrayList();
		this.observableListTerminados = FXCollections.observableArrayList();
		this.observableListBCP = FXCollections.observableArrayList();
		this.observableListBCP.setAll(procesos);
		this.tablaListos.setItems(observableListListos);
		this.tablaBloqueados.setItems(observableListBloqueados);
		this.tablaTerminados.setItems(observableListTerminados);
		this.procesoBloqueo = false;
		this.finalizado = false;
		this.pausar = true;
		this.procesoError = false;
		this.procesoNuevo = false;
		hilos();
		configurarTablaListos();
		configurarTablaBloqueados();
		configurarTablaTerminados();
		configurarLogger();
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
		actualizadorGUI.start();
		pausar = false;
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
		cvt.configurar(ventanaDialogo, observableListBCP);
		ventanaDialogo.showAndWait();
	}

	private EventHandler<KeyEvent> eventosTeclado(){
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>() {
			private boolean consumir = false;
			@Override
			public void handle(KeyEvent event) {
				if ( (event.getEventType() == KeyEvent.KEY_PRESSED) && !consumir) {
					consumir = true;

					if(  (!finalizado) && (procesoActual.getEstado() != Estado.TERMINADO) ){
						switch (event.getCode()) {
						case I:
							if(!pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA) ){
								procesoBloqueo = true;
							}
							break;
						case E:
							if(!pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA)){
								procesoError = true;							
							}
							break;
						case N:
							if(!pausar){ 
								procesoNuevo = true; 
							}
							break;
						case B:
							try {
								pausar = true;
								estadisticas();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							break;
						case P:
							if(!pausar){ 
								pausar = true; 
							}
							break;
						case C:
							if(pausar){	
								pausar = false;
							}
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

	private void hilos(){
		actualizadorGUI = new Thread( new Task<Void>() {

			private void actualizaGUI(){
				String total, transcurrido, restante, quantumStr;

				if(!finalizado){
					total = Long.toString(reloj);
					transcurrido = Long.toString(procesoActual.getTiempoTranscurido());
					restante = Long.toString(procesoActual.getTiempoRestante());
					quantumStr = Integer.toString(quantumContador);
					Platform.runLater(()->{
						txtReloj.setText(total);
						txtQuantumContador.setText(quantumStr);
						txtTiempoTranscurrido.setText(transcurrido);
						txtTiempoRestante.setText(restante);
						observableListBloqueados.setAll(procesosBloqueados);
						observableListListos.setAll(procesosListos);
					});
				}
			}

			@Override
			public Void call() {
				Platform.runLater(()->{
					observableListListos.setAll(procesosListos.toArray(new Proceso[procesosListos.size()]));
					txtPendientes.setText(Integer.toString(procesosNuevos.size()));
				});
				iniciaSiguienteProceso();

				while (!finalizado) {
					if(!pausar){
						pausar = true;
						revisarEventos();
						actualizaGUI();
						revisarProcesoActual();
						for(Proceso p:procesosListos){
							p.aumentaTiempoEpera();
						}
						revisarProcesosBloqueados();
						reloj++;
						pausar = false;
					}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
				}
				rellenaCampos("","","","","","","");
				return null;
			}

			private void crearNuevoProceso(){
				Random random = new Random();
				Operacion[] operaciones = Operacion.values();
				Proceso nuevo;
				Operacion op;
				int enMemoria = procesosListos.size() + procesosBloqueados.size() + 1; 

				do{
					op = operaciones[random.nextInt(operaciones.length)];
				}while(op==Operacion.INDEFINIDA);
				nuevo = new Proceso(op, random.nextInt(15)+4,random.nextInt(200)+1, random.nextInt(200)+1);
				Platform.runLater(()-> observableListBCP.add(nuevo) );

				if( enMemoria < 4){
					Platform.runLater(()-> observableListListos.add(nuevo));
					procesosListos.add(nuevo);
					nuevo.setTiempoLlegada(reloj);
					nuevo.setEstado(Estado.LISTO);
				} else {
					procesosNuevos.add(nuevo);
				}

				Platform.runLater(()-> txtPendientes.setText(Integer.toString(procesosNuevos.size()) ));
				procesoNuevo = false;
			}

			private void iniciaSiguienteProceso(){
				if(!procesosListos.isEmpty()){
					procesoActual = procesosListos.poll();
				} else if(!procesosBloqueados.isEmpty()){
					procesoActual = new Proceso(INFINITO, Operacion.INDEFINIDA, INFINITO, 0, 0);
				} else {
					finalizado = true;		
					return;
				}

				Platform.runLater(()-> observableListListos.remove(procesoActual));
				rellenaCampos(Integer.toString(procesoActual.getId()), 
						String.valueOf(procesoActual.getOperacion()), 
						Double.toString(procesoActual.getArgA()), 
						Double.toString(procesoActual.getArgB()), 
						Long.toString(procesoActual.getTiempo()), 
						Long.toString(procesoActual.getTiempoRestante()), 
						Long.toString(procesoActual.getTiempoTranscurido()));

				procesoActual.setTiempoRespuesta(reloj);
				procesoActual.setEstado(Estado.EJECUTANDO);
				procesoActual.ejecutar();
				quantumContador = 0;
			}

			private void rellenaCampos(String id, String operacion, String opA, String opB, 
					String tiempoEstimado, String tiempoRestante,String tiempoTranscurrido ){
				Platform.runLater(()->{
					txtNumeroPrograma.setText(id);
					txtOperacion.setText(operacion);
					txtOpA.setText(opA);
					txtOpB.setText(opB);
					txtTiempoEstimado.setText(tiempoEstimado);
					txtTiempoTranscurrido.setText(tiempoTranscurrido);
					txtTiempoRestante.setText(tiempoRestante);
				});
			}

			private void revisarEventos(){
				if(procesoNuevo){
					crearNuevoProceso();
				}else if(procesoBloqueo){
					if(procesoActual.getTiempoRestante() > 0){
						procesoActual.setEstado(Estado.BLOQUEADO);
						procesosBloqueados.add(procesoActual);
						iniciaSiguienteProceso();
					} else {
						logger.info("El proceso que se intentó bloquear [PID: " 
								+ procesoActual.getId()+"] recien habia terminado");
					}
					procesoBloqueo = false;
				} else if(procesoError){
					procesoActual.setEstado(Estado.ERROR);
					terminaIniciaSiguienteProceso();	
					procesoError = false;
				} 

				if(!procesosListos.isEmpty() && (procesoActual.getOperacion() == Operacion.INDEFINIDA) ){
					iniciaSiguienteProceso();
				}
			}

			private void revisarProcesoActual(){
				if(procesoActual.getTiempoRestante() <= 0){
					procesoActual.setEstado(Estado.TERMINADO);
					terminaIniciaSiguienteProceso();
				} else if(quantumContador == quantum){
					Platform.runLater(()-> observableListListos.add(procesoActual));
					procesosListos.add(procesoActual);
					procesoActual.setEstado(Estado.LISTO);
					iniciaSiguienteProceso();

				}

				if(!finalizado) {
					procesoActual.aumentaTiempoEjecucion();
					quantumContador++;
				}
			}

			private void revisarProcesosBloqueados(){
				for(Proceso p: procesosBloqueados){
					if(p.getTiempoBloqueado() >= 7 || (p.getEstado() != Estado.BLOQUEADO) ){
						procesosListos.add(p);
						procesosBloqueados.remove(p);
						p.setEstado(Estado.LISTO);
						p.reiniciaTiempoBloqueo();
						p.aumentaTiempoEpera();
						if(procesoActual.getOperacion() == Operacion.INDEFINIDA ){
							iniciaSiguienteProceso();
						}
					} else{
						p.aumentaTiempoBloqueo();
						p.aumentaTiempoEpera();
					}
				}
			}

			private void terminaIniciaSiguienteProceso(){
				procesoActual.setTiempoFinalizacion(reloj);
				Proceso pA = procesoActual;
				Platform.runLater(()-> {
					observableListListos.remove(pA);
					observableListTerminados.add(pA);
				});

				if(!procesosNuevos.isEmpty()){
					Proceso p = procesosNuevos.poll();
					Platform.runLater(()-> {
						txtPendientes.setText(Integer.toString(procesosNuevos.size()));
						observableListListos.add(p);
					});
					procesosListos.add(p);
					p.setTiempoLlegada(reloj);	
					p.setEstado(Estado.LISTO);
				}

				iniciaSiguienteProceso();
			}

		});

		actualizadorGUI.setDaemon(true);
	}
}
