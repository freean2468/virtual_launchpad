package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

// can't be declared as a singleton
public class RootController implements Initializable {
	public static final int PADS = 64;
	public static final int PAD_WIDTH = 80;
	public static final int PAD_HEIGHT = 80;
	
	@FXML private VBox root;
	@FXML private MenuBar menu;
	@FXML private ComboBox<String> comboTrack;
	@FXML private TilePane mainPane;
	
	@FXML private Button btnPlayOnOff;
	@FXML private Slider sliderLaunchpadSize;
	
	public class Launchpad {
		private TilePane container;
		private ArrayList<Rectangle> pads;
		private TextField trackName;
		
		Launchpad(String trackName, double length) {
			this.trackName = new TextField();
			this.trackName.setText(trackName);
			
			this.pads = new ArrayList<Rectangle>();
			
			length = Math.floor(length);
			
			for (int i = 0 ; i < PADS; ++i) {
				Rectangle pad = new Rectangle();
				pad.setArcHeight(5.0);
				pad.setArcWidth(5.0);
				pad.setFill(Color.WHITE);
				pad.setStroke(Color.LIGHTGRAY);
				pad.setStrokeType(StrokeType.OUTSIDE);
				this.pads.add(pad);
			}
			
			this.container = new TilePane();
			/*<TilePane.margin>
	            <Insets bottom="20.0" left="20.0" right="20.0" top="20.0" />
	         </TilePane.margin>*/
			
			resize(length);
		}
		
		public void init() {
			for (Rectangle pad : pads) {
				pad.setStroke(Color.LIGHTGRAY);
				pad.setWidth(PAD_WIDTH);
				pad.setHeight(PAD_HEIGHT);
				pad.setFill(Color.WHITE);
			}
		}
		
		public TilePane getContainer() {
			return container;
		}
		
		public void resize(double length) {
			double margin = length/40;
			double lengthWithPadding = Math.floor(length-length*0.1-margin*2);
			double padLength = lengthWithPadding / 8;
			
			for (Rectangle pad : pads) {
				pad.setHeight(padLength);
				pad.setWidth(padLength);
				pad.setStrokeWidth(padLength/20);
			}
			
			double gap = padLength/9;
			TilePane.setMargin(this.container, new Insets(margin, margin,margin,margin));
			this.container.setHgap(gap);
			this.container.setVgap(gap);
			this.container.setPrefWidth(length);
			this.container.setPrefHeight(length);
			this.container.setPrefTileWidth(lengthWithPadding/8);
			this.container.setPrefTileHeight(lengthWithPadding/8);
			this.container.getChildren().clear();
			this.container.getChildren().addAll(pads);
		}
	}
	
	ArrayList<Launchpad> launchpads;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("initialized!");
		
		mainPane.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
		
		sliderLaunchpadSize.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> Observable, Number oldValue, Number newValue) {
				mainPane.setPrefTileWidth(newValue.doubleValue());
				mainPane.setPrefTileHeight(newValue.doubleValue());
				for (Launchpad launchpad : launchpads) {
					launchpad.resize(newValue.doubleValue());
				}
			}
		});
	}
	
	public void initMainPane(int row, int column, int trackNumber, String trackName) {
		mainPane.getChildren().clear();
		root.setPrefSize(Control.BASELINE_OFFSET_SAME_AS_HEIGHT, Control.BASELINE_OFFSET_SAME_AS_HEIGHT);
		mainPane.setPrefSize(root.getWidth(), root.getHeight());
		double prefTileSize = mainPane.getPrefWidth()/column; 
		mainPane.setPrefTileWidth(prefTileSize);
		mainPane.setPrefTileHeight(prefTileSize);
		
		launchpads = new ArrayList<Launchpad>();
		
		for (int i = 0; i < trackNumber; ++i) {
			Launchpad launchpad = new Launchpad(trackName, mainPane.getPrefTileWidth());
			launchpads.add(launchpad);
			mainPane.getChildren().add(launchpad.getContainer());
		}
	}
	
	@FXML
	public void handleOpenFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Midi Files", "*.mid"));
		fileChooser.setTitle("Open Midi File");
		File selectedFile = fileChooser.showOpenDialog(Main.stage);
		String selectedFilePath = selectedFile.getPath();
		MusicPlayer.getInstance().go(selectedFilePath);
	}
	
	@FXML
	public void handleClose() {
		System.out.println("Close");
	}
	
	@FXML
	public void handleComboTrack() {
		MusicPlayer.getInstance().onOff();
		if (comboTrack.getValue() != null) {
//			initLaunchpad();
//			System.out.println("combo : " + comboTrack.getValue());
			MusicPlayer.getInstance().setTrack(comboTrack.getValue());
		}
	}
	
	@FXML
	public void btnPlayOnOffAction() {
		MusicPlayer.getInstance().onOff();
	}
	
	public ComboBox<String> getComboTrack() {
		return comboTrack;
	}
	
	public TilePane getMainPane() {
		return mainPane;
	}
	
	public ArrayList<Launchpad> getLaunchpads() {
		return launchpads;
	}
}
