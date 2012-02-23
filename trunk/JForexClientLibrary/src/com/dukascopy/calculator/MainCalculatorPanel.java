/*      */ package com.dukascopy.calculator;
/*      */ 
/*      */ import com.dukascopy.calculator.button.CalculatorButton;
/*      */ import com.dukascopy.calculator.complex.Complex;
/*      */ import com.dukascopy.calculator.function.Mean;
/*      */ import com.dukascopy.calculator.function.Mode;
/*      */ import com.dukascopy.calculator.function.Numeral;
/*      */ import com.dukascopy.calculator.function.PObject;
/*      */ import com.dukascopy.calculator.function.PopStDev;
/*      */ import com.dukascopy.calculator.function.StDev;
/*      */ import com.dukascopy.calculator.graph.Graph;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Vector;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.Spring;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.border.BevelBorder;
/*      */ import javax.swing.text.JTextComponent;
/*      */ 
/*      */ public class MainCalculatorPanel extends JPanel
/*      */   implements KeyListener, ReadOnlyCalculatorApplet
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   private Graph graph;
/*      */   private OObject value;
/*      */   private AngleType angleType;
/*      */   private int mode;
/*      */   private boolean stat;
/*      */   private OObject memory;
/*      */   private Vector<Complex> statMemory;
/*      */   private Vector<Complex> statMemoryNeg;
/*      */   private int frameHeight;
/*      */   private MainCalculatorPanel applet;
/*      */   private DisplayPanel displayPanel;
/*      */   private AbstractCalculatorPanel calculatorPanel;
/*      */   private HashMap<SpecialButtonType, AbstractCalculatorPanel> calculatorPanels;
/*      */   private int mSize;
/*      */   private Parser parser;
/*      */   private boolean shift;
/*      */   private static final int bWidth = 23;
/*      */   private static final int bHeight = 10;
/*      */   private static final int sSize = 3;
/*      */   private static final int dHeight = 30;
/*      */   private static final float bTextSize = 4.0F;
/*      */   private static final float eTextSize = 6.4F;
/*      */   private static final float dTextSize = 9.2F;
/*      */   private static final float sTextSize = 2.5F;
/*      */   private Vector<HistoryItem> history;
/*      */   private static final int HISTORY_SIZE = 24;
/*      */   HistoryItem tempParserList;
/*      */   int historyPosition;
/*  169 */   private static final Color panelColour = Color.LIGHT_GRAY;
/*      */   protected JFrame jframe;
/*      */   private boolean shiftDown;
/*      */   private Notation notation;
/*      */   private JTextComponent textComponent;
/*      */   private DataTransfer dataTransfer;
/*  196 */   private final int minimumSize = 3;
/*      */   private Vector<Integer> sizes;
/*      */ 
/*      */   public MainCalculatorPanel()
/*      */   {
/*  207 */     this.applet = this;
/*      */   }
/*      */ 
/*      */   public void init()
/*      */   {
/*  216 */     this.applet = this;
/*  217 */     frame(null);
/*      */     try {
/*  219 */       SwingUtilities.invokeAndWait(new Runnable() {
/*  220 */         public void run() { MainCalculatorPanel.this.setup();
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*  235 */     SwingUtilities.invokeLater(new Runnable() {
/*  236 */       public void run() { MainCalculatorPanel.access$000();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static void showFrame()
/*      */   {
/*  245 */     JFrame frame = createFrame();
/*  246 */     frame.setDefaultCloseOperation(3);
/*  247 */     frame.setVisible(true);
/*      */   }
/*      */ 
/*      */   public static JFrame createFrame()
/*      */   {
/*  261 */     MainCalculatorPanel main = new MainCalculatorPanel();
/*  262 */     JFrame frame = new JFrame("Scientific Calculator");
/*  263 */     main.frame(frame);
/*  264 */     main.setup();
/*      */ 
/*  266 */     frame.setContentPane(main);
/*  267 */     frame.setResizable(false);
/*  268 */     frame.setUndecorated(false);
/*  269 */     frame.pack();
/*  270 */     return frame;
/*      */   }
/*      */ 
/*      */   public void setup()
/*      */   {
/*  280 */     setTextComponent(null);
/*  281 */     this.graph = null;
/*      */ 
/*  284 */     setValue(new Complex());
/*      */ 
/*  287 */     setSizes();
/*      */ 
/*  290 */     this.parser = new Parser();
/*  291 */     this.shift = false;
/*  292 */     this.angleType = AngleType.DEGREES;
/*  293 */     this.mode = 0;
/*  294 */     this.stat = false;
/*  295 */     this.notation = new Notation();
/*  296 */     this.memory = new Complex();
/*  297 */     this.statMemory = new Vector();
/*  298 */     this.statMemoryNeg = new Vector();
/*      */ 
/*  301 */     addKeyListener(this);
/*  302 */     setFocusable(true);
/*      */ 
/*  304 */     removeAll();
/*      */ 
/*  307 */     this.displayPanel = new DisplayPanel(this);
/*  308 */     this.displayPanel.setBorder(new BevelBorder(1));
/*  309 */     setPanels();
/*      */ 
/*  312 */     this.dataTransfer = new DataTransfer(this);
/*  313 */     this.dataTransfer.setEnabled(true);
/*      */ 
/*  315 */     KeyStroke copy = KeyStroke.getKeyStroke(67, 2, false);
/*  316 */     registerKeyboardAction(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  319 */         MainCalculatorPanel.this.dataTransfer.copy();
/*      */       }
/*      */     }
/*      */     , "Copy", copy, 0);
/*      */ 
/*  326 */     setBase(Base.DECIMAL);
/*  327 */     this.history = new Vector();
/*  328 */     this.tempParserList = new HistoryItem(null, getAngleType(), getBase(), getNotation());
/*  329 */     this.historyPosition = -1;
/*  330 */     this.shiftDown = false;
/*  331 */     setOn(true);
/*  332 */     setValue(new Complex(0.0D));
/*  333 */     updateDisplay(true, true);
/*  334 */     requestFocusInWindow();
/*      */ 
/*  336 */     resetCalculator();
/*      */   }
/*      */ 
/*      */   public void resetCalculator() {
/*  340 */     ((CalculatorButton)this.calculatorPanel.buttons().elementAt(35)).actionPerformed(null);
/*      */   }
/*      */ 
/*      */   private void setPanels()
/*      */   {
/*  353 */     this.displayPanel.setUp();
/*  354 */     this.calculatorPanels = new HashMap();
/*  355 */     for (SpecialButtonType sbt : SpecialButtonType.values()) {
/*  356 */       AbstractCalculatorPanel calculatorPanel = AbstractCalculatorPanel.createPanel(this, sbt, panelColour);
/*  357 */       this.calculatorPanels.put(sbt, calculatorPanel);
/*      */     }
/*      */ 
/*  360 */     setCalculatorPanel(SpecialButtonType.NONE);
/*      */   }
/*      */ 
/*      */   private void setCalculatorPanel(SpecialButtonType sbt)
/*      */   {
/*  370 */     if (this.calculatorPanel != null)
/*  371 */       remove(this.calculatorPanel);
/*  372 */     this.calculatorPanel = ((AbstractCalculatorPanel)this.calculatorPanels.get(sbt));
/*  373 */     add(this.calculatorPanel);
/*  374 */     this.calculatorPanel.setDisplayPanel();
/*  375 */     this.calculatorPanel.repaint();
/*  376 */     repaint();
/*      */   }
/*      */ 
/*      */   public void clearHistory()
/*      */   {
/*  386 */     this.history = new Vector();
/*  387 */     this.historyPosition = -1;
/*      */   }
/*      */ 
/*      */   public void upHistory()
/*      */   {
/*  399 */     if (this.displayPanel.displayLabelHasCaret()) {
/*  400 */       setCaretToEntry();
/*      */     }
/*      */ 
/*  403 */     if (this.historyPosition >= this.history.size() - 1) return;
/*      */ 
/*  405 */     if (this.historyPosition == -1)
/*      */     {
/*  407 */       if ((getValue() instanceof Error)) {
/*  408 */         setValue(new Complex());
/*      */       }
/*  410 */       this.tempParserList.list = this.parser.getList();
/*  411 */       this.tempParserList.angleType = getAngleType();
/*  412 */       this.tempParserList.base = getBase();
/*  413 */       this.tempParserList.notation = getNotation();
/*      */     }
/*      */     else {
/*  416 */       ((HistoryItem)this.history.elementAt(this.historyPosition)).list = this.parser.getList();
/*  417 */       ((HistoryItem)this.history.elementAt(this.historyPosition)).angleType = getAngleType();
/*  418 */       ((HistoryItem)this.history.elementAt(this.historyPosition)).base = getBase();
/*  419 */       ((HistoryItem)this.history.elementAt(this.historyPosition)).notation = getNotation();
/*      */     }
/*      */ 
/*  422 */     this.historyPosition += 1;
/*      */ 
/*  424 */     this.parser.setList(((HistoryItem)this.history.elementAt(this.historyPosition)).list);
/*  425 */     setAngleType(((HistoryItem)this.history.elementAt(this.historyPosition)).angleType);
/*  426 */     setBase(((HistoryItem)this.history.elementAt(this.historyPosition)).base);
/*  427 */     setNotation(((HistoryItem)this.history.elementAt(this.historyPosition)).notation);
/*      */ 
/*  429 */     this.displayPanel.setExpression(this.parser);
/*      */   }
/*      */ 
/*      */   public boolean downHistory()
/*      */   {
/*  439 */     if (this.historyPosition < 0) {
/*  440 */       if (this.displayPanel.displayLabelScrollable()) {
/*  441 */         setCaretToDisplay();
/*  442 */         return true;
/*      */       }
/*  444 */       return false;
/*      */     }
/*      */ 
/*  447 */     ((HistoryItem)this.history.elementAt(this.historyPosition)).list = this.parser.getList();
/*  448 */     ((HistoryItem)this.history.elementAt(this.historyPosition)).angleType = getAngleType();
/*  449 */     ((HistoryItem)this.history.elementAt(this.historyPosition)).base = getBase();
/*  450 */     ((HistoryItem)this.history.elementAt(this.historyPosition)).notation = getNotation();
/*      */ 
/*  452 */     this.historyPosition -= 1;
/*  453 */     if (this.historyPosition == -1) {
/*  454 */       this.parser.setList(this.tempParserList.list);
/*  455 */       setAngleType(this.tempParserList.angleType);
/*  456 */       setBase(this.tempParserList.base);
/*  457 */       setNotation(this.tempParserList.notation);
/*      */     } else {
/*  459 */       this.parser.setList(((HistoryItem)this.history.elementAt(this.historyPosition)).list);
/*  460 */       setAngleType(((HistoryItem)this.history.elementAt(this.historyPosition)).angleType);
/*  461 */       setBase(((HistoryItem)this.history.elementAt(this.historyPosition)).base);
/*  462 */       setNotation(((HistoryItem)this.history.elementAt(this.historyPosition)).notation);
/*      */     }
/*      */ 
/*  465 */     this.displayPanel.setExpression(this.parser);
/*  466 */     return true;
/*      */   }
/*      */ 
/*      */   public void pushHistory()
/*      */   {
/*  476 */     LinkedList list = this.parser.getList();
/*  477 */     if (list.size() == 0) return;
/*  478 */     this.history.add(0, new HistoryItem(list, getAngleType(), getBase(), getNotation()));
/*      */ 
/*  480 */     while (this.history.size() > 24) {
/*  481 */       this.history.removeElementAt(24);
/*      */     }
/*  483 */     this.historyPosition = -1;
/*      */   }
/*      */ 
/*      */   public void right()
/*      */   {
/*  492 */     this.displayPanel.right();
/*      */   }
/*      */ 
/*      */   public void left()
/*      */   {
/*  501 */     this.displayPanel.left();
/*      */   }
/*      */ 
/*      */   public Action backward()
/*      */   {
/*  510 */     return this.displayPanel.backward();
/*      */   }
/*      */ 
/*      */   public void newExpression()
/*      */   {
/*  519 */     this.displayPanel.newExpression();
/*      */   }
/*      */ 
/*      */   public DisplayPanel displayPanel() {
/*  523 */     return this.displayPanel;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  535 */     this.displayPanel.clear(getParser());
/*  536 */     this.displayPanel.setExpression(this.parser);
/*      */   }
/*      */ 
/*      */   public void delete()
/*      */   {
/*  544 */     this.displayPanel.delete(getParser());
/*      */   }
/*      */ 
/*      */   public void copy()
/*      */   {
/*  551 */     this.dataTransfer.copy();
/*      */   }
/*      */ 
/*      */   public void insert(PObject p)
/*      */   {
/*  560 */     this.displayPanel.insert(p, getParser());
/*      */   }
/*      */ 
/*      */   public void updateDisplay(boolean entry, boolean extra)
/*      */   {
/*  580 */     if (this.displayPanel != null) {
/*  581 */       this.displayPanel.update(entry, extra);
/*      */     }
/*      */ 
/*  584 */     if (this.textComponent != null)
/*  585 */       this.textComponent.setText(getValue().toString());
/*      */   }
/*      */ 
/*      */   public int displayHeight(int minSize)
/*      */   {
/*  590 */     return 30 * minSize;
/*      */   }
/*      */ 
/*      */   public int buttonHeight(int minSize) {
/*  594 */     return 10 * minSize;
/*      */   }
/*      */ 
/*      */   public int buttonWidth(int minSize) {
/*  598 */     return 23 * minSize;
/*      */   }
/*      */ 
/*      */   public int strutSize(int minSize) {
/*  602 */     return 3 * minSize;
/*      */   }
/*      */ 
/*      */   public int displayHeight() {
/*  606 */     return displayHeight(minSize());
/*      */   }
/*      */ 
/*      */   public int buttonHeight() {
/*  610 */     return buttonHeight(minSize());
/*      */   }
/*      */ 
/*      */   public int buttonWidth() {
/*  614 */     return buttonWidth(minSize());
/*      */   }
/*      */ 
/*      */   public int strutSize() {
/*  618 */     return strutSize(minSize());
/*      */   }
/*      */ 
/*      */   public void setMinSize(int mSize)
/*      */   {
/*  631 */     this.mSize = mSize;
/*  632 */     if (frame() == null)
/*  633 */       return;
/*  634 */     setPanels();
/*  635 */     frame().pack();
/*  636 */     if (this.graph != null)
/*  637 */       this.graph.updateMenu();
/*      */   }
/*      */ 
/*      */   public int minSize() {
/*  641 */     return this.mSize;
/*      */   }
/*      */ 
/*      */   public float buttonTextSize() {
/*  645 */     return this.mSize * 4.0F;
/*      */   }
/*      */ 
/*      */   public float entryTextSize() {
/*  649 */     return this.mSize * 6.4F;
/*      */   }
/*      */ 
/*      */   public float displayTextSize() {
/*  653 */     return this.mSize * 9.2F;
/*      */   }
/*      */ 
/*      */   public float extraTextSize() {
/*  657 */     return this.mSize * 2.5F;
/*      */   }
/*      */ 
/*      */   public void setShift(boolean value)
/*      */   {
/*  669 */     this.shift = value;
/*  670 */     if (this.shift) {
/*  671 */       if (this.stat)
/*  672 */         setCalculatorPanel(SpecialButtonType.SHIFT_STAT);
/*      */       else
/*  674 */         switch (4.$SwitchMap$com$dukascopy$calculator$Base[getBase().ordinal()]) {
/*      */         case 1:
/*  676 */           setCalculatorPanel(SpecialButtonType.SHIFT);
/*  677 */           break;
/*      */         default:
/*  679 */           setCalculatorPanel(SpecialButtonType.SHIFT_HEX); break;
/*      */         }
/*      */     }
/*  682 */     else if (this.stat)
/*  683 */       setCalculatorPanel(SpecialButtonType.STAT);
/*      */     else
/*  685 */       switch (4.$SwitchMap$com$dukascopy$calculator$Base[getBase().ordinal()]) {
/*      */       case 1:
/*  687 */         setCalculatorPanel(SpecialButtonType.NONE);
/*  688 */         break;
/*      */       default:
/*  690 */         setCalculatorPanel(SpecialButtonType.HEX);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void setOn(boolean value)
/*      */   {
/*  701 */     if (this.displayPanel != null) {
/*  702 */       this.displayPanel.setOn(value);
/*  703 */       this.displayPanel.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getOn()
/*      */   {
/*  710 */     if (this.displayPanel != null) {
/*  711 */       return this.displayPanel.getOn();
/*      */     }
/*  713 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean getShift() {
/*  717 */     return this.shift;
/*      */   }
/*      */ 
/*      */   public Parser getParser() {
/*  721 */     return this.parser;
/*      */   }
/*      */ 
/*      */   public OObject getValue() {
/*  725 */     return this.value;
/*      */   }
/*      */ 
/*      */   public void setValue(OObject o)
/*      */   {
/*  751 */     this.value = o;
/*  752 */     if ((this.value != null) && (this.displayPanel != null))
/*  753 */       this.displayPanel.setValue();
/*      */   }
/*      */ 
/*      */   public void setMemory(OObject o)
/*      */   {
/*  764 */     this.memory = o;
/*      */   }
/*      */ 
/*      */   public OObject getMemory()
/*      */   {
/*  771 */     return this.memory;
/*      */   }
/*      */ 
/*      */   public void setAngleType(AngleType angleType)
/*      */   {
/*  781 */     this.angleType = angleType;
/*      */   }
/*      */ 
/*      */   public AngleType getAngleType()
/*      */   {
/*  788 */     return this.angleType;
/*      */   }
/*      */ 
/*      */   public boolean getStat()
/*      */   {
/*  796 */     return this.stat;
/*      */   }
/*      */ 
/*      */   public void setStat(boolean stat)
/*      */   {
/*  806 */     this.stat = stat;
/*  807 */     if (stat == true)
/*  808 */       setBase(Base.DECIMAL);
/*  809 */     setShift(getShift());
/*      */   }
/*      */ 
/*      */   public int getMode() {
/*  813 */     return this.mode;
/*      */   }
/*      */ 
/*      */   public void setMode(int i)
/*      */   {
/*  823 */     this.mode = (i % ((frame() != null) && (this.sizes.size() > 1) ? 4 : 3));
/*      */   }
/*      */ 
/*      */   public void setMode(PObject p)
/*      */   {
/*  836 */     if ((p instanceof Numeral)) {
/*  837 */       Numeral numeral = (Numeral)p;
/*  838 */       if (this.mode == 3) {
/*  839 */         int size = 0;
/*  840 */         switch (numeral.get()) { case '1':
/*  841 */           size = 1; break;
/*      */         case '2':
/*  842 */           size = 2; break;
/*      */         case '3':
/*  843 */           size = 3; break;
/*      */         case '4':
/*  844 */           size = 4; break;
/*      */         case '5':
/*  845 */           size = 5; break;
/*      */         case '6':
/*  846 */           size = 6; break;
/*      */         case '7':
/*  847 */           size = 7; break;
/*      */         case '8':
/*  848 */           size = 8; break;
/*      */         case '9':
/*  849 */           size = 9; break;
/*      */         default:
/*  850 */           size = 0;
/*      */         }
/*  852 */         if (size < this.sizes.size()) {
/*  853 */           size = ((Integer)this.sizes.elementAt(size)).intValue();
/*  854 */           if (minSize() != size) setMinSize(size);
/*  855 */           setMode(0);
/*  856 */           updateDisplay(true, true);
/*      */         }
/*  858 */       } else if (numeral.name().equals("1"))
/*      */       {
/*  860 */         if (this.mode == 2)
/*  861 */           setAngleType(AngleType.DEGREES);
/*  862 */         else if (this.mode == 1)
/*  863 */           setStat(false);
/*  864 */         setMode(0);
/*  865 */         updateDisplay(true, true);
/*  866 */       } else if (numeral.name().equals("2"))
/*      */       {
/*  868 */         if (this.mode == 2)
/*  869 */           setAngleType(AngleType.RADIANS);
/*  870 */         else if (this.mode == 1)
/*  871 */           setStat(true);
/*  872 */         setMode(0);
/*  873 */         updateDisplay(true, true);
/*  874 */       } else if (numeral.name().equals("3")) {
/*  875 */         setMode(0);
/*  876 */         updateDisplay(true, true);
/*      */       }
/*  878 */     } else if ((p instanceof Mode)) {
/*  879 */       setMode(getMode() + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearStatMemory()
/*      */   {
/*  888 */     this.statMemory.clear();
/*  889 */     this.statMemoryNeg.clear();
/*      */   }
/*      */ 
/*      */   public Complex statAdd(Complex d)
/*      */   {
/*  904 */     this.statMemory.add(d);
/*  905 */     return new Complex(statSize());
/*      */   }
/*      */ 
/*      */   public Complex statSub(Complex d)
/*      */   {
/*  915 */     this.statMemoryNeg.add(d);
/*  916 */     return new Complex(statSize());
/*      */   }
/*      */ 
/*      */   private double statSize()
/*      */   {
/*  925 */     return this.statMemory.size() - this.statMemoryNeg.size();
/*      */   }
/*      */ 
/*      */   public Mean statMean() {
/*  929 */     Mean mean = new Mean();
/*  930 */     Complex d = new Complex();
/*  931 */     for (Complex o : this.statMemory) {
/*  932 */       d = d.add(o);
/*      */     }
/*  934 */     for (Complex o : this.statMemoryNeg) {
/*  935 */       d = d.subtract(o);
/*      */     }
/*  937 */     if (statSize() > 0.0D)
/*  938 */       mean.setValue(d.divide(new Complex(statSize(), 0.0D)));
/*      */     else {
/*  940 */       mean.setError(true);
/*      */     }
/*  942 */     return mean;
/*      */   }
/*      */ 
/*      */   public Complex statSumSquares() {
/*  946 */     Mean m = statMean();
/*  947 */     if ((m.error()) || (!(m.value() instanceof Complex)))
/*  948 */       throw new RuntimeException("Stat Error");
/*  949 */     Complex e = (Complex)(Complex)m.value();
/*  950 */     Complex d = new Complex();
/*  951 */     for (Complex o : this.statMemory) {
/*  952 */       Complex c = o.subtract(e);
/*  953 */       d = d.add(c.square());
/*      */     }
/*  955 */     for (Complex o : this.statMemoryNeg) {
/*  956 */       Complex c = o.subtract(e);
/*  957 */       d = d.subtract(c.square());
/*      */     }
/*  959 */     return d;
/*      */   }
/*      */ 
/*      */   public StDev statSampleStDev() {
/*  963 */     StDev stDev = new StDev();
/*      */     try {
/*  965 */       Complex d = statSumSquares();
/*      */ 
/*  967 */       if (statSize() < 2.0D) {
/*  968 */         stDev.setError(true);
/*  969 */         return stDev;
/*      */       }
/*  971 */       stDev.setValue(d.divide(new Complex(statSize() - 1.0D, 0.0D)).sqrt());
/*  972 */       return stDev;
/*      */     }
/*      */     catch (Exception e) {
/*  975 */       stDev.setError(true);
/*  976 */     }return stDev;
/*      */   }
/*      */ 
/*      */   public PopStDev statPopulationStDev()
/*      */   {
/*  981 */     PopStDev stDev = new PopStDev();
/*      */     try {
/*  983 */       Complex d = statSumSquares();
/*      */ 
/*  985 */       if (statSize() < 1.0D) {
/*  986 */         stDev.setError(true);
/*  987 */         return stDev;
/*      */       }
/*  989 */       stDev.setValue(d.divide(new Complex(statSize(), 0.0D)).sqrt());
/*  990 */       return stDev;
/*      */     }
/*      */     catch (Exception e) {
/*  993 */       stDev.setError(true);
/*  994 */     }return stDev;
/*      */   }
/*      */ 
/*      */   public void frame(JFrame jframe)
/*      */   {
/* 1004 */     this.jframe = jframe;
/*      */   }
/*      */ 
/*      */   public JFrame frame() {
/* 1008 */     return this.jframe;
/*      */   }
/*      */ 
/*      */   public void keyPressed(KeyEvent keyEvent)
/*      */   {
/* 1017 */     if (keyEvent.getKeyCode() == 37)
/* 1018 */       ((CalculatorButton)this.calculatorPanel.buttons().elementAt(40)).actionPerformed(null);
/* 1019 */     else if (keyEvent.getKeyCode() == 39)
/* 1020 */       ((CalculatorButton)this.calculatorPanel.buttons().elementAt(41)).actionPerformed(null);
/* 1021 */     else if (keyEvent.getKeyCode() == 38)
/* 1022 */       ((CalculatorButton)this.calculatorPanel.buttons().elementAt(43)).actionPerformed(null);
/* 1023 */     else if (keyEvent.getKeyCode() == 40)
/* 1024 */       ((CalculatorButton)this.calculatorPanel.buttons().elementAt(44)).actionPerformed(null);
/* 1025 */     else if (keyEvent.getKeyCode() == 65406) {
/* 1026 */       if (!this.shiftDown) {
/* 1027 */         ((CalculatorButton)this.calculatorPanel.buttons().elementAt(10)).actionPerformed(null);
/* 1028 */         this.shiftDown = true;
/*      */       }
/*      */     }
/* 1031 */     else if (keyEvent.getKeyCode() == 65485)
/* 1032 */       if (!this.shiftDown) {
/* 1033 */         copy();
/*      */       }
/*      */       else
/* 1036 */         ((CalculatorButton)this.calculatorPanel.buttons().elementAt(15)).actionPerformed(null);
/*      */   }
/*      */ 
/*      */   public void keyReleased(KeyEvent keyEvent)
/*      */   {
/* 1052 */     if (keyEvent.getKeyCode() == 65406) {
/* 1053 */       this.shiftDown = false;
/*      */     } else {
/* 1055 */       char c = keyEvent.getKeyChar();
/*      */ 
/* 1059 */       if (((keyEvent.getModifiersEx() & 0x80) != 0) && (keyEvent.getKeyCode() == 67))
/*      */       {
/* 1062 */         this.dataTransfer.copy();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void keyTyped(KeyEvent keyEvent)
/*      */   {
/* 1080 */     if ((keyEvent.getModifiersEx() & 0x80) == 0) {
/* 1081 */       char c = keyEvent.getKeyChar();
/* 1082 */       if (c == '\n')
/* 1083 */         c = '=';
/* 1084 */       CalculatorButton b = (CalculatorButton)this.calculatorPanel.keyMap().get(Character.valueOf(c));
/* 1085 */       if (b != null) {
/* 1086 */         b.doClick();
/*      */       }
/* 1088 */       else if ((c == '\b') && (!this.shift)) {
/* 1089 */         ((CalculatorButton)this.calculatorPanel.buttons().elementAt(30)).actionPerformed(null);
/*      */       }
/* 1091 */       else if (c == '\033')
/* 1092 */         resetCalculator();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Spring scale(Spring spring, int s)
/*      */   {
/* 1108 */     if (s < 2)
/* 1109 */       return spring;
/* 1110 */     Spring result = Spring.sum(spring, spring);
/* 1111 */     for (int i = 2; i < s; i++) {
/* 1112 */       result = Spring.sum(result, spring);
/*      */     }
/* 1114 */     return result;
/*      */   }
/*      */ 
/*      */   public Base getBase() {
/* 1118 */     return this.parser.base();
/*      */   }
/*      */ 
/*      */   public void setBase(Base b)
/*      */   {
/* 1132 */     if (getBase() == b) return;
/*      */ 
/* 1134 */     this.parser.base(b);
/*      */ 
/* 1137 */     switch (4.$SwitchMap$com$dukascopy$calculator$Base[b.ordinal()]) {
/*      */     case 1:
/* 1139 */       setCalculatorPanel(SpecialButtonType.NONE);
/* 1140 */       break;
/*      */     default:
/* 1142 */       setCalculatorPanel(SpecialButtonType.HEX);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTextComponent(JTextComponent textComponent)
/*      */   {
/* 1154 */     this.textComponent = textComponent;
/*      */   }
/*      */ 
/*      */   public final JTextComponent getTextComponent()
/*      */   {
/* 1164 */     return this.textComponent;
/*      */   }
/*      */ 
/*      */   public Notation getNotation()
/*      */   {
/* 1171 */     return this.notation;
/*      */   }
/*      */ 
/*      */   public void setNotation(Notation notation)
/*      */   {
/* 1178 */     this.notation = notation;
/*      */   }
/*      */ 
/*      */   public final Insets getFrameInsets()
/*      */   {
/* 1186 */     if (this.jframe == null) return new Insets(29, 4, 4, 4);
/* 1187 */     return this.jframe.getInsets();
/*      */   }
/*      */ 
/*      */   private int getFrameWidth(int mSize)
/*      */   {
/* 1196 */     int x = getFrameInsets().left + getFrameInsets().right;
/* 1197 */     return 4 * strutSize(mSize) + 5 * mSize + 8 * buttonWidth(mSize) + x;
/*      */   }
/*      */ 
/*      */   private int getFrameHeight(int mSize)
/*      */   {
/* 1206 */     int y = getFrameInsets().top + getFrameInsets().bottom;
/* 1207 */     return 3 * strutSize(mSize) + 4 * mSize + 5 * buttonHeight(mSize) + displayHeight(mSize) + y;
/*      */   }
/*      */ 
/*      */   public int graphHeight()
/*      */   {
/* 1216 */     if (this.jframe == null) {
/* 1217 */       return getFrameHeight(this.mSize);
/*      */     }
/* 1219 */     return this.jframe.getHeight();
/*      */   }
/*      */ 
/*      */   private void setSizes()
/*      */   {
/* 1227 */     this.mSize = 4;
/*      */ 
/* 1230 */     if (this.jframe == null) return;
/*      */ 
/* 1233 */     int width = Toolkit.getDefaultToolkit().getScreenSize().width;
/* 1234 */     int height = Toolkit.getDefaultToolkit().getScreenSize().height;
/*      */ 
/* 1236 */     int max = 3;
/* 1237 */     for (int i = 0; i < 11; i++) {
/* 1238 */       if ((getFrameWidth(3 + i) > width) || (getFrameHeight(3 + i) > height))
/*      */       {
/* 1240 */         max = i;
/* 1241 */         break;
/* 1242 */       }if (i == 10) {
/* 1243 */         max = i;
/*      */       }
/*      */     }
/* 1246 */     this.sizes = new Vector(max);
/* 1247 */     for (int i = 3; i < 3 + max; i++) {
/* 1248 */       this.sizes.add(Integer.valueOf(i));
/*      */     }
/* 1250 */     if (max < 4)
/* 1251 */       this.mSize = 3;
/*      */   }
/*      */ 
/*      */   public int getSizesSize() {
/* 1255 */     return this.sizes.size();
/*      */   }
/*      */ 
/*      */   public int getMinSize() {
/* 1259 */     return 3;
/*      */   }
/*      */ 
/*      */   private void setCaretToEntry()
/*      */   {
/* 1266 */     this.displayPanel.setCaretToEntry();
/*      */   }
/*      */ 
/*      */   private void setCaretToDisplay()
/*      */   {
/* 1273 */     this.displayPanel.setCaretToDisplay();
/*      */   }
/*      */ 
/*      */   public void displayGraph()
/*      */   {
/* 1280 */     if (this.graph == null)
/* 1281 */       this.graph = new Graph(this);
/*      */     else
/* 1283 */       this.graph.setVisible(true);
/* 1284 */     this.graph.setLocus(getValue());
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.MainCalculatorPanel
 * JD-Core Version:    0.6.0
 */