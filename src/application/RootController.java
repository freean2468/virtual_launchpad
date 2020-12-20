package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.shape.Rectangle;

public class RootController implements Initializable {
	@FXML private Rectangle btn11;
	@FXML private Rectangle btn12;
	@FXML private Rectangle btn13;
	@FXML private Rectangle btn14;
	@FXML private Rectangle btn15;
	@FXML private Rectangle btn16;
	@FXML private Rectangle btn17;
	@FXML private Rectangle btn18;
	@FXML private Rectangle btn21;
	@FXML private Rectangle btn22;
	@FXML private Rectangle btn23;
	@FXML private Rectangle btn24;
	@FXML private Rectangle btn25;
	@FXML private Rectangle btn26;
	@FXML private Rectangle btn27;
	@FXML private Rectangle btn28;
	@FXML private Rectangle btn31;
	@FXML private Rectangle btn32;
	@FXML private Rectangle btn33;
	@FXML private Rectangle btn34;
	@FXML private Rectangle btn35;
	@FXML private Rectangle btn36;
	@FXML private Rectangle btn37;
	@FXML private Rectangle btn38;
	@FXML private Rectangle btn41;
	@FXML private Rectangle btn42;
	@FXML private Rectangle btn43;
	@FXML private Rectangle btn44;
	@FXML private Rectangle btn45;
	@FXML private Rectangle btn46;
	@FXML private Rectangle btn47;
	@FXML private Rectangle btn48;
	@FXML private Rectangle btn51;
	@FXML private Rectangle btn52;
	@FXML private Rectangle btn53;
	@FXML private Rectangle btn54;
	@FXML private Rectangle btn55;
	@FXML private Rectangle btn56;
	@FXML private Rectangle btn57;
	@FXML private Rectangle btn58;
	@FXML private Rectangle btn61;
	@FXML private Rectangle btn62;
	@FXML private Rectangle btn63;
	@FXML private Rectangle btn64;
	@FXML private Rectangle btn65;
	@FXML private Rectangle btn66;
	@FXML private Rectangle btn67;
	@FXML private Rectangle btn68;
	@FXML private Rectangle btn71;
	@FXML private Rectangle btn72;
	@FXML private Rectangle btn73;
	@FXML private Rectangle btn74;
	@FXML private Rectangle btn75;
	@FXML private Rectangle btn76;
	@FXML private Rectangle btn77;
	@FXML private Rectangle btn78;
	@FXML private Rectangle btn81;
	@FXML private Rectangle btn82;
	@FXML private Rectangle btn83;
	@FXML private Rectangle btn84;
	@FXML private Rectangle btn85;
	@FXML private Rectangle btn86;
	@FXML private Rectangle btn87;
	@FXML private Rectangle btn88;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("initialized!");

		try {
			MusicPlayer mp = new MusicPlayer(this);
			mp.go();
		} catch (Exception ex) { 
			ex.printStackTrace(); 
		}
		
//		btn11.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				handleBtn11Action(event);
//			}
//		});
//		
//		btn12.setOnAction(event->handleBtn12Action(event));
//		btn13.setOnAction(event->handleBtn13Action(event));
	}
	
	public void handleBtn11Action() {
		System.out.println("버튼11 클릭");
	}
	
	public void handleBtn12Action() {
		System.out.println("버튼12 클릭");
	}
	
	public void handleBtn13Action() {
		System.out.println("버튼13 클릭");
	}
	
	
}
