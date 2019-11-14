package src.assignment3;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
    @author metsis
    @author tesic
    @author wen
 */
public class SentimentAnalysisApp {

	//*************************************
	//SWING OBJECTS FOR INITIAL GUI
	//*************************************
	
	//Frame Components
	static private final JFrame appFrame = new JFrame("Sentiment Analysis Application");
	static protected final JPanel contentPane = new JPanel();
	static private final JPanel FirstPanel = new JPanel(); 

	//Content Pane Components
	static protected final JComboBox<String> appMenu = new JComboBox<String>();
	static private final JButton viewButton = new JButton("View Database");
	static private final JButton saveButton = new JButton("Save Database");
	
	
	public static final ReviewHandler rh = new ReviewHandler();
	
	
	//Main Thread
	public static void main(String [] args) {
    	
    	SwingUtilities.invokeLater(new Runnable() { //Starts App
    		public void run() {
    			
    			startGUI();
    			
    			File databaseFile = new File(ReviewHandler.DATA_FILE_NAME);
    			
    			if(databaseFile.exists()) {
    				
    				
    				rh.loadSerialDB();
    			}
    		}
    	});
    }
	
	
	//Create and display GUI
	private static void startGUI() {
		
		//Initialize Frame  
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeContentPane();
		appFrame.setContentPane(contentPane);
		appFrame.pack(); //Sizes Frame (contents are at or above their preferred sizes)
		appFrame.setLocationRelativeTo(null); //Centers Frame onscreen
		appFrame.setVisible(true);
	}	
	
	
	//Initialize Content Pane for Frame
	private static void initializeContentPane() {
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		initializeFirstContentPane();
		contentPane.add(FirstPanel);
	}
	
	//***************************************************************************
	//					Initialize First Content Pane Panel 
	//***************************************************************************
	private static void initializeFirstContentPane() {
		
		//Initialize Menu Component
		initializeAppMenu();
		
		//Initialize View Database Button
		ActionListener vbListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Create new thread for viewButton response 
				Runnable vbRunnable = new Runnable() {
				
					public void run() {
						//Add code to Print Database contents 
						//method that this thread invokes must be synchronized
					}
				};
				
				Thread vbThread = new Thread(vbRunnable);
				vbThread.start();
			}
		};
		
		viewButton.addActionListener(vbListener);
			
		//Initialize Save Button
		ActionListener sbListener = new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					
					Runnable sbRunnable = new Runnable() {
						public void run() {
							//Add code to save Database
							//Method that this thread invokes must be synchronized
						}
					};
				
					Thread sbThread = new Thread(sbRunnable);
					sbThread.start();
				}
		};
		
		saveButton.addActionListener(sbListener);
		
		
		//Set Upper Panel Layout 
		GroupLayout upperLayout = new GroupLayout(FirstPanel);
		upperLayout.setAutoCreateGaps(true);
		upperLayout.setAutoCreateContainerGaps(true);
		
		FirstPanel.setLayout(upperLayout);
		
		upperLayout.setVerticalGroup( 
			upperLayout.createSequentialGroup()
				.addComponent(appMenu)
				.addGroup(upperLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(viewButton)
						.addComponent(saveButton))
		);
		
		upperLayout.setHorizontalGroup(
				upperLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(appMenu)
					.addGroup(upperLayout.createSequentialGroup()
							.addComponent(viewButton)
							.addComponent(saveButton))
		);
		
		
		
	}
	
	
	
	private static void initializeAppMenu() {
		
		appMenu.addItem("Menu Options");
		appMenu.addItem("1: Load new movie review collection");
		appMenu.addItem("2: Delete movie review from database");
		appMenu.addItem("3: Search movie reviews in database by id");
		appMenu.addItem("4: Search movie reviews in database by substring");
		appMenu.addItem("0: Exit Program");
		appMenu.setSelectedIndex(0);
		
		ItemListener menuListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					String selection = (String)e.getItem();
					
					switch(selection) {
						case "1: Load new movie review collection":
							loadReviews();
							break;
						
						case "2: Delete movie review from database":
							deleteReview();
							break;
						
						case "3: Search movie reviews in database by id":
							searchID();
							break;
						
						case "4: Search movie reviews in database by substring":
							searchSubstring();
							break;
						
						case "0: Exit Program":
							//exit();
							break;
					}
				}
			}
		};
		
		appMenu.addItemListener(menuListener);
	}
	
	
	//****************************************************
	//Menu Option 1
	//****************************************************
	static int realClass = 0;
	private static void loadReviews() {
		
		//Update GUI
		contentPane.removeAll();
		final JTextField inputField = new JTextField("Enter the filepath, followed by enter");
		final JComboBox<String> classSelection = new JComboBox<String>();
		classSelection.addItem("Select the Real Class:");
		classSelection.addItem("Positive");
		classSelection.addItem("Negative");
		classSelection.addItem("Unknown");
		classSelection.setSelectedIndex(0);
		contentPane.add(classSelection);
		contentPane.add(inputField);
		appFrame.setContentPane(contentPane);
		
		//Handle ComboBox Selection
		ItemListener csListener = new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					String selection = (String)e.getItem();
					
					switch(selection) {
						case "Positive": 
							realClass = 1;
							break;
						
						case "Negative":
							realClass = 0;
							break;
						
						case "Unknown":
							realClass = 2;
							break;
					}
					
				}
			}
		};
		
		classSelection.addItemListener(csListener);
		
		
		//Handle filepath Input
		inputField.setFocusable(true);
		
		class NewKeyAdapter extends KeyAdapter {
			
			public void keyReleased(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					Runnable inputFieldRunnable = new Runnable() {
						public void run()
						{
							String path = inputField.getText();
							rh.loadReviews(path, realClass);
						}
					};
					
					Thread inputFieldThread = new Thread(inputFieldRunnable);
					inputFieldThread.start();
				}
			}
			
			
		}
		
		NewKeyAdapter keyListener = new NewKeyAdapter();
		inputField.addKeyListener(keyListener);
		
		//Update GUI to display program output
		//JTextArea programOutput = new JTextArea(1000,1000);
		//programOutput.setText("test");
		initializeFirstContentPane();
		//contentPane.add(programOutput);
		//appFrame.setContentPane(contentPane);
		
		
		
		
		
		
	}
	
	//Menu Option 2
	private static void deleteReview() {
		
	}

	//Menu Option 3
	private static void searchID() {
	
	}

	//Menu Option 4
	private static void searchSubstring() {
	
	}
	
}

	