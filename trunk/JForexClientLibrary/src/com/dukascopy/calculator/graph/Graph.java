/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import com.dukascopy.calculator.Error;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.ReadOnlyCalculatorApplet;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class Graph extends JFrame
/*    */ {
/*    */   private Menu menu;
/*    */   private Model model;
/*    */   private View view;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public Graph(ReadOnlyCalculatorApplet applet)
/*    */   {
/* 15 */     super("Scientific Calculator Graph");
/* 16 */     this.model = new Model();
/* 17 */     this.view = new View(this.model, this);
/* 18 */     this.menu = new Menu(applet, this.view, this.model);
/* 19 */     setJMenuBar(this.menu);
/* 20 */     int h = applet.graphHeight();
/* 21 */     setSize(3 * h / 2, h);
/* 22 */     setDefaultCloseOperation(1);
/* 23 */     setContentPane(this.view);
/* 24 */     setVisible(true);
/*    */   }
/*    */ 
/*    */   public void setLocus(OObject oobject)
/*    */   {
/* 32 */     if ((oobject != null) && (!(oobject instanceof Error)))
/*    */     {
/* 36 */       Locus locus = new Locus(oobject, this.view);
/* 37 */       this.model.reset(locus);
/* 38 */       this.view.repaint();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateMenu()
/*    */   {
/* 46 */     this.menu.updateSizes();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Graph
 * JD-Core Version:    0.6.0
 */