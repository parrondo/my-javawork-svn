/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import java.util.Vector;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.text.Caret;
/*     */ 
/*     */ public class EntryLabel extends ScrollableLabel
/*     */ {
/*     */   private static final String endhtml = "<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>";
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public EntryLabel(ReadOnlyDisplayPanel panel)
/*     */   {
/*  13 */     super(panel, new EntryNavigator());
/*     */   }
/*     */ 
/*     */   public void update(boolean on)
/*     */   {
/*  25 */     StringBuilder text = new StringBuilder();
/*  26 */     text.append("<html><p style=\"font-size:");
/*  27 */     text.append(Float.toString(this.panel.getApplet().entryTextSize()));
/*     */ 
/*  29 */     text.append("pt\">");
/*  30 */     if (on) {
/*  31 */       if (this.panel.getApplet().getMode() == 0) {
/*  32 */         setCaretVisible(true);
/*  33 */         text.append(this.expression);
/*  34 */         text.append("<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>");
/*  35 */         this.textPane.setText(text.toString());
/*  36 */         this.textPane.getCaret().setDot(this.dotPosition);
/*  37 */       } else if (this.panel.getApplet().getMode() == 2) {
/*  38 */         setCaretVisible(false);
/*  39 */         text.append("Degrees: 1&nbsp;&nbsp;Radians: 2");
/*  40 */         text.append("<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>");
/*  41 */         this.textPane.setText(text.toString());
/*  42 */         this.textPane.getCaret().setDot(1);
/*  43 */       } else if (this.panel.getApplet().getMode() == 1) {
/*  44 */         setCaretVisible(false);
/*  45 */         text.append("Comp: 1&nbsp;&nbsp;Stat: 2");
/*  46 */         text.append("<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>");
/*  47 */         this.textPane.setText(text.toString());
/*  48 */         this.textPane.getCaret().setDot(1);
/*  49 */       } else if (this.panel.getApplet().getMode() == 3) {
/*  50 */         setCaretVisible(false);
/*  51 */         text.append("Set size: 0&ndash;");
/*  52 */         text.append(this.panel.getApplet().getSizesSize() - 1);
/*  53 */         text.append(" (current value: ");
/*  54 */         text.append(this.panel.getApplet().minSize() - this.panel.getApplet().getMinSize());
/*     */ 
/*  56 */         text.append(")");
/*  57 */         text.append("<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>");
/*  58 */         this.textPane.setText(text.toString());
/*  59 */         this.textPane.getCaret().setDot(1);
/*     */       }
/*     */     } else {
/*  62 */       setCaretVisible(false);
/*  63 */       text.append("<sup> </sup><sub>&nbsp;<sub>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sup> </sup><sub>&nbsp;</sub></p></html>");
/*  64 */       this.textPane.setText(text.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Action backward()
/*     */   {
/*  74 */     return this.backward;
/*     */   }
/*     */ 
/*     */   public synchronized void delete(Parser parser)
/*     */   {
/*  84 */     this.textPane.getCaret().setDot(this.textPane.getCaretPosition());
/*  85 */     int position = getDotPosition();
/*     */ 
/*  90 */     PObject p = parser.del(position);
/*  91 */     if (p == null) {
/*  92 */       this.dotPosition = ((Integer)this.navigator.dots().get(position)).intValue();
/*  93 */       return;
/*     */     }
/*     */ 
/*  96 */     int length = 0;
/*  97 */     length = p.name_array().length;
/*  98 */     this.expression = parser.getExpression();
/*     */ 
/* 100 */     int currentDot = ((Integer)this.navigator.dots().get(position)).intValue();
/* 101 */     this.navigator.dots().remove(position);
/* 102 */     for (int i = position; i < this.navigator.dots().size(); i++) {
/* 103 */       int q = ((Integer)this.navigator.dots().elementAt(i)).intValue();
/* 104 */       q -= length;
/* 105 */       this.navigator.dots().setElementAt(Integer.valueOf(q), i);
/*     */     }
/*     */ 
/* 108 */     this.dotPosition = (currentDot - length);
/*     */   }
/*     */ 
/*     */   public synchronized void setExpression(Parser parser)
/*     */   {
/* 117 */     this.expression = parser.getExpression();
/* 118 */     this.newExpression = false;
/* 119 */     this.navigator.dots().clear();
/* 120 */     int i = 1;
/* 121 */     this.navigator.dots().add(Integer.valueOf(i));
/* 122 */     for (PObject pobject : parser.getList()) {
/* 123 */       i += pobject.name_array().length;
/* 124 */       this.navigator.dots().add(Integer.valueOf(i));
/*     */     }
/* 126 */     this.dotPosition = i;
/*     */   }
/*     */ 
/*     */   public synchronized void clear(Parser parser)
/*     */   {
/* 135 */     parser.clearExpression();
/* 136 */     this.expression = "";
/* 137 */     this.dotPosition = 0;
/*     */   }
/*     */ 
/*     */   public synchronized void insert(PObject p, Parser parser)
/*     */   {
/* 147 */     this.textPane.getCaret().setDot(this.textPane.getCaretPosition());
/* 148 */     int position = getDotPosition();
/*     */ 
/* 151 */     int length = p.name_array().length;
/*     */ 
/* 157 */     if (this.newExpression) {
/* 158 */       this.newExpression = false;
/* 159 */       this.navigator.dots().clear();
/* 160 */       this.navigator.dots().add(Integer.valueOf(1));
/* 161 */       position = 0;
/*     */     }
/* 163 */     parser.add(position, p);
/* 164 */     this.expression = parser.getExpression();
/*     */ 
/* 166 */     int currentDot = ((Integer)this.navigator.dots().get(position)).intValue();
/* 167 */     for (int i = position; i < this.navigator.dots().size(); i++) {
/* 168 */       int q = ((Integer)this.navigator.dots().elementAt(i)).intValue();
/* 169 */       q += length;
/* 170 */       this.navigator.dots().setElementAt(Integer.valueOf(q), i);
/*     */     }
/* 172 */     this.navigator.dots().insertElementAt(Integer.valueOf(currentDot), position);
/*     */ 
/* 174 */     this.dotPosition = (currentDot + length);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.EntryLabel
 * JD-Core Version:    0.6.0
 */