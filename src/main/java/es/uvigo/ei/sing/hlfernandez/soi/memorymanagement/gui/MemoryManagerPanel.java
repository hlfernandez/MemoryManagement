package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryBlock;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster.MemoryAdjuster;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster.MemoryAdjusterFactory;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.demo.MemoryManagementDemo;

public class MemoryManagerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JComboBox<AdjustType> adjustStrategies;
	private MemoryManagerCanvas memoryCanvas;
	private MemoryManager manager;
	private JLabel statusLabel;

	public MemoryManagerPanel(MemoryManager manager) {
		super();
		this.manager = manager;
		this.manager.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				statusLabel.setText(getStatusLabel());
				memoryCanvas.repaint();
			}
		});
		init();
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.add(getToolbar(), 		BorderLayout.NORTH);
		this.add(getMemoryCanvas(), BorderLayout.CENTER);
		this.add(getSouthPanel(), 	BorderLayout.SOUTH);
	}

	private Component getSouthPanel() {
		this.statusLabel = new JLabel(getStatusLabel());
		JPanel toret = new JPanel(new GridLayout(1, 1));
		Dimension d = new Dimension(Integer.MAX_VALUE, 20);
		toret.setMaximumSize(d);
		toret.setSize(d);
		toret.setPreferredSize(d);
		toret.add(this.statusLabel);
		return toret;
	}

	private MemoryManagerCanvas getMemoryCanvas() {
		this.memoryCanvas = new MemoryManagerCanvas(manager);
		return this.memoryCanvas;
	}

	private Component getToolbar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.add(new AbstractAction("Resize") {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				resizeMemoryAction();
			}
		});
		toolbar.add(new AbstractAction("Add block") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				addBlockAction();
			}
		});
		toolbar.add(new AbstractAction("Remove block") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				removeBlockAction();
			}
		});
		toolbar.add(new AbstractAction("Remove all") {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				removeAllBlocksAction();
			}
		});
		toolbar.add(new AbstractAction("Demo configuration") {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				demoConfigurationAction();
			}
		});
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(getAdjustStrategies());
		return toolbar;
	}
	
	private JComboBox<AdjustType> getAdjustStrategies() {
		adjustStrategies = new JComboBox<AdjustType>(
			new AdjustType[]{
				new AdjustType("First", MemoryAdjusterFactory.createFirstMemoryAdjuster()),
				new AdjustType("Next", MemoryAdjusterFactory.createNextMemoryAdjuster()),
				new AdjustType("Best", MemoryAdjusterFactory.createBestMemoryAdjuster()),
				new AdjustType("Worst", MemoryAdjusterFactory.createWorstMemoryAdjuster()),
			}
		);
		adjustStrategies.addItemListener(new ItemListener() {
			 public void itemStateChanged(ItemEvent event) {
			       if (event.getStateChange() == ItemEvent.SELECTED) {
			          memoryAdjustChanged((AdjustType) event.getItem());
			       }
			    }
		});
		return adjustStrategies;
	}
	
	class AdjustType {
		private String name;
		private MemoryAdjuster adjuster;

		public AdjustType(String name, MemoryAdjuster adjuster) {
			this.name = name;
			this.adjuster = adjuster;
		}
		
		public String getName() {
			return name;
		}
		
		public MemoryAdjuster getAdjuster() {
			return adjuster;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private void memoryAdjustChanged(AdjustType adjustType) {
		this.manager.setMemoryAdjuster(adjustType.getAdjuster());
	}   
	
	private void resizeMemoryAction() {
		String size = JOptionPane.showInputDialog(this, "New memory size:" );
		if (size != null) {
			try {
				if(!this.manager.resize(Integer.parseInt(size))) {
					JOptionPane.showMessageDialog(this,
							"New memory size should be greater than " + this.manager.getSize(),
							"Memory problem",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch(NumberFormatException e) {
			}
		}
	}
	
	private void addBlockAction() {
		String size = JOptionPane.showInputDialog(this, "Block size:" );
		if (size != null) {
			try {
				if(!this.manager.createProcess(higherPid() + 1, Integer.parseInt(size))) {
					  JOptionPane.showMessageDialog(this,
						        "Not enought memory",
						        "Memory problem",
						        JOptionPane.ERROR_MESSAGE);
				}
			} catch(NumberFormatException e) {
			}
		}
	}
	
	private int higherPid() {
		return this.manager.getBlocks().stream().mapToInt(MemoryBlock::getPid)
				.max().getAsInt();
	}
	
	private void removeBlockAction() {
		Integer[] pids = getPids();
		if(pids.length > 0) {
		    Integer pid = (Integer) JOptionPane.showInputDialog(this, 
		            "PID to remove: ",
		            "Remove block",
		            JOptionPane.QUESTION_MESSAGE, 
		            null, 
		            pids, 
		            pids[0]);
		    if (pid != null) {
			    	if(!this.manager.destroyProcess(pid)) {
						  JOptionPane.showMessageDialog(this,
							        "Error removing pid " + pid,
							        "Memory problem",
							        JOptionPane.ERROR_MESSAGE);
					}
		    }
		}
	}
	
	private Integer[] getPids() {
		List<Integer> pidsList = this.manager.getBlocks().stream().map(MemoryBlock::getPid).collect(Collectors.toList());
		if(pidsList.contains(new Integer(0))) {
			pidsList.remove(new Integer(0));
		}
		Integer[] pids = pidsList.toArray(new Integer[pidsList.size()]);
		return pids;
	}

	private void removeAllBlocksAction() {
		this.manager.clear();
	}
	
	private void demoConfigurationAction() {
		this.manager.clear();
		MemoryManagementDemo.applyDemoConfiguration(manager);
	}

	class MemoryManagerCanvas extends JPanel {
		private static final long serialVersionUID = 1L;
		private MemoryManager manager;
		private Map<Rectangle2D, MemoryBlock> shapes = new HashMap<Rectangle2D, MemoryBlock>();

		public MemoryManagerCanvas(MemoryManager manager) {
			this.manager = manager;
			this.addMouseListener(new Listener());
			this.addMouseMotionListener(new Listener());
		}
		
		class Listener extends MouseAdapter {
			@Override
			public void mouseMoved(MouseEvent e) {
				MemoryBlock clickedBlock = findBlockAt(e);
				if (clickedBlock != null) {
					statusLabel.setText(getTooltip(clickedBlock));
				} else {
					statusLabel.setText(getStatusLabel());
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				statusLabel.setText(getStatusLabel());
			}
		}

		@Override
		public void paint(Graphics g) {
			this.shapes.clear();
			Graphics2D g2 = (Graphics2D) g;

			int height = this.getHeight() - 1;
			int width = this.getWidth();
			
			double wordHeight = (double) (height / (double) manager.getSize());

			double currentBlockYBase = 0;
			for(MemoryBlock block : manager.getBlocks()) {
				int blockSize = block.getSize();
				Rectangle2D rec = new Rectangle2D.Double(0d, currentBlockYBase, width - 1, wordHeight * blockSize);
				currentBlockYBase += wordHeight * blockSize;
				
				if(block.isFree()) {
					g2.setColor(Color.decode("#edebe8"));
				} else {
					g2.setColor(getBlockColor(block.getPid()));
				}
				g2.fill(rec);
				
				g2.setColor(Color.BLACK);
				g2.draw(rec);
				
				this.shapes.put(rec, block);
			}
		}


		private MemoryBlock findBlockAt(MouseEvent e) {
			for (Rectangle2D rec : shapes.keySet()) {
				if (rec.contains(e.getX(), e.getY())) {
					return shapes.get(rec);
				}
			}
			return null;
		}

		private String getTooltip(MemoryBlock block) {
				StringBuilder sB = new StringBuilder();
				sB
					.append("<html>Status: ")
					.append(block.isFree()?"Free":"Used")
					.append("; ")
					.append("Start: ")
					.append(block.getStart())
					.append("; ")
					.append("Size: ")
					.append(block.getSize())
					.append("; ")
					.append("PID: ")
					.append(block.getPid())
					.append("</html>");
				return sB.toString();
		}
		
	}
	
	private String getStatusLabel() {
		return "Memory size: " + this.manager.getSize();
	}
	
	private static final Color[] COLORS = new Color[] {
		Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
		Color.PINK, Color.MAGENTA, Color.CYAN, Color.YELLOW, 
		Color.decode("#a93d33"), Color.decode("#449458"),
		Color.decode("#ff6600")
	};
	
	private static Color getBlockColor(int pid) {
		return COLORS[pid % COLORS.length];
	}
}
