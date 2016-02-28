package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ControladorVistaTerminados{

	@FXML private TableView<Proceso> tabla;
	@FXML private TableColumn<Proceso, Integer> id;
	@FXML private TableColumn<Proceso, String> operacion;
	@FXML private TableColumn<Proceso, Estado> estado;
	@FXML private TableColumn<Proceso, Long> llegada;
	@FXML private TableColumn<Proceso, Long> finalizacion;
	@FXML private TableColumn<Proceso, Long> retorno;
	@FXML private TableColumn<Proceso, Long> respuesta;
	@FXML private TableColumn<Proceso, Long> espera;
	@FXML private TableColumn<Proceso, Long> servicio;
	@FXML private TableColumn<Proceso, Long> restante;
	@FXML private TableColumn<Proceso, Long> bloqueo;
	@FXML private Button okButton;
	private Stage parentStage;
	
	
	@FXML
	private void cerrar(){
		parentStage.close();
	}
	
	public void configurar(Stage temp, ObservableList<Proceso> lista){
        parentStage = temp;
        id.setCellValueFactory(new PropertyValueFactory<Proceso, Integer>("id"));
        operacion.setCellValueFactory(new PropertyValueFactory<Proceso, String>("resultadoCompleto"));
        estado.setCellValueFactory(new PropertyValueFactory<Proceso, Estado>("estado"));
        restante.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempoRestante"));
        bloqueo.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("tiempoRestanteBloqueo"));
        llegada.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("llegada"));
        finalizacion.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("finalizacion"));
        retorno.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("retorno"));
        respuesta.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("respuesta"));
        espera.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("espera"));
        servicio.setCellValueFactory(new PropertyValueFactory<Proceso, Long>("servicio"));
        tabla.setItems(lista);
    }
	
	
}
