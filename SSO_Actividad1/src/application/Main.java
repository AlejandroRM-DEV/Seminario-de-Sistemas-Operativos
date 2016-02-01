package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * Actividad 1. Simular el Proceso por Lotes
 * 
 * @author Alejandro Ramírez Muñoz
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Scene scene = new Scene(new AnchorPane());
			ManejadorVentanas manejador = new ManejadorVentanas(scene);
			manejador.mostrarPantallaCaptura();
			primaryStage.setTitle("Seminario de Solución de Problemas de Sistemas Operativos");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					Platform.exit();
					System.exit(0);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
