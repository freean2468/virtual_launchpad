package application;

import javax.sound.midi.*;

public class MusicPlayer implements ControllerEventListener{
	RootController rc;
	
	public MusicPlayer(RootController _rc) {
		rc = _rc;
	}
	
	public void go() {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			
			int[] eventsIWant = {127};
			sequencer.addControllerEventListener(this, eventsIWant);
			
			Sequence seq = new Sequence(Sequence.PPQ, 4);
			Track track = seq.createTrack();
			
			int r = 0;
			
			for (int i = 5; i < 60; i+= 4) {
				r = (int)((Math.random()*50)+1);
				track.add(makeEvent(144, 1, r, 100, i));
				track.add(makeEvent(176, 1, 127, r, i));
				track.add(makeEvent(128, 1, r, 100, i+2));
			}
			
			sequencer.setSequence(seq);
			sequencer.setTempoInBPM(220);
			sequencer.start();
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	@Override
	public void controlChange(ShortMessage event) {
		System.out.println("la ~ data2 : " + event.getData2());
		rc.handleBtn11Action();
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
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
}
