import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.datatransfer.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

class Notepad implements ActionListener, KeyListener, AdjustmentListener, DocumentListener, UndoableEditListener, ChangeListener, ItemListener, WindowListener, MouseListener{

	JFrame appFrame;
	JMenuBar menuBar;
	JTextArea textArea;
	JScrollPane scrollPane;

	JMenu fileMenu, editMenu, formatMenu, viewMenu, helpMenu;

	JMenuItem fileMenuItemNew, fileMenuItemOpen, fileMenuItemSave, fileMenuItemSaveAs ;
	JMenuItem fileMenuItemPageSetup, fileMenuItemPrint ;
	JMenuItem fileMenuItemExit ;

	JMenuItem editMenuItemUndo , editMenuItemCut , editMenuItemCopy , editMenuItemPaste , editMenuItemDelete;
	JMenuItem editMenuItemFind , editMenuItemFindNext , editMenuItemReplace , editMenuItemGoto;
	JMenuItem editMenuItemSelectAll , editMenuItemTimeDate;

	JCheckBoxMenuItem formatMenuItemWordWrap;
	JMenuItem formatMenuItemFont ;

	JCheckBoxMenuItem viewMenuItemStatusBar;

	JMenuItem helpMenuItemViewHelp, helpMenuItemAbout;

	JFileChooser fileChooser = null;

	boolean fileContentModified = false;

	JComboBox fonts, styles;
	JSpinner sizes;

	UndoManager undoManager;
	Clipboard clipboard;

	JLabel fontLabel;
	String fontChoice = "Lucida Console";
	int styleChoice = Font.PLAIN;
	int sizeChoice = 16;

	JLabel statusLabel;

	JFrame findFrame;
	JTextField findText, replaceText;
	String searchString = "";
	int findIndex = 0;

	PrinterJob printerJob;
	PageFormat pageFormat;

	Notepad () {

// select the look and feel
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//MetalLookAndFeel.setCurrentTheme(new OceanTheme());
			//MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
		} catch (Exception e) {
			e.printStackTrace();
		}

// create a new frame, text area, scroll pane, undo and font settings

		appFrame = new JFrame("Untitled - Notepad");
		textArea = new JTextArea("",35,82);
		textArea.setFont(new Font(fontChoice,styleChoice,sizeChoice));
		textArea.setForeground(Color.BLACK);
		textArea.getDocument().addDocumentListener(this);
		textArea.getDocument().addUndoableEditListener(this);
		textArea.addKeyListener(this);
		textArea.addMouseListener(this);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		undoManager = new UndoManager();
		statusLabel = new JLabel("");
		Dimension d = new Dimension(35,26);
		statusLabel.setPreferredSize(d);
		clipboard = appFrame.getToolkit ().getSystemClipboard ();
		printerJob = PrinterJob.getPrinterJob();
		pageFormat = printerJob.defaultPage();

// create a new menu bar

		menuBar = new JMenuBar();

// File Menu

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		fileMenuItemNew = new JMenuItem("New");
		fileMenuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenuItemNew.setActionCommand("New");
		fileMenuItemNew.addActionListener(this);
		fileMenuItemNew.setMnemonic('N');
		fileMenu.add(fileMenuItemNew);

		fileMenuItemOpen = new JMenuItem("Open...");
		fileMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenuItemOpen.setActionCommand("Open");
		fileMenuItemOpen.addActionListener(this);
		fileMenuItemOpen.setMnemonic('O');
		fileMenu.add(fileMenuItemOpen);

		fileMenuItemSave = new JMenuItem("Save");
		fileMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenuItemSave.setActionCommand("Save");
		fileMenuItemSave.addActionListener(this);
		fileMenuItemSave.setMnemonic('S');
		fileMenu.add(fileMenuItemSave);

		fileMenuItemSaveAs = new JMenuItem("Save As");
		fileMenuItemSaveAs.setActionCommand("Save As");
		fileMenuItemSaveAs.addActionListener(this);
		fileMenuItemSaveAs.setMnemonic('A');
		fileMenu.add(fileMenuItemSaveAs);

		fileMenu.addSeparator();

		fileMenuItemPageSetup = new JMenuItem("Page Setup...");
		fileMenuItemPageSetup.setActionCommand("Page Setup");
		fileMenuItemPageSetup.addActionListener(this);
		fileMenuItemPageSetup.setMnemonic('u');
		fileMenu.add(fileMenuItemPageSetup);

		fileMenuItemPrint = new JMenuItem("Print...");
		fileMenuItemPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		fileMenuItemPrint.setActionCommand("Print");
		fileMenuItemPrint.addActionListener(this);
		fileMenuItemPrint.setMnemonic('P');
		fileMenu.add(fileMenuItemPrint);

		fileMenu.addSeparator();

		fileMenuItemExit = new JMenuItem("Exit");
		fileMenuItemExit.setActionCommand("Exit");
		fileMenuItemExit.addActionListener(this);
		fileMenuItemExit.setMnemonic('x');
		fileMenu.add(fileMenuItemExit);

// Edit Menu

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);

		editMenuItemUndo = new JMenuItem("Undo");
		editMenuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		editMenuItemUndo.setActionCommand("Undo");
		editMenuItemUndo.addActionListener(this);
		editMenuItemUndo.setMnemonic('U');
		editMenu.add(editMenuItemUndo);

		editMenu.addSeparator();

		editMenuItemCut = new JMenuItem("Cut");
		editMenuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		editMenuItemCut.setActionCommand("Cut");
		editMenuItemCut.addActionListener(this);
		editMenuItemCut.setMnemonic('t');
		editMenu.add(editMenuItemCut);

		editMenuItemCopy = new JMenuItem("Copy");
		editMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		editMenuItemCopy.setActionCommand("Copy");
		editMenuItemCopy.addActionListener(this);
		editMenuItemCopy.setMnemonic('C');
		editMenu.add(editMenuItemCopy);

		editMenuItemPaste = new JMenuItem("Paste");
		editMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenuItemPaste.setActionCommand("Paste");
		editMenuItemPaste.addActionListener(this);
		editMenuItemPaste.setMnemonic('P');
		editMenu.add(editMenuItemPaste);

		editMenuItemDelete = new JMenuItem("Delete");
		editMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke("released DELETE"));
		editMenuItemDelete.setActionCommand("Delete");
		editMenuItemDelete.addActionListener(this);
		editMenuItemDelete.setMnemonic('l');
		editMenu.add(editMenuItemDelete);

		editMenu.addSeparator();

		editMenuItemFind = new JMenuItem("Find...");
		editMenuItemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		editMenuItemFind.setActionCommand("Find");
		editMenuItemFind.addActionListener(this);
		editMenuItemFind.setMnemonic('F');
		editMenu.add(editMenuItemFind);

		editMenuItemFindNext = new JMenuItem("Find Next");
		editMenuItemFindNext.setAccelerator(KeyStroke.getKeyStroke("F3"));
		editMenuItemFindNext.setActionCommand("Find Next");
		editMenuItemFindNext.addActionListener(this);
		editMenuItemFindNext.setMnemonic('N');
		editMenu.add(editMenuItemFindNext);

		editMenuItemReplace = new JMenuItem("Replace...");
		editMenuItemReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		editMenuItemReplace.setActionCommand("Replace");
		editMenuItemReplace.addActionListener(this);
		editMenuItemReplace.setMnemonic('R');
		editMenu.add(editMenuItemReplace);

		editMenuItemGoto = new JMenuItem("Go To...");
		editMenuItemGoto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		editMenuItemGoto.setActionCommand("Go To");
		editMenuItemGoto.addActionListener(this);
		editMenuItemGoto.setMnemonic('G');
		editMenu.add(editMenuItemGoto);

		editMenu.addSeparator();

		editMenuItemSelectAll = new JMenuItem("Select All");
		editMenuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		editMenuItemSelectAll.setActionCommand("Select All");
		editMenuItemSelectAll.addActionListener(this);
		editMenuItemSelectAll.setMnemonic('A');
		editMenu.add(editMenuItemSelectAll);

		editMenuItemTimeDate = new JMenuItem("Time Date");
		editMenuItemTimeDate.setAccelerator(KeyStroke.getKeyStroke("F5"));
		editMenuItemTimeDate.setActionCommand("Time Date");
		editMenuItemTimeDate.addActionListener(this);
		editMenuItemTimeDate.setMnemonic('D');
		editMenu.add(editMenuItemTimeDate);

// Format Menu

		formatMenu = new JMenu("Format");
		formatMenu.setMnemonic('O');
		menuBar.add(formatMenu);

		formatMenuItemWordWrap = new JCheckBoxMenuItem("Word Wrap");
		formatMenuItemWordWrap.setActionCommand("Word Wrap");
		formatMenuItemWordWrap.addActionListener(this);
		formatMenuItemWordWrap.setMnemonic('W');
		formatMenu.add(formatMenuItemWordWrap);

		formatMenuItemFont = new JMenuItem("Font...");
		formatMenuItemFont.setActionCommand("Font");
		formatMenuItemFont.addActionListener(this);
		formatMenuItemFont.setMnemonic('F');
		formatMenu.add(formatMenuItemFont);

// View Menu

		viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		menuBar.add(viewMenu);

		viewMenuItemStatusBar = new JCheckBoxMenuItem("Status Bar");
		viewMenuItemStatusBar.setActionCommand("Status Bar");
		viewMenuItemStatusBar.addActionListener(this);
		viewMenuItemStatusBar.setMnemonic('S');
		viewMenu.add(viewMenuItemStatusBar);

// Help Menu

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		menuBar.add(helpMenu);

		helpMenuItemViewHelp = new JMenuItem("View Help");
		helpMenuItemViewHelp.setActionCommand("View Help");
		helpMenuItemViewHelp.addActionListener(this);
		helpMenuItemViewHelp.setMnemonic('H');
		helpMenu.add(helpMenuItemViewHelp);

		helpMenu.addSeparator();

		helpMenuItemAbout = new JMenuItem("About Notepad");
		helpMenuItemAbout.setActionCommand("About Notepad");
		helpMenuItemAbout.addActionListener(this);
		helpMenuItemAbout.setMnemonic('A');
		helpMenu.add(helpMenuItemAbout);

// setup the tool bar

		JToolBar toolBar = new JToolBar();
		JButton newButton = new JButton();
		JButton openButton = new JButton();
		JButton saveButton = new JButton();
		JButton printButton = new JButton();
		JButton pageSetupButton = new JButton();
		JButton findButton = new JButton();
		JButton cutButton = new JButton();
		JButton copyButton = new JButton();
		JButton pasteButton = new JButton();
		JButton undoButton = new JButton();
		JButton timeDateButton = new JButton();
		ImageIcon newIcon= new ImageIcon("Icons/New.jpg");
		ImageIcon openIcon= new ImageIcon("Icons/Open.jpg");
		ImageIcon saveIcon= new ImageIcon("Icons/Save.jpg");
		ImageIcon printIcon= new ImageIcon("Icons/Print.jpg");
		ImageIcon pageSetupIcon = new ImageIcon("Icons/Pagesetup.jpg");
		ImageIcon findIcon= new ImageIcon("Icons/Find.jpg");
		ImageIcon cutIcon= new ImageIcon("Icons/Cut.jpg");
		ImageIcon copyIcon= new ImageIcon("Icons/Copy.jpg");
		ImageIcon pasteIcon= new ImageIcon("Icons/Paste.jpg");
		ImageIcon undoIcon= new ImageIcon("Icons/Undo.jpg");
		ImageIcon timeDateIcon= new ImageIcon("Icons/Timedate.jpg");
            newButton.setIcon(newIcon); newButton.setActionCommand("New"); newButton.setToolTipText("New"); newButton.addActionListener(this);
            openButton.setIcon(openIcon); openButton.setActionCommand("Open"); openButton.setToolTipText("Open"); openButton.addActionListener(this);
            saveButton.setIcon(saveIcon); saveButton.setActionCommand("Save"); saveButton.setToolTipText("Save"); saveButton.addActionListener(this);
            printButton.setIcon(printIcon); printButton.setActionCommand("Print"); printButton.setToolTipText("Print"); printButton.addActionListener(this);
            pageSetupButton.setIcon(pageSetupIcon); pageSetupButton.setActionCommand("Page Setup"); pageSetupButton.setToolTipText("Page Setup"); pageSetupButton.addActionListener(this);
            findButton.setIcon(findIcon); findButton.setActionCommand("Find"); findButton.setToolTipText("Find"); findButton.addActionListener(this);
            cutButton.setIcon(cutIcon); cutButton.setActionCommand("Cut"); cutButton.setToolTipText("Cut"); cutButton.addActionListener(this);
            copyButton.setIcon(copyIcon); copyButton.setActionCommand("Copy"); copyButton.setToolTipText("Copy"); copyButton.addActionListener(this);
            pasteButton.setIcon(pasteIcon); pasteButton.setActionCommand("Paste"); pasteButton.setToolTipText("Paste"); pasteButton.addActionListener(this);
            undoButton.setIcon(undoIcon); undoButton.setActionCommand("Undo"); undoButton.setToolTipText("Undo"); undoButton.addActionListener(this);
            timeDateButton.setIcon(timeDateIcon); timeDateButton.setActionCommand("Time Date"); timeDateButton.setToolTipText("Time Date"); timeDateButton.addActionListener(this);
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(printButton);
		toolBar.add(pageSetupButton);
		toolBar.add(findButton);
		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.add(undoButton);
		toolBar.add(timeDateButton);

// finally display the frame

		appFrame.setJMenuBar(menuBar);
		appFrame.setLayout(new BorderLayout());
		//appFrame.add(toolBar, BorderLayout.NORTH);
		appFrame.add(scrollPane, BorderLayout.CENTER);
		appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Image appIcon = Toolkit.getDefaultToolkit().getImage("Notepad.jpg");
		appFrame.setIconImage(appIcon);
		appFrame.addWindowListener(this);
		appFrame.pack();
		appFrame.setVisible(true);
	}


// actionListener event handler
	public void actionPerformed( ActionEvent ae ) {

		String s = ae.getActionCommand();

		do {

			if ( s.equals("New") ){
				fileNewHandler(ae);
				break;
			}

			if ( s.equals("Open") ){
				fileOpenHandler(ae);
				break;
			}

			if ( s.equals("Save") ){
				fileSaveHandler(ae);
				break;
			}

			if ( s.equals("Save As") ){
				fileSaveAsHandler(ae);
				break;
			}

			if ( s.equals("Page Setup") ){
				filePageSetupHandler(ae);
				break;
			}

			if ( s.equals("Print") ){
				filePrintHandler(ae);
				break;
			}

			if ( s.equals("Exit") ){
				fileExitHandler(ae);
				break;
			}

			if ( s.equals("Undo") ){
				editUndoHandler(ae);
				break;
			}

			if ( s.equals("Cut") ){
				editCutHandler(ae);
				break;
			}

			if ( s.equals("Copy") ){
				editCopyHandler(ae);
				break;
			}

			if ( s.equals("Paste") ){
				editPasteHandler(ae);
				break;
			}

			if ( s.equals("Delete") ){
				editDeleteHandler(ae);
				break;
			}

			if ( s.equals("Find") ){
				editFindHandler(ae);
				break;
			}

			if ( s.equals("Find Next") ){
				editFindNextHandler(ae);
				break;
			}

			if ( s.equals("Replace") ){
				editReplaceHandler(ae);
				break;
			}

			if ( s.equals("Go To") ){
				editGotoHandler(ae);
				break;
			}

			if ( s.equals("Select All") ){
				editSelectAllHandler(ae);
				break;
			}

			if ( s.equals("Time Date") ){
				editTimeDateHandler(ae);
				break;
			}

			if ( s.equals("Word Wrap") ){
				formatWordWrapHandler(ae);
				break;
			}

			if ( s.equals("Font") ){
				formatFontHandler(ae);
				break;
			}

			if ( s.equals("Status Bar") ){
				viewStatusBarHandler(ae);
				break;
			}

			if ( s.equals("View Help") ){
				helpViewHelpHandler(ae);
				break;
			}

			if ( s.equals("About Notepad") ){
				helpAboutHandler(ae);
				break;
			}

			if ( s.equals("Search Text") ){
				findSearchTextHandler(ae);
				break;
			}

			if ( s.equals("Cancel Find") ){
				findCancelSearchHandler(ae);
				break;
			}

			if ( s.equals("Replace Text") ){
				replaceTextHandler(ae);
				break;
			}

			if ( s.equals("Replace All") ){
				replaceAllHandler(ae);
				break;
			}

			if ( s.equals("Cancel Replace") ){
				replaceCancelHandler(ae);
				break;
			}

		} while ( false );

		// register for notifications again, as after
		// opendialog no notification occurs!
		textArea.getDocument().addDocumentListener(this);
		textArea.getDocument().addUndoableEditListener(this);
	}

// KeyListener event handler
	public void keyPressed( KeyEvent ke ) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}
	public void keyReleased( KeyEvent ke ) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}
	public void keyTyped( KeyEvent ae ) {
		if ( ae.getKeyChar() == KeyEvent.VK_ESCAPE ){
			findFrame.dispose();
		}
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}


// MouseListener event handler
	public void mousePressed(MouseEvent e) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void mouseReleased(MouseEvent e) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void mouseEntered(MouseEvent e) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void mouseExited(MouseEvent e) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void mouseClicked(MouseEvent e) {
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

// AdjustmentListener event handler
	public void adjustmentValueChanged( AdjustmentEvent ae ) {
		updateStatusLabel();
	}

// DocumentListener event handler
	public void changedUpdate( DocumentEvent de ) {
		fileContentModified = true;
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void insertUpdate( DocumentEvent de ) {
		fileContentModified = true;
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

	public void removeUpdate( DocumentEvent de ) {
		fileContentModified = true;
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

// UndoableEditListener event handler
	public void undoableEditHappened( UndoableEditEvent ue ) {
		fileContentModified = true;
		undoManager.addEdit(ue.getEdit());
		findIndex = textArea.getCaretPosition();
		updateStatusLabel();
	}

// ItemListener event handler
	public void itemStateChanged( ItemEvent ie ) {

		do {
			if ( ie.getStateChange() != ItemEvent.SELECTED) {
				break;
			}

			if ( ie.getSource() == fonts) {
				fontChoice = (String)fonts.getSelectedItem();
			}

			if ( ie.getSource() == styles ) {
				styleChoice = styles.getSelectedIndex();
			}

			textArea.setFont(new Font(fontChoice, styleChoice, sizeChoice));
			fontLabel.setFont(new Font(fontChoice, styleChoice, sizeChoice));
		} while ( false );
	}

// ChangeListener event handler
	public void stateChanged( ChangeEvent ce ) {
		try {
			String size = sizes.getModel().getValue().toString();
			sizeChoice = Integer.parseInt(size);
			textArea.setFont(new Font(fontChoice,styleChoice,sizeChoice));
			fontLabel.setFont(new Font(fontChoice, styleChoice, sizeChoice));
		} catch (NumberFormatException nfe) {
		}
	}

// WindowListener event handler
	public void windowActivated( WindowEvent we ) {}
	public void windowDeactivated( WindowEvent we ) {}
	public void windowIconified( WindowEvent we ) {}
	public void windowDeiconified( WindowEvent we ) {}
	public void windowOpened( WindowEvent we ) {}
	public void windowClosed( WindowEvent we ) {}
	public void windowClosing( WindowEvent we ) {
		do {
			if( false == fileContentModified ) {
				break;
			}

			String options[] = { "Save", "Don't Save", "Cancel" };
			int result = JOptionPane.showOptionDialog(appFrame,"Do you want to save changes?","Confirm",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
			if ( result == JOptionPane.NO_OPTION ) {
				break;
			}

			if ( result == JOptionPane.CANCEL_OPTION ) {
				return;
			}

			if ( fileChooser == null )
			{
				fileChooser = new JFileChooser();
				int returnVal = fileChooser.showSaveDialog(appFrame);

				if (returnVal != JFileChooser.APPROVE_OPTION) {
					fileChooser = null;
					return;
				}
			}

			try {
				FileWriter output = new FileWriter(fileChooser.getSelectedFile());
				BufferedWriter bufOutput = new BufferedWriter(output);
				String s = textArea.getText();
				bufOutput.write(s,0,s.length());
				bufOutput.close();
				fileContentModified = false;
			} catch ( IOException e ) {
				e.printStackTrace();
			}

		} while ( false );
		System.exit(0);
	}

// menu item handlers
	void fileNewHandler( ActionEvent ae ) {
		if( true == fileContentModified ) {
			String options[] = { "Save", "Don't Save", "Cancel" };
			int result = JOptionPane.showOptionDialog(appFrame,"Do you want to save changes?","Confirm",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
			if ( result == JOptionPane.CANCEL_OPTION ) {
				return;
			}
			if ( result == JOptionPane.YES_OPTION ) {
				fileSaveHandler(ae);
				if ( null == fileChooser ) {
					return;
				}
			}
		}
		fileChooser = null;
		textArea.setText("");
		appFrame.setTitle("Untitled - Notepad");
		fileContentModified = false;
	}

	void fileOpenHandler( ActionEvent ae ) {
		if( true == fileContentModified ) {
			String options[] = { "Save", "Don't Save", "Cancel" };
			int result = JOptionPane.showOptionDialog(appFrame,"Do you want to save changes?","Confirm",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
			if ( result == JOptionPane.CANCEL_OPTION ) {
				return;
			}
			if ( result == JOptionPane.YES_OPTION ) {
				fileSaveHandler(ae);
				if ( null == fileChooser ) {
					return;
				}
			}
			fileContentModified = false;
		}

		fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(appFrame);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			fileChooser = null;
			return;
		}

		appFrame.setTitle(fileChooser.getSelectedFile().getName()+" - Notepad");

		try {
			FileReader input = new FileReader(fileChooser.getSelectedFile());
			textArea.read(input,null);
			input.close();
		} catch ( FileNotFoundException e) {
			JOptionPane.showMessageDialog(appFrame, "Error opening file : "+fileChooser.getSelectedFile().getName(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	void fileSaveHandler( ActionEvent ae ) {

		if ( fileChooser == null )
		{
			fileSaveAsHandler(ae);
			return;
		}

		try {
			FileWriter output = new FileWriter(fileChooser.getSelectedFile());
			BufferedWriter bufOutput = new BufferedWriter(output);
			String s = textArea.getText();
			bufOutput.write(s,0,s.length());
			bufOutput.close();
			fileContentModified = false;

		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	void fileSaveAsHandler( ActionEvent ae ) {
		fileChooser = new JFileChooser();
		int returnVal = fileChooser.showSaveDialog(appFrame);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			fileChooser = null;
			return;
		}

		if ( true == fileChooser.getSelectedFile().exists() )
		{
			String options[] = { "Yes", "No" };
			int result = JOptionPane.showOptionDialog(appFrame,"Overwrite existing file?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
			if ( result != JOptionPane.YES_OPTION ) {
				fileChooser = null;
				return;
			}
		}

		appFrame.setTitle(fileChooser.getSelectedFile().getName()+" - Notepad");

		try {
			FileWriter output = new FileWriter(fileChooser.getSelectedFile());
			BufferedWriter bufOutput = new BufferedWriter(output);
			String s = textArea.getText();
			bufOutput.write(s,0,s.length());
			bufOutput.close();
			fileContentModified = false;
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	void filePageSetupHandler( ActionEvent ae ) {
		pageFormat=printerJob.pageDialog(pageFormat);
	}

	void filePrintHandler( ActionEvent ae ) {
		try {
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			if ( pageFormat.getOrientation() == PageFormat.LANDSCAPE ){
				pras.add(OrientationRequested.LANDSCAPE);
			} else {
				pras.add(OrientationRequested.PORTRAIT);
			}

			textArea.print(null,null,true,null,pras,true);
		} catch( Exception e ) {
		}
	}

	void fileExitHandler( ActionEvent ae ) {
		if( true == fileContentModified ) {
			String options[] = { "Save", "Don't Save", "Cancel" };
			int result = JOptionPane.showOptionDialog(appFrame,"Do you want to save changes?","Confirm",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
			if ( result == JOptionPane.CANCEL_OPTION ) {
				return;
			}
			if ( result == JOptionPane.YES_OPTION ) {
				fileSaveHandler(ae);
				if ( null == fileChooser ){
					return;
				}
			}
			fileContentModified = false;
		}
		System.exit(0);
	}

	void editUndoHandler( ActionEvent ae ) {
		try {
			undoManager.undo();
		} catch ( Exception e ) {
		}
	}

	void editCutHandler( ActionEvent ae ) {
		editCopyHandler(ae);
		textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
	}

	void editCopyHandler( ActionEvent ae ) {
		String selection = textArea.getSelectedText();
		if (selection == null) {
			return;
		}

		StringSelection clipString = new StringSelection(selection);
		clipboard.setContents(clipString, clipString);
	}

	void editPasteHandler( ActionEvent ae ) {
		Transferable clipData = clipboard.getContents(this);
		try {
			String clipString = (String) clipData.getTransferData(DataFlavor.stringFlavor);
			textArea.replaceRange(clipString, textArea.getSelectionStart(), textArea.getSelectionEnd());
		} catch (Exception e) {
		}
	}

	void editDeleteHandler( ActionEvent ae ) {
		String selection = textArea.getSelectedText();
		if (selection == null) {
			return;
		}

		StringSelection clipString = new StringSelection(selection);
		textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
	}

	void editFindHandler( ActionEvent ae ) {
		findFrame = new JFrame("Find");
		Image appIcon = Toolkit.getDefaultToolkit().getImage("Notepad.jpg");
		findFrame.setIconImage(appIcon);
		JPanel findPanel1 = new JPanel();
		JPanel findPanel2 = new JPanel();
		JLabel findTextLabel = new JLabel("Find what:\n");
		findText = new JTextField(20);
		findText.addKeyListener(this);
		JButton findNextButton = new JButton("Find Next");
		JButton findCancelButton = new JButton("Cancel");
		findNextButton.setActionCommand("Search Text");
		findCancelButton.setActionCommand("Cancel Find");
		findNextButton.addActionListener(this);
		findCancelButton.addActionListener(this);
		findPanel1.add(findTextLabel);
		findPanel1.add(findText);
		findPanel2.setLayout(new GridLayout(2,1));
		findPanel2.add(findNextButton);
		findPanel2.add(findCancelButton);
		findFrame.setLayout( new BorderLayout() );
		findFrame.add(findPanel1, BorderLayout.WEST);
		findFrame.add(findPanel2, BorderLayout.EAST);
		findFrame.setLocation(100,100);
		findFrame.pack();
		findFrame.setVisible(true);
	}

	void editFindNextHandler( ActionEvent ae ) {
		if ( searchString.equals("") ) {
			editFindHandler(ae);
		} else {
			findSearchNextHandler(ae);
		}
	}

	void editReplaceHandler( ActionEvent ae ) {
		findFrame = new JFrame("Replace");
		Image appIcon = Toolkit.getDefaultToolkit().getImage("Notepad.jpg");
		findFrame.setIconImage(appIcon);
		JPanel replacePanel1 = new JPanel();
		JPanel replacePanel2 = new JPanel();
		JLabel findTextLabel = new JLabel("Find what:");
		JLabel replaceTextLabel = new JLabel("Replace with:");
		findText = new JTextField(10);
		findText.addKeyListener(this);
		replaceText = new JTextField(10);
		replaceText.addKeyListener(this);
		JButton replaceTextButton = new JButton("Replace");
		JButton replaceAllButton = new JButton("Replace All");
		JButton replaceCancelButton = new JButton("Cancel");
		replaceTextButton.setActionCommand("Replace Text");
		replaceAllButton.setActionCommand("Replace All");
		replaceCancelButton.setActionCommand("Cancel Replace");
		replaceTextButton.addActionListener(this);
		replaceAllButton.addActionListener(this);
		replaceCancelButton.addActionListener(this);
		replacePanel1.setLayout(new GridLayout(3,2));
		replacePanel1.add(findTextLabel);
		replacePanel1.add(findText);
		replacePanel1.add(replaceTextLabel);
		replacePanel1.add(replaceText);
		replacePanel2.setLayout(new GridLayout(3,1));
		replacePanel2.add(replaceTextButton);
		replacePanel2.add(replaceAllButton);
		replacePanel2.add(replaceCancelButton);
		findFrame.setLayout( new BorderLayout() );
		findFrame.add(replacePanel1, BorderLayout.WEST);
		findFrame.add(replacePanel2, BorderLayout.EAST);
		findFrame.setLocation(100,100);
		findFrame.pack();
		findFrame.setVisible(true);
	}

	void editGotoHandler( ActionEvent ae ) {
		do {
			try {
				String str = (String)JOptionPane.showInputDialog(appFrame,"Line number:\t","Goto line",JOptionPane.PLAIN_MESSAGE,null,null,null);
				if( str == null ) {
					break;
				}

				int lineNumber = Integer.parseInt(str);
				if ( lineNumber > textArea.getLineCount() ) {
					JOptionPane.showMessageDialog(appFrame,"Line number out of range","Notepad Goto line",JOptionPane.ERROR_MESSAGE);
					continue;
				}

				for ( int i = 0 ; i < textArea.getLineCount() ; i++ ){
					if ( i+1 == lineNumber ) {
						textArea.setCaretPosition(textArea.getLineStartOffset(i));
						return;
					}
				}
			} catch ( Exception e ) {
			}
		} while ( true );
	}

	void editSelectAllHandler( ActionEvent ae ) {
		textArea.selectAll();
	}

	void editTimeDateHandler( ActionEvent ae ) {
		Date date = new Date();
		textArea.replaceRange(date.toString(), textArea.getSelectionStart(), textArea.getSelectionEnd());
	}

	void formatWordWrapHandler( ActionEvent ae ) {
		textArea.setLineWrap(formatMenuItemWordWrap.getState());
	}

	void formatFontHandler( ActionEvent ae ) {
		GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();

		JFrame fontFrame = new JFrame("Select Font");
		fontLabel = new JLabel("\nThis is a sample text");

		JPanel fontSelectorPanel = new JPanel();
		fontSelectorPanel.add(new JLabel("Font:"));
		fonts = new JComboBox(gEnv.getAvailableFontFamilyNames());
		fonts.setSelectedItem(fontChoice);
		fonts.setMaximumRowCount(5);
		fonts.addItemListener(this);
		fontSelectorPanel.add(fonts);

		fontSelectorPanel.add(new JLabel("Style:"));
		String[] styleNames = {"Plain", "Bold", "Italic", "Bold Italic"};
		styles = new JComboBox(styleNames);
		styles.setSelectedItem(styleChoice);
		styles.addItemListener(this);
		fontSelectorPanel.add(styles);

		fontSelectorPanel.add(new JLabel("Size:"));
		sizes = new JSpinner(new SpinnerNumberModel(sizeChoice, 6, 24, 1));
		sizes.addChangeListener(this);
		fontSelectorPanel.add(sizes);

		JPanel fontLabelPanel = new JPanel();
		fontLabelPanel.add(fontLabel);

		fontFrame.setLayout(new BorderLayout());
		fontFrame.add(fontSelectorPanel,BorderLayout.NORTH );
		fontFrame.add(fontLabelPanel, BorderLayout.SOUTH);
		fontFrame.pack();
		fontFrame.setVisible(true);
	}

	void viewStatusBarHandler( ActionEvent ae ) {
		if ( true == viewMenuItemStatusBar.getState() ) {
			updateStatusLabel();
			appFrame.add(statusLabel, BorderLayout.SOUTH);
		} else {
			appFrame.remove(statusLabel);
		}
		appFrame.pack();
	}

	void helpViewHelpHandler( ActionEvent ae ) {
		JFrame helpFrame = SingletonFrame.getInstance();
		JTextPane helpTextPane = new JTextPane();
		helpTextPane.setEditable(false);
		JScrollPane helpScrollPane = new JScrollPane(helpTextPane);
		Image appIcon = Toolkit.getDefaultToolkit().getImage("Notepad.jpg");
		helpFrame.setIconImage(appIcon);
		helpFrame.add(helpScrollPane);
		helpFrame.setSize(300,675);
		helpFrame.setLocation(900,0);
		helpFrame.setResizable(false);
		helpFrame.setVisible(true);

		try {
			String s = "file:///";
			File helpFile = new File("Help.html");
			s += helpFile.getAbsolutePath();
			URL url = new URL(s);
			helpTextPane.setPage(url);
		} catch (Exception e) {
		}
	}

	void helpAboutHandler( ActionEvent ae ) {
		ImageIcon icon = new ImageIcon("Notepad.jpg");
		JOptionPane.showMessageDialog(appFrame, "Notepad Version1.0\n"+"By T L Sudheendran","About",JOptionPane.WARNING_MESSAGE,icon);
	}

	void findSearchTextHandler( ActionEvent ae ) {
		searchString = findText.getText();
		String s2 = textArea.getText();
		int index = s2.indexOf(searchString,findIndex);
		if ( index > 0 ){
			findIndex = index;
			try {
				textArea.setCaretPosition(index);
			} catch ( Exception e ) {
			}
		} else {
			JOptionPane.showMessageDialog(appFrame, "Cannot find string "+searchString,"Notepad",JOptionPane.PLAIN_MESSAGE);
		}
		findFrame.dispose();
	}

	void findSearchNextHandler( ActionEvent ae ) {
		searchString = findText.getText();
		String s2 = textArea.getText();
		int index = s2.indexOf(searchString,findIndex+1);
		if ( index > 0 ){
			findIndex = index;
			try {
				textArea.setCaretPosition(index);
			} catch ( Exception e ) {
			}
		} else {
			JOptionPane.showMessageDialog(appFrame, "Cannot find string "+searchString,"Notepad",JOptionPane.PLAIN_MESSAGE);
		}
		findFrame.dispose();
	}

	void findCancelSearchHandler ( ActionEvent ae ) {
		findFrame.dispose();
	}

	void replaceTextHandler( ActionEvent ae ) {
		searchString = findText.getText();
		String s1 = replaceText.getText();
		String s2 = textArea.getText();
		int index = s2.indexOf(searchString,0);
		StringBuffer result = new StringBuffer();
		if( index > 0 ) {
			result.append(s2.substring(0, index));
			result.append(s1);
			result.append(s2.substring(index+searchString.length()));
			textArea.setText(result.toString());
		} else {
			JOptionPane.showMessageDialog(appFrame, "Cannot find string "+searchString,"Notepad",JOptionPane.PLAIN_MESSAGE);
		}
		findFrame.dispose();
	}

	void replaceAllHandler( ActionEvent ae ) {
		searchString = findText.getText();
		String s1 = replaceText.getText();
		String s2 = textArea.getText();
		int index = s2.indexOf(searchString,0);
		StringBuffer result = new StringBuffer();
		if( index > 0 ) {
			int s = 0;
			while ( ( index = s2.indexOf(searchString,s) ) > 0 ) {
				result.append(s2.substring(s, index));
				result.append(s1);
				s = index+searchString.length();
			}
			result.append(s2.substring(s));
			textArea.setText(result.toString());
		} else {
			JOptionPane.showMessageDialog(appFrame, "Cannot find string "+searchString,"Notepad",JOptionPane.PLAIN_MESSAGE);
		}
		findFrame.dispose();
	}

	void replaceCancelHandler( ActionEvent ae ) {
		findFrame.dispose();
	}

	void updateStatusLabel(){
		int caretPosition = textArea.getCaretPosition();
		int i = textArea.getLineCount();
		Integer col=1, row=1;
		String s = "Ln "+row.toString()+", Col "+col.toString();
		while ( i > 0 ){
			row = i--;
			try{
				if ( caretPosition >= textArea.getLineStartOffset(i) )
				{
					col = caretPosition - textArea.getLineStartOffset(i)+1;
					s = "Ln "+row.toString()+", Col "+col.toString();
					break;
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		statusLabel.setText(s);
	}

	public static void main ( String args[] ) {
		Notepad obj = new Notepad();
	}
}

class SingletonFrame extends JFrame {
	private static SingletonFrame myInstance;
	private SingletonFrame() {}
	public static SingletonFrame getInstance() { return ( (myInstance == null) ? myInstance = new SingletonFrame() : myInstance ); }
}
