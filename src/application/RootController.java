package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RootController implements Initializable {
	@FXML private Rectangle btn1;
	@FXML private Rectangle btn2;
	@FXML private Rectangle btn3;
	@FXML private Rectangle btn4;
	@FXML private Rectangle btn5;
	@FXML private Rectangle btn6;
	@FXML private Rectangle btn7;
	@FXML private Rectangle btn8;
	@FXML private Rectangle btn9;
	@FXML private Rectangle btn10;
	@FXML private Rectangle btn11;
	@FXML private Rectangle btn12;
	@FXML private Rectangle btn13;
	@FXML private Rectangle btn14;
	@FXML private Rectangle btn15;
	@FXML private Rectangle btn16;
	@FXML private Rectangle btn17;
	@FXML private Rectangle btn18;
	@FXML private Rectangle btn19;
	@FXML private Rectangle btn20;
	@FXML private Rectangle btn21;
	@FXML private Rectangle btn22;
	@FXML private Rectangle btn23;
	@FXML private Rectangle btn24;
	@FXML private Rectangle btn25;
	@FXML private Rectangle btn26;
	@FXML private Rectangle btn27;
	@FXML private Rectangle btn28;
	@FXML private Rectangle btn29;
	@FXML private Rectangle btn30;
	@FXML private Rectangle btn31;
	@FXML private Rectangle btn32;
	@FXML private Rectangle btn33;
	@FXML private Rectangle btn34;
	@FXML private Rectangle btn35;
	@FXML private Rectangle btn36;
	@FXML private Rectangle btn37;
	@FXML private Rectangle btn38;
	@FXML private Rectangle btn39;
	@FXML private Rectangle btn40;
	@FXML private Rectangle btn41;
	@FXML private Rectangle btn42;
	@FXML private Rectangle btn43;
	@FXML private Rectangle btn44;
	@FXML private Rectangle btn45;
	@FXML private Rectangle btn46;
	@FXML private Rectangle btn47;
	@FXML private Rectangle btn48;
	@FXML private Rectangle btn49;
	@FXML private Rectangle btn50;
	@FXML private Rectangle btn51;
	@FXML private Rectangle btn52;
	@FXML private Rectangle btn53;
	@FXML private Rectangle btn54;
	@FXML private Rectangle btn55;
	@FXML private Rectangle btn56;
	@FXML private Rectangle btn57;
	@FXML private Rectangle btn58;
	@FXML private Rectangle btn59;
	@FXML private Rectangle btn60;
	@FXML private Rectangle btn61;
	@FXML private Rectangle btn62;
	@FXML private Rectangle btn63;
	@FXML private Rectangle btn64;
	
	public ArrayList<Rectangle> launchpad = new ArrayList<Rectangle>(); 
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("initialized!");
		
		launchpad.add(btn1);
		launchpad.add(btn2);
		launchpad.add(btn3);
		launchpad.add(btn4);
		launchpad.add(btn5);
		launchpad.add(btn6);
		launchpad.add(btn7);
		launchpad.add(btn8);
		launchpad.add(btn9);
		launchpad.add(btn10);
		launchpad.add(btn11);
		launchpad.add(btn12);
		launchpad.add(btn13);
		launchpad.add(btn14);
		launchpad.add(btn15);
		launchpad.add(btn16);
		launchpad.add(btn17);
		launchpad.add(btn18);
		launchpad.add(btn19);
		launchpad.add(btn20);
		launchpad.add(btn21);
		launchpad.add(btn22);
		launchpad.add(btn23);
		launchpad.add(btn24);
		launchpad.add(btn25);
		launchpad.add(btn26);
		launchpad.add(btn27);
		launchpad.add(btn28);
		launchpad.add(btn29);
		launchpad.add(btn30);
		launchpad.add(btn31);
		launchpad.add(btn32);
		launchpad.add(btn33);
		launchpad.add(btn34);
		launchpad.add(btn35);
		launchpad.add(btn36);
		launchpad.add(btn37);
		launchpad.add(btn38);
		launchpad.add(btn39);
		launchpad.add(btn40);
		launchpad.add(btn41);
		launchpad.add(btn42);
		launchpad.add(btn43);
		launchpad.add(btn44);
		launchpad.add(btn45);
		launchpad.add(btn46);
		launchpad.add(btn47);
		launchpad.add(btn48);
		launchpad.add(btn49);
		launchpad.add(btn50);
		launchpad.add(btn51);
		launchpad.add(btn52);
		launchpad.add(btn53);
		launchpad.add(btn54);
		launchpad.add(btn55);
		launchpad.add(btn56);
		launchpad.add(btn57);
		launchpad.add(btn58);
		launchpad.add(btn59);
		launchpad.add(btn60);
		launchpad.add(btn61);
		launchpad.add(btn62);
		launchpad.add(btn63);
		launchpad.add(btn64);

		try {
			MusicPlayer mp = new MusicPlayer(this);
			mp.go();
		} catch (Exception ex) { 
			ex.printStackTrace(); 
		}
		
		// setOnActions Directly on the programming.
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
	
	@FXML
	public void handleBtn11Action() {
		System.out.println("버튼11 클릭");
		btn1.setFill(Color.RED);
	}
	
	public void handleBtn12Action() {
		System.out.println("버튼12 클릭");
	}
	
	public void handleBtn13Action() {
		System.out.println("버튼13 클릭");
	}
	
	
}
