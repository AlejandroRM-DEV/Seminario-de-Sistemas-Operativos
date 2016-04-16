package application;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ControladorVistaCaptura {
	@FXML private TextField txtTotal;
	@FXML private TextField txtQuantum;
	@FXML private Button btnIniciar;

	private Queue<Proceso> procesos;
	private ManejadorVentanas manejador;

	public void configurar(ManejadorVentanas manejador){
		this.manejador = manejador;
		procesos = new ArrayDeque<>();
	}

	@FXML
	private void iniciar(){
		Random random = new Random();
		Operacion[] operaciones = Operacion.values();
		Operacion op;
		
		int total, quantum;
		
		try {
			total = Integer.parseInt(txtTotal.getText());
			quantum = Integer.parseInt(txtQuantum.getText());
			//total = random.nextInt(15)+5;
			//quantum = random.nextInt(6)+2;
			
			if(total <= 0 || quantum <= 0){
				throw new IllegalArgumentException("El Total de procesos y el Quantum deben ser mayores a 0");
			}
			
			for(int i = 0; i < total; i++){
				do{
					op = operaciones[random.nextInt(operaciones.length)];
				}while(op==Operacion.INDEFINIDA);
				procesos.add(new Proceso(op, 
						random.nextInt(15)+4, 
						random.nextInt(200)+1, 
						random.nextInt(200)+1, 
						random.nextInt(25)+1));
			}
			
			manejador.mostrarPantallaEjecucion(procesos, quantum);
		} catch (NumberFormatException e) {
			alerta("Formato de número inválido", "Se ingreso un caracter que no es un Digito");
		} catch (IllegalArgumentException e) {
			alerta("Formato de número inválido", e.getMessage());
		}
	}
	
	private void alerta(String error, String descripcion){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error de Formato");
		alert.setHeaderText(error);
		alert.setContentText(descripcion);
		alert.showAndWait();
	}
}
