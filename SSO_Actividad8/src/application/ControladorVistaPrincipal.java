package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Marco.Color;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorVistaPrincipal{
	private static final int INFINITO = Integer.MAX_VALUE;

	@FXML private MenuItem ejecutar;
	@FXML private ProgressBar pbMarco0;
	@FXML private ProgressBar pbMarco1;
	@FXML private ProgressBar pbMarco2;
	@FXML private ProgressBar pbMarco3;
	@FXML private ProgressBar pbMarco4;
	@FXML private ProgressBar pbMarco5;
	@FXML private ProgressBar pbMarco6;
	@FXML private ProgressBar pbMarco7;
	@FXML private ProgressBar pbMarco8;
	@FXML private ProgressBar pbMarco9;
	@FXML private ProgressBar pbMarco10;
	@FXML private ProgressBar pbMarco11;
	@FXML private ProgressBar pbMarco12;
	@FXML private ProgressBar pbMarco13;
	@FXML private ProgressBar pbMarco14;
	@FXML private ProgressBar pbMarco15;
	@FXML private ProgressBar pbMarco16;
	@FXML private ProgressBar pbMarco17;
	@FXML private ProgressBar pbMarco18;
	@FXML private ProgressBar pbMarco19;
	@FXML private ProgressBar pbMarco20;
	@FXML private ProgressBar pbMarco21;
	@FXML private ProgressBar pbMarco22;
	@FXML private ProgressBar pbMarco23;
	@FXML private ProgressBar pbMarco24;
	@FXML private ProgressBar pbMarco25;
	@FXML private ProgressBar pbMarco26;
	@FXML private ProgressBar pbMarco27;
	@FXML private TextField txtMarco0;
	@FXML private TextField txtMarco1;
	@FXML private TextField txtMarco2;
	@FXML private TextField txtMarco3;
	@FXML private TextField txtMarco4;
	@FXML private TextField txtMarco5;
	@FXML private TextField txtMarco6;
	@FXML private TextField txtMarco7;
	@FXML private TextField txtMarco8;
	@FXML private TextField txtMarco9;
	@FXML private TextField txtMarco10;
	@FXML private TextField txtMarco11;
	@FXML private TextField txtMarco12;
	@FXML private TextField txtMarco13;
	@FXML private TextField txtMarco14;
	@FXML private TextField txtMarco15;
	@FXML private TextField txtMarco16;
	@FXML private TextField txtMarco17;
	@FXML private TextField txtMarco18;
	@FXML private TextField txtMarco19;
	@FXML private TextField txtMarco20;
	@FXML private TextField txtMarco21;
	@FXML private TextField txtMarco22;
	@FXML private TextField txtMarco23;
	@FXML private TextField txtMarco24;
	@FXML private TextField txtMarco25;
	@FXML private TextField txtMarco26;
	@FXML private TextField txtMarco27;
	@FXML private TextField txtSigID;
	@FXML private TextField txtSigTam;
	@FXML private TextField txtOpA;
	@FXML private TextField txtOpB;
	@FXML private TextField txtTamano;
	@FXML private TextField txtSuspendidos;
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
	private int marcosDisponibles;

	private Logger logger;
	private ConsoleHandler handler;

	private Marco marcos[];

	private Queue<Integer[]> objectList;
	private int position;
	private int contadorBloquear;

	private ReentrantLock lock = new ReentrantLock();

	private int calcularMarcos(int tamano){
		int necesarios = tamano / 5;
		necesarios += (tamano % 5 == 0)?0:1;
		return necesarios;
	}

	private void cambiaColor(int id, Color color){
		for ( Marco m : marcos ) {
			if ( m.getId() == id ) {
				m.setStyle(color);
			}
		}
	}

	private void cargarNuevos(){
		while ( !procesosNuevos.isEmpty()
				&& (marcosDisponibles >= calcularMarcos(procesosNuevos.peek().getTamano())) ) {
			Proceso p = procesosNuevos.poll();
			procesosListos.add(p);
			llenarMarcos(p.getTamano(), p.getId(), Color.AZUL);
			Platform.runLater(() -> {
				txtPendientes.setText(Integer.toString(procesosNuevos.size()));
				observableListListos.add(p);
			});
			p.setTiempoLlegada(reloj);
			p.setEstado(Estado.LISTO);
		}
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
		this.marcos = new Marco[] { new Marco(pbMarco0, txtMarco0), new Marco(pbMarco1, txtMarco1),
				new Marco(pbMarco2, txtMarco2), new Marco(pbMarco3, txtMarco3), new Marco(pbMarco4, txtMarco4),
				new Marco(pbMarco5, txtMarco5), new Marco(pbMarco6, txtMarco6), new Marco(pbMarco7, txtMarco7),
				new Marco(pbMarco8, txtMarco8), new Marco(pbMarco9, txtMarco9), new Marco(pbMarco10, txtMarco10),
				new Marco(pbMarco11, txtMarco11), new Marco(pbMarco12, txtMarco12), new Marco(pbMarco13, txtMarco13),
				new Marco(pbMarco14, txtMarco14), new Marco(pbMarco15, txtMarco15), new Marco(pbMarco16, txtMarco16),
				new Marco(pbMarco17, txtMarco17), new Marco(pbMarco18, txtMarco18), new Marco(pbMarco19, txtMarco19),
				new Marco(pbMarco20, txtMarco20), new Marco(pbMarco21, txtMarco21), new Marco(pbMarco22, txtMarco22),
				new Marco(pbMarco23, txtMarco23), new Marco(pbMarco24, txtMarco24), new Marco(pbMarco25, txtMarco25),
				new Marco(pbMarco26, txtMarco26), new Marco(pbMarco27, txtMarco27), };

		marcos[0].setProgress(1);
		marcos[0].setStyle(Color.NEGRO);
		marcos[0].setText("S. O.");
		marcos[0].setId(Integer.MIN_VALUE);
		marcos[0].setLibre(false);
		marcos[1].setProgress(1);
		marcos[1].setStyle(Color.NEGRO);
		marcos[1].setText("S. O.");
		marcos[1].setId(Integer.MIN_VALUE);
		marcos[1].setLibre(false);
		marcosDisponibles = 26;
		new File("procesos.ser").delete();
		position = 0;
		contadorBloquear = 0;
		objectList = new ArrayDeque<>();
		hilos();
		configurarTablaListos();
		configurarTablaBloqueados();
		configurarTablaTerminados();
		configurarLogger();
	}

	private void configurarLogger(){
		logger = Logger.getGlobal();
		logger.setLevel(Level.INFO);
		logger.setUseParentHandlers(false);

		handler = new ConsoleHandler();
		handler.setLevel(Level.INFO);
		logger.addHandler(handler);
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
		while ( !procesosNuevos.isEmpty()
				&& (marcosDisponibles >= calcularMarcos(procesosNuevos.peek().getTamano())) ) {
			Proceso p = procesosNuevos.poll();
			procesosListos.add(p);
			llenarMarcos(p.getTamano(), p.getId(), Color.AZUL);
			p.setTiempoLlegada(reloj);
			p.setEstado(Estado.LISTO);
		}
		if ( !procesosNuevos.isEmpty() ) {
			Proceso p = procesosNuevos.peek();
			Platform.runLater(() -> {
				txtSigID.setText("" + p.getId());
				txtSigTam.setText("" + p.getTamano());
			});
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
		EventHandler<KeyEvent> evento = new EventHandler<KeyEvent>(){
			private boolean consumir = false;

			@Override
			public void handle(KeyEvent event){
				if ( (event.getEventType() == KeyEvent.KEY_PRESSED) && !consumir ) {
					consumir = true;
					lock.lock();
					if ( (!finalizado) && (procesoActual.getEstado() != Estado.TERMINADO) ) {
						switch ( event.getCode() ) {
						case I:
							if ( !pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA) ) {
								contadorBloquear++;
								procesoBloqueo = true;
							}
							break;
						case E:
							if ( !pausar && (procesoActual.getOperacion() != Operacion.INDEFINIDA) ) {
								procesoError = true;
							}
							break;
						case N:
							if ( !pausar ) {
								procesoNuevo = true;
							}
							break;
						case B:
							try {
								pausar = true;
								estadisticas();
							} catch ( IOException e1 ) {
								e1.printStackTrace();
							}
							break;
						case P:
						case T:
							if ( !pausar ) {
								pausar = true;
							}
							break;
						case C:
							if ( pausar ) {
								pausar = false;
							}
							break;
						case S:
							if ( !pausar && !procesosBloqueados.isEmpty() ) {
								try {
									ByteArrayOutputStream bos = new ByteArrayOutputStream();
									ObjectOutput out = new ObjectOutputStream(bos);
									Proceso p = procesosBloqueados.poll();
									out.writeObject(p);
									out.close();
									liberarMarco(p.getId());
									cargarNuevos();
									byte[] buf = bos.toByteArray();
									Integer[] objectInfo = new Integer[2];
									objectInfo[0] = position;
									objectInfo[1] = buf.length;
									objectList.add(objectInfo);

									RandomAccessFile tmpFile = new RandomAccessFile("procesos.ser", "rw");
									tmpFile.seek(position);
									tmpFile.write(buf);
									tmpFile.close();
									position += buf.length;
									txtSuspendidos.setText(""+objectList.size());
								} catch ( IOException e ) {
									e.printStackTrace();
								}
							}
							break;
						case R:
							if ( !pausar && !objectList.isEmpty() ) {
								Integer[] objectInfo = (Integer[]) objectList.peek();
								byte[] buf = new byte[objectInfo[1]];

								try {
									RandomAccessFile tmpFile = new RandomAccessFile("procesos.ser", "rw");
									tmpFile.seek(objectInfo[0]);
									tmpFile.readFully(buf);
									tmpFile.close();
									ByteArrayInputStream bis = new ByteArrayInputStream(buf);
									ObjectInputStream ois = new ObjectInputStream(bis);
									Proceso p = (Proceso) ois.readObject();
									ois.close();
									if ( marcosDisponibles >= calcularMarcos(p.getTamano()) ) {
										procesosBloqueados.add(p);
										llenarMarcos(p.getTamano(), p.getId(), Color.AMARILLO);
										objectList.poll();
										txtSuspendidos.setText(""+objectList.size());
									} else {
										logger.info("Memoria insuficiente para restablecer [PID: " + p.getId() + "]");
									}
								} catch ( IOException e ) {
									e.printStackTrace();
								} catch ( ClassNotFoundException e ) {
									e.printStackTrace();
								}
							}
							break;
						default:
							break;
						}
						try {
							Thread.sleep(500);
						} catch ( InterruptedException e ) {
						}
					}
					lock.unlock();
				} else if ( event.getEventType() == KeyEvent.KEY_RELEASED ) {
					consumir = false;
				}
				event.consume();
			}
		};

		return evento;
	}

	private void hilos(){
		actualizadorGUI = new Thread(new Task<Void>(){

			private void actualizaGUI(){
				String total, transcurrido, restante, quantumStr;

				if ( !finalizado ) {
					total = Long.toString(reloj);
					transcurrido = Long.toString(procesoActual.getTiempoTranscurido());
					restante = Long.toString(procesoActual.getTiempoRestante());
					quantumStr = Integer.toString(quantumContador);
					Platform.runLater(() -> {
						txtReloj.setText(total);
						txtQuantumContador.setText(quantumStr);
						observableListBloqueados.setAll(procesosBloqueados);
						observableListListos.setAll(procesosListos);
					});

					if ( procesoActual.getOperacion() != Operacion.INDEFINIDA ) {
						Platform.runLater(() -> {
							txtTiempoTranscurrido.setText(transcurrido);
							txtTiempoRestante.setText(restante);
						});
					}
				}
			}

			@Override
			public Void call(){
				Platform.runLater(() -> {
					observableListListos.setAll(procesosListos.toArray(new Proceso[procesosListos.size()]));
					txtPendientes.setText(Integer.toString(procesosNuevos.size()));
				});
				iniciaSiguienteProceso();

				while ( !finalizado ) {
					if ( !pausar ) {
						lock.lock();
						pausar = true;
						revisarEventos();
						actualizaGUI();
						revisarProcesoActual();
						for ( Proceso p : procesosListos ) {
							p.aumentaTiempoEpera();
						}
						revisarProcesosBloqueados();
						reloj++;
						pausar = false;
						lock.unlock();
					}
					try {
						Thread.sleep(1000);
					} catch ( InterruptedException e ) {
					}
				}
				rellenaCampos("", "", "", "", "", "", "", "");
				return null;
			}

			private void crearNuevoProceso(){
				Random random = new Random();
				Operacion[] operaciones = Operacion.values();
				Proceso nuevo;
				Operacion op;

				do {
					op = operaciones[random.nextInt(operaciones.length)];
				} while ( op == Operacion.INDEFINIDA );
				nuevo = new Proceso(op, random.nextInt(15) + 4, random.nextInt(200) + 1, random.nextInt(200) + 1,
						random.nextInt(25) + 4);
				Platform.runLater(() -> observableListBCP.add(nuevo));

				if ( marcosDisponibles >= calcularMarcos(nuevo.getTamano()) ) {
					Platform.runLater(() -> observableListListos.add(nuevo));
					procesosListos.add(nuevo);
					nuevo.setTiempoLlegada(reloj);
					nuevo.setEstado(Estado.LISTO);
					llenarMarcos(nuevo.getTamano(), nuevo.getId(), Color.AZUL);
				} else {
					procesosNuevos.add(nuevo);
					Platform.runLater(() -> {
						txtSigID.setText("" + procesosNuevos.peek().getId());
						txtSigTam.setText("" + procesosNuevos.peek().getTamano());
					});
				}

				Platform.runLater(() -> txtPendientes.setText(Integer.toString(procesosNuevos.size())));
				procesoNuevo = false;
			}

			private void iniciaSiguienteProceso(){
				if ( !procesosListos.isEmpty() ) {
					procesoActual = procesosListos.poll();
				} else if ( !procesosBloqueados.isEmpty() || !objectList.isEmpty() ) {
					procesoActual = new Proceso(INFINITO, Operacion.INDEFINIDA, INFINITO, 0, 0, 0);
				} else {
					finalizado = true;
					return;
				}

				Platform.runLater(() -> observableListListos.remove(procesoActual));
				if ( procesoActual.getOperacion() != Operacion.INDEFINIDA ) {
					rellenaCampos(Integer.toString(procesoActual.getId()), Integer.toString(procesoActual.getTamano()),
							String.valueOf(procesoActual.getOperacion()), Double.toString(procesoActual.getArgA()),
							Double.toString(procesoActual.getArgB()), Long.toString(procesoActual.getTiempo()),
							Long.toString(procesoActual.getTiempoRestante()),
							Long.toString(procesoActual.getTiempoTranscurido()));
					cambiaColor(procesoActual.getId(), Color.VERDE);
				} else {
					rellenaCampos("", "", "", "", "", "", "", "");
				}
				procesoActual.setTiempoRespuesta(reloj);
				procesoActual.setEstado(Estado.EJECUTANDO);
				procesoActual.ejecutar();
				quantumContador = 0;
			}

			private void rellenaCampos(String id, String tamano, String operacion, String opA, String opB,
					String tiempoEstimado, String tiempoRestante, String tiempoTranscurrido){
				Platform.runLater(() -> {
					txtNumeroPrograma.setText(id);
					txtTamano.setText(tamano);
					txtOperacion.setText(operacion);
					txtOpA.setText(opA);
					txtOpB.setText(opB);
					txtTiempoEstimado.setText(tiempoEstimado);
					txtTiempoTranscurrido.setText(tiempoTranscurrido);
					txtTiempoRestante.setText(tiempoRestante);
				});
			}

			private void revisarEventos(){
				if ( procesoNuevo ) {
					crearNuevoProceso();
				} else if ( procesoBloqueo ) {
					while ( contadorBloquear > 0 && (procesoActual.getOperacion() != Operacion.INDEFINIDA) ) {
						if ( procesoActual.getTiempoRestante() > 0 ) {
							procesoActual.setEstado(Estado.BLOQUEADO);
							procesosBloqueados.add(procesoActual);
							cambiaColor(procesoActual.getId(), Color.AMARILLO);
							iniciaSiguienteProceso();
						} else {
							logger.info("El proceso que se intentó bloquear [PID: " + procesoActual.getId()
									+ "] recien habia terminado");
						}
						contadorBloquear--;
					}
					procesoBloqueo = false;
				} else if ( procesoError ) {
					procesoActual.setEstado(Estado.ERROR);
					terminaIniciaSiguienteProceso();
					procesoError = false;
				}

				if ( !procesosListos.isEmpty() && (procesoActual.getOperacion() == Operacion.INDEFINIDA) ) {
					iniciaSiguienteProceso();
				}
			}

			private void revisarProcesoActual(){
				if ( procesoActual.getTiempoRestante() <= 0 ) {
					procesoActual.setEstado(Estado.TERMINADO);
					terminaIniciaSiguienteProceso();
				} else if ( quantumContador == quantum ) {
					cambiaColor(procesoActual.getId(), Color.AZUL);
					Platform.runLater(() -> observableListListos.add(procesoActual));
					procesosListos.add(procesoActual);
					procesoActual.setEstado(Estado.LISTO);
					iniciaSiguienteProceso();

				}

				if ( !finalizado ) {
					procesoActual.aumentaTiempoEjecucion();
					quantumContador++;
				}
			}

			private void revisarProcesosBloqueados(){
				for ( Proceso p : procesosBloqueados ) {
					if ( p.getTiempoBloqueado() >= 16 || (p.getEstado() != Estado.BLOQUEADO) ) {
						cambiaColor(p.getId(), Color.AZUL);
						procesosListos.add(p);
						procesosBloqueados.remove(p);
						p.setEstado(Estado.LISTO);
						p.reiniciaTiempoBloqueo();
						p.aumentaTiempoEpera();
						if ( procesoActual.getOperacion() == Operacion.INDEFINIDA ) {
							iniciaSiguienteProceso();
						}
					} else {
						p.aumentaTiempoBloqueo();
						p.aumentaTiempoEpera();
					}
				}
			}

			private void terminaIniciaSiguienteProceso(){
				procesoActual.setTiempoFinalizacion(reloj);
				Proceso termino = procesoActual;
				Platform.runLater(() -> {
					observableListListos.remove(termino);
					observableListTerminados.add(termino);
				});
				liberarMarco(procesoActual.getId());

				cargarNuevos();
				if ( !procesosNuevos.isEmpty() ) {
					Proceso p = procesosNuevos.peek();
					Platform.runLater(() -> {
						txtSigID.setText("" + p.getId());
						txtSigTam.setText("" + p.getTamano());
					});
				} else {
					Platform.runLater(() -> {
						txtSigID.setText("");
						txtSigTam.setText("");
					});
				}
				iniciaSiguienteProceso();
			}

		});

		actualizadorGUI.setDaemon(true);
	}

	private void liberarMarco(int id){
		for ( Marco m : marcos ) {
			if ( m.getId() == id ) {
				m.liberar();
				marcosDisponibles++;
			}
		}
	}

	private void llenarMarcos(int tamano, int id, Color color){
		int paginas = tamano;
		int necesarios = calcularMarcos(tamano);

		if ( marcosDisponibles >= necesarios ) {
			for ( int i = 0; i < 28 && necesarios > 0; i++ ) {
				if ( marcos[i].isLibre() ) {
					if ( paginas >= 5 ) {
						marcos[i].setProgress(1);
					} else {
						marcos[i].setProgress((double) paginas / 5);
					}
					marcos[i].setStyle(color);
					marcos[i].setText("" + id);
					marcos[i].setId(id);
					paginas -= 5;
					necesarios--;
					marcosDisponibles--;
				}
			}
		}
	}
}
