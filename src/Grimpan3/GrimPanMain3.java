package Grimpan3;

import java.awt.EventQueue;

public class GrimPanMain3 {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrimPanFrame3 frame = new GrimPanFrame3();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
