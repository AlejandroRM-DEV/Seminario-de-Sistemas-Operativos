package application;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import application.Proceso.Estado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControladorVistaPrincipal{
	@FXML private TextField txtReloj;
	@FXML private TextField txtNombre;
	@FXML private TextField txtOperacion;
	@FXML private TextField txtPendientes;
	@FXML private TextField txtNumeroPrograma;
	@FXML private TextField txtTiempoRestante;
	@FXML private TextField txtTiempoEstimado;
	@FXML private TextField txtTiempoTranscurrido;
	@FXML private MenuItem ejecutar;
	@FXML private TableView<Proceso> lote;
	@FXML private TableView<ItemTerminado> terminados;
	@FXML private TableColumn<Proceso, String> columnaNombre;
	@FXML private TableColumn<Proceso, Integer> columnaTiempo;
	@FXML private TableColumn<ItemTerminado, String> columnaLote;
	@FXML private TableColumn<ItemTerminado, Integer> columnaNumero;
	@FXML private TableColumn<ItemTerminado, String> columnaOperacion;

	private ObservableList<Proceso> datosTablaLote;
	private ObservableList<ItemTerminado> datosTablaTerminados;
	private Proceso procesoActual;
	private Runnable hiloActualizarDatos;
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> controlHiloActualizarDatos;
	private ScheduledFuture<?> controlHiloProceso;
	private Queue<Lote> lotes;
	private Lote loteActual;
	private ManejadorVentanas manejador;
	
	private int tiempoReloj;
	private int tiempoTranscurrido;
	private int numeroLote;

	private void actualizaTerminados(){
		datosTablaTerminados.add(new ItemTerminado(numeroLote, procesoActual));
	}

	public void configurar(ManejadorVentanas manejador, Queue<Lote> lotes){
		this.manejador = manejador;
		this.lotes = lotes;
		tiempoReloj = 0;	
		datosTablaLote = FXCollections.observableArrayList();
		datosTablaTerminados = FXCollections.observableArrayList();
		lote.setItems(datosTablaLote);
		terminados.setItems(datosTablaTerminados);
		configurarHilos();
		configurarTablaLote();
		configurarTablaTerminados();
	}

	private void configurarHilos(){
		hiloActualizarDatos = new Runnable() {
			@Override
			public void run() {
				txtReloj.setText("" + (++tiempoReloj));
				
				if(procesoActual.getEstado() == Estado.TERMINADO){
					controlHiloProceso.cancel(true);
					datosTablaLote.remove(procesoActual);
					actualizaTerminados();
					iniciaSiguienteProceso();
				}
				else if(!controlHiloActualizarDatos.isCancelled()){
					txtTiempoTranscurrido.setText("" + (++tiempoTranscurrido));
					txtTiempoRestante.setText("" + (procesoActual.getTiempoRestante()));
				}
			}
		};
		executor = Executors.newScheduledThreadPool(2);
	}

	private void configurarTablaLote(){
		columnaNombre.setCellValueFactory(new PropertyValueFactory<Proceso, String>("programador"));
		columnaTiempo.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("tiempo"));
	}

	private void configurarTablaTerminados(){
		columnaLote.setCellValueFactory(new PropertyValueFactory<ItemTerminado, String>("idLote"));
		columnaNumero.setCellValueFactory(new PropertyValueFactory<ItemTerminado, Integer>("idProceso"));
		columnaOperacion.setCellValueFactory(new PropertyValueFactory<ItemTerminado, String>("resultadoCompleto"));
	}

	@FXML
	private void ejecutar(){
		ejecutar.setDisable(true);
		numeroLote = 0;
		iniciaSiguienteLote();
		iniciaSiguienteProceso();
		tiempoReloj = 0;
		controlHiloActualizarDatos = executor.scheduleAtFixedRate(hiloActualizarDatos, 0, 1, TimeUnit.SECONDS);
	}

	private void finalizar(){
		controlHiloActualizarDatos.cancel(true);
		
		datosTablaLote.clear();
		txtNombre.setText("");
		txtOperacion.setText("");
		txtTiempoEstimado.setText("");
		txtNumeroPrograma.setText("");
		txtTiempoTranscurrido.setText("");
		txtTiempoRestante.setText("");
	}

	private void iniciaSiguienteLote(){
		if(!lotes.isEmpty()){
			numeroLote++;
			loteActual = lotes.poll();
			datosTablaLote.setAll(loteActual.dameProcesos());
			txtPendientes.setText("" + lotes.size());
		}
	}

	private void iniciaSiguienteProceso(){
		if(!loteActual.estaVacio()){
			procesoActual = loteActual.extrae();
		} else if(!lotes.isEmpty()){
			iniciaSiguienteLote();
			procesoActual = loteActual.extrae();
		} else {
			finalizar();
			return;
		}

		datosTablaLote.remove(procesoActual);
		
		tiempoTranscurrido = 0;
		procesoActual.run();
		controlHiloProceso = executor.scheduleAtFixedRate(procesoActual, 0, 1, TimeUnit.SECONDS);
		
		txtNombre.setText(procesoActual.getProgramador());
		txtNumeroPrograma.setText("" + procesoActual.getId());
		txtOperacion.setText("" + procesoActual.getOperacion());
		txtTiempoEstimado.setText("" + procesoActual.getTiempo());
		txtTiempoRestante.setText("" + procesoActual.getTiempo());
		txtTiempoTranscurrido.setText("0");
	}

	@FXML
	private void nuevaCaptura(){
		manejador.nuevaCaptura();
	}
}
