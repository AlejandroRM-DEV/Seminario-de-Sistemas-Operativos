package application;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControladorVistaCaptura {
	@FXML private TextField txtTotal;
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
		int total = Integer.parseInt(txtTotal.getText());
		Operacion op;
		Proceso.reiniciarIDS();
		for(int i = 0; i < total; i++){
			do{
				op = operaciones[random.nextInt(operaciones.length)];
			}while(op==Operacion.INDEFINIDA);
			procesos.add(new Proceso(op, 
					10,//random.nextInt(15)+4, 
					random.nextInt(200)+1, 
					random.nextInt(200)+1));
		}
		manejador.mostrarPantallaEjecucion(procesos);
	}
}
