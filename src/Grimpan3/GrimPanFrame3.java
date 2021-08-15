package Grimpan3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


import org.xml.sax.InputSource;

public class GrimPanFrame3 extends JFrame {

	private String defaultDir = "C:/Temp/";
	private final FileNameExtensionFilter grimFileModelFilter = 
			new FileNameExtensionFilter("Grim Files", "grm");
	private JFileChooser jFileChooser1 = null;
	private JFileChooser jFileChooser2 = null;

	private GrimPanFrame3 thisClass = this;
	private GrimPanModel model = null;
	private DrawPanel drawPanel;
	
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnShape;
	private JMenu mnSetting;
	private JMenu mnHelp;
	private JMenuItem mntmAbout;
	private JMenuItem mntmNew;
	private JMenuItem mntmOpen;
	private JMenuItem mntmExit;
	private JMenuItem mntmLineColor;
	private JMenuItem mntmLineWidth;
	private JMenuItem mntmFillColor;
	private JCheckBoxMenuItem chckbxmntmFill;
	private JRadioButtonMenuItem rdbtnmntmLine;
	private JRadioButtonMenuItem rdbtnmntmPencil;
	private JRadioButtonMenuItem rdbtnmntmPolygon;

	private ButtonGroup btnGroup = new ButtonGroup();
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JRadioButtonMenuItem rdbtnmntmRegular;
	private JRadioButtonMenuItem rdbtnmntmOval;
	private JMenu mnEdit;
	private JMenuItem mntmMove;
	private JMenuItem mntmDelete;
	private JMenuItem mntmSaveAsSvg;
	private JRadioButtonMenuItem rdbtnmntmStar;
	private JRadioButtonMenuItem rdbtnmntmRectangle;
	private JRadioButtonMenuItem rdbtnmntmCurve;

	public JPopupMenu popup;
	private JMenuItem mntmRotate;

	/**
	 * Create the frame.
	 */
	public GrimPanFrame3() {
		
		model = new GrimPanModel();
		drawPanel = new DrawPanel(model);
		initialize();
	}
	
	void initialize() {
		popup = new JPopupMenu();

		
		setTitle("그림판");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 800, 600);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File   ");
		menuBar.add(mnFile);
		
		mntmNew = new JMenuItem("New  ");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearDrawPanel();
			}
		});
		mnFile.add(mntmNew);
		
		mntmOpen = new JMenuItem("Open ");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAction();
			}
		});
		mnFile.add(mntmOpen);
		
		mntmSave = new JMenuItem("Save  ");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem("Save As ...");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsAction();
			}
		});
		mnFile.add(mntmSaveAs);
		
		mntmSaveAsSvg = new JMenuItem("Save As SVG");
		mntmSaveAsSvg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsSVGAction();
			}
		});
		mnFile.add(mntmSaveAsSvg);
		mnFile.addSeparator();
		
		mntmExit = new JMenuItem("Exit ");
		mnFile.add(mntmExit);
		
		mnShape = new JMenu("Shape ");
		menuBar.add(mnShape);
		
		rdbtnmntmPencil = new JRadioButtonMenuItem("Pencil");
		rdbtnmntmPencil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_PENCIL);
			}
		});
		
		rdbtnmntmLine = new JRadioButtonMenuItem("Line ");
		rdbtnmntmLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_LINE);
			}
		});
		mnShape.add(rdbtnmntmLine);
		rdbtnmntmLine.setSelected(true);
		
		btnGroup.add(rdbtnmntmLine);
		mnShape.add(rdbtnmntmPencil);
		
		rdbtnmntmPolygon = new JRadioButtonMenuItem("Polygon");
		rdbtnmntmPolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_POLYGON);
			}
		});
		mnShape.add(rdbtnmntmPolygon);
		
		rdbtnmntmRegular = new JRadioButtonMenuItem("Regular");
		rdbtnmntmRegular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_REGULAR);

				Object[] possibleValues = { 
						"3", "4", "5", "6", "7",
						"8", "9", "10", "11", "12"
				};
				Object selectedValue = JOptionPane.showInputDialog(thisClass,
						"Choose one", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						possibleValues, possibleValues[0]);
				if (selectedValue!=null){
					model.setNPolygon(Integer.parseInt((String)selectedValue));
				}
				drawPanel.repaint();
			}
		});
		mnShape.add(rdbtnmntmRegular);
		
		rdbtnmntmOval = new JRadioButtonMenuItem("Oval");
		rdbtnmntmOval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_OVAL);
			}
		});
		
		mnShape.add(rdbtnmntmOval);
		
		
		rdbtnmntmRectangle = new JRadioButtonMenuItem("Rectangle");
		rdbtnmntmRectangle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_Rectangle);
			}
		});
		mnShape.add(rdbtnmntmRectangle);
		
		btnGroup.add(rdbtnmntmPencil);
		btnGroup.add(rdbtnmntmPolygon);
		btnGroup.add(rdbtnmntmRegular);
		btnGroup.add(rdbtnmntmOval);
		btnGroup.add(rdbtnmntmRectangle);
		btnGroup.add(rdbtnmntmCurve);
		
		rdbtnmntmCurve = new JRadioButtonMenuItem("CURVE");
		rdbtnmntmCurve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_CURVE);
			}
		});
		mnShape.add(rdbtnmntmCurve);
		
		mnEdit = new JMenu("Edit  ");
		menuBar.add(mnEdit);
		
		mntmMove = new JMenuItem("Move");
		mntmMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveAction();
			}
		});
		mnEdit.add(mntmMove);
		
		mntmDelete = new JMenuItem("Delete");
		mntmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAction();
			}
		});
		mnEdit.add(mntmDelete);
		
		mntmRotate = new JMenuItem("Rotate");
		mntmRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotateAction();
			}
		});
		mnEdit.add(mntmRotate);
		
		mnSetting = new JMenu("Setting ");
		menuBar.add(mnSetting);
		
		mntmLineColor = new JMenuItem("Line Color");
		mntmLineColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = 
						JColorChooser.showDialog(thisClass, 
								"Choose a color",
								Color.black);					
					model.setStrokeColor(color);
					drawPanel.repaint();
			}
		});
		mnSetting.add(mntmLineColor);
		
		mntmLineWidth = new JMenuItem("Line Width");
		mntmLineWidth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputVal = JOptionPane.showInputDialog(thisClass, "Line Width", "1");
				if (inputVal!=null){
					model.setStrokeWidth(Float.parseFloat(inputVal));
					drawPanel.repaint();
				}
			}
		});
		mnSetting.add(mntmLineWidth);
		
		mntmFillColor = new JMenuItem("Fill Color");
		mntmFillColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = 
						JColorChooser.showDialog(thisClass, 
								"Choose a color",
								Color.black);					
					model.setFillColor(color);
					drawPanel.repaint();
			}
		});
		mnSetting.add(mntmFillColor);
		
		chckbxmntmFill = new JCheckBoxMenuItem("Fill");
		chckbxmntmFill.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean fillState = chckbxmntmFill.getState();
				model.setShapeFill(fillState);
				drawPanel.repaint();
			}
		});
		mnSetting.add(chckbxmntmFill);
		
		mnHelp = new JMenu("Help ");
		menuBar.add(mnHelp);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(thisClass,
						"GrimPan Ver0.0.1 \nProgrammed by J, hufs.ac.kr", 
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		contentPane.add(drawPanel);
		
		jFileChooser1 = new JFileChooser(defaultDir);
		jFileChooser1.setDialogTitle("Open Saved GrimPan");
		FileNameExtensionFilter filter = 
				new FileNameExtensionFilter("GrimPan or SVG File", "grm", "svg");
		jFileChooser1.setFileFilter(filter);

		jFileChooser2 = new JFileChooser(defaultDir);
		jFileChooser2.setDialogType(JFileChooser.SAVE_DIALOG);
		jFileChooser2.setDialogTitle("Save As ...");
		jFileChooser2.setFileFilter(grimFileModelFilter);
		
		
		JMenuItem item1 = new JMenuItem("Color");
	    JMenuItem item2 = new JMenuItem("Oval");
	    JMenuItem item3 = new JMenuItem("Rectangle");

	    popup.add(item1);
	    popup.add(item2);
	    popup.add(item3);
	    
	    
	    drawPanel.addMouseListener(new MouseAdapter() {
	        public void mouseReleased(MouseEvent e){
	            if ( e.getButton() == 3 ){ // 1-left, 2-middle, 3-right button
	                popup.show(e.getComponent(), e.getX(), e.getY());
	            }
	        }
	    });
	    
	    item1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
				Color color = 
						JColorChooser.showDialog(thisClass, 
								"Choose a color",
								Color.black);					
					model.setStrokeColor(color);
					drawPanel.repaint();
	    	}
		});
	    
	    item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_OVAL);
			}
		});
	    
	    item3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setEditState(GrimPanModel.SHAPE_Rectangle);
			}
		});



	}
	
	protected void rotateAction() {
		// TODO Auto-generated method stub
		model.setEditState(GrimPanModel.EDIT_ROTATE);
		if (model.curDrawShape != null){
			model.shapeList.add(new GrimShape(model.curDrawShape, 
					model.getStrokeWidth(), model.getStrokeColor(),
					model.getFillColor(), model.isShapeFill()));
			model.curDrawShape = null;
		}
		drawPanel.repaint();
	}

	void clearDrawPanel(){
		model.shapeList.clear();
		model.polygonPoints = new ArrayList<Point>();
		model.curDrawShape = null;
		
		drawPanel.repaint();
	}
	void openAction() {
		if (jFileChooser1.showOpenDialog(this) ==
				JFileChooser.APPROVE_OPTION) {
			File selFile = jFileChooser1.getSelectedFile();
			if (selFile == null) return;
			String fileName = selFile.getName();

			if (getExtension(fileName).equals("grm")){
				readShapeFromGrimSaveFile(selFile);
			}
			else {
				readShapeFromSVGSaveFile(selFile);
			}
			model.setSaveFile(selFile);
			setTitle("그림판 - "+fileName);
			drawPanel.repaint();
		}
	}
	void saveAction() {
		if (model.getSaveFile()==null){
			model.setSaveFile(new File(defaultDir+"noname.grm"));
			setTitle("그림판 - "+defaultDir+"noname.grm");
		}
		File selFile = model.getSaveFile();
		saveGrimPanData(selFile);	
	}
	void saveAsAction() {
		if (jFileChooser2.showSaveDialog(this) ==
				JFileChooser.APPROVE_OPTION) {
				File selFile = jFileChooser2.getSelectedFile();
				model.setSaveFile(selFile);
				setTitle("그림판 - "+selFile.getName());
				saveGrimPanData(selFile);
			}
	}
	void readShapeFromGrimSaveFile(File saveFile) {
		model.setSaveFile(saveFile);
		try {
			ObjectInputStream input =
					new ObjectInputStream(new FileInputStream(model.getSaveFile()));
			model.shapeList = (ArrayList<GrimShape>) input.readObject();
			input.close();

		} catch (ClassNotFoundException e) {
			System.err.println("Class not Found");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void readShapeFromSVGSaveFile(File saveFile) {
		model.setSaveFile(saveFile);
		model.attsMapList = new ArrayList<HashMap<String, String>>();
		SaxSVGParseHandler saxTreeHandler = new SaxSVGParseHandler(model); 

		try {
			SAXParserFactory saxf = SAXParserFactory.newInstance();
			//saxf.setFeature("http://xml.org/sax/features/validation", false);
			SAXParser saxParser = saxf.newSAXParser();
			saxParser.parse(new InputSource(new FileInputStream(model.getSaveFile())), saxTreeHandler);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		int drawCount = 0;
		for (HashMap<String, String> map:model.attsMapList){

			ArrayList<GrimShape> gslist = SVG2GrimShapeTranslator.translateSVG2Shape(map);
			//System.out.println("shapelist size="+view.gmodel.shapeList.size());
			if (gslist != null && gslist.size()!=0){
				model.shapeList.addAll(gslist);
			}
			drawCount++;
			//System.out.println("drawcount="+drawCount);
		}

	}

	void saveGrimPanData(File saveFile){
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(saveFile));
			output.writeObject(model.shapeList);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void moveAction(){
		model.setEditState(GrimPanModel.EDIT_MOVE);
		if (model.curDrawShape != null){
			model.shapeList.add(new GrimShape(model.curDrawShape, 
					model.getStrokeWidth(), model.getStrokeColor(),
					model.getFillColor(), model.isShapeFill()));
			model.curDrawShape = null;
		}
		drawPanel.repaint();
	}
	void deleteAction(){
		model.setEditState(GrimPanModel.EDIT_REMOVE);
		if (model.curDrawShape != null){
			model.shapeList.add(new GrimShape(model.curDrawShape, 
					model.getStrokeWidth(), model.getStrokeColor(),
					model.getFillColor(), model.isShapeFill()));
			model.curDrawShape = null;
		}
		drawPanel.repaint();
	}
		
	void saveAsSVGAction(){

		File svgFile = new File(defaultDir+"noname.svg");

		if (model.getSaveFile()!=null){
			String saveFileName = model.getSaveFile().getName();
			svgFile = new File(defaultDir+saveFileName.replace(".grm", ".svg"));
		}

		PrintWriter svgout = null;
		try {
			svgout = new PrintWriter(svgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		svgout.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
		//svgout.println("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN' 'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>");
		svgout.print("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		svgout.println("     xmlns='http://www.w3.org/2000/svg' ");
		svgout.print("width='"+this.getWidth()+"' ");
		svgout.print("height='"+this.getHeight()+"' ");
		svgout.println("overflow='visible' xml:space='preserve'>");
		for (GrimShape gs:model.shapeList){
			svgout.println("    "+GrimShape2SVGTranslator.translateShape2SVG(gs));
		}
		svgout.println("</svg>");
		svgout.close();
	}
	static public String getExtension(String fileName) {
		String ext = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			ext = fileName.substring(i+1);
		}
		return ext;
	}

}
