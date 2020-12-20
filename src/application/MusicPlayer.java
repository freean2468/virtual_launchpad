package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.sound.midi.*;

public class MusicPlayer implements ControllerEventListener{
	public static final int NOTE_ON_EVENT = 126;
	public static final int NOTE_OFF_EVENT = 127;
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static final int OCTAVE = 12;
	
	private RootController rc;
	
	public MusicPlayer(RootController _rc) {
		rc = _rc;
	}
	
	public void go() {
		File myFile = null;
		TreeMap<Integer, String> keyRange = null;
		
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			
			int[] eventsIWant = {126, 127};
			sequencer.addControllerEventListener(this, eventsIWant);
			
			// making a sequence and a track directly
//			Sequence seq = new Sequence(Sequence.PPQ, 4);
//			Track track = seq.createTrack();
//			
//			int r = 0;
//			
//			for (int i = 5; i < 60; i+= 4) {
//				r = (int)((Math.random()*50)+1);
//				track.add(makeEvent(144, 1, r, 100, i));
//				track.add(makeEvent(176, 1, 127, r, i));	// register controller event
//				track.add(makeEvent(128, 1, r, 100, i+2));
//			}
//			
//			sequencer.setSequence(seq);
//			sequencer.setTempoInBPM(220);
//			sequencer.start();
			
			myFile = new File("./mid/gymnopedie_1.mid");
			Sequence seq = MidiSystem.getSequence(myFile);
			Track trackForController = seq.createTrack();
			
			HashMap<Integer, String> map = new HashMap<Integer, String>();
	        KeyComparator bvc = new KeyComparator(map);
	        keyRange = new TreeMap<Integer, String>(bvc);
			
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
								if (!map.containsKey(key)) {
									map.put(key, noteName);
								}
							}
						}
					}
				}
			}
			
			keyRange.putAll(map);
			System.out.println("총 키 갯수 : " + keyRange.size());
			System.out.println(keyRange);
			
			trackNumber = 0;
			
			for (Track track : seq.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				
				if (trackNumber == 2) {
					for (int i = 0; i < track.size(); i++) {
						MidiEvent event = track.get(i);
						long tick = event.getTick();
						System.out.print("@" + tick + " ");
						MidiMessage message = event.getMessage();
						
						if (message instanceof ShortMessage) {
							ShortMessage sm = (ShortMessage) message;
							int channel = sm.getChannel();
							System.out.print("Channel: " + channel + " ");
							if (sm.getCommand() == NOTE_ON) {
								// bests : 9, 14, 114, 121
								track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 0, 9, 0, tick));
								int key = sm.getData1();
								int octave = (key / OCTAVE) - 1;
								int note = key % OCTAVE;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
								System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
								
								trackForController.add(makeEvent(176, channel, NOTE_ON_EVENT, key-keyRange.firstKey(), tick));
								trackForController.add(makeEvent(176, channel, NOTE_OFF_EVENT, key-keyRange.firstKey(), tick+(velocity*100)));
							} else if (sm.getCommand() == NOTE_OFF) {
								int key = sm.getData1();
								int octave = (key / 12) - 1;
								int note = key % 12;
								String noteName = NOTE_NAMES[note];
								int velocity = sm.getData2();
								System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
							} else {
								System.out.println("command: " + sm.getCommand());
							}
						} else {
							System.out.println("Other message: " + message.getClass());
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
		if (event.getData1() == NOTE_ON_EVENT)
			rc.launchpad.get(event.getData2()).setFill(Color.CORAL);
		else if (event.getData1() == NOTE_OFF_EVENT)
			rc.launchpad.get(event.getData2()).setFill(Color.WHITE);
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
	
	class KeyComparator implements Comparator<Integer> {
	    Map<Integer, String> base;

	    public KeyComparator(Map<Integer, String> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(Integer a, Integer b) {
	        if (a <= b) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
}
