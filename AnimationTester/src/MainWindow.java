import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainWindow implements ActionListener
{
	public static JFrame mainFrame;
	public static JPanel toolPanel;
	public static JPanel topPanel;
	public static JPanel picPanel;
	public static JPanel layerPanel;
	public static JPanel buttonPanel;
	public static JPanel logPanel;
	
	public static JLabel picLabel;
	public static JScrollPane layerScroll;
	
	public static BufferedImage test;
	public static JFileChooser fileChooser;
	public static JFileChooser fileSaver;
	public static ArrayList<File> fileList;
	public static JList<String> layerList;
	public static DefaultListModel<String> data;
	
	public static JButton openButton;
	public static JButton clearButton;
	
	public static JButton playButton;
	public static JButton pauseButton;
	public static JButton saveButton;
	
	public static JTextField logField;
	public static JTextField speedField;
	public static int speedValue;
	
	public static String logStartingText;
	public static Calendar calendar;
	public static SimpleDateFormat dateFormat;
	
	public static int animCount;
	public static Timer animTimer;
	
	
	public static void main(String[] args)
	{
		fileList = new ArrayList<File>();
		animCount = 0;
		
		createMainWindow();
		createFileChooser();

	}
	
	public static void createMainWindow()
	{
		mainFrame = new JFrame("MiniAnim");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.PAGE_AXIS));		
		mainFrame.setVisible(true);
		//Will this need to go later, or does it just prevent user resizing?
		mainFrame.setResizable(false);
		
		//Adding other pieces
		createToolBar();
		createTopPanel();
		createLogPanel();
		
		//Tidy everything up in the end
		mainFrame.pack();
	}

	public static void createTopPanel()
	{
		//Prepping log so it can print loading errors
		logStartingText = new String("Welcome!");
		
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		//topPanel.setBackground(Color.BLUE);
		topPanel.setSize(new Dimension(100, 100));
		createPicPanel();
		createLayerPanel();
		topPanel.setPreferredSize(topPanel.getPreferredSize());
		topPanel.validate();
		mainFrame.add(topPanel);
	}
	
	public static void createPicPanel()
	{
		picPanel = new JPanel();
		picPanel.setBackground(new Color(0x808080));
		picPanel.setLayout(new GridBagLayout());
		picPanel.setPreferredSize(new Dimension(400, 400));
		try
		{
			test = ImageIO.read(MainWindow.class.getResource("/img/startImage.png"));
		}
		catch (IOException e)
		{
			logStartingText = "Start-up Error: could not find default image";
		}
		picLabel = new JLabel();
		
		//Error-catching in case default image is not found
		if (test != null)
		{
			picLabel.setIcon(new ImageIcon(test));
		}
		picPanel.add(picLabel);
		

		//Add to parent panel
		topPanel.add(picPanel);
		
	}
	
	public static void createLayerPanel()
	{
		layerPanel = new JPanel();
		layerPanel.setBackground(new Color(0x5b7673));
		layerPanel.setPreferredSize(new Dimension(300, 400));
		topPanel.add(layerPanel);
		createLayerList();
	}
	
	private static void createLogPanel()
	{
		//Creating calendar access for time stamping
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		logPanel = new JPanel();
		logField = new JTextField(logStartingText, 70);
		logField.setEditable(false);
		logPanel.add(logField);
		
		//After adding text field
		logPanel.setPreferredSize(logPanel.getPreferredSize());
		//logPanel.setSize(new Dimension(700, 25));
		mainFrame.add(logPanel);
		
	}
	
	public static void createToolBar()
	{
		toolPanel = new JPanel();
		
		playButton = new JButton(new ImageIcon(MainWindow.class.getResource("/img/play.png")));
		playButton.setPreferredSize(new Dimension(28,28));
		playButton.setMargin(new Insets(0, 0, 0, 0));
		pauseButton = new JButton(new ImageIcon(MainWindow.class.getResource("/img/pause.png")));
		pauseButton.setPreferredSize(new Dimension(28,28));
		pauseButton.setMargin(new Insets(0, 0, 0, 0));
		saveButton = new JButton(new ImageIcon(MainWindow.class.getResource("/img/save.png")));
		saveButton.setPreferredSize(new Dimension(28,28));
		saveButton.setMargin(new Insets(0, 0, 0, 0));
		
		speedValue = 200;
		speedField = new JTextField("" + speedValue);
		speedField.setPreferredSize(new Dimension(40, 31));
		speedField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		animTimer = new Timer(speedValue, timer);
		
		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					speedValue = Integer.parseInt(speedField.getText());
				}
				catch (NumberFormatException e1)
				{
					printToLog("Invalid action-- frame delay value must be integer.  Reverting to default");
					speedField.setText("" + speedValue);
				}
				animTimer.setDelay(speedValue);
				animTimer.start();
			}
		}
		);
		
		pauseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				animTimer.stop();
				printToLog("Event-- animation paused");
			}
		}
		);
		
		saveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//if no files loaded, don't try to make a GIF
				if(fileList.size() <= 0)
				{
					printToLog("Invalid action-- there's nothing to save!");
				}
				else
				{
					try
					{
						exportGIF();
					} catch (HeadlessException | IOException e1)
					{
						printToLog("Error-- GIF export failed.  Please report to CritterBucket");
					}
				}
				
			}

			public void exportGIF() throws HeadlessException, FileNotFoundException, IOException
			{
				String outputFileName;
				//Pops up a save menu
				fileSaver = new JFileChooser();
				int returnValue = fileSaver.showSaveDialog(null);
				
				//Actually does the saving part, assuming user hit save
				if (returnValue == JFileChooser.APPROVE_OPTION)
				{
					
					outputFileName = fileSaver.getSelectedFile() + ".gif";						
					
					//Using GifSequenceWriter
					//Thar be try/catch blocks, as far as the eye can see!
					ImageOutputStream outputStream = null;
					outputStream = new FileImageOutputStream(new File(outputFileName));
				
					GifSequenceWriter writer = null;
					writer = new GifSequenceWriter(outputStream, BufferedImage.TYPE_INT_ARGB, speedValue, true);
				
					//Finally writing images...
					for(int i = 0; i <= fileList.size() - 1; ++i)
					{
						BufferedImage nextImage = null;
						nextImage = ImageIO.read(fileList.get(i));
						writer.writeToSequence(nextImage);	
					}
					writer.close();
					outputStream.close();
					printToLog("Event-- file saved!");
				}
			}
		}
		);
		
		
		toolPanel.add(saveButton);
		toolPanel.add(playButton);
		toolPanel.add(pauseButton);
		toolPanel.add(speedField);
		
		toolPanel.setPreferredSize(toolPanel.getPreferredSize());
		
		mainFrame.add(toolPanel);

	}
	
	public static void animate()
	{
		
		if (fileList.size() > 0)
		{
			if (animCount < fileList.size() - 1)
			{
				animCount++;
			}
			else
			{
				animCount = 0;
			}
			try
			{
				picLabel.setIcon(new ImageIcon(ImageIO.read(fileList.get(animCount))));
			}
			catch (IOException e1)
			{
				printToLog("Error-- failed to switch image frame during animation");
			}
		}
		else
		{
			printToLog("Invalid action-- cannot animate if no files loaded");
			animTimer.stop();
			
		}
	}
	
	static Action timer = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			animate();
		}
	};

	public static void createFileChooser()
	{
		//First, make it so you can't accidentally rename files you're trying to open... >.>
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new ImageFileFilter());
		openButton = new JButton("Add image");
		openButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{				
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					fileList.add(fileChooser.getSelectedFile());
					populateLayerList();
					printToLog("Event-- file added");
					
				}
			}
		}
		);
		
		clearButton = new JButton("Clear images");
		clearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileList.clear();
				clearLayerList();
			}
		}
		);
		
		//Adding the buttons
		layerPanel.add(openButton);
		layerPanel.add(clearButton);		
		layerPanel.validate();
		
	}
	
	public static void createLayerList()
	{
		layerScroll = new JScrollPane();
		layerScroll.setPreferredSize(new Dimension(200, 350));
		layerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		data = new DefaultListModel<String>();
		layerList = new JList<String>(data);
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.setLayoutOrientation(JList.VERTICAL);
		layerList.setVisibleRowCount(-1);
		layerList.revalidate();
		
		//Add action handler for when an item is selected
		layerList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				if (evt.getValueIsAdjusting())
				{
					return;
				}
				//Change to selected image!
				try
				{
					//Basically getting rid of a crash that happens when the list is cleared
					if (layerList.getSelectedIndex() >= 0)
					{
						picLabel.setIcon(new ImageIcon(ImageIO.read(fileList.get(layerList.getSelectedIndex()))));
					}
					else
					{
						picLabel.setIcon(new ImageIcon(MainWindow.class.getResource("/img/startImage.png")));
					}
				}
				catch (IOException e)
				{
					printToLog("Error-- failed to update selected image; God only knows why...");
				}
				
			}
		}
		);
		
		//Add list to layerScroll and then to layerPanel
		layerScroll.setViewportView(layerList);
		layerPanel.add(layerScroll);
	}
	
	public static void populateLayerList()
	{
		data.addElement(fileList.get(fileList.size()-1).getName());
		layerList.revalidate();
		
	}
	public static void clearLayerList()
	{
		data.clear();
		printToLog("Event-- images cleared");
		picLabel.setIcon(new ImageIcon(MainWindow.class.getResource("/img/startImage.png")));
	}
	
	//Print to log with a time stamp
	public static void printToLog(String input)
	{
		logField.setText(input + " | " + dateFormat.format(Calendar.getInstance().getTime()));
	}

	public void actionPerformed(ActionEvent arg0)
	{
		// TODO Think this is where Action Listeners should have gone...
		
	}
}
