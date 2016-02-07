package application;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import application.Proceso.Operacion;

public class ControladorVistaCaptura {
	@FXML private TextField txtTotal;
	@FXML private Button btnIniciar;

	private Queue<Proceso> procesos;
	private ManejadorVentanas manejador;

	public void configurar(ManejadorVentanas manejador){
		this.manejador = manejador;
		procesos = new ArrayDeque<>();
	}

	private void finalizar(){
		Queue<Lote> lotes = new ArrayDeque<>();
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
	private void iniciar(){
		Random random = new Random();
		Operacion[] operaciones = Operacion.values();
		int total = Integer.parseInt(txtTotal.getText());

		Proceso.reiniciarIDS();
		for(int i = 0; i < total; i++){
			procesos.add(new Proceso(operaciones[random.nextInt(operaciones.length)], 
					random.nextInt(20)+1, 
					random.nextInt(200)+1, 
					random.nextInt(200)+1));
		}
		finalizar();
	}
}
