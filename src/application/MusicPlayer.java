package application;

import java.io.File;
import java.util.ArrayList;
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
		int trackNumber = event.getChannel();
		int key = event.getData2();
		int eventCode = event.getData1();
		Main.rootController.handleKeyEvent(eventCode, trackNumber, key);
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
	
	public void onOff() {
		if (sequencer.isRunning())
			sequencer.stop();
		else
			sequencer.start();
	}
	
	public void closeSequencer() {
		sequencer.close();
	}
	
	public static MusicPlayer getInstance() {
		return singleton;
	}
	
	private void setSequence() {
		try {
			Sequence seq = MidiSystem.getSequence(midFile);
			
			// controller에 전달할 이벤트를 담을 전용 트랙 
			Track trackForController = seq.createTrack();
			
			ArrayList<TreeMap<Integer, String>> keyRangeList = new ArrayList<TreeMap<Integer, String>>();
			 
	        ObservableList<String> comboItems = FXCollections.observableArrayList();
	        
	        trackNumber = 0;
			String trackName = null;
			
			// find out a key range and track information
			for (Track track : seq.getTracks()) {
				++trackNumber;
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
				
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == NOTE_ON) {
							int key = sm.getData1();
							int note = key % OCTAVE;
							String noteName = NOTE_NAMES[note];

							if (!keyRange.containsKey(key)) {
								keyRange.put(key, noteName);
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
				keyRangeList.add(keyRange);
			}
			
			// track수를 Javafx combobox로 전달
			Main.rootController.getComboTrack().setItems(comboItems);
			
			// track수만큼 launchpad를 생성하려면 mainPane부터 설정
			int row = 1;	// 고정 
			int column = (trackNumber+1)/2;
			
			Main.rootController.initMainPane(row, column, trackNumber, trackName, keyRangeList);
			
			trackNumber = 0;
			
			// register events to play
			for (Track track : seq.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				
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
							
							trackForController.add(makeEvent(176, trackNumber, NOTE_ON_EVENT, key, tick));
							trackForController.add(makeEvent(176, trackNumber, NOTE_OFF_EVENT, key, tick+(velocity*100)));
						} else if (sm.getCommand() == NOTE_OFF) {
							int key = sm.getData1();
							int octave = (key / 12) - 1;
							int note = key % 12;
							String noteName = NOTE_NAMES[note];
							int velocity = sm.getData2();
//							System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
							trackForController.add(makeEvent(176, trackNumber, NOTE_OFF_EVENT, key, tick));
						} else {
//								System.out.println("command: " + sm.getCommand());
						}
					} 
					else {
//							System.out.println("Other message: " + message.getClass());
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
