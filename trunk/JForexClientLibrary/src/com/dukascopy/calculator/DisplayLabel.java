/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Caret;
/*     */ import javax.swing.text.DefaultCaret;
/*     */ 
/*     */ public class DisplayLabel extends ScrollableLabel
/*     */ {
/*     */   private DisplayCaret displayCaret;
/*     */   private ScrollData scrollData;
/*     */   private boolean clearDisplay;
/*     */   public static final long BIAS = 1023L;
/*     */   public static final long E_MAX = 1023L;
/*     */   public static final long E_MIN = -1022L;
/*     */   private static final int DIGITS = 20;
/*     */   private static final String start = "<sub>&nbsp;</sub>&nbsp;<sup>&nbsp;</sup>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public DisplayLabel(ReadOnlyDisplayPanel panel)
/*     */   {
/*  16 */     super(panel, new DisplayNavigator());
/*  17 */     this.scrollData = new ScrollData(this);
/*  18 */     this.displayCaret = new DisplayCaret(this.scrollData);
/*  19 */     setNewExpression();
/*  20 */     this.textPane.setCaret(this.displayCaret);
/*  21 */     this.clearDisplay = false;
/*     */   }
/*     */ 
/*     */   public void setNewExpression()
/*     */   {
/*  28 */     int m = getDigits();
/*  29 */     int precision = 20;
/*  30 */     Base b = getBase();
/*  31 */     Notation n = this.panel.getApplet().getNotation();
/*  32 */     double factor = 1.0D;
/*  33 */     if (this.panel.getApplet().getAngleType() == AngleType.DEGREES)
/*  34 */       factor = 1.0D;
/*  35 */     OObject o = this.panel.getApplet().getValue();
/*  36 */     if (o != null)
/*  37 */       setExpression(o, m, precision, b, n, factor);
/*  38 */     synchronized (this.displayCaret) {
/*  39 */       this.displayCaret.updateFlag = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCaretVisible(boolean b)
/*     */   {
/*  49 */     this.caretVisible = false;
/*  50 */     updateCaretVisibility();
/*     */   }
/*     */ 
/*     */   public void update(boolean on)
/*     */   {
/*  59 */     if (on) {
/*  60 */       if (this.panel.getApplet().getMode() == 0) {
/*  61 */         synchronized (this.displayCaret) {
/*  62 */           if (this.clearDisplay) setNewExpression();
/*  63 */           this.textPane.setText(this.expression);
/*  64 */           this.textPane.getCaret().setDot(this.dotPosition);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  73 */         clear();
/*  74 */         this.textPane.setText(this.expression);
/*     */       }
/*     */     } else {
/*  77 */       clear();
/*  78 */       this.textPane.setText(this.expression);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setExpression(OObject o, int m, int precision, Base b, Notation n, double factor)
/*     */   {
/*  92 */     StringBuilder sb = new StringBuilder();
/*  93 */     sb.append("<html><p style=\"font-size:");
/*  94 */     sb.append(Float.toString(this.panel.getApplet().displayTextSize()));
/*  95 */     sb.append("pt;font-family:");
/*  96 */     sb.append(UIManager.getFont("Label.font").getName());
/*  97 */     sb.append("\">");
/*  98 */     sb.append("<sub>&nbsp;</sub>&nbsp;<sup>&nbsp;</sup>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
/*  99 */     this.navigator.dots().clear();
/* 100 */     int i = 53;
/* 101 */     this.navigator.dots().add(Integer.valueOf(i));
/* 102 */     StringArray stringArray = o.toHTMLStringVector(m, precision, b, n, factor);
/*     */ 
/* 104 */     stringArray.removeDoubleSuperscripts();
/* 105 */     for (Vector v : stringArray) {
/* 106 */       for (String s : v) {
/* 107 */         sb.append(s);
/*     */       }
/* 109 */       i += v.size();
/* 110 */       this.navigator.dots().add(Integer.valueOf(i));
/*     */     }
/* 112 */     sb.append("</p></html>");
/* 113 */     this.expression = sb.toString();
/* 114 */     this.dotPosition = i;
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 121 */     this.expression = "<sub>&nbsp;</sub>&nbsp;<sup>&nbsp;</sup>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
/* 122 */     this.dotPosition = 54;
/* 123 */     this.clearDisplay = true;
/*     */   }
/*     */ 
/*     */   public Base getBase()
/*     */   {
/* 132 */     return this.panel.getApplet().getBase();
/*     */   }
/*     */ 
/*     */   public int getDigits()
/*     */   {
/* 141 */     return 20;
/*     */   }
/*     */ 
/*     */   public void left()
/*     */   {
/* 149 */     this.scrollData.left();
/*     */   }
/*     */ 
/*     */   public void right()
/*     */   {
/* 158 */     this.scrollData.right();
/*     */   }
/*     */ 
/*     */   final LeftOrRight getScrollDirections()
/*     */   {
/* 166 */     return this.scrollData.getScrollDirections();
/*     */   }
/*     */ 
/*     */   private class ScrollData
/*     */   {
/*     */     private DisplayLabel displayLabel;
/*     */     private final Vector<Integer> dots;
/*     */     private Vector<Integer> distances;
/*     */     boolean atRight;
/*     */ 
/*     */     public ScrollData(DisplayLabel displayLabel)
/*     */     {
/* 230 */       this.dots = displayLabel.navigator.dots();
/* 231 */       this.displayLabel = displayLabel;
/*     */ 
/* 233 */       this.distances = new Vector();
/* 234 */       this.atRight = true;
/*     */     }
/*     */ 
/*     */     public void update()
/*     */     {
/* 243 */       this.distances = new Vector();
/* 244 */       if (this.dots.isEmpty()) return;
/* 245 */       ListIterator i = this.dots.listIterator();
/* 246 */       Rectangle r = null;
/*     */       try { r = DisplayLabel.this.textPane.modelToView(((Integer)i.next()).intValue()); } catch (BadLocationException e) {
/* 248 */         return;
/* 249 */       }if (r == null) return;
/* 250 */       int p = r.x;
/* 251 */       while (i.hasNext()) {
/*     */         try { r = DisplayLabel.this.textPane.modelToView(((Integer)i.next()).intValue()); } catch (BadLocationException e) {
/* 253 */           return;
/* 254 */         }if (r == null) return;
/* 255 */         int q = r.x;
/* 256 */         this.distances.add(Integer.valueOf(q - p));
/* 257 */         p = q;
/*     */       }
/*     */     }
/*     */ 
/*     */     final LeftOrRight getScrollDirections()
/*     */     {
/* 266 */       if ((this.dots.isEmpty()) || (this.distances.size() != this.dots.size() - 1))
/* 267 */         return LeftOrRight.NEITHER;
/* 268 */       boolean left = false;
/* 269 */       boolean right = false;
/* 270 */       if (this.atRight) {
/* 271 */         right = ((Integer)this.dots.lastElement()).intValue() > this.displayLabel.dotPosition;
/* 272 */         double distance = 0.0D;
/* 273 */         ListIterator i = this.dots.listIterator();
/* 274 */         ListIterator j = this.distances.listIterator();
/* 275 */         while ((j.hasNext()) && 
/* 276 */           (((Integer)i.next()).intValue() < this.displayLabel.dotPosition)) {
/* 277 */           distance += ((Integer)j.next()).intValue();
/*     */         }
/* 279 */         left = distance > this.displayLabel.getWidth();
/*     */       } else {
/* 281 */         left = ((Integer)this.dots.firstElement()).intValue() < this.displayLabel.dotPosition;
/* 282 */         double distance = 0.0D;
/* 283 */         ListIterator i = this.dots.listIterator(this.dots.size());
/* 284 */         ListIterator j = this.distances.listIterator(this.distances.size());
/*     */ 
/* 286 */         while ((j.hasPrevious()) && 
/* 287 */           (((Integer)i.previous()).intValue() > this.displayLabel.dotPosition)) {
/* 288 */           distance += ((Integer)j.previous()).intValue();
/*     */         }
/* 290 */         right = distance > this.displayLabel.getWidth();
/*     */       }
/* 292 */       if ((left) && (right))
/* 293 */         return LeftOrRight.BOTH;
/* 294 */       if (left)
/* 295 */         return LeftOrRight.LEFT;
/* 296 */       if (right) {
/* 297 */         return LeftOrRight.RIGHT;
/*     */       }
/* 299 */       return LeftOrRight.NEITHER;
/*     */     }
/*     */ 
/*     */     public void left()
/*     */     {
/* 308 */       int width = this.displayLabel.getWidth();
/* 309 */       if (this.atRight)
/*     */       {
/* 311 */         int distance = 0;
/* 312 */         ListIterator i = this.dots.listIterator();
/* 313 */         ListIterator j = this.distances.listIterator();
/* 314 */         while ((j.hasNext()) && 
/* 315 */           (((Integer)i.next()).intValue() < this.displayLabel.dotPosition)) {
/* 316 */           distance += ((Integer)j.next()).intValue();
/*     */         }
/* 318 */         if (width >= distance) return;
/*     */ 
/* 320 */         i = this.dots.listIterator();
/* 321 */         j = this.distances.listIterator();
/* 322 */         int position = ((Integer)i.next()).intValue();
/* 323 */         while (j.hasNext()) {
/* 324 */           int d = ((Integer)j.next()).intValue();
/* 325 */           if (distance - d < width) break;
/* 326 */           distance -= d;
/* 327 */           position = ((Integer)i.next()).intValue();
/*     */         }
/* 329 */         this.displayLabel.dotPosition = position;
/* 330 */         this.atRight = false;
/*     */       } else {
/* 332 */         ListIterator i = this.dots.listIterator();
/* 333 */         while (i.hasNext())
/* 334 */           if (((Integer)i.next()).intValue() < this.displayLabel.dotPosition)
/*     */             continue;
/* 336 */         i.previous();
/* 337 */         if (!i.hasPrevious()) return;
/* 338 */         this.displayLabel.dotPosition = ((Integer)i.previous()).intValue();
/*     */       }
/*     */ 
/* 341 */       this.displayLabel.textPane.getCaret().setDot(this.displayLabel.dotPosition);
/*     */     }
/*     */ 
/*     */     public void right()
/*     */     {
/* 350 */       int width = this.displayLabel.getWidth();
/* 351 */       if (!this.atRight)
/*     */       {
/* 353 */         int distance = 0;
/* 354 */         ListIterator i = this.dots.listIterator(this.dots.size());
/*     */ 
/* 356 */         ListIterator j = this.distances.listIterator(this.distances.size());
/* 357 */         while ((j.hasPrevious()) && 
/* 358 */           (((Integer)i.previous()).intValue() > this.displayLabel.dotPosition)) {
/* 359 */           distance += ((Integer)j.previous()).intValue();
/*     */         }
/* 361 */         if (width >= distance) return;
/*     */ 
/* 363 */         i = this.dots.listIterator(this.dots.size());
/* 364 */         j = this.distances.listIterator(this.distances.size());
/* 365 */         int position = ((Integer)i.previous()).intValue();
/* 366 */         while (j.hasPrevious()) {
/* 367 */           int d = ((Integer)j.previous()).intValue();
/* 368 */           if (distance - d < width) break;
/* 369 */           distance -= d;
/* 370 */           position = ((Integer)i.previous()).intValue();
/*     */         }
/* 372 */         this.displayLabel.dotPosition = position;
/* 373 */         this.atRight = true;
/*     */       } else {
/* 375 */         ListIterator i = this.dots.listIterator(this.dots.size());
/* 376 */         while (i.hasPrevious())
/* 377 */           if (((Integer)i.previous()).intValue() > this.displayLabel.dotPosition)
/*     */             continue;
/* 379 */         i.next();
/* 380 */         if (!i.hasNext()) return;
/* 381 */         this.displayLabel.dotPosition = ((Integer)i.next()).intValue();
/*     */       }
/*     */ 
/* 384 */       this.displayLabel.textPane.getCaret().setDot(this.displayLabel.dotPosition);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DisplayCaret extends DefaultCaret
/*     */   {
/*     */     private DisplayLabel.ScrollData scrollData;
/*     */     public boolean updateFlag;
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public DisplayCaret(DisplayLabel.ScrollData scrollData)
/*     */     {
/* 182 */       this.scrollData = scrollData;
/* 183 */       this.updateFlag = false;
/*     */     }
/*     */ 
/*     */     public void paint(Graphics graphics)
/*     */     {
/* 196 */       synchronized (this) {
/* 197 */         super.paint(graphics);
/* 198 */         if (this.updateFlag) this.scrollData.update();
/* 199 */         this.updateFlag = false;
/* 200 */         notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.DisplayLabel
 * JD-Core Version:    0.6.0
 */