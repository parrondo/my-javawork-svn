/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.Spring;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.SpringLayout.Constraints;
/*     */ 
/*     */ public class DisplayPanel extends JPanel
/*     */   implements ReadOnlyDisplayPanel
/*     */ {
/*     */   private ScrollableLabel labelWithCaret;
/*     */   private EntryLabel entryLabel;
/*     */   private DisplayLabel displayLabel;
/*     */   private ExtraPanel extraPanel;
/*     */   private MiniPanel leftPanel;
/*     */   private MiniPanel rightPanel;
/*     */   private final MainCalculatorPanel applet;
/*     */   private boolean on;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public DisplayPanel(MainCalculatorPanel applet)
/*     */   {
/*  27 */     this.applet = applet;
/*  28 */     this.entryLabel = new EntryLabel(this);
/*  29 */     this.displayLabel = new DisplayLabel(this);
/*  30 */     this.extraPanel = new ExtraPanel(this);
/*  31 */     this.leftPanel = new MiniPanel(true);
/*  32 */     this.rightPanel = new MiniPanel(false);
/*  33 */     add(this.entryLabel);
/*  34 */     add(this.displayLabel);
/*  35 */     setCaretToEntry();
/*  36 */     add(this.extraPanel);
/*     */   }
/*     */ 
/*     */   public void setUp()
/*     */   {
/*  50 */     Insets insets = getInsets();
/*  51 */     int ib = insets.bottom;
/*  52 */     int il = insets.left;
/*  53 */     int ir = insets.right;
/*  54 */     int it = insets.top;
/*     */ 
/*  57 */     int b = getApplet().strutSize() + 4 * getApplet().minSize() + 6 * getApplet().buttonWidth();
/*     */ 
/*  59 */     int c = getApplet().displayHeight();
/*     */ 
/*  61 */     int d = (int)(0.09D * b + 0.5D);
/*     */ 
/*  63 */     int v = (int)(0.125D * getApplet().extraTextSize() + 0.5D);
/*  64 */     int h = 2 * v;
/*  65 */     int dh = (int)(0.58D * (c - 3 * v) + 0.5D);
/*  66 */     int m = (int)(0.1D * dh + 0.5D);
/*     */ 
/*  69 */     Spring vSpring = Spring.constant(v, v, v);
/*     */ 
/*  71 */     Spring hSpring = Spring.sum(vSpring, vSpring);
/*     */ 
/*  73 */     Spring eSpring = Spring.constant(d);
/*     */ 
/*  75 */     Spring dSpring = Spring.constant(dh);
/*     */ 
/*  77 */     Spring mSpring = Spring.constant(m);
/*     */ 
/*  79 */     Spring rSpring = Spring.sum(mSpring, hSpring);
/*     */ 
/*  81 */     Spring zSpring = Spring.constant(0, 0, 2);
/*     */ 
/*  83 */     SpringLayout layout = new SpringLayout();
/*  84 */     setLayout(layout);
/*  85 */     SpringLayout.Constraints constraints = null;
/*     */ 
/*  88 */     constraints = layout.getConstraints(this.entryLabel);
/*  89 */     constraints.setWidth(Spring.constant(b - 2 * h - m));
/*  90 */     constraints.setHeight(Spring.constant(c - 2 * v - dh - ib - it));
/*  91 */     layout.putConstraint("North", this.entryLabel, vSpring, "North", this);
/*     */ 
/*  93 */     layout.putConstraint("West", this.entryLabel, hSpring, "West", this);
/*     */ 
/*  95 */     layout.putConstraint("East", this.entryLabel, Spring.minus(rSpring), "East", this);
/*     */ 
/*  99 */     constraints = layout.getConstraints(this.extraPanel);
/* 100 */     constraints.setWidth(eSpring);
/* 101 */     constraints.setHeight(dSpring);
/* 102 */     layout.putConstraint("North", this.extraPanel, zSpring, "South", this.entryLabel);
/*     */ 
/* 104 */     layout.putConstraint("South", this.extraPanel, Spring.minus(vSpring), "South", this);
/*     */ 
/* 107 */     layout.putConstraint("West", this.extraPanel, hSpring, "West", this);
/*     */ 
/* 110 */     constraints = layout.getConstraints(this.leftPanel);
/* 111 */     constraints.setWidth(mSpring);
/* 112 */     constraints.setHeight(dSpring);
/* 113 */     layout.putConstraint("North", this.leftPanel, zSpring, "South", this.entryLabel);
/*     */ 
/* 115 */     layout.putConstraint("South", this.leftPanel, Spring.minus(vSpring), "South", this);
/*     */ 
/* 118 */     layout.putConstraint("West", this.leftPanel, zSpring, "East", this.extraPanel);
/*     */ 
/* 121 */     constraints = layout.getConstraints(this.displayLabel);
/* 122 */     constraints.setHeight(dSpring);
/* 123 */     constraints.setWidth(Spring.constant(b - d - 2 * m - 2 * h - il - ir));
/*     */ 
/* 125 */     layout.putConstraint("North", this.displayLabel, zSpring, "South", this.entryLabel);
/*     */ 
/* 127 */     layout.putConstraint("South", this.displayLabel, Spring.minus(vSpring), "South", this);
/*     */ 
/* 130 */     layout.putConstraint("West", this.displayLabel, zSpring, "East", this.leftPanel);
/*     */ 
/* 132 */     constraints = layout.getConstraints(this.displayLabel);
/*     */ 
/* 134 */     constraints.setHeight(dSpring);
/*     */ 
/* 136 */     constraints = layout.getConstraints(this.rightPanel);
/* 137 */     constraints.setWidth(mSpring);
/* 138 */     constraints.setHeight(dSpring);
/* 139 */     layout.putConstraint("North", this.rightPanel, zSpring, "South", this.entryLabel);
/*     */ 
/* 141 */     layout.putConstraint("South", this.rightPanel, Spring.minus(vSpring), "South", this);
/*     */ 
/* 144 */     layout.putConstraint("West", this.rightPanel, zSpring, "East", this.displayLabel);
/*     */ 
/* 146 */     layout.putConstraint("East", this.rightPanel, Spring.minus(hSpring), "East", this);
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics graphics)
/*     */   {
/* 159 */     graphics.setColor(Color.WHITE);
/* 160 */     graphics.fillRect(0, 0, getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   public void left()
/*     */   {
/* 170 */     this.labelWithCaret.left();
/*     */   }
/*     */ 
/*     */   public void right()
/*     */   {
/* 180 */     this.labelWithCaret.right();
/*     */   }
/*     */ 
/*     */   public void update(boolean entry, boolean extra)
/*     */   {
/* 193 */     if (entry)
/*     */     {
/* 195 */       this.entryLabel.update(this.on);
/*     */     }
/* 197 */     this.displayLabel.update(this.on);
/* 198 */     this.extraPanel.repaint();
/* 199 */     LeftOrRight leftOrRight = LeftOrRight.NEITHER;
/* 200 */     if (displayLabelHasCaret()) {
/* 201 */       leftOrRight = this.displayLabel.getScrollDirections();
/*     */     }
/* 203 */     switch (1.$SwitchMap$com$dukascopy$calculator$LeftOrRight[leftOrRight.ordinal()]) {
/*     */     case 1:
/* 205 */       this.leftPanel.setIlluminated(true);
/* 206 */       this.rightPanel.setIlluminated(false);
/* 207 */       break;
/*     */     case 2:
/* 209 */       this.leftPanel.setIlluminated(false);
/* 210 */       this.rightPanel.setIlluminated(true);
/* 211 */       break;
/*     */     case 3:
/* 213 */       this.leftPanel.setIlluminated(true);
/* 214 */       this.rightPanel.setIlluminated(true);
/* 215 */       break;
/*     */     case 4:
/* 217 */       this.leftPanel.setIlluminated(false);
/* 218 */       this.rightPanel.setIlluminated(false);
/* 219 */       break;
/*     */     default:
/* 221 */       this.leftPanel.setIlluminated(false);
/* 222 */       this.rightPanel.setIlluminated(false);
/*     */     }
/*     */ 
/* 225 */     this.leftPanel.repaint();
/* 226 */     this.rightPanel.repaint();
/*     */   }
/*     */ 
/*     */   final boolean displayLabelScrollable()
/*     */   {
/* 235 */     return this.displayLabel.getScrollDirections() != LeftOrRight.NEITHER;
/*     */   }
/*     */ 
/*     */   public Action backward()
/*     */   {
/* 245 */     return this.entryLabel.backward();
/*     */   }
/*     */ 
/*     */   public void setExpression(Parser parser)
/*     */   {
/* 254 */     this.entryLabel.setExpression(parser);
/*     */   }
/*     */ 
/*     */   public void newExpression()
/*     */   {
/* 262 */     this.entryLabel.newExpression();
/*     */   }
/*     */ 
/*     */   public void clear(Parser parser)
/*     */   {
/* 272 */     this.entryLabel.clear(parser);
/*     */   }
/*     */ 
/*     */   public void delete(Parser parser)
/*     */   {
/* 282 */     this.entryLabel.delete(parser);
/*     */   }
/*     */ 
/*     */   public void insert(PObject p, Parser parser)
/*     */   {
/* 293 */     this.entryLabel.insert(p, parser);
/*     */   }
/*     */ 
/*     */   public void setOn(boolean value)
/*     */   {
/* 302 */     this.on = value;
/*     */   }
/*     */ 
/*     */   public boolean getOn()
/*     */   {
/* 311 */     return this.on;
/*     */   }
/*     */ 
/*     */   public void setValue()
/*     */   {
/* 318 */     this.displayLabel.setNewExpression();
/*     */   }
/*     */ 
/*     */   public ReadOnlyCalculatorApplet getApplet()
/*     */   {
/* 326 */     return this.applet;
/*     */   }
/*     */ 
/*     */   public final DisplayLabel getDisplayLabel()
/*     */   {
/* 332 */     return this.displayLabel;
/*     */   }
/*     */ 
/*     */   public void setCaretToEntry()
/*     */   {
/* 339 */     this.labelWithCaret = this.entryLabel;
/* 340 */     this.displayLabel.updateCaretVisibility();
/* 341 */     this.entryLabel.updateCaretVisibility();
/*     */   }
/*     */ 
/*     */   public void setCaretToDisplay()
/*     */   {
/* 348 */     setCaretToDisplay(true);
/*     */   }
/*     */ 
/*     */   public void setCaretToDisplay(boolean display) {
/* 352 */     if (display) {
/* 353 */       this.labelWithCaret = this.displayLabel;
/* 354 */       this.entryLabel.updateCaretVisibility();
/* 355 */       this.displayLabel.updateCaretVisibility();
/*     */     }
/*     */     else {
/* 358 */       this.entryLabel.setCaretVisible(false);
/* 359 */       this.displayLabel.setCaretVisible(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasCaret(ScrollableLabel scrollableLabel)
/*     */   {
/* 371 */     return this.labelWithCaret == scrollableLabel;
/*     */   }
/*     */ 
/*     */   public boolean displayLabelHasCaret()
/*     */   {
/* 381 */     return hasCaret(this.displayLabel);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.DisplayPanel
 * JD-Core Version:    0.6.0
 */