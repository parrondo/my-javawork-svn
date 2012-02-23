/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.Spring;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.SpringLayout.Constraints;
/*     */ 
/*     */ public class SpringUtilities
/*     */ {
/*     */   public static void printSizes(Component c)
/*     */   {
/*  23 */     System.out.println("minimumSize = " + c.getMinimumSize());
/*  24 */     System.out.println("preferredSize = " + c.getPreferredSize());
/*  25 */     System.out.println("maximumSize = " + c.getMaximumSize());
/*     */   }
/*     */ 
/*     */   public static void makeGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad)
/*     */   {
/*     */     SpringLayout layout;
/*     */     try
/*     */     {
/*  48 */       layout = (SpringLayout)parent.getLayout();
/*     */     } catch (ClassCastException exc) {
/*  50 */       System.err.println("The first argument to makeGrid must use SpringLayout.");
/*  51 */       return;
/*     */     }
/*     */ 
/*  54 */     Spring xPadSpring = Spring.constant(xPad);
/*  55 */     Spring yPadSpring = Spring.constant(yPad);
/*  56 */     Spring initialXSpring = Spring.constant(initialX);
/*  57 */     Spring initialYSpring = Spring.constant(initialY);
/*  58 */     int max = rows * cols;
/*     */ 
/*  62 */     Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
/*     */ 
/*  64 */     Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
/*     */ 
/*  66 */     for (int i = 1; i < max; i++) {
/*  67 */       SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
/*     */ 
/*  70 */       maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
/*  71 */       maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
/*     */     }
/*     */ 
/*  76 */     for (int i = 0; i < max; i++) {
/*  77 */       SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
/*     */ 
/*  80 */       cons.setWidth(maxWidthSpring);
/*  81 */       cons.setHeight(maxHeightSpring);
/*     */     }
/*     */ 
/*  86 */     SpringLayout.Constraints lastCons = null;
/*  87 */     SpringLayout.Constraints lastRowCons = null;
/*  88 */     for (int i = 0; i < max; i++) {
/*  89 */       SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
/*     */ 
/*  91 */       if (i % cols == 0) {
/*  92 */         lastRowCons = lastCons;
/*  93 */         cons.setX(initialXSpring);
/*     */       } else {
/*  95 */         cons.setX(Spring.sum(lastCons.getConstraint("East"), xPadSpring));
/*     */       }
/*     */ 
/*  99 */       if (i / cols == 0)
/* 100 */         cons.setY(initialYSpring);
/*     */       else {
/* 102 */         cons.setY(Spring.sum(lastRowCons.getConstraint("South"), yPadSpring));
/*     */       }
/*     */ 
/* 105 */       lastCons = cons;
/*     */     }
/*     */ 
/* 109 */     SpringLayout.Constraints pCons = layout.getConstraints(parent);
/* 110 */     pCons.setConstraint("South", Spring.sum(Spring.constant(yPad), lastCons.getConstraint("South")));
/*     */ 
/* 114 */     pCons.setConstraint("East", Spring.sum(Spring.constant(xPad), lastCons.getConstraint("East")));
/*     */   }
/*     */ 
/*     */   private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols)
/*     */   {
/* 125 */     SpringLayout layout = (SpringLayout)parent.getLayout();
/* 126 */     Component c = parent.getComponent(row * cols + col);
/* 127 */     return layout.getConstraints(c);
/*     */   }
/*     */ 
/*     */   public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad)
/*     */   {
/*     */     SpringLayout layout;
/*     */     try
/*     */     {
/* 151 */       layout = (SpringLayout)parent.getLayout();
/*     */     } catch (ClassCastException exc) {
/* 153 */       System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
/* 154 */       return;
/*     */     }
/*     */ 
/* 158 */     Spring x = Spring.constant(initialX);
/* 159 */     for (int c = 0; c < cols; c++) {
/* 160 */       Spring width = Spring.constant(0);
/* 161 */       for (int r = 0; r < rows; r++) {
/* 162 */         width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
/*     */       }
/*     */ 
/* 166 */       for (int r = 0; r < rows; r++) {
/* 167 */         SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
/*     */ 
/* 169 */         constraints.setX(x);
/* 170 */         constraints.setWidth(width);
/*     */       }
/* 172 */       x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
/*     */     }
/*     */ 
/* 176 */     Spring y = Spring.constant(initialY);
/* 177 */     for (int r = 0; r < rows; r++) {
/* 178 */       Spring height = Spring.constant(0);
/* 179 */       for (int c = 0; c < cols; c++) {
/* 180 */         height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
/*     */       }
/*     */ 
/* 184 */       for (int c = 0; c < cols; c++) {
/* 185 */         SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
/*     */ 
/* 187 */         constraints.setY(y);
/* 188 */         constraints.setHeight(height);
/*     */       }
/* 190 */       y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
/*     */     }
/*     */ 
/* 194 */     SpringLayout.Constraints pCons = layout.getConstraints(parent);
/* 195 */     pCons.setConstraint("South", y);
/* 196 */     pCons.setConstraint("East", x);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.SpringUtilities
 * JD-Core Version:    0.6.0
 */