package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.gui;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import javax.swing.JFrame;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster.MemoryAdjusterFactory;

public class Launcher {
	public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
		int memorySize = 103;
		try {
			memorySize = Integer.parseInt(args[0]);
		} catch(ArrayIndexOutOfBoundsException | NumberFormatException ex) {}
		
		JFrame frame = new JFrame("Memory manager");
		final MemoryManagerPanel boardPanel = new MemoryManagerPanel(
				new MemoryManager(
					memorySize,
					MemoryAdjusterFactory.createFirstMemoryAdjuster()
				)
			);
		frame.add(boardPanel);
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
