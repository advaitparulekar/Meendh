import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Image;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.border.TitledBorder;

/**
 * Audio tone generator, using the Java sampled sound API.
 * 
 * @author andrew Thompson
 * @version 2007/12/6
 */
public class Tone extends JFrame implements Runnable {

	static AudioFormat af;
	static SourceDataLine sdl;
	private Thread my_thread;
	final JSlider sTone = new JSlider(JSlider.VERTICAL, 200, 2000, 441);
	final JSlider sDuration = new JSlider(JSlider.VERTICAL, 0, 2000, 1000);
	final JSlider sVolume = new JSlider(JSlider.VERTICAL, 0, 100, 20);
	JButton bGenerate = new JButton("Generate Tone");

	public Tone() {
		super("Audio Tone");
		// Use current OS look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.err.println("Internal Look And Feel Setting Error.");
			System.err.println(e);
		}

		JPanel pMain = new JPanel(new BorderLayout());

		sTone.setPaintLabels(true);
		sTone.setPaintTicks(true);
		sTone.setMajorTickSpacing(200);
		sTone.setMinorTickSpacing(100);
		sTone.setToolTipText("Tone (in Hertz or cycles per second - middle C is 441 Hz)");
		sTone.setBorder(new TitledBorder("Frequency"));
		pMain.add(sTone, BorderLayout.CENTER);

		sDuration.setPaintLabels(true);
		sDuration.setPaintTicks(true);
		sDuration.setMajorTickSpacing(200);
		sDuration.setMinorTickSpacing(100);
		sDuration.setToolTipText("Duration in milliseconds");
		sDuration.setBorder(new TitledBorder("Length"));
		pMain.add(sDuration, BorderLayout.EAST);

		sVolume.setPaintLabels(true);
		sVolume.setPaintTicks(true);
		sVolume.setSnapToTicks(false);
		sVolume.setMajorTickSpacing(20);
		sVolume.setMinorTickSpacing(10);
		sVolume.setToolTipText("Volume 0 - none, 100 - full");
		sVolume.setBorder(new TitledBorder("Volume"));
		pMain.add(sVolume, BorderLayout.WEST);

		bGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				start();
			}
		});

		JPanel pNorth = new JPanel(new BorderLayout());
		pNorth.add(bGenerate, BorderLayout.WEST);

		pMain.add(pNorth, BorderLayout.NORTH);
		pMain.setBorder(new javax.swing.border.EmptyBorder(5, 3, 5, 3));

		getContentPane().add(pMain);
		pack();
		setLocation(0, 20);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public void run() {
		try {
			while (true) {
				generateTone(sTone.getValue(),
						(int) (sVolume.getValue() * 1.28));
			}
		} catch (LineUnavailableException lue) {
			System.out.println(lue);
		}
	}

	public void start() {
		my_thread = new Thread(this, "my_thread");
		my_thread.start();
	}

	/**
	 * Generates a tone.
	 * 
	 * @param hz
	 *            Base frequency (neglecting harmonic) of the tone in cycles per
	 *            second
	 * @param msecs
	 *            The number of milliseconds to play the tone.
	 * @param volume
	 *            Volume, form 0 (mute) to 100 (max).
	 * @param addHarmonic
	 *            Whether to add an harmonic, one octave up.
	 */
	public static void generateTone(int hz, int volume)
			throws LineUnavailableException {

		float frequency = 44100;
		byte[] buf;
		AudioFormat af;
		buf = new byte[1];
		af = new AudioFormat(frequency, 8, 1, true, false);
		SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		sdl = AudioSystem.getSourceDataLine(af);
		sdl.open(af);
		sdl.start();
		int i = 1;
		while(i > 0) {
			double angle = i / (frequency / hz) * 2.0 * Math.PI;
			buf[0] = (byte) ((Math.sin(angle) + Math.sin(angle * 2)
					+ Math.sin(angle) + 0.2 * Math.sin(angle * 3) + 0.3
					* Math.sin(angle) + Math.sin(angle * 4)) * (Math.pow(Math.E,volume*(1-volume))-1));
			sdl.write(buf, 0, 1);
			i++;
		}
		sdl.drain();
		sdl.stop();
		sdl.close();
	}

	public static void main(String[] args) {
		//Runnable r = new Runnable() {
			//public void run() {
				Tone t = new Tone();
				t.setVisible(true);
			//}
		//};
		//SwingUtilities.invokeLater(r);
	}
}