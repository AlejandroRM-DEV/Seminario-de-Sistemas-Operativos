package application;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import application.Proceso.Operacion;

public class ControladorVistaCaptura {
	@FXML private TextField txtTotal;
	@FXML private TextField txtNombre;
	@FXML private TextField txtOp1;
	@FXML private TextField txtOp2;
	@FXML private TextField txtTiempo;
	@FXML private TextField txtID;
	@FXML private ComboBox<Operacion> cmbOperacion;
	@FXML private Button btnIniciar;
	@FXML private Button btnGuardar;

	private Queue<Lote> lotes;
	private Queue<Proceso> procesos;
	private HashSet<Integer> ids;
	private ManejadorVentanas manejador;

	private int totalProcesos;

	public void configurar(ManejadorVentanas manejador){
		this.manejador = manejador;
		lotes = new ArrayDeque<>();
		procesos = new ArrayDeque<>();
		ids = new HashSet<>();
		cmbOperacion.getItems().addAll(Operacion.values());
		cmbOperacion.setValue(Operacion.SUMA);
	}

	@FXML
	private void finalizar(){
		while(!procesos.isEmpty()){
			Lote lote = new Lote();
			for(int i = 0; i < 4 && !procesos.isEmpty(); i++){
				lote.agregar(procesos.poll());
			}
			lotes.add(lote);
		}
		manejador.mostrarPantallaEjecucion(lotes);
	}

	@FXML
	private void guardar(){
		if(validar()){
			if(cmbOperacion.getValue().equals(Operacion.RAIZ_CUADRADA)){
				procesos.add(new Proceso(
						txtNombre.getText(), 
						cmbOperacion.getValue(), 
						Integer.valueOf(txtID.getText()), 
						Integer.valueOf(txtTiempo.getText()), 
						Double.valueOf(txtOp1.getText())
						));
			} else {
				procesos.add(new Proceso(
						txtNombre.getText(), 
						cmbOperacion.getValue(), 
						Integer.valueOf(txtID.getText()), 
						Integer.valueOf(txtTiempo.getText()), 
						Double.valueOf(txtOp1.getText()),
						Double.valueOf(txtOp2.getText())
						));
			}
			ids.add(Integer.valueOf(txtID.getText()));
			limpiarCampos();
			if(procesos.size() == totalProcesos){
				finalizar();
			}
		}
	}

	@FXML
	private void iniciar(){
		try {
			totalProcesos = Integer.valueOf(txtTotal.getText());
			if(totalProcesos > 0){
				txtTotal.setDisable(true);
				btnIniciar.setDisable(true);
				btnGuardar.setDisable(false);
				txtNombre.setDisable(false);
				txtOp1.setDisable(false);
				txtOp2.setDisable(false);
				txtTiempo.setDisable(false);
				txtID.setDisable(false);
				cmbOperacion.setDisable(false);
			}
		} catch (NumberFormatException e) {
			alerta("Formato de número inválido", "Sólo se permiten números positivos, intente de nuevo");
		}
	}

	private void limpiarCampos(){
		txtNombre.setText("");
		txtOp1.setText("");
		txtOp2.setText("");
		txtTiempo.setText("");
		txtID.setText("");
	}

	private void alerta(String error, String descripcion){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error de Formato");
		alert.setHeaderText(error);
		alert.setContentText(descripcion);
		alert.showAndWait();
	}

	private boolean validar(){
		int id = 0, tiempo = 0;
		double op1 = 0, op2 = 0;
		Operacion operacion = cmbOperacion.getValue();

		try {
			id = Integer.valueOf(txtID.getText());
			tiempo = Integer.valueOf(txtTiempo.getText());
			op1 = Double.valueOf(txtOp1.getText());
			op2 = Double.valueOf(txtOp2.getText());
		} catch (NumberFormatException e) {
			alerta("Formato de número inválido", e.getMessage());
			return false;
		}

		if( txtNombre.getText().isEmpty() ){
			alerta("Formato de nombre inválido", "El nombre del programador no puede ser vacio");
			return false;
		} else if( ids.contains(id) ){
			alerta("Duplicación de datos", "El ID de proceso ya existe");
			return false;
		} else if( tiempo <= 0 ){
			alerta("Formato de número inválido", "El tiempo tiene que ser positivo");
			return false;
		} else if( (operacion == Operacion.DIVISION || operacion == Operacion.MODULO) && op2 == 0 ){
			alerta("Formato de número inválido", "División y Módulo con 0 es indefinido");
			return false;
		} else if( operacion == Operacion.RAIZ_CUADRADA && op1 == 0 ){
			alerta("Formato de número inválido", "Raíz cuadrada de 0 es indefinida");
			return false;
		}
		return true;
	}
}
