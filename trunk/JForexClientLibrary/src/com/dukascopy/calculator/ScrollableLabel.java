/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.text.Caret;
/*     */ 
/*     */ public abstract class ScrollableLabel extends JViewport
/*     */ {
/*     */   protected boolean caretVisible;
/*     */   protected Navigator navigator;
/*     */   protected String expression;
/*     */   protected int dotPosition;
/*     */   protected JPanel jPanel;
/*     */   protected JTextPane textPane;
/*     */   protected ReadOnlyDisplayPanel panel;
/*     */   protected boolean newExpression;
/*     */   protected Action backward;
/*     */   protected Action forward;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public ScrollableLabel(ReadOnlyDisplayPanel panel, Navigator navigator)
/*     */   {
/*  21 */     setBackground(Color.white);
/*  22 */     this.jPanel = new WhitePanel();
/*  23 */     this.textPane = new JTextPane();
/*  24 */     this.textPane.setContentType("text/html");
/*  25 */     this.textPane.setEditable(false);
/*  26 */     this.navigator = navigator;
/*  27 */     this.textPane.setNavigationFilter(navigator);
/*  28 */     this.jPanel.add(this.textPane);
/*  29 */     setView(this.jPanel);
/*  30 */     this.expression = "";
/*  31 */     this.dotPosition = 0;
/*  32 */     this.newExpression = true;
/*  33 */     this.panel = panel;
/*  34 */     update(false);
/*  35 */     this.backward = this.textPane.getActionMap().get("caret-backward");
/*  36 */     this.forward = this.textPane.getActionMap().get("caret-forward");
/*     */ 
/*  39 */     this.textPane.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent e) {
/*  41 */         ((MainCalculatorPanel)((DisplayPanel)ScrollableLabel.this.panel).getApplet()).requestFocus();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public abstract void update(boolean paramBoolean);
/*     */ 
/*     */   public void left()
/*     */   {
/*  61 */     int pos = this.textPane.getCaret().getDot();
/*     */ 
/*  63 */     for (ListIterator i = this.navigator.dots().listIterator(); i.hasNext(); ) {
/*  64 */       int p = ((Integer)i.next()).intValue();
/*  65 */       if (p >= pos) {
/*  66 */         i.previous();
/*  67 */         break;
/*     */       }
/*     */     }
/*  70 */     this.dotPosition = (i.hasPrevious() ? ((Integer)i.previous()).intValue() : 0);
/*     */   }
/*     */ 
/*     */   public void right()
/*     */   {
/*  80 */     int pos = this.textPane.getCaret().getDot();
/*     */ 
/*  82 */     ListIterator i = this.navigator.dots().listIterator(this.navigator.dots().size());
/*  83 */     while (i.hasPrevious()) {
/*  84 */       int p = ((Integer)i.previous()).intValue();
/*  85 */       if (p <= pos) {
/*  86 */         i.next();
/*  87 */         break;
/*     */       }
/*     */     }
/*  90 */     this.dotPosition = (i.hasNext() ? (Integer)i.next() : (Integer)this.navigator.dots().lastElement()).intValue();
/*     */   }
/*     */ 
/*     */   protected int getDotPosition()
/*     */   {
/* 101 */     if (this.navigator == null)
/* 102 */       System.out.println("ScrollableLabel.getDotPosition(): navigator == null");
/* 103 */     int p = this.navigator.dots().indexOf(Integer.valueOf(this.textPane.getCaret().getDot()));
/* 104 */     return p == -1 ? 0 : p;
/*     */   }
/*     */ 
/*     */   public void newExpression()
/*     */   {
/* 114 */     this.newExpression = true;
/* 115 */     setCaretVisible(false);
/*     */   }
/*     */ 
/*     */   public void updateCaretVisibility()
/*     */   {
/* 122 */     if ((this.caretVisible) && (this.panel.hasCaret(this)))
/* 123 */       this.textPane.getCaret().setVisible(true);
/*     */     else {
/* 125 */       this.textPane.getCaret().setVisible(false);
/*     */     }
/* 127 */     this.textPane.getCaret().setDot(this.dotPosition);
/*     */   }
/*     */ 
/*     */   public void setCaretVisible(boolean b)
/*     */   {
/* 135 */     this.caretVisible = b;
/* 136 */     updateCaretVisibility();
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g) {
/* 140 */     Graphics2D g2 = (Graphics2D)g;
/* 141 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 143 */     super.paintComponent(g);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ScrollableLabel
 * JD-Core Version:    0.6.0
 */