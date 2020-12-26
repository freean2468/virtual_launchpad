package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
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
	
	/*
	 * Vbox(root) 
	 * 	- MenuBar
	 * 		- Menu(menuTrack)
	 *  - ToolBar
	 *  	- ComboBox(comboTrack)
	 *  	- Button(btnPlayOnOff)
	 *  	- Slider(sliderLaunchpadSize)
	 *  - TilePane(mainPane)
	 *  	- TilePane(container in Launchpad)
	 *  		- Rectangle(pad in pads in Launchpad)
	 */
	
	@FXML private VBox root;
	@FXML private Menu menuTrack;
	@FXML private ComboBox<String> comboTrack;
	@FXML private TilePane mainPane;
	
	@FXML private Button btnPlayOnOff;
	@FXML private Slider sliderLaunchpadSize;
	
	public class Launchpad {
		private TilePane container;
		private ArrayList<Rectangle> pads;
		private TextField trackName;
		private double padLength;
		
		public class Key {
			private TreeMap<Integer, String> keyRange;
			private int firstKeyIndex;
			
			public Key(TreeMap<Integer, String> keyRange) {
				this.keyRange = keyRange;
				Map.Entry<Integer, String> firstEntry = keyRange.firstEntry();
				this.firstKeyIndex = findFirstKeyIndex((firstEntry != null) ? firstEntry.getValue() : "C");
//				System.out.println("총 키 갯수 : " + keyRange.size());
//				System.out.println(keyRange);
				
				// 한 런치패드로 7옥타브 + 2키까지 표현 가능
				if (keyRange.size() > 0)
					if ((keyRange.lastKey() - keyRange.firstKey())/MusicPlayer.NOTE_NAMES.length > 7) {
						System.out.println("Keys are out of range!");
					}
			}
			
			public int findFirstKeyIndex(String key) {
				for (int i = 0; i < MusicPlayer.NOTE_NAMES.length; ++i) {
					if (MusicPlayer.NOTE_NAMES[i].equals(key))
						return i;
				}
				return -1;
			}
			
			public TreeMap<Integer, String> getKeyRange() {
				return keyRange;
			}
			
			public int getFirstKeyIndex() {
				return firstKeyIndex;
			}
			
			public int getPadIndex(int key) {
				int padIndex = key - keyRange.firstKey() + firstKeyIndex;
				for (int i = 0; i < 8; ++i) {
					for (int j = 0; j < 8; ++j) {
						if (padIndex == i*8+j) {
							return (7-i)*8+j;
						}
					}
				}
				// never have to reach here
				return -1;
			}
		}
		
		private Key key;
		
		
		public Launchpad(String trackName, double length, TreeMap<Integer, String> keyRange) {
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
			this.key = new Key(keyRange);
			
			resize(length);
		}
		
		public TilePane getContainer() {
			return container;
		}
		
		public void resize(double length) {
			double margin = length/40.0;
			double gap = length/60.0;
			double lengthWithPadding = Math.floor(length-length*0.1-margin*2-gap*7);
			padLength = lengthWithPadding / 8.0;
			
			for (Rectangle pad : pads) {
				pad.setHeight(padLength);
				pad.setWidth(padLength);
				pad.setStrokeWidth(padLength/16.0);
			}
			
			TilePane.setMargin(this.container, new Insets(margin, margin,margin,margin));
			this.container.setHgap(gap);
			this.container.setVgap(gap);
			this.container.setPrefWidth(length);
			this.container.setPrefHeight(length);
			this.container.setPrefTileWidth(lengthWithPadding/8.0);
			this.container.setPrefTileHeight(lengthWithPadding/8.0);
			this.container.getChildren().clear();
			this.container.getChildren().addAll(pads);
		}
		
		public void padOn(int key) {
			int padIndex = this.key.getPadIndex(key);
			Rectangle pad = pads.get(padIndex);
			
			int octave = key/MusicPlayer.NOTE_NAMES.length;
			Color color;
			switch (octave) {
			case 0: color = Color.BLACK; 			break;
			case 1: color = Color.DARKRED; 			break;
			case 2: color = Color.PURPLE;	 		break;
			case 3: color = Color.BLUE; 			break;
			case 4: color = Color.STEELBLUE; 		break;
			case 5: color = Color.TURQUOISE; 		break;
			case 6: color = Color.GOLD;		 		break;
			case 7: color = Color.CORAL;			break;
			case 8: color = Color.MISTYROSE;		break;
			case 9: color = Color.OLDLACE;			break;
			case 10:color = Color.LAVENDERBLUSH; 	break;
			default: color = Color.WHITE; 			break;
			}
			pad.setFill(color);
			
			double length = padLength+padLength*0.08;
			pad.setWidth(length);
			pad.setHeight(length);
			pad.setStroke(Color.BLACK);
		}
		
		public void padOff(int key) {
			int padIndex = this.key.getPadIndex(key);
			Rectangle pad = pads.get(padIndex);
			pad.setFill(Color.WHITE);
			pad.setWidth(padLength);
			pad.setHeight(padLength);
			pad.setStroke(Color.LIGHTGRAY);
		}
	}
	
	ArrayList<Launchpad> launchpads;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		System.out.println("initialized!");
		
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
	
	public void initMainPane(int row, int column, int trackNumber, String trackName, ArrayList<TreeMap<Integer, String>> keyRangeList) {
		mainPane.getChildren().clear();
		root.setPrefSize(Control.BASELINE_OFFSET_SAME_AS_HEIGHT, Control.BASELINE_OFFSET_SAME_AS_HEIGHT);
		mainPane.setPrefSize(root.getWidth(), root.getHeight());
		double prefTileSize = mainPane.getPrefWidth()/column; 
		mainPane.setPrefTileWidth(prefTileSize);
		mainPane.setPrefTileHeight(prefTileSize);
		
		launchpads = new ArrayList<Launchpad>();
		
		for (int i = 0; i < trackNumber; ++i) {
			Launchpad launchpad = new Launchpad(trackName, mainPane.getPrefTileWidth(), keyRangeList.get(i));
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
		if (selectedFile != null) {
			String selectedFilePath = selectedFile.getPath();
			menuTrack.getItems().clear();
			MusicPlayer.getInstance().go(selectedFilePath);
		}
	}
	
	@FXML
	public void handleClose() {
		System.out.println("Close");
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
	
	public void addTrackIntoMenuBar(String trackName) {
		CheckMenuItem item = new CheckMenuItem(trackName);
		item.setSelected(true);
		item.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> Observable, Boolean oldValue, Boolean newValue) {
				int idx = menuTrack.getItems().indexOf(item);
				if (newValue == false) {
					menuTrack.getItems().remove(idx);
					mainPane.getChildren().remove(idx);
				}
			}
		});
		menuTrack.getItems().add(item);
	}
	
	public ArrayList<Launchpad> getLaunchpads() {
		return launchpads;
	}
	
	public void handleKeyEvent(int eventCode, int trackNumber, int key) {
		if (eventCode == MusicPlayer.NOTE_OFF_EVENT)
			launchpads.get(trackNumber-1).padOff(key);
		else if (eventCode == MusicPlayer.NOTE_ON_EVENT)
			launchpads.get(trackNumber-1).padOn(key);
	}
}
