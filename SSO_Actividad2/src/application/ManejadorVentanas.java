package application;

import java.io.IOException;
import java.util.Queue;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ManejadorVentanas  {	
	private Scene scene;
	private FXMLLoader loader;

	private ControladorVistaPrincipal ventanaPrincipal;
	private ControladorVistaCaptura ventanaCaptura;
	public ManejadorVentanas(Scene scene){
		this.scene = scene;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void mostrarPantallaCaptura(){
		loader = new FXMLLoader(getClass().getResource("VistaCaptura.fxml"));
		try {
			scene.setRoot((Parent) loader.load());
			ventanaCaptura = loader.getController();
			ventanaCaptura.configurar(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mostrarPantallaEjecucion(Queue<Lote> lotes){
		loader = new FXMLLoader(getClass().getResource("VistaPrincipal.fxml"));
		try {
			scene.setRoot((Parent) loader.load());
			ventanaPrincipal = loader.getController();
			ventanaPrincipal.configurar(this,lotes);
			scene.getWindow().sizeToScene();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void nuevaCaptura(){
		mostrarPantallaCaptura();
		scene.getWindow().sizeToScene();
	}
}
