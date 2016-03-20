package application;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ControladorVistaCaptura{
	private static final int RETARDO_MILISEGUNDOS = 1000;
	private static final int DORMIR_SEGUNDOS = 10;
	private static final int MAX_ELEMENTOS_TRATABLES = 12;
	private static final int TAMANO_BUFFER = 40;

	@FXML private TextField txtProductor;
	@FXML private TextField txtProductorRestante;
	@FXML private TextField txtConsumidor;
	@FXML private TextField txtConsumidorRestante;
	@FXML private TextField txt1;
	@FXML private TextField txt2;
	@FXML private TextField txt3;
	@FXML private TextField txt4;
	@FXML private TextField txt5;
	@FXML private TextField txt6;
	@FXML private TextField txt7;
	@FXML private TextField txt8;
	@FXML private TextField txt9;
	@FXML private TextField txt10;
	@FXML private TextField txt11;
	@FXML private TextField txt12;
	@FXML private TextField txt13;
	@FXML private TextField txt14;
	@FXML private TextField txt15;
	@FXML private TextField txt16;
	@FXML private TextField txt17;
	@FXML private TextField txt18;
	@FXML private TextField txt19;
	@FXML private TextField txt20;
	@FXML private TextField txt21;
	@FXML private TextField txt22;
	@FXML private TextField txt23;
	@FXML private TextField txt24;
	@FXML private TextField txt25;
	@FXML private TextField txt26;
	@FXML private TextField txt27;
	@FXML private TextField txt28;
	@FXML private TextField txt29;
	@FXML private TextField txt30;
	@FXML private TextField txt31;
	@FXML private TextField txt32;
	@FXML private TextField txt33;
	@FXML private TextField txt34;
	@FXML private TextField txt35;
	@FXML private TextField txt36;
	@FXML private TextField txt37;
	@FXML private TextField txt38;
	@FXML private TextField txt39;
	@FXML private TextField txt40;
	@FXML private Button btnIniciar;

	private Random ran = new Random();
	private ReentrantLock lock = new ReentrantLock();
	private TextField[] buffer;
	private int indiceProductor;
	private int indiceConsumidor;
	private int length;
	private Thread consumidor;
	private Thread productor;
	private ManejadorVentanas manejador;

	private void actualizarGUI(TextField txt, String msg){
		Platform.runLater(() -> txt.setText(msg));
	}

	private int aumentar(int i){
		return (i + 1) % TAMANO_BUFFER;
	}

	public void configurar(ManejadorVentanas manejador){
		this.manejador = manejador;
		indiceProductor = 0;
		indiceConsumidor = 0;
		length = 0;
		buffer = new TextField[] { txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8, txt9, txt10, txt11, txt12, txt13,
				txt14, txt15, txt16, txt17, txt18, txt19, txt20, txt21, txt22, txt23, txt24, txt25, txt26, txt27, txt28,
				txt29, txt30, txt31, txt32, txt33, txt34, txt35, txt36, txt37, txt38, txt39, txt40 };

		productor = new Thread(new Runnable(){
			private int producir;
			private int dormir;

			private String producir(){
				int unicode;
				do{
					unicode= '\u00FF' + (int)(('\u0021' - '\u00FF' + 1) * Math.random());
				}while(!Character.isLetterOrDigit(unicode));
				return Character.toString((char)unicode);
			}

			@Override
			public void run(){
				try {
					while ( true ) {
						actualizarGUI(txtProductor, "Intentando entrar");
						Thread.sleep(RETARDO_MILISEGUNDOS);

						if ( length < 40 ) {
							lock.lock();
							actualizarGUI(txtProductor, "Trabajando");
							producir = ran.nextInt(MAX_ELEMENTOS_TRATABLES) + 1;

							for ( int i = 0; i < producir && length < 40; i++ ) {
								actualizarGUI(buffer[indiceProductor], producir());
								indiceProductor = aumentar(indiceProductor);
								length++;
								Thread.sleep(RETARDO_MILISEGUNDOS);
							}
							lock.unlock();
						}

						actualizarGUI(txtProductor, "Durmiendo");
						dormir = ran.nextInt(DORMIR_SEGUNDOS) + 1;
						while ( dormir > 0 ) {
							actualizarGUI(txtProductorRestante, "" + dormir);
							Thread.sleep(1000);
							dormir--;
						}
						actualizarGUI(txtProductorRestante, "");
					}
				} catch ( InterruptedException ex ) {

				}
			}

		});

		consumidor = new Thread(new Runnable(){
			private int consumir;
			private int dormir;

			@Override
			public void run(){
				try {
					while ( true ) {
						actualizarGUI(txtConsumidor, "Intentando entrar");
						Thread.sleep(RETARDO_MILISEGUNDOS);

						if ( length > 0 ) {
							lock.lock();
							actualizarGUI(txtConsumidor, "Trabajando");
							consumir = ran.nextInt(MAX_ELEMENTOS_TRATABLES) + 1;

							for ( int i = 0; i < consumir && length > 0; i++ ) {
								actualizarGUI(buffer[indiceConsumidor], "");
								indiceConsumidor = aumentar(indiceConsumidor);
								length--;
								Thread.sleep(RETARDO_MILISEGUNDOS);
							}
							lock.unlock();
						}

						actualizarGUI(txtConsumidor, "Durmiendo");
						dormir = ran.nextInt(DORMIR_SEGUNDOS) + 1;
						while ( dormir > 0 ) {
							actualizarGUI(txtConsumidorRestante, "" + dormir);
							Thread.sleep(1000);
							dormir--;
						}
						actualizarGUI(txtConsumidorRestante, "");
					}
				} catch ( InterruptedException ex ) {

				}
			}
		});

		this.manejador.getScene().addEventFilter(KeyEvent.ANY, eventosTeclado());

	}

	private EventHandler<KeyEvent> eventosTeclado(){
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event){
				if ( event.getEventType() == KeyEvent.KEY_PRESSED ) {
					switch ( event.getCode() ) {
					case ESCAPE:
						productor.interrupt();
						consumidor.interrupt();
						break;
					default:
						break;
					}
				}
				event.consume();
			}
		};
		return evento;
	}

	@FXML
	private void iniciar(){
		btnIniciar.setDisable(true);
		productor.start();
		consumidor.start();
	}

}