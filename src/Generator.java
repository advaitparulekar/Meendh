import java.awt.MouseInfo;

import javax.sound.sampled.*;

public class Generator{
	static AudioFormat af;
	static SourceDataLine sdl;
	volatile static int Mouse_Position;

	static class Noise_Maker implements Runnable {

		
		public void makeNoise() throws LineUnavailableException {
			int volume = 100;
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
			double[] amplitudes = {2, 0.3, 1, 0.1, 0.25, 0.3, 0.28};
			while (i > 0) {
				System.out.println(Mouse_Position);
				double angle = i / (frequency / Mouse_Position) * 2.0 * Math.PI;
				buf[0] = (byte) (fromFourier(angle, amplitudes)*100);
				sdl.write(buf, 0, 1);
				i++;
			}
			sdl.drain();
			sdl.stop();
			sdl.close();
		}

		@Override
		public void run() {
			try {
				makeNoise();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	static class Mouse_Hearer implements Runnable {
		@Override
		public void run() {
			while (true) {
				Mouse_Position = MouseInfo.getPointerInfo().getLocation().x;
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public static void main(String[] args) {
		Thread Noise_Thread = new Thread(new Noise_Maker(), "Noise Thread");
		Noise_Thread.start();
		Thread Mouse_Tracker = new Thread(new Mouse_Hearer(), "Mouse Thread");
		Mouse_Tracker.start();
	}
	
	public static double fromFourier(double angle, double[] amplitudes) {
		double ans = 0;
		for (int i = 0; i < amplitudes.length; i++) {
			ans = (ans + Math.sin((i+1)*angle)*amplitudes[i]);
		}
		return ans;
	}
}
