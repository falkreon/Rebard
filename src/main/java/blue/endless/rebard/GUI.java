package blue.endless.rebard;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import blue.endless.rebard.score.Score;

public class GUI {
	//Thread playingThread;
	//Thread countdownThread;
	Score activeScore;
	SequencerThread activeThread;
	
	JFrame frame = new JFrame();
	
	int stopPlayback = KeyEvent.VK_ESCAPE;
	
	private AbstractAction openFileAction = new AbstractAction() {
		private static final long serialVersionUID = -6434724902527515453L;
		
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
	

	public GUI() {

		JPanel buttonPanel;
		JButton btPlayButton;
		JButton btStopButton; // Button to stop the performance
		//JTextArea taText;
		//JScrollPane spText;

		JPanel settingsPanel;
		JLabel lbCd;
		JSpinner spnCd;
		JLabel lbFps;
		JSpinner spnFpsSpinner;
		JLabel lbDelayLabel;
		JSpinner spnDelaySpinner;
		JCheckBox keyboardCheckBox;
		JCheckBox loopCheckBox;
		JLabel lbLabel5;

		buttonPanel = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		buttonPanel.setLayout( gbPanel0 );

		btPlayButton = new JButton( "Play"  );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 18;
		gbcPanel0.gridwidth = 10;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btPlayButton, gbcPanel0 );
		buttonPanel.add( btPlayButton );

		btStopButton = new JButton( "Stop"  );
		gbcPanel0.gridx = 10;
		gbcPanel0.gridy = 18;
		gbcPanel0.gridwidth = 10;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btStopButton, gbcPanel0 );
		buttonPanel.add( btStopButton );

		/*
		taText = new JTextArea(2,10);

		spText = new JScrollPane( taText );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 6;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 12;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( spText, gbcPanel0 );
		spText.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		pnPanel0.add( spText );*/

		settingsPanel = new JPanel();
		settingsPanel.setBorder( BorderFactory.createTitledBorder( "Settings" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		settingsPanel.setLayout( gbPanel1 );

		lbCd = new JLabel( "WaitMultiplier:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbCd, gbcPanel1 );
		settingsPanel.add( lbCd );


		SpinnerNumberModel m = new SpinnerNumberModel(1.0, 0, 100.0, 0.1);
		spnCd = new JSpinner(m);
		gbcPanel1.gridx = 4;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( spnCd, gbcPanel1 );
		settingsPanel.add( spnCd );

		lbFps = new JLabel( "   Min FPS (Delay=0):"  );
		gbcPanel1.gridx = 8;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbFps, gbcPanel1 );
		settingsPanel.add( lbFps );

		spnFpsSpinner = new JSpinner( );
		gbcPanel1.gridx = 12;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 3;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( spnFpsSpinner, gbcPanel1 );
		settingsPanel.add( spnFpsSpinner );

		lbDelayLabel = new JLabel( "   Start delay:"  );
		gbcPanel1.gridx = 15;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 2;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbDelayLabel, gbcPanel1 );
		settingsPanel.add( lbDelayLabel );

		spnDelaySpinner = new JSpinner( );
		gbcPanel1.gridx = 17;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 3;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( spnDelaySpinner, gbcPanel1 );
		settingsPanel.add( spnDelaySpinner );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 6;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( settingsPanel, gbcPanel0 );
		buttonPanel.add( settingsPanel );


		//spnDelaySpinner.setValue(3);
		spnDelaySpinner.setValue(3);
		if((int)spnDelaySpinner.getValue() <= 0) spnDelaySpinner.setValue(3);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnCd,"0.00"); 
		spnCd.setEditor(editor);
		editor = new JSpinner.NumberEditor(spnDelaySpinner, "#"); 
		spnDelaySpinner.setEditor(editor);
		//spnCd.setValue(1.0);
		spnCd.setValue(1.0);
		if((double)spnCd.getValue() <= 0) spnCd.setValue((double)1);

		/*
		lbLabel4 = new JLabel( "Octave Target:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel4, gbcPanel1 );
		pnPanel1.add( lbLabel4 );
		
		gbcPanel1.gridx = 4;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 6;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;*/

		loopCheckBox = new JCheckBox( "Loop"  );
		gbcPanel1.gridx = 18;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( loopCheckBox, gbcPanel1 );
		settingsPanel.add( loopCheckBox );
		loopCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//Settings.SaveBool("loop", loopCheckBox.isSelected());
			}
		});
		loopCheckBox.setSelected(false);
		
		/*
		trueTimingsCheckBox = new JCheckBox( "True Timings"  );
		gbcPanel1.gridx = 20;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( trueTimingsCheckBox, gbcPanel1 );
		pnPanel1.add( trueTimingsCheckBox );
		trueTimingsCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Settings.SaveBool("truetimings", trueTimingsCheckBox.isSelected());
			}
		});
		trueTimingsCheckBox.setSelected(Settings.LoadBool("truetimings"));*/
		
		keyboardCheckBox = new JCheckBox( "Use full keyboard layout"  );
		keyboardCheckBox.setSelected(false);
		gbcPanel1.gridx = 10;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 0;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( keyboardCheckBox, gbcPanel1 );
		settingsPanel.add( keyboardCheckBox );
		keyboardCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
			}
		});
		keyboardCheckBox.setSelected(true);
		/*
		lbLabel6 = new JLabel("<html><a href=\"" + Keyboard.IMG + "\">[?]</a></html>");
		lbLabel6.setCursor(new Cursor(Cursor.HAND_CURSOR));
		gbcPanel1.gridx = 11;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 0;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.SOUTH;
		gbPanel1.setConstraints( lbLabel6, gbcPanel1 );
		pnPanel1.add( lbLabel6 );
		lbLabel6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					JFrame keyFrame = new JFrame();
					JPanel panel = new JPanel(); 
					panel.setSize(500,640);
					//panel.setBackground(Color.CYAN); 
					ImageIcon icon = new ImageIcon(new ImageIcon(Main.class.getResource("/keyboardLayout.png")).getImage()); 
					JLabel label = new JLabel(); 
					label.setIcon(icon); 
					panel.add(label);
					keyFrame.add(panel);
					keyFrame.setAlwaysOnTop(true);
					keyFrame.setSize(icon.getIconWidth()+25, icon.getIconHeight()+50);
					keyFrame.setTitle("TBbard - Full keyboard layout");
					keyFrame.setIconImage(new ImageIcon(Main.class.getResource("/icon.png")).getImage());
					keyFrame.setVisible(true);
				} catch (Exception ex) {
					//It looks like there's a problem
				}
			}
		});*/
		/*
		holdCheckBox = new JCheckBox( "Hold long notes"  );
		holdCheckBox.setSelected(true);
		gbcPanel1.gridx = 14;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 4;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( holdCheckBox, gbcPanel1 );
		pnPanel1.add( holdCheckBox );
		holdCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Settings.SaveBool("hold", holdCheckBox.isSelected());
			}
		});
		holdCheckBox.setSelected(Settings.LoadBool("hold"));*/

		gbcPanel1.gridx = 3;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 16;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;

		lbLabel5 = new JLabel( "Instrument:"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 3;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel5, gbcPanel1 );
		settingsPanel.add( lbLabel5 );

		JButton openBtn = new JButton("Open");
		gbcPanel1.gridx = 17;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 18;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( openBtn, gbcPanel1 );
		settingsPanel.add( openBtn );
		openBtn.setAction(openFileAction);
		
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

		btPlayButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeThread==null) {
					activeThread = new SequencerThread();
					activeThread.play(activeScore.getSequence(0), 3, new IngameSynth());
					//btPlayButton.setText("Pause");
				} else {
					if (btPlayButton.getText().equals("Play")) {
						activeThread.setPaused(false);
						btPlayButton.setText("Pause");
					} else {
						activeThread.setPaused(true);
						btPlayButton.setText("Play");
					}
				}
				/*
				//taText.requestFocusInWindow();
				if(countdownThread != null && countdownThread.isAlive()) {
					return;
				}

				if(btPlayButton.getText().equals("Playing")) {
					playingThread.suspend();
					btPlayButton.setText("Paused");
					return;
				} else
					if(btPlayButton.getText().equals("Paused")) {
						countdownThread = new Thread() { // Making a new thread instead of sleeping the old one
							@Override
							public void run() {
								try {
									double countdown = Math.ceil(((int)spnDelaySpinner.getValue()));
									while(countdown > 0){
										System.out.println("Countdown (ms): " + countdown);
										btPlayButton.setText((int)countdown + "...");
										Thread.sleep(1000);
										countdown--;
									}
								} catch(Exception e) {
									
								}
								playingThread.resume();
								btPlayButton.setText("Playing");
							}
						};
						countdownThread.start();

						
						return;
					}

				playingThread = new Thread() { // Making a new thread instead of sleeping the old one
					@Override
					public void run() {
						try {
							Settings.SaveInt("fps", (int)spnFpsSpinner.getValue());
							Settings.SaveInt("delay", (int)spnDelaySpinner.getValue());
							Settings.SaveDouble("waitmult", (double)spnCd.getValue());
							Notes n = new Notes((int) spnFpsSpinner.getValue());
							//n.running = true;
							//n.holdNotes = holdCheckBox.isSelected();
							//n.fullKeyboard = keyboardCheckBox.isSelected();
							//n.waitMultiplier = (double)spnCd.getValue();
							//n.slowdownConstant = (int) Math.ceil((double) 1000/(int)spnFpsSpinner.getValue());
							double countdown = Math.ceil(((int)spnDelaySpinner.getValue()));
							countdownThread = this;
							while(countdown > 0){
								//if(n.running == false){
								//	System.out.println("Stopping countdown.");
								//	btPlayButton.setText("Play");
								//	return;
								//}
								System.out.println("Countdown (ms): " + countdown);
								btPlayButton.setText((int)countdown + "...");
								Thread.sleep(1000);
								countdown--;
							}
							countdownThread = null;
							btPlayButton.setText("Playing");



							do{
								//Notes.waitMultiplier = (double) spnCd.getValue();

								int charsProcessed = 0;

								Thread.sleep(1000); //This lets the game's music buffer catch up if the looping song has a very high tempo

							} while (loopCheckBox.isSelected());
							btPlayButton.setText("Play");
							n.releaseHeldKey();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//n.releaseHeldKey();	
					}
				};*/
				//playingThread.start();// We're using a new thread to be able to access Stop still.
				//taText.requestFocusInWindow();
			}
		});

		/**
		 * Action Listener for the stop button
		 */
		/*
		btStopButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				//n.running = false;
				//taText.requestFocusInWindow();
				if (playingThread != null){
					playingThread.stop();
				}
				if (countdownThread != null){
					countdownThread.stop();
				}

				//btPlayButton.setText("Play");
				//taText.requestFocusInWindow();
				//n.releaseHeldKey();
			}
		});*/
		btStopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeThread!=null) {
					activeThread.stopPlaying();
					activeThread = null;
				}
			}
			
		});

		try {
			frame.setIconImage(new ImageIcon(Main.class.getResource("/icon.png")).getImage());
		} catch (Exception e1) {
			System.out.println("Icon not found.");
			e1.printStackTrace();
		}
		frame.add(buttonPanel);
		frame.setSize(675, 500);
		frame.setTitle("Rebard");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);	
		frame.requestFocusInWindow();
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
		if (f==null || score==null) {
			frame.setTitle("Rebard");
		} else {
			this.activeScore = score;
			frame.setTitle("Rebard - "+f.getName());
		}
	}
}
