/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.io.PrintStream;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.UIManager;
/*    */ 
/*    */ public class View extends JPanel
/*    */ {
/*    */   protected Graph graph;
/*    */   protected Model model;
/*    */   protected Transformation transformation;
/*    */   protected double distanceValue;
/*    */   public static final double delta = 0.001D;
/*    */   private int lastWidth;
/*    */   private int lastHeight;
/*    */   AtomicBoolean forceUpdateBool;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public View(Model model, Graph graph)
/*    */   {
/* 14 */     this.model = model;
/* 15 */     this.graph = graph;
/* 16 */     this.forceUpdateBool = new AtomicBoolean(false);
/* 17 */     this.transformation = new Transformation(this);
/*    */ 
/* 19 */     Font font = UIManager.getFont("Label.font");
/* 20 */     setFont(font.deriveFont(10.0F));
/* 21 */     this.distanceValue = 1.0D;
/* 22 */     this.lastWidth = (this.lastHeight = 0);
/*    */   }
/*    */ 
/*    */   public void forceUpdate()
/*    */   {
/* 28 */     this.forceUpdateBool.set(true);
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics graphics)
/*    */   {
/* 36 */     if ((this.lastWidth != getWidth()) || (this.lastHeight != getHeight()) || (this.forceUpdateBool.compareAndSet(true, false)))
/*    */     {
/* 38 */       System.out.println("*** changed size  or forced update *** ");
/* 39 */       this.graph.updateMenu();
/* 40 */       setCursor(new Cursor(3));
/* 41 */       this.model.updatePaths();
/* 42 */       this.lastWidth = getWidth();
/* 43 */       this.lastHeight = getHeight();
/*    */     }
/*    */ 
/* 46 */     Graphics2D graphics2d = (Graphics2D)graphics;
/* 47 */     graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */ 
/* 49 */     graphics2d.setColor(Color.WHITE);
/* 50 */     graphics2d.fillRect(0, 0, this.lastWidth, this.lastHeight);
/* 51 */     graphics2d.setColor(Color.BLACK);
/* 52 */     this.model.draw(this, graphics2d);
/* 53 */     System.out.println("finished updating view");
/*    */   }
/*    */ 
/*    */   public final Transformation getTransformation()
/*    */   {
/* 62 */     return this.transformation;
/*    */   }
/*    */ 
/*    */   public Model getModel()
/*    */   {
/* 70 */     return this.model;
/*    */   }
/*    */ 
/*    */   double distance()
/*    */   {
/* 79 */     return this.distanceValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.View
 * JD-Core Version:    0.6.0
 */