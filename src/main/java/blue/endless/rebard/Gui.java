package blue.endless.rebard;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import blue.endless.rebard.gui.ColorTheme;
import blue.endless.rebard.gui.LinearPanel;
import blue.endless.rebard.gui.ScoreView;
import blue.endless.rebard.gui.SimpleLabel;
import blue.endless.rebard.score.Score;

public class Gui {
	private static final int REPAINT_DELAY = 300;
	private static String fontName = null;
	
	//Thread playingThread;
	//Thread countdownThread;
	Score activeScore;
	SequencerThread activeThread;
	
	JFrame frame = new JFrame() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (activeThread!=null && activeThread.isAlive() && !activeThread.isPaused() && !activeThread.isStopped()) {
				scoreView.setTimestamp(activeThread.getTimestamp());
				//this.repaint(REPAINT_DELAY);
			}
		}
	};
	JButton fileIconButton = new JButton();
	//JLabel fileLabel = new JLabel("");
	SimpleLabel fileLabel = new SimpleLabel();
	ScoreView scoreView = new ScoreView();
	
	Image playImage;
	Image noFileImage;
	Image midiFileImage;
	
	int stopPlayback = KeyEvent.VK_ESCAPE;
	
	private AbstractAction openFileAction = new AbstractAction() {
		private static final long serialVersionUID = 42L; // Secure random number; chosen by fair diceroll
		
		@Override
		public Object getValue(String key) {
			if (key==Action.NAME) return "Open...";
			
			return super.getValue(key);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "MIDI Files (.mid)";
				}

				@Override
				public boolean accept(File f) {
					if(f.getName().toLowerCase().matches(".+\\.mid") || f.isDirectory()) return true;
					return false;
				}
			});
			int selection = fc.showOpenDialog(frame);
			if(selection == JFileChooser.CANCEL_OPTION) return;

			File selectedFile = fc.getSelectedFile();
			
			loadScore(selectedFile);
		}
	};
	
	private SimpleAction closeFileAction = new SimpleAction("Close", (evt)-> {
		setActiveScore(null, null);
	});
	/*
	private AbstractAction closeFileAction = new AbstractAction() {
		private static final long serialVersionUID = 42L; // Secure random number; chosen by fair diceroll
		
		@Override
		public Object getValue(String key) {
			if (key==Action.NAME) return "Close";
			
			return super.getValue(key);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (activeThread!=null) {
				activeThread.stopPlaying();
				activeThread = null;
				activeScore = null;
				frame.setTitle("Rebard");
				frame.repaint();
			}
		}
	};*/
	
	private AbstractAction playAction = new SimpleAction("Play", (evt)->{
		
	});

	public Gui() {
		ColorTheme defaultTheme = new ColorTheme();
		defaultTheme.panelBackground = (0xFF_303030);
		
		noFileImage = loadImage("/mime/none.png", 64);
		midiFileImage = loadImage("/mime/audio_midi.png", 64);
		
		frame.setSize(800, 600);
		frame.setTitle("Rebard");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.getContentPane().setBackground(new Color(20, 20, 20));
		
		JMenuBar menus = frame.getJMenuBar();
		if (menus==null) {
			menus = new JMenuBar();
			frame.setJMenuBar(menus);
		}
		
		menus.setBackground(ColorTheme.argb(defaultTheme.menuBackground));
		menus.setForeground(ColorTheme.argb(defaultTheme.menuText));
		menus.setBorder(null);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setForeground(ColorTheme.argb(defaultTheme.panelText));
		fileMenu.add(openFileAction);
		fileMenu.add(closeFileAction);
		menus.add(fileMenu);
		
		
		
		
		LinearPanel playlistPanel = new LinearPanel(LinearPanel.VERTICAL);
		playlistPanel.setBorderThickness(6);
		playlistPanel.setTheme(defaultTheme);
		playlistPanel.add(Box.createHorizontalStrut(40));
		//frame.getContentPane().add(playlistPanel);
		
		LinearPanel focusPanel = new LinearPanel(LinearPanel.VERTICAL);
		frame.getContentPane().add(focusPanel);
		
		LinearPanel filePanel = new LinearPanel(LinearPanel.HORIZONTAL);
		filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		
		focusPanel.add(filePanel);
		focusPanel.add(scoreView);
		focusPanel.add(Box.createVerticalGlue());
		
		
		//BufferedImage missingFileImage;
		//try {
		//	missingFileImage = ImageIO.read(Gui.class.getResourceAsStream("/mime/none.png"));
		//} catch (Exception e2) {
		//	e2.printStackTrace();
		//	missingFileImage = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		//}
		
		
		fileIconButton.setIcon(new ImageIcon(noFileImage));
		fileIconButton.setBackground(new Color(0,0,0,0));
		fileIconButton.setBorder(null);
		fileIconButton.setFocusable(false);
		fileIconButton.setRequestFocusEnabled(false);
		fileIconButton.setFocusPainted(false);
		fileIconButton.addActionListener((evt)->{
			frame.repaint();
		});
		filePanel.add(fileIconButton);
		filePanel.add(Box.createHorizontalStrut(16));
		
		fileLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
		
		filePanel.add(fileLabel);
		//filePanel.setBackground(new Color(210, 255, 210));
		
		
		//filePanel.add(Box.createHorizontalGlue());
		
		Image playSpeakerImage = loadImage("/assets/play_speaker.png", -1);
		Image playKeyboardImage = loadImage("/assets/play_keyboard.png", -1);
		/*
		try {
			playSpeakerImage = ImageIO.read(Gui.class.getResourceAsStream("/assets/play.png"));
		} catch (Exception e2) {
			e2.printStackTrace();
			playSpeakerImage = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		}*/
		JButton playButton = new JButton(new ImageIcon(playKeyboardImage));
		playButton.createToolTip().setTipText("Play using Keyboard");
		playButton.addActionListener((evt)->{
			if (activeScore==null) return;
			if (activeThread==null) {
				activeThread = new SequencerThread();
				activeThread.onUpdate(this::onPlayerUpdate);
				activeThread.play(activeScore.getSequence(0), 3, new IngameSynth());
				//frame.repaint(REPAINT_DELAY);
			} else if (activeThread.isPaused()) {
				activeThread.unpause();
				//frame.repaint(REPAINT_DELAY);
			} else {
				activeThread.pause();
			}
		});
		playButton.setBorder(null);
		playButton.setBackground(new Color(0,0,0,0));
		filePanel.add(playButton);
		
		JButton previewButton = new JButton(new ImageIcon(playSpeakerImage));
		previewButton.createToolTip().setTipText("Preview in General Midi");
		previewButton.addActionListener((evt)->{
			if (activeScore==null) return;
			if (activeThread==null) {
				activeThread = new SequencerThread();
				activeThread.onUpdate(this::onPlayerUpdate);
				activeThread.play(activeScore.getSequence(0), 0, new GeneralMidiSynth());
				//frame.repaint(REPAINT_DELAY);
			} else if (activeThread.isPaused()) {
				activeThread.unpause();
				//frame.repaint(REPAINT_DELAY);
			} else {
				activeThread.pause();
			}
		});
		previewButton.setBorder(null);
		previewButton.setBackground(new Color(0,0,0,0));
		filePanel.add(previewButton);
		
		
		focusPanel.setTheme(defaultTheme);
		frame.pack();
		final int _maxHeight = frame.getHeight();
		
		frame.addComponentListener(new ComponentListener() {
			private int maxHeight = _maxHeight;
			@Override
			public void componentResized(ComponentEvent e) {
				frame.setSize(frame.getWidth(), Math.max(frame.getHeight(), maxHeight)); //Might crash
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
			
		});
		
		frame.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					
					if (droppedFiles.size()==0) return; //Not likely but you know how computers are.
					
					File selectedFile = droppedFiles.get(0);
					
					loadScore(selectedFile);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
		if (!visible) {
			if (activeThread!=null) {
				activeThread.stopPlaying();
				activeThread = null;
			}
			activeScore = null;
			frame.setTitle("Rebard");
		}
	}
	
	public void loadScore(File f) {
		frame.setTitle("Processing...");
		MidiParser midi = new MidiParser();
		
		try {
			setActiveScore(f, midi.getScore(f));
		} catch (Exception e1) {
			e1.printStackTrace();
			setActiveScore(null, null);
		}
	}
	
	public void setActiveScore(File f, Score score) {
		//Stop any current playback
		if (activeThread!=null) {
			activeThread.stopPlaying();
			activeThread = null;
		}
		
		if (f==null || score==null) {
			this.activeScore = null;
			fileIconButton.setIcon(new ImageIcon(noFileImage));
			fileLabel.setText("");
			frame.setTitle("Rebard");
			scoreView.setSequence(null);
		} else {
			this.activeScore = score;
			fileIconButton.setIcon(new ImageIcon(midiFileImage));
			fileLabel.setText(f.getName());
			frame.setTitle("Rebard - "+f.getName());
			scoreView.setSequence(score.getSequence(0));
		}
	}
	
	public static Image loadImage(String resource, int scaledHeight) {
		BufferedImage result;
		try {
			result = ImageIO.read(Gui.class.getResourceAsStream(resource));
		} catch (Exception e2) {
			e2.printStackTrace();
			BufferedImage errImage = new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB);
			errImage.setRGB(0, 0, 0xFF_000000);
			errImage.setRGB(1, 1, 0xFF_000000);
			errImage.setRGB(1, 0, 0xFF_FF00FF);
			errImage.setRGB(0, 1, 0xFF_FF00FF);
			result = errImage;
		}
		
		if (scaledHeight==-1) {
			return result;
		} else {
			double aspectRatio = result.getWidth() / (double) result.getHeight();
			return result.getScaledInstance((int)(scaledHeight*aspectRatio), scaledHeight, Image.SCALE_DEFAULT);
		}
	}
	
	/* Used here under LGPL from java2s.com - changes have been made */
	public static Image getScaledIcon(Icon icon, int scaledHeight) {
		double aspectRatio = icon.getIconWidth() / (double)icon.getIconHeight();
		BufferedImage buffer = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffer.getGraphics();
		icon.paintIcon(new JLabel(), g, 0, 0);
		g.dispose();
		return buffer.getScaledInstance((int)(scaledHeight*aspectRatio), scaledHeight, Image.SCALE_DEFAULT);
	}
	
	public static String getFancyFontName() {
		if (fontName!=null) return fontName;
		for(String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			System.out.println(s);
		}
		
		
		fontName = Font.DIALOG;
		return fontName;
	}
	
	private static final long MILLIS_PER_DRAW = (long) ((1.0 / 60.0) * 1_000.0);
	private long millisSinceLastDraw = 0;
	
	private void onPlayerUpdate(long elapsed) {
		millisSinceLastDraw += elapsed;
		if (millisSinceLastDraw>MILLIS_PER_DRAW) {
			millisSinceLastDraw = millisSinceLastDraw % MILLIS_PER_DRAW; //We don't care about any whole frames we missed
			
			frame.repaint();
		}
	}
}
