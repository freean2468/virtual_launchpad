package application;

import java.io.File;
import java.util.Comparator;
import java.util.TreeMap;

import javafx.scene.paint.Color;

import javax.sound.midi.*;

public class MusicPlayer implements ControllerEventListener{
	public static final int NOTE_ON_EVENT = 126;
	public static final int NOTE_OFF_EVENT = 127;
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static final int OCTAVE = 12;
	
	public static int firstKeyIndex; 
	
	private RootController rc;
	
	public MusicPlayer(RootController _rc) {
		rc = _rc;
	}
	
	public void go(String filePath) {
		File myFile = null;
		TreeMap<Integer, String> keyRange = null;
		
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			
			int[] eventsIWant = {126, 127};
			sequencer.addControllerEventListener(this, eventsIWant);
			
			// Menubar 컨트롤로 로드할 수 있도록 바꾸자.
			myFile = new File(filePath);
			Sequence seq = MidiSystem.getSequence(myFile);
			Track trackForController = seq.createTrack();
			
	        keyRange = new TreeMap<Integer, String>(new Comparator<Integer>() {
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
			
			int trackNumber = 0;
			
			// find out a key range.
			for (Track track : seq.getTracks()) {
				trackNumber++;
				if (trackNumber == 2) {
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
						}
					}
				}
			}
			
			System.out.println("총 키 갯수 : " + keyRange.size());
			System.out.println(keyRange);
			
			firstKeyIndex = findOutFirstKeyIndex(keyRange.firstEntry().getValue());
			
			// 한 런치패드로 7옥타브 + 2키까지 표현 가능 
			if ((keyRange.lastKey() - keyRange.firstKey())/NOTE_NAMES.length > 7) {
				System.out.println("Keys are out of range!");
			}
			
			trackNumber = 0;
			
			for (Track track : seq.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				
				if (trackNumber == 2) {
					for (int i = 0; i < track.size(); i++) {
						MidiEvent event = track.get(i);
						long tick = event.getTick();
//						System.out.print("@" + tick + " ");
						MidiMessage message = event.getMessage();
						
						if (message instanceof ShortMessage) {
							ShortMessage sm = (ShortMessage) message;
							int channel = sm.getChannel();
//							System.out.print("Channel: " + channel + " ");
							if (sm.getCommand() == NOTE_ON) {
								// bests : 9, 14, 114, 121
								track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 0, 9, 0, tick));
								int key = sm.getData1();
								int octave = (key / OCTAVE) - 1;
								int note = key % OCTAVE;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
//								System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
								
								trackForController.add(makeEvent(176, channel, NOTE_ON_EVENT, key-keyRange.firstKey()+firstKeyIndex, tick));
								trackForController.add(makeEvent(176, channel, NOTE_OFF_EVENT, key-keyRange.firstKey()+firstKeyIndex, tick+(velocity*100)));
							} else if (sm.getCommand() == NOTE_OFF) {
								int key = sm.getData1();
								int octave = (key / 12) - 1;
								int note = key % 12;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
//								System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
							} else {
//								System.out.println("command: " + sm.getCommand());
							}
						} else {
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

	@Override
	public void controlChange(ShortMessage event) {
//		System.out.println("data1 : " + event.getData1() + " data2 : " + event.getData2());
		if (event.getData1() == NOTE_ON_EVENT) {
			int key = event.getData2();
			int remain = key/NOTE_NAMES.length;
			Color color;
			switch (remain) {
			case 0:
				color = Color.PURPLE;
				break;
			case 1:
				color = Color.AQUAMARINE;
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
			rc.launchpad.get(key).setFill(color);
		}
		else if (event.getData1() == NOTE_OFF_EVENT) {
			rc.launchpad.get(event.getData2()).setFill(Color.WHITE);
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
	
	public int findOutFirstKeyIndex(String key) {
		for (int i = 0; i < NOTE_NAMES.length; ++i) {
			if (NOTE_NAMES[i].equals(key))
				return i;
		}
		return -1;
	}
}
