package application;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.sound.midi.*;

public class MusicPlayer implements ControllerEventListener{
	//https://mido.readthedocs.io/en/latest/meta_message_types.html
	public static final int TRACK_NAME = 0x03;
	
	public static final int NOTE_ON_EVENT = 126;
	public static final int NOTE_OFF_EVENT = 127;
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static final int OCTAVE = 12;
	
	private static final MusicPlayer singleton = new MusicPlayer();
	
	private Sequencer sequencer;
	private int trackNumber = 0;
	private int trackNumberToShow = 0;
	private String trackName;
	private File midFile = null;
	
	private MusicPlayer() {
		try {
			sequencer = MidiSystem.getSequencer();

			sequencer.open();
			
			int[] eventsIWant = {126, 127};
			sequencer.addControllerEventListener(this, eventsIWant);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void go(String filePath) {
		try {			
			midFile = new File(filePath);
			setSequence();
		} catch (Exception ex) { 
			ex.printStackTrace(); 
		}
	}

	@Override
	public void controlChange(ShortMessage event) {
//		System.out.println("data1 : " + event.getData1() + " data2 : " + event.getData2());
		int key = event.getData2();
		Rectangle pad = Main.rootController.getLaunchpad().get(key);
		
		if (event.getData1() == NOTE_ON_EVENT) {
			int remain = key/NOTE_NAMES.length;
			Color color;
			switch (remain) {
			case 0:
				color = Color.PURPLE;
				break;
			case 1:
				color = Color.BLUE;
				break;
			case 2:
				color = Color.TURQUOISE;
				break;
			case 3:
				color = Color.CORAL;
				break;
			case 4:
				color = Color.LIGHTCORAL;
				break;
			case 5:
				color = Color.HONEYDEW;
				break;
			case 6:
				color = Color.LIGHTCORAL;
				break;
			default:
				color = Color.GOLDENROD;
				break;
			}
			pad.setFill(color);
			pad.setWidth(RootController.PAD_WIDTH+15);
			pad.setHeight(RootController.PAD_HEIGHT+15);
			pad.setStroke(Color.BLACK);
		}
		else if (event.getData1() == NOTE_OFF_EVENT) {
			pad.setFill(Color.WHITE);
			pad.setWidth(RootController.PAD_WIDTH);
			pad.setHeight(RootController.PAD_HEIGHT);
			pad.setStroke(Color.LIGHTGRAY);
		}
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, long tick) {
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			// comd == message type
			//			128 == note off
			//			144 == note on
			//			176 == controllerEvent
			// 			192 == change-instrument message
			// chan == channel == musician
			// one == changed depending on the message type
			//			144 == note to play
			//			176 == event number
			//			192 == 변경할 악기 
			// two == changed depending on the message type
			//			144 == velocity
			a.setMessage(comd, chan, one, two);
			
			// tick == duration
			event = new MidiEvent(a, tick);
		} catch (Exception e) { }
		return event;
	}
	
	public int findFirstKeyIndex(String key) {
		for (int i = 0; i < NOTE_NAMES.length; ++i) {
			if (NOTE_NAMES[i].equals(key))
				return i;
		}
		return -1;
	}
	
	public void stop() {
		sequencer.stop();
	}
	
	public void closeSequencer() {
		sequencer.close();
	}
	
	public static MusicPlayer getInstance() {
		return singleton;
	}
	
	public void setTrack(String trackName) {
		trackNumberToShow = Integer.parseInt((trackName.length() > 1) ? trackName.split("-")[0] : trackName);
		setSequence();
	}
	
	private void setSequence() {
		try {
			Sequence seq = MidiSystem.getSequence(midFile);
			
			// controller에 전달할 이벤트를 담을 전용 트랙 
			Track trackForController = seq.createTrack();
			
			TreeMap<Integer, String> keyRange = new TreeMap<Integer, String>(new Comparator<Integer>() {
	    	    // Note: this comparator imposes orderings that are inconsistent with
	    	    // equals.
	    		@Override
	    	    public int compare(Integer a, Integer b) {
	    			if (a == b) {
	    				return 0;
	    			}
	    			else if (a < b) {
	    	            return -1;
	    	        } else {
	    	            return 1;
	    	        } 
	    	    }
	    	});
			 
	        ObservableList<String> comboItems = FXCollections.observableArrayList();
	        
	        trackNumber = 0;
	        
			// find out a key range and track information
			for (Track track : seq.getTracks()) {
				++trackNumber;
				String trackName = null;
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == NOTE_ON) {
							int key = sm.getData1();
							int note = key % OCTAVE;
							String noteName = NOTE_NAMES[note];

							if (trackNumber == trackNumberToShow) {
								if (!keyRange.containsKey(key)) {
									keyRange.put(key, noteName);
								}
							}
						}
					} else if (message instanceof MetaMessage) {
						MetaMessage mm = (MetaMessage) message;
						if (mm.getType() == TRACK_NAME) {
							System.out.println("meta message : " + new String(mm.getData()));
							trackName = "-" + new String(mm.getData());
			            }
					}
				}
				
				comboItems.add((trackName != null) ? trackNumber+trackName : String.valueOf(trackNumber));
			}
			
			// track수를 Javafx combobox로 전달
			Main.rootController.getComboTrack().setItems(comboItems);
			
			System.out.println("총 키 갯수 : " + keyRange.size());
			System.out.println(keyRange);
			
			Map.Entry<Integer, String> firstEntry = keyRange.firstEntry();
			
			int firstKeyIndex = findFirstKeyIndex((firstEntry != null) ? firstEntry.getValue() : "C");
			
			// 한 런치패드로 7옥타브 + 2키까지 표현 가능
			if (keyRange.size() > 0)
				if ((keyRange.lastKey() - keyRange.firstKey())/NOTE_NAMES.length > 7) {
					System.out.println("Keys are out of range!");
				}
			
			trackNumber = 0;
			
			// register events to play
			for (Track track : seq.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				
				if (trackNumber == trackNumberToShow) {
					for (int i = 0; i < track.size(); i++) {
						MidiEvent event = track.get(i);
						long tick = event.getTick();
	//					System.out.print("@" + tick + " ");
						MidiMessage message = event.getMessage();
						
						if (message instanceof ShortMessage) {
							ShortMessage sm = (ShortMessage) message;
							int channel = sm.getChannel();
	//						System.out.print("Channel: " + channel + " ");
							if (sm.getCommand() == NOTE_ON) {
								// bests : 9, 14, 114, 121
//								track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 0, 9, 0, tick));
								int key = sm.getData1();
								int octave = (key / OCTAVE) - 1;
								int note = key % OCTAVE;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
	//							System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
								
								trackForController.add(makeEvent(176, channel, NOTE_ON_EVENT, key-keyRange.firstKey()+firstKeyIndex, tick));
								trackForController.add(makeEvent(176, channel, NOTE_OFF_EVENT, key-keyRange.firstKey()+firstKeyIndex, tick+(velocity*100)));
							} else if (sm.getCommand() == NOTE_OFF) {
								int key = sm.getData1();
								int octave = (key / 12) - 1;
								int note = key % 12;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
	//							System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
							} else {
//								System.out.println("command: " + sm.getCommand());
							}
						} 
						else {
//							System.out.println("Other message: " + message.getClass());
						}
					}
				}
				
				System.out.println();
			}
			
			sequencer.setSequence(seq);
			sequencer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
