package application;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class Marco{
	public static enum Color{
		NEGRO, AZUL, VERDE, AMARILLO, BLANCO
	}
	private ProgressBar pbMarco;
	private TextField txtMarco;
	private boolean libre;
	private int id;

	public Marco(ProgressBar pbMarco, TextField txtMarco) {
		super();
		this.pbMarco = pbMarco;
		this.txtMarco = txtMarco;
		this.setLibre(true);
		this.id = -1;
	}

	public void setProgress(double progreso){
		Platform.runLater(()->this.pbMarco.setProgress(progreso));
		setLibre(false);
	}

	public void setStyle(Color color){
		switch ( color ) {
		case NEGRO:
			Platform.runLater(()->this.pbMarco.setStyle("-fx-accent: BLACK;"));
			break;
		case AZUL:
			Platform.runLater(()->this.pbMarco.setStyle("-fx-accent: BLUE;"));
			break;
		case VERDE:
			Platform.runLater(()->this.pbMarco.setStyle("-fx-accent: GREEN;"));
			break;
		case AMARILLO:
			Platform.runLater(()->this.pbMarco.setStyle("-fx-accent: YELLOW;"));
			break;
		case BLANCO:
			Platform.runLater(()->this.pbMarco.setStyle("-fx-accent: WHITE;"));
			break;
		default:
			break;
		}
	}
	
	public void setText(String texto){
		Platform.runLater(()->this.txtMarco.setText(texto));
	}
	
	public void liberar(){
		setProgress(0);
		setStyle(Color.BLANCO);
		setText("");
		setLibre(true);
	}

	public boolean isLibre(){
		return libre;
	}

	public void setLibre(boolean libre){
		this.libre = libre;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}
}
