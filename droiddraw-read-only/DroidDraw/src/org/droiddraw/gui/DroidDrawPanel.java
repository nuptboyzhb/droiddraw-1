package org.droiddraw.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.xml.parsers.ParserConfigurationException;

import org.droiddraw.AndroidEditor;
import org.droiddraw.Main;
import org.droiddraw.property.ColorProperty;
import org.droiddraw.util.DroidDrawHandler;
import org.droiddraw.widget.AbsoluteLayout;
import org.droiddraw.widget.AnalogClock;
import org.droiddraw.widget.AutoCompleteTextView;
import org.droiddraw.widget.Button;
import org.droiddraw.widget.CheckBox;
import org.droiddraw.widget.DatePicker;
import org.droiddraw.widget.DigitalClock;
import org.droiddraw.widget.EditView;
import org.droiddraw.widget.FrameLayout;
import org.droiddraw.widget.Gallery;
import org.droiddraw.widget.GridView;
import org.droiddraw.widget.ImageButton;
import org.droiddraw.widget.ImageSwitcher;
import org.droiddraw.widget.ImageView;
import org.droiddraw.widget.Layout;
import org.droiddraw.widget.LinearLayout;
import org.droiddraw.widget.ListView;
import org.droiddraw.widget.MapView;
import org.droiddraw.widget.ProgressBar;
import org.droiddraw.widget.RadioButton;
import org.droiddraw.widget.RadioGroup;
import org.droiddraw.widget.RatingBar;
import org.droiddraw.widget.RelativeLayout;
import org.droiddraw.widget.ScrollView;
import org.droiddraw.widget.Spinner;
import org.droiddraw.widget.TabHost;
import org.droiddraw.widget.TabWidget;
import org.droiddraw.widget.TableLayout;
import org.droiddraw.widget.TableRow;
import org.droiddraw.widget.TextView;
import org.droiddraw.widget.Ticker;
import org.droiddraw.widget.TimePicker;
import org.droiddraw.widget.ToggleButton;
import org.droiddraw.widget.Widget;
import org.xml.sax.SAXException;


public class DroidDrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Dimension d = new Dimension(1100,600);
	JTabbedPane jtb = new JTabbedPane();
	TextArea text;
	JTextArea jtext;
	JPopupMenu popup;
	JTree tree;

	public String getSelectedText() {
		if (text != null) {
			return text.getSelectedText();
		}
		else {
			return jtext.getSelectedText();
		}
	}
	
	public void deleteSelectedText() {
		if (text != null) {
			String txt = text.getText();
			int start = text.getSelectionStart();
			int end = text.getSelectionEnd();
			if (end < txt.length())
				text.setText(txt.substring(0, start)+txt.substring(end+1));
			else 
				text.setText(txt.substring(0, start));
		}
		else {
			String txt = jtext.getText();
			int start = jtext.getSelectionStart();
			int end = jtext.getSelectionEnd();
			if (end < txt.length())
				jtext.setText(txt.substring(0, start)+txt.substring(end+1));
			else 
				jtext.setText(txt.substring(0, start));
		}
	}
	
	public void insertText(String txt) {
		int start = (text != null)?text.getSelectionStart():jtext.getSelectionStart();
		deleteSelectedText();
		if (text != null) {
			text.insert(txt, start);
		}
		else {
			jtext.insert(txt, start);
		}
	}
	
	@Override
  public Dimension getMinimumSize() {
		return d;
	}

	public void selectAll() {
		if (text != null) {
			text.selectAll();
		}
		else {
			jtext.selectAll();
		}
	}
	
	@Override
  public Dimension getPreferredSize() {
		return d;
	}
	
	public void editSelected() {
		jtb.setSelectedIndex(2);
	}
	
	public void save(File f) {
		try {
			AndroidEditor.instance().generate(new PrintWriter(new FileWriter(f)));
			AndroidEditor.instance().setChanged(false);

		} catch (IOException ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	public void open(File f) {
		try {
			this.open(new FileReader(f));
		} catch (FileNotFoundException ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	public void open(FileReader r) {
		try {
			StringBuffer buff = new StringBuffer();
			char[] data = new char[4098];
			int read = r.read(data);
			while (read != -1) {
				buff.append(data, 0, read);
				read = r.read(data);
			}
			AndroidEditor.instance().removeAllWidgets();
			DroidDrawHandler.loadFromString(buff.toString());
			if (text != null)
				text.setText(buff.toString());
			else
				jtext.setText(buff.toString());
			repaint();
			AndroidEditor.instance().setChanged(false);
		} 
		catch (IOException ex) {
			AndroidEditor.instance().error(ex);
		}
		catch (SAXException ex) {
			AndroidEditor.instance().error(ex);
		}
		catch (ParserConfigurationException ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	protected static final void switchToLookAndFeel(String clazz) {
		try {
			UIManager.setLookAndFeel(clazz);
		} catch (Exception ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	protected static final void setupRootLayout(Layout l) {
		l.setPosition(AndroidEditor.OFFSET_X+l.getPadding(Widget.LEFT)+l.getMargin(Widget.LEFT),AndroidEditor.OFFSET_Y+l.getPadding(Widget.TOP)+l.getMargin(Widget.TOP));
		l.setPropertyByAttName("android:layout_width", "fill_parent");
		l.setPropertyByAttName("android:layout_height", "fill_parent");
		l.apply();
	}
	
	public DroidDrawPanel(String screen, boolean applet) {	
		switchToLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		AndroidEditor ae = AndroidEditor.instance();
		
		if (applet) {
			text = new TextArea(10,50);
		}
		else {
			jtext = new JTextArea(10,50);
			jtext.getDocument().addUndoableEditListener(new UndoableEditListener() {
				public void undoableEditHappened(UndoableEditEvent e) {
					AndroidEditor.instance().queueUndoRecord(e.getEdit());
				}
			});
			popup = new JPopupMenu();
			JMenuItem it = new JMenuItem("Cut");
			popup.add(it);
			it.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					if (getSelectedText() != null && getSelectedText().length() != 0) {
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection( getSelectedText() ), null );
						deleteSelectedText();
					}
				}
			});
			it = new JMenuItem("Copy");
			it.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					if (getSelectedText() != null && getSelectedText().length() != 0) {
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection( getSelectedText() ), null );
						}
				}
			});
			Main.addCopyAction(it);
			popup.add(it);
			it = new JMenuItem("Paste");
			it.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					try {
						String txt = ( String ) c.getData( DataFlavor.stringFlavor );
						if ( txt != null ) {
							insertText( txt );
						}
					}
					catch ( UnsupportedFlavorException ex ) { ex.printStackTrace(); }
					catch ( IOException ex ) { ex.printStackTrace(); }
				}
			});
			popup.add(it);

			
			jtext.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						popup.show(jtext, e.getX() + 3, e.getY() + 3);
					}
				}
			});
		}
		
		AbsoluteLayout al = new AbsoluteLayout();
		setupRootLayout(al);
		ae.setLayout(al);
		
		Image img = null;
		
		if ("qvgap".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.QVGA_PORTRAIT);
			img = ImageResources.instance().getImage("emu2");
		}
		else if ("hvgal".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.HVGA_LANDSCAPE);
			img = ImageResources.instance().getImage("emu3");
		}
		else if ("hvgap".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.HVGA_PORTRAIT);
			img = ImageResources.instance().getImage("emu4");
		}
		else if ("qvgal".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.QVGA_LANDSCAPE);
			img = ImageResources.instance().getImage("emu1");
		}
		else if ("wvgap".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.WVGA_PORTRAIT);
			img = ImageResources.instance().getImage("emu4");
		}
		else if ("wvgal".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.WVGA_LANDSCAPE);
			img = ImageResources.instance().getImage("emu1");
		}
		else if ("wvgap".equals(screen)) {
			ae.setScreenMode(AndroidEditor.ScreenMode.HVGA_PORTRAIT);
			img = ImageResources.instance().getImage("emu4");
		}
		else if ("wvgal".equals(screen)) {
			img = ImageResources.instance().getImage("emu1");
		}
		final Viewer viewer = new Viewer(ae, this, img);
		JPanel jp = new JPanel();
		
		
		ae.setViewer(viewer);
		
		setLayout(new BorderLayout());
		
		JButton gen;
		JButton edit;
		
		gen = new JButton("Generate");
		gen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringWriter sw = new StringWriter();
				AndroidEditor.instance().generate(new PrintWriter(sw));
				if (text != null)
					text.setText(sw.getBuffer().toString());
				else
					jtext.setText(sw.getBuffer().toString());
			}
		});

		edit = new JButton("Edit");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelected();
			}
		});

		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AndroidEditor.instance().removeWidget(AndroidEditor.instance().getSelected());
				viewer.repaint();
			}
		});
		
		
		//ButtonGroup bg = viewer.getListener().getInterfaceStateGroup();

		JToolBar tb = new JToolBar();
		
		//Enumeration<AbstractButton> buttons = bg.getElements();
		//while (buttons.hasMoreElements()) {
		//	tb.add(buttons.nextElement());
		//	tb.addSeparator();
		//}

		//tb.add(viewer.getListener().getWidgetSelector());
		tb.addSeparator();
		tb.add(edit);
		tb.addSeparator();
		tb.add(delete);
		tb.addSeparator();
		tb.setFloatable(false);
		
		JPanel p = new JPanel();
		SpringLayout sl = new SpringLayout();
		p.setLayout(sl);
		JLabel lbl = new JLabel("Root Layout:");
		//tb.add(lbl);
		//sl.putConstraint(SpringLayout.WEST, lbl, 5, SpringLayout.WEST, p);
		
		final JComboBox layout = new JComboBox(new String[] {AbsoluteLayout.TAG_NAME, LinearLayout.TAG_NAME, RelativeLayout.TAG_NAME, ScrollView.TAG_NAME, TableLayout.TAG_NAME, TabHost.TAG_NAME});
		if (!System.getProperty("os.name").toLowerCase().contains("mac os x"))
			layout.setLightWeightPopupEnabled(false);
		
		final ActionListener layoutActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxChanged")) {
					String select = (String)((JComboBox)e.getSource()).getSelectedItem();
					Layout l = null;
					if (select.equals(AbsoluteLayout.TAG_NAME)) {
						l = new AbsoluteLayout();
					}
					else if (select.equals(LinearLayout.TAG_NAME)) {
						l = new LinearLayout();
					}
					else if (select.equals(RelativeLayout.TAG_NAME)) {
						l = new RelativeLayout();
					}
					else if (select.equals(ScrollView.TAG_NAME)) {
						l = new ScrollView();
					}
					else if (select.equals(TableLayout.TAG_NAME)) {
						l = new TableLayout();
					}
					else if (select.equals(TabHost.TAG_NAME)) {
						l = new TabHost();
					}
					viewer.repaint();
					setupRootLayout(l);
					AndroidEditor.instance().setLayout(l);
				}
			}
		};
		
		layout.addActionListener(layoutActionListener);
		tb.add(layout);
		// This is 1.6.x specific *sigh*
		//sl.putConstraint(SpringLayout.BASELINE, lbl, 0, SpringLayout.BASELINE, layout);
		//sl.putConstraint(SpringLayout.NORTH, tb, 5, SpringLayout.SOUTH, layout);
		p.add(tb);
		
		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					AndroidEditor.instance().removeAllWidgets();
					String layoutXML;
					if (text != null)
						layoutXML = text.getText();
					else
						layoutXML = jtext.getText();
					if (layoutXML.length() > 0) {
						DroidDrawHandler.loadFromString(layoutXML);
					}		
					layout.removeActionListener(layoutActionListener);
					layout.setSelectedItem(AndroidEditor.instance().getLayout().toString());
					layout.addActionListener(layoutActionListener);
					viewer.repaint();
				} 
				catch (Exception ex) {
					AndroidEditor.instance().error(ex);
				}
			}
		});
		
		
		p.setSize(200, 300);
		p.validate();
		
		jp.setLayout(new BorderLayout());
		
		JComboBox screen_size = new JComboBox(new String[] {"QVGA Landscape", "QVGA Portrait", "HVGA Landscape", "HVGA Portrait", "WVGA Landscape", "WVGA Portrait"});
		screen_size.setSelectedIndex(3);
		JPanel top = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		top.setLayout(new GridLayout(2,2));

		top.add(lbl);
		top.add(layout);
		top.add(new JLabel("Screen Size:"));
		top.add(screen_size);
		p = new JPanel();
		p.setLayout(fl);
		p.add(top);
		jp.add(p, BorderLayout.NORTH);
		jp.add(viewer, BorderLayout.CENTER);
		jp.setBorder(BorderFactory.createTitledBorder("Screen"));
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Layout", jp);
		
		
		//setLayout(new BorderLayout());
		
		
		//add(jp, BorderLayout.WEST);
		
		JPanel out = new JPanel();
		out.setLayout(new BorderLayout());
		out.add(text!=null?text:new JScrollPane(jtext), BorderLayout.CENTER);
		
		TitledBorder border = BorderFactory.createTitledBorder("Output");
		
		out.setBorder(border);
		//JPanel jp2 = new JPanel();
		//jp2.setLayout(f2);
		//jp2.setLayout(new GridLayout(0,1));
		//jp2.add(p);
		
		//p.setBorder(BorderFactory.createTitledBorder("Tools"));
		
		JPanel wp = new JPanel();
		JPanel mp = new JPanel();
		wp.setLayout(new GridLayout(0,1));
		
		Button b = new Button(Button.TAG_NAME);
		((ColorProperty)b.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(b));
		
		CheckBox cb = new CheckBox(CheckBox.TAG_NAME);
		((ColorProperty)cb.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(cb));
		
		

		RadioButton rb = new RadioButton(RadioButton.TAG_NAME);
		((ColorProperty)rb.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(rb));
		
		mp.add(new WidgetPanel(new RadioGroup()));
				
		mp.add(new WidgetPanel(new ImageButton()));
		mp.add(new WidgetPanel(new ImageView()));
		

		Gallery g = new Gallery();
		g.setWidth(100);
		g.setHeight(40);
		mp.add(new WidgetPanel(g));
		
		wp.add(mp);
		mp = new JPanel();
		mp.setLayout(new FlowLayout(FlowLayout.LEFT));
		mp.add(new WidgetPanel(new Spinner()));
		
		EditView ev = new EditView(EditView.TAG_NAME);
		((ColorProperty)ev.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(ev));

		
		AutoCompleteTextView actv = new AutoCompleteTextView("AutoComplete");
		((ColorProperty)actv.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(actv));
		
		TextView tv = new TextView(TextView.TAG_NAME);
		((ColorProperty)tv.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(tv));
		
		mp.add(new WidgetPanel(new ProgressBar()));
		
		mp.add(new WidgetPanel(new GridView()));
				
		wp.add(mp);
		mp = new JPanel();
		mp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		AnalogClock ac = new AnalogClock();
		ac.setSize(50, 50);
		mp.add(new WidgetPanel(ac));
		
		DigitalClock dcb = new DigitalClock();
		((ColorProperty)dcb.getPropertyByAttName("android:textColor")).setColorValue(Color.black);
		mp.add(new WidgetPanel(dcb));
		
		DatePicker dp = new DatePicker();
		dp.setSize(140, 40);
		mp.add(new WidgetPanel(dp));
		
		mp.add(new WidgetPanel(new TimePicker()));
		
		mp.add(new WidgetPanel(new ListView()));
		mp.add(new WidgetPanel(new ImageSwitcher()));
		wp.add(mp);
		mp = new JPanel();
		mp.setLayout(new FlowLayout(FlowLayout.LEFT));
		mp.add(new WidgetPanel(new TabWidget()));
		MapView mapView = new MapView();
		mapView.setHeight(100);
		mapView.setWidth(100);
		mp.add(new WidgetPanel(mapView));
		RatingBar rating = new RatingBar();
		mp.add(new WidgetPanel(rating));

		ToggleButton togg = new ToggleButton("Toggle", "Toggle Off");
		togg.setHeight(50);
		togg.setWidth(100);
		mp.add(new WidgetPanel(togg));
		
		wp.add(mp);
		
		//wp.setSize(wp.getWidth(), 150);
		JPanel ppp = new JPanel();
		ppp.add(wp);
		JScrollPane jswp = new JScrollPane(ppp);
		jswp.setMinimumSize(new Dimension(wp.getWidth(), 160));
		
		JPanel lp = new JPanel();
		lp.setLayout(new GridLayout(0,1));
		mp = new JPanel();
		mp.setLayout(new FlowLayout());
		mp.add(new WidgetPanel(new AbsoluteLayout()));
		mp.add(new WidgetPanel(new FrameLayout()));
		mp.add(new WidgetPanel(new LinearLayout()));
		mp.add(new WidgetPanel(new ScrollView()));
		lp.add(mp);
		mp = new JPanel();

		mp.add(new WidgetPanel(new RelativeLayout()));
		TableRow tr = new TableRow();
		tr.setSizeInternal(70, tr.getHeight());
		mp.add(new WidgetPanel(tr));
		mp.add(new WidgetPanel(new TableLayout()));
		
		mp.add(new WidgetPanel(new Ticker()));
		lp.add(mp);
		mp = new JPanel();
		mp.add(lp);
		JScrollPane jslp = new JScrollPane(mp);
		
		jtb.addTab("Widgets", jswp);
		jtb.addTab("Layouts", jslp);
		jtb.addTab("Properties", AndroidEditor.instance().getPropertiesPanel());
		if (!applet) {
			jtb.addTab("Strings", new StringsPanel());
			jtb.addTab("Colors", new ColorsPanel());
			jtb.addTab("Arrays", new ArrayPanel());
		}
		tree = new JTree(AndroidEditor.instance().getLayoutTreeModel());
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				Widget w = (Widget)tree.getLastSelectedPathComponent();
				AndroidEditor.instance().select(w);
			}			
		});
		jtb.addTab("Support", new DonatePanel());
	
		//add(out, BorderLayout.CENTER);
		
		JButton undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AndroidEditor.instance().undo();
			}
		});
		JButton redo = new JButton("Redo");
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AndroidEditor.instance().redo();
			}
		});
		JToolBar gp = new JToolBar();
		gp.add(gen);
		gp.add(load);
		gp.addSeparator();
		gp.add(undo);
		gp.add(redo);
		gp.addSeparator();
		gp.add(new ClearAction());
		
		JSplitPane ctl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jtb, out);
		final JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jp, ctl);
		
		//p = new JPanel();
		//p.setLayout(fl);
		//p.add(tb);
		//add(p, BorderLayout.NORTH);
		add(gp, BorderLayout.NORTH);
		
		tree.setBorder(BorderFactory.createTitledBorder("Layout Explorer"));
		tree.setMinimumSize(new Dimension(200, 400));
		JScrollPane treeScroll = new JScrollPane(tree);
		treeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(treeScroll, BorderLayout.WEST);
		add(jsp, BorderLayout.CENTER);
		screen_size.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcb = (JComboBox)e.getSource();
				int ix = jcb.getSelectedIndex();
				AndroidEditor ae = AndroidEditor.instance();
				switch (ix) {
				case 0:
					ae.setScreenMode(AndroidEditor.ScreenMode.QVGA_LANDSCAPE);
					viewer.resetScreen(ImageResources.instance().getImage("emu1"));
					break;
				case 1:
					ae.setScreenMode(AndroidEditor.ScreenMode.QVGA_PORTRAIT);
					viewer.resetScreen(ImageResources.instance().getImage("emu2"));
					break;
				case 2:
					ae.setScreenMode(AndroidEditor.ScreenMode.HVGA_LANDSCAPE);
					viewer.resetScreen(ImageResources.instance().getImage("emu3"));
					break;
				case 3:
					ae.setScreenMode(AndroidEditor.ScreenMode.HVGA_PORTRAIT);
					viewer.resetScreen(ImageResources.instance().getImage("emu4"));
					break;
				case 4:
					ae.setScreenMode(AndroidEditor.ScreenMode.WVGA_LANDSCAPE);
					viewer.resetScreen(ImageResources.instance().getImage("emu5"));
					setSize(1000,750);
					
					break;
				case 5:
					ae.setScreenMode(AndroidEditor.ScreenMode.WVGA_PORTRAIT);
					viewer.resetScreen(ImageResources.instance().getImage("emu6"));
					setSize(1000,750);
					
					break;
				}
				jsp.validate();
				viewer.repaint();
			}
		});
		
		validate();
	}

	public void clear() {
		AndroidEditor.instance().removeAllWidgets();
		AndroidEditor.instance().select( AndroidEditor.instance().getLayout() );
		if (text != null) {
			text.setText("");
		} else {
			jtext.setText("");
		}
	}
	
	class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 5831210554019337455L;

		public ClearAction() {
			super("Clear");
		}

		public void actionPerformed(ActionEvent e) {
			clear();
			repaint();
		}
	}
}
