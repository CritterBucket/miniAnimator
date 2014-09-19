import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainWindow implements ActionListener
{
	public static JFrame mainFrame;
	public static JMenuBar menuBar;
	public static JPanel topPanel;
	public static JPanel picPanel;
	public static JPanel layerPanel;
	public static JPanel buttonPanel;
	public static JPanel logPanel;
	public static JLabel picLabel;
	
	public static BufferedImage test;
	public static JFileChooser fileChooser;
	public static ArrayList<File> fileList;
	public static JList<String> layerList;
	public static DefaultListModel<String> data;
	
	public static JButton openButton;
	public static JButton clearButton;
	public static JButton playPutton;
	public static JButton pauseButton;
	
	public static JTextField logField;
	public static JTextField speedField;
	
	public static String logStartingText;
	public static Calendar calendar;
	public static SimpleDateFormat dateFormat;
	
	
	public static void main(String[] args)
	{
		fileList = new ArrayList<File>();
		
		createMainWindow();
		createFileChooser();

	}
	
	public static void createMainWindow()
	{
		mainFrame = new JFrame("Animation Tester");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.PAGE_AXIS));		
		mainFrame.setVisible(true);
		//Will this need to go later, or does it just prevent user resizing?
		mainFrame.setResizable(false);
		
		//Adding other pieces
		createTopPanel();
		createLogPanel();
		
		//Trying to add menu bar
		createMenu();
		mainFrame.setJMenuBar(menuBar);
		mainFrame.repaint();
		mainFrame.revalidate();
		
		//Tidy everything up in the end
		mainFrame.pack();
	}

	public static void createTopPanel()
	{
		//Prepping log so it can print loading errors
		logStartingText = new String("Welcome!");
		
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.setBackground(Color.BLUE);
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
		picPanel.setBackground(Color.BLUE);
		picPanel.setLayout(new GridBagLayout());
		picPanel.setPreferredSize(new Dimension(400, 400));
		try
		{
			test = ImageIO.read(new File("C:/Users/Caitlin/Desktop/255.png"));
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
		layerPanel.setBackground(Color.RED);
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
	
	public static void createMenu()
	{
		//JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		
		//Instantiate pieces and parts
		menuBar = new JMenuBar();
		
		//Building first menu
		menu = new JMenu("A menu");
		menu.getAccessibleContext().setAccessibleDescription("The only menu with menu items");
		menuBar.add(menu);
		
		//A group of JMenuItems
		menuItem = new JMenuItem("A menu item");
		menuItem.getAccessibleContext().setAccessibleDescription("This doesn't do anything");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Another menu item");
		menuItem.getAccessibleContext().setAccessibleDescription("This doesn't do anything");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("A third menu item");
		menuItem.getAccessibleContext().setAccessibleDescription("This doesn't do anything");
		menu.add(menuItem);
		
		//Adding a submenu
		menu.addSeparator();
		submenu = new JMenu("A submenu");
		
		menuItem = new JMenuItem("An item in the submenu");
		submenu.add(menuItem);
		menuItem = new JMenuItem("Another item in the submenu");
		submenu.add(menuItem);
		menu.add(submenu);
		
		//Building a second menu in the bar
		menu = new JMenu("Another menu");
		menu.getAccessibleContext().setAccessibleDescription("Does nothing but look pretty");
		menuBar.add(menu);

	}

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
					printToLog("File added");
					
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
		data = new DefaultListModel<String>();
		layerList = new JList<String>(data);
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.setLayoutOrientation(JList.VERTICAL);
		layerList.setPreferredSize(new Dimension(200, 200));
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
						//TO-DO: replace println with what it says to do :P
						System.out.println("Should change to default image here; method createLyerList()");
					}
				}
				catch (IOException e)
				{
					printToLog("Failed to update image to selected for some reason...");
				}
				
			}
		}
		);
		
		//Add list to layerPanel
		layerPanel.add(layerList);
	}
	
	public static void populateLayerList()
	{
		data.addElement(fileList.get(fileList.size()-1).getName());
		layerList.revalidate();
		
	}
	public static void clearLayerList()
	{
		data.clear();
	}
	
	//Print to log with a time stamp
	public static void printToLog(String input)
	{
		logField.setText(input + " | " + dateFormat.format(calendar.getInstance().getTime()));
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
